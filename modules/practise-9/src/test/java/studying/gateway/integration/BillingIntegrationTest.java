package studying.gateway.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;
import studying.gateway.dto.Api;
import studying.gateway.exception.GatewayException;
import studying.gateway.repository.GatewayRepository;
import studying.gateway.services.*;

@SpringBootTest(
    properties = {
      "spring.jpa.properties.hibernate.generate_statistics=true",
      "app.recovery-delay-ms=3600000"
    })
class BillingIntegrationTest {
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

  static {
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRES::getUsername);
    r.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Autowired AccountService accounts;
  @Autowired BillingService billing;
  @Autowired ChatService chat;
  @Autowired RecoveryService recovery;
  @Autowired JdbcTemplate sql;
  @Autowired GatewayRepository db;
  @Autowired PlatformTransactionManager txManager;
  @Autowired EntityManagerFactory emf;
  @MockitoBean OllamaGateway ollama;
  UUID user;
  String auth;
  Api.Chat command = new Api.Chat("qwen2.5:0.5b", "Hello", 80);

  @BeforeEach
  void setup() {
    sql.execute(
        "TRUNCATE wallet_transactions, usage_records, ai_requests, api_keys, wallets, users"
            + " CASCADE");
    sql.update("DELETE FROM model_prices WHERE input_price_per_1000=9999");
    sql.update(
        "UPDATE model_prices SET input_price_per_1000=100,output_price_per_1000=200,valid_to=null");
    user = accounts.create(UUID.randomUUID() + "@example.com").id();
    auth = "Bearer " + accounts.issue(user).key();
    accounts.topUp(user, 10000);
  }

  @Test
  void successAndReplayAndConflict() {
    when(ollama.generate(command))
        .thenAnswer(invocation -> {
          assertFalse(
              org.springframework.transaction.support.TransactionSynchronizationManager
                  .isActualTransactionActive());
          assertTrue(accounts.wallet(user).reservedBalance() > 0);
          return new OllamaGateway.Result("Answer", 18, 42);
        });
    var first = chat.chat(auth, "same", command);
    var second = chat.chat(auth, "same", command);
    assertEquals(first.requestId(), second.requestId());
    assertEquals(11, first.usage().chargedCredits());
    assertEquals(9989, accounts.wallet(user).balance());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    verify(ollama, times(1)).generate(command);
    assertEquals(
        409,
        assertThrows(
                GatewayException.class,
                () -> chat.chat(auth, "same", new Api.Chat(command.model(), "changed", 80)))
            .getStatus());
    assertEquals(
        List.of("TOP_UP", "RESERVE", "CHARGE", "RELEASE"),
        sql.queryForList(
            "select type from wallet_transactions order by created_at,id", String.class));
  }

  @Test
  void providerFailureReleasesReserveAndReplayDoesNotRetry() {
    when(ollama.generate(command)).thenThrow(new IllegalStateException("offline"));
    var error = assertThrows(GatewayException.class, () -> chat.chat(auth, "failed", command));
    assertEquals(503, error.getStatus());
    assertEquals("FAILED", billing.state(error.getRequestId()).status());
    assertEquals(10000, accounts.wallet(user).balance());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    assertEquals("FAILED", chat.chat(auth, "failed", command).status());
    verify(ollama, times(1)).generate(command);
  }

  @Test
  void snapshotPriceAndUnderestimatedReserve() {
    var reservation = billing.reserve(auth, "price", command);
    sql.update("UPDATE model_prices SET valid_to=CURRENT_TIMESTAMP WHERE valid_to IS NULL");
    sql.update(
        "INSERT INTO"
            + " model_prices(id,model_id,input_price_per_1000,output_price_per_1000,valid_from)"
            + " SELECT ?,id,9999,9999,CURRENT_TIMESTAMP FROM models WHERE name=?",
        UUID.randomUUID(),
        command.model());
    var result = billing.complete(reservation.id(), "Answer", 18, 42);
    assertEquals(11, result.usage().chargedCredits());
    var next = billing.reserve(auth, "excess", new Api.Chat(command.model(), "a", 1));
    var failed = billing.complete(next.id(), "Answer", 1000000, 1000000);
    assertEquals("FAILED", failed.status());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    assertEquals(9989, accounts.wallet(user).balance());
  }

  @Test
  void recoveryAndLateCompletionAreIdempotent() {
    var reservation = billing.reserve(auth, "timeout", command);
    sql.update(
        "UPDATE ai_requests SET created_at=? WHERE id=?",
        java.sql.Timestamp.from(Instant.now().minusSeconds(1200)),
        reservation.id());
    recovery.recover();
    recovery.recover();
    assertEquals("EXPIRED", billing.complete(reservation.id(), "late", 10, 10).status());
    assertEquals(10000, accounts.wallet(user).balance());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    assertEquals(
        1,
        sql.queryForObject(
            "select count(*) from wallet_transactions where type='RELEASE'", Integer.class));
  }

  @Test
  void revokedAndExpiredKeysAreRejected() {
    var key = accounts.issue(user);
    accounts.revoke(key.id());
    assertEquals(
        401,
        assertThrows(
                GatewayException.class,
                () -> billing.reserve("Bearer " + key.key(), "revoked", command))
            .getStatus());
    sql.update("UPDATE api_keys SET expires_at=CURRENT_TIMESTAMP - INTERVAL '1 second'");
    assertEquals(
        401,
        assertThrows(GatewayException.class, () -> billing.reserve(auth, "expired", command))
            .getStatus());
  }

  @Test
  void parallelRequestsCannotOverdraw() throws Exception {
    UUID poor = accounts.create("parallel@example.com").id();
    accounts.topUp(poor, 100);
    String token = "Bearer " + accounts.issue(poor).key();
    // 261 estimated input tokens => 27 credits + 53 output credits = 80.
    var body = new Api.Chat(command.model(), "Hello", 265);
    CountDownLatch ready = new CountDownLatch(2), start = new CountDownLatch(1);
    try (var pool = Executors.newFixedThreadPool(2)) {
      List<Future<Integer>> futures = new ArrayList<>();
      for (int i = 0; i < 2; i++) {
        String idem = "parallel-" + i;
        futures.add(
            pool.submit(() -> {
              ready.countDown();
              start.await();
              try {
                billing.reserve(token, idem, body);
                return 200;
              } catch (GatewayException e) {
                return e.getStatus();
              }
            }));
      }
      assertTrue(ready.await(5, TimeUnit.SECONDS));
      start.countDown();
      List<Integer> codes = new ArrayList<>();
      for (var future : futures) codes.add(future.get(10, TimeUnit.SECONDS));
      Collections.sort(codes);
      assertEquals(List.of(200, 402), codes);
    }
    assertEquals(100, accounts.wallet(poor).balance());
    assertEquals(80, accounts.wallet(poor).reservedBalance());
  }

  @Test
  void parallelIdenticalRequestsCreateOneReservation() throws Exception {
    CountDownLatch start = new CountDownLatch(1);
    try (var pool = Executors.newFixedThreadPool(2)) {
      Callable<BillingService.Reservation> task =
          () -> {
            start.await();
            return billing.reserve(auth, "concurrent-replay", command);
          };
      var first = pool.submit(task);
      var second = pool.submit(task);
      start.countDown();
      var a = first.get(10, TimeUnit.SECONDS);
      var b = second.get(10, TimeUnit.SECONDS);
      assertEquals(a.id(), b.id());
      assertNotEquals(a.fresh(), b.fresh());
    }
    assertEquals(1, sql.queryForObject("select count(*) from ai_requests", Integer.class));
    assertEquals(
        1,
        sql.queryForObject(
            "select count(*) from wallet_transactions where type='RESERVE'", Integer.class));
  }

  @Test
  void invalidUsageAndTimeoutReleaseReserve() {
    when(ollama.generate(command)).thenReturn(new OllamaGateway.Result("bad", -1, 5));
    assertEquals(
        503,
        assertThrows(GatewayException.class, () -> chat.chat(auth, "bad-usage", command))
            .getStatus());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    when(ollama.generate(command))
        .thenThrow(new org.springframework.web.client.ResourceAccessException("timeout"));
    assertEquals(
        503,
        assertThrows(GatewayException.class, () -> chat.chat(auth, "provider-timeout", command))
            .getStatus());
    assertEquals(0, accounts.wallet(user).reservedBalance());
    assertEquals(10000, accounts.wallet(user).balance());
  }

  @Test
  void constraintsPaginationAndNoNPlusOne() {
    assertThrows(
        org.springframework.dao.DataIntegrityViolationException.class,
        () ->
            accounts.create(
                sql.queryForObject("select email from users where id=?", String.class, user)));
    for (int i = 0; i < 4; i++) {
      var r = billing.reserve(auth, "n1-" + i, command);
      billing.complete(r.id(), "ok", 10, 10);
    }
    var stats = emf.unwrap(SessionFactory.class).getStatistics();
    stats.clear();
    var page = accounts.transactions(user, 0, 5);
    assertEquals(5, page.content().size());
    assertTrue(page.hasNext());
    // User existence + one page with fetched AiRequest, regardless of row count.
    assertEquals(2, stats.getPrepareStatementCount());
    assertNotEquals(
        page.content().getFirst().id(),
        accounts.transactions(user, 1, 5).content().getFirst().id());
    assertThrows(
        Exception.class, () -> sql.update("UPDATE wallets SET balance=-1 WHERE user_id=?", user));
  }
}

package studying.gateway.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studying.gateway.dto.Api;
import studying.gateway.exception.GatewayException;
import studying.gateway.model.*;
import studying.gateway.repository.GatewayRepository;

@Service
@RequiredArgsConstructor
public class BillingService {
  private final GatewayRepository db;

  public record Reservation(UUID id, boolean fresh) {}

  @Transactional
  public Reservation reserve(String authorization, String idem, Api.Chat command) {
    if (authorization == null || !authorization.startsWith("Bearer ")) throw invalidKey();
    ApiKey key = db.key(Credits.hash(authorization.substring(7)));
    if (key == null || !key.valid()) throw invalidKey();
    Wallet wallet = db.wallet(key.getUser().getId(), true);
    // All operations for one user acquire the wallet lock before reading request state.
    String hash =
        Credits.hash(
            command.model().length()
                + ":"
                + command.model()
                + command.prompt()
                + ":"
                + command.maxTokens());
    AiRequest previous = db.previous(key.getUser().getId(), idem);
    if (previous != null) {
      if (!previous.getRequestHash().equals(hash))
        throw new GatewayException(
            409, "IDEMPOTENCY_CONFLICT", "Ключ уже использован с другим телом", previous.getId());
      return new Reservation(previous.getId(), false);
    }
    ModelPrice price = db.price(command.model(), Instant.now());
    int estimatedInput =
        Math.addExact(command.prompt().getBytes(StandardCharsets.UTF_8).length, 256);
    long reserve =
        Credits.cost(
            estimatedInput,
            command.maxTokens(),
            price.getInputPricePer1000(),
            price.getOutputPricePer1000());
    if (wallet.available() < reserve)
      throw new GatewayException(402, "INSUFFICIENT_CREDITS", "Недостаточно кредитов");
    AiRequest request =
        new AiRequest(key.getUser(), key, price, idem, hash, command.maxTokens(), reserve);
    db.save(request);
    wallet.reserve(reserve);
    key.used();
    db.save(new WalletTransaction(wallet, request, "RESERVE", reserve));
    return new Reservation(request.getId(), true);
  }

  @Transactional
  public boolean processing(UUID id) {
    AiRequest initial = db.request(id);
    db.wallet(initial.getUser().getId(), true);
    db.refresh(initial);
    if (!initial.pending()) return false;
    initial.processing();
    return true;
  }

  @Transactional
  public Api.ChatView complete(UUID id, String content, int input, int output) {
    AiRequest request = db.request(id);
    Wallet wallet = db.wallet(request.getUser().getId(), true);
    // Re-read after lock acquisition: EntityManager may have read the request before waiting.
    db.refresh(request);
    if (!request.pending()) return view(request, wallet);
    if (content == null || input < 0 || output < 0 || (long) input + output > Integer.MAX_VALUE)
      throw new GatewayException(503, "INVALID_USAGE", "Ollama вернула некорректный usage", id);
    ModelPrice price = request.getModelPrice();
    long cost =
        Credits.cost(input, output, price.getInputPricePer1000(), price.getOutputPricePer1000());
    if (cost > request.getReservedCredits() + wallet.available()) {
      failLocked(request, wallet, "USAGE_EXCEEDS_BALANCE", false);
      return view(request, wallet);
    }
    wallet.charge(cost, request.getReservedCredits());
    db.save(new WalletTransaction(wallet, request, "CHARGE", cost));
    long release = Math.max(0, request.getReservedCredits() - cost);
    wallet.release(release);
    db.save(new WalletTransaction(wallet, request, "RELEASE", release));
    db.save(new UsageRecord(request, input, output));
    request.complete(cost, content);
    return view(request, wallet);
  }

  @Transactional
  public void fail(UUID id, String code, boolean expired) {
    AiRequest request = db.request(id);
    Wallet wallet = db.wallet(request.getUser().getId(), true);
    db.refresh(request);
    if (request.pending()) failLocked(request, wallet, code, expired);
  }

  private void failLocked(AiRequest request, Wallet wallet, String code, boolean expired) {
    wallet.release(request.getReservedCredits());
    db.save(new WalletTransaction(wallet, request, "RELEASE", request.getReservedCredits()));
    request.fail(code, expired);
  }

  @Transactional(readOnly = true)
  public Api.ChatView state(UUID id) {
    AiRequest request = db.request(id);
    return view(request, db.wallet(request.getUser().getId(), false));
  }

  private Api.ChatView view(AiRequest request, Wallet wallet) {
    UsageRecord usage = db.usage(request.getId());
    return new Api.ChatView(
        request.getId(),
        request.getModel().getName(),
        request.getResponseContent(),
        usage == null
            ? null
            : new Api.Usage(
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens(),
                request.getActualCost()),
        wallet.getBalance(),
        request.getStatus(),
        request.getErrorCode());
  }

  private GatewayException invalidKey() {
    return new GatewayException(401, "INVALID_API_KEY", "API-ключ недействителен");
  }
}

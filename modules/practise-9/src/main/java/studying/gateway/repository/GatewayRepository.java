package studying.gateway.repository;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Repository;
import studying.gateway.exception.GatewayException;
import studying.gateway.model.*;

@Repository
public class GatewayRepository {
  @PersistenceContext private EntityManager em;

  public void refresh(Object value) {
    em.refresh(value);
  }

  public void save(Object value) {
    em.persist(value);
  }

  public User user(UUID id) {
    User user = em.find(User.class, id);
    if (user == null) throw new GatewayException(404, "USER_NOT_FOUND", "Пользователь не найден");
    return user;
  }

  public Wallet wallet(UUID userId, boolean lock) {
    var query =
        em.createQuery("select w from Wallet w where w.user.id=:id", Wallet.class)
            .setParameter("id", userId);
    if (lock) query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
    return query
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new GatewayException(404, "USER_NOT_FOUND", "Пользователь не найден"));
  }

  public ApiKey key(String hash) {
    return em.createQuery(
            "select k from ApiKey k join fetch k.user where k.keyHash=:hash", ApiKey.class)
        .setParameter("hash", hash)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public ApiKey key(UUID id) {
    return em.find(ApiKey.class, id);
  }

  public ModelPrice price(String model, Instant now) {
    return em.createQuery(
            "select p from ModelPrice p join fetch p.model m where m.name=:name and"
                + " m.status='ACTIVE' and p.validFrom<=:now and (p.validTo is null or"
                + " p.validTo>:now) order by p.validFrom desc,p.id",
            ModelPrice.class)
        .setParameter("name", model)
        .setParameter("now", now)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElseThrow(
            () -> new GatewayException(404, "MODEL_NOT_FOUND", "Модель или тариф не найдены"));
  }

  public AiRequest request(UUID id) {
    return em.createQuery(
            "select r from AiRequest r join fetch r.model join fetch r.modelPrice join fetch r.user"
                + " where r.id=:id",
            AiRequest.class)
        .setParameter("id", id)
        .getResultStream()
        .findFirst()
        .orElseThrow(() -> new GatewayException(404, "REQUEST_NOT_FOUND", "Запрос не найден"));
  }

  public AiRequest previous(UUID user, String key) {
    return em.createQuery(
            "select r from AiRequest r join fetch r.model join fetch r.modelPrice where"
                + " r.user.id=:user and r.idempotencyKey=:key",
            AiRequest.class)
        .setParameter("user", user)
        .setParameter("key", key)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public UsageRecord usage(UUID request) {
    return em.createQuery("select u from UsageRecord u where u.aiRequest.id=:id", UsageRecord.class)
        .setParameter("id", request)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public List<WalletTransaction> transactions(UUID user, int offset, int limit) {
    // Fetch the optional request in the same query: DTO mapping never produces N+1.
    return em.createQuery(
            "select t from WalletTransaction t left join fetch t.aiRequest where"
                + " t.wallet.user.id=:id order by t.createdAt desc,t.id desc",
            WalletTransaction.class)
        .setParameter("id", user)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public List<ModelPrice> models(int offset, int limit) {
    return em.createQuery(
            "select p from ModelPrice p join fetch p.model m where m.status='ACTIVE' and"
                + " p.validFrom<=:now and (p.validTo is null or p.validTo>:now) order by"
                + " m.name,p.id",
            ModelPrice.class)
        .setParameter("now", Instant.now())
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public List<UUID> expired(Instant before) {
    return em.createQuery(
            "select r.id from AiRequest r where r.status in ('RESERVED','PROCESSING') and"
                + " r.createdAt<:before order by r.createdAt,r.id",
            UUID.class)
        .setParameter("before", before)
        .setMaxResults(100)
        .getResultList();
  }

  public boolean modelExists(String name) {
    return !em.createQuery("select m.id from Model m where m.name=:name", UUID.class)
        .setParameter("name", name)
        .getResultList()
        .isEmpty();
  }
}

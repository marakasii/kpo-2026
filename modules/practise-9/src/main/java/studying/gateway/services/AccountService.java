package studying.gateway.services;

import java.security.SecureRandom;
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
@Transactional
public class AccountService {
  private final GatewayRepository db;

  public Api.UserView create(String email) {
    User user = new User(email.trim().toLowerCase(Locale.ROOT));
    db.save(user);
    db.save(new Wallet(user));
    return new Api.UserView(user.getId(), user.getEmail(), user.getStatus());
  }

  public Api.KeyView issue(UUID userId) {
    User user = db.user(userId);
    if (!user.getStatus().equals("ACTIVE"))
      throw new GatewayException(401, "INVALID_API_KEY", "Пользователь заблокирован");
    byte[] bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    String secret = "sk-local-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    ApiKey key = new ApiKey(user, secret.substring(0, 16), Credits.hash(secret), null);
    db.save(key);
    return new Api.KeyView(key.getId(), secret, key.getKeyPrefix(), key.getExpiresAt());
  }

  public void revoke(UUID id) {
    ApiKey key = db.key(id);
    if (key == null) throw new GatewayException(404, "KEY_NOT_FOUND", "Ключ не найден");
    key.revoke();
  }

  public Api.WalletView topUp(UUID user, long amount) {
    Wallet wallet = db.wallet(user, true);
    wallet.topUp(amount);
    db.save(new WalletTransaction(wallet, null, "TOP_UP", amount));
    return view(wallet);
  }

  @Transactional(readOnly = true)
  public Api.WalletView wallet(UUID user) {
    return view(db.wallet(user, false));
  }

  private Api.WalletView view(Wallet w) {
    return new Api.WalletView(w.getBalance(), w.getReservedBalance(), w.available());
  }

  @Transactional(readOnly = true)
  public Api.Page<Api.TransactionView> transactions(UUID user, int page, int size) {
    db.user(user);
    var rows = db.transactions(user, Math.multiplyExact(page, size), size + 1);
    var content =
        rows.stream()
            .limit(size)
            .map(t ->
                new Api.TransactionView(
                    t.getId(),
                    t.getType(),
                    t.getAmount(),
                    t.getBalanceAfter(),
                    t.getReservedAfter(),
                    t.getAiRequest() == null ? null : t.getAiRequest().getId(),
                    t.getCreatedAt()))
            .toList();
    return new Api.Page<>(content, page, size, rows.size() > size);
  }

  @Transactional(readOnly = true)
  public Api.Page<Api.ModelView> models(int page, int size) {
    var rows = db.models(Math.multiplyExact(page, size), size + 1);
    return new Api.Page<>(
        rows.stream()
            .limit(size)
            .map(p ->
                new Api.ModelView(
                    p.getModel().getId(),
                    p.getModel().getName(),
                    p.getInputPricePer1000(),
                    p.getOutputPricePer1000()))
            .toList(),
        page,
        size,
        rows.size() > size);
  }
}

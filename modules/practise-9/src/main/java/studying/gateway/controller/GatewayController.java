package studying.gateway.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import studying.gateway.dto.Api;
import studying.gateway.services.*;

@RestController
@Validated
@RequiredArgsConstructor
public class GatewayController {
  private final AccountService accounts;
  private final ChatService chat;
  private final BillingService billing;

  @PostMapping("/api/users")
  @ResponseStatus(HttpStatus.CREATED)
  public Api.UserView create(@Valid @RequestBody Api.CreateUser body) {
    return accounts.create(body.email());
  }

  @PostMapping("/api/users/{id}/api-keys")
  @ResponseStatus(HttpStatus.CREATED)
  public Api.KeyView key(@PathVariable UUID id) {
    return accounts.issue(id);
  }

  @DeleteMapping("/api/api-keys/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revoke(@PathVariable UUID id) {
    accounts.revoke(id);
  }

  @PostMapping("/api/users/{id}/top-ups")
  public Api.WalletView topUp(@PathVariable UUID id, @Valid @RequestBody Api.TopUp body) {
    return accounts.topUp(id, body.amount());
  }

  @GetMapping("/api/users/{id}/wallet")
  public Api.WalletView wallet(@PathVariable UUID id) {
    return accounts.wallet(id);
  }

  @GetMapping("/api/users/{id}/wallet/transactions")
  public Api.Page<Api.TransactionView> transactions(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "0") @Min(0) @Max(1000000) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return accounts.transactions(id, page, size);
  }

  @GetMapping("/api/models")
  public Api.Page<Api.ModelView> models(
      @RequestParam(defaultValue = "0") @Min(0) @Max(1000000) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return accounts.models(page, size);
  }

  @PostMapping("/v1/chat/completions")
  public Api.ChatView chat(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestHeader("Idempotency-Key") @NotBlank @Size(max = 255) String idem,
      @Valid @RequestBody Api.Chat body) {
    return chat.chat(authorization, idem, body);
  }

  @GetMapping("/api/requests/{id}")
  public Api.ChatView state(@PathVariable UUID id) {
    return billing.state(id);
  }
}

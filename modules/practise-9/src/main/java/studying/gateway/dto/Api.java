package studying.gateway.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;

public final class Api {
  private Api() {}

  public record CreateUser(@NotBlank @Email @Size(max = 320) String email) {}

  public record TopUp(@Positive @Max(1000000000000L) long amount) {}

  public record Chat(
      @NotBlank @Size(max = 255) String model,
      @NotBlank @Size(max = 16000) String prompt,
      @Min(1) @Max(4096) int maxTokens) {}

  public record UserView(UUID id, String email, String status) {}

  public record KeyView(UUID id, String key, String prefix, Instant expiresAt) {}

  public record WalletView(long balance, long reservedBalance, long available) {}

  public record Usage(
      int promptTokens, int completionTokens, int totalTokens, long chargedCredits) {}

  public record ChatView(
      UUID requestId,
      String model,
      String content,
      Usage usage,
      long balance,
      String status,
      String errorCode) {}

  public record TransactionView(
      UUID id,
      String type,
      long amount,
      long balanceAfter,
      long reservedAfter,
      UUID requestId,
      Instant createdAt) {}

  public record ModelView(UUID id, String name, long inputPricePer1000, long outputPricePer1000) {}

  public record Page<T>(List<T> content, int page, int size, boolean hasNext) {}

  public record Error(String code, String message, UUID requestId) {}
}

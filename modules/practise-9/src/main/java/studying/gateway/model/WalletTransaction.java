package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "wallet_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletTransaction {
  @Id private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id")
  private Wallet wallet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ai_request_id")
  private AiRequest aiRequest;

  private String type;
  private long amount;
  private long balanceAfter;
  private long reservedAfter;
  private Instant createdAt = Instant.now();

  public WalletTransaction(Wallet wallet, AiRequest request, String type, long amount) {
    this.wallet = wallet;
    aiRequest = request;
    this.type = type;
    this.amount = amount;
    balanceAfter = wallet.getBalance();
    reservedAfter = wallet.getReservedBalance();
  }
}

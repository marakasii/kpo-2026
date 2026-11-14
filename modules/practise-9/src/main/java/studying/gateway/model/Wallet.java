package studying.gateway.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "wallets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {
  @Id private UUID id = UUID.randomUUID();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private long balance;
  private long reservedBalance;
  @Version private long version;

  public Wallet(User user) {
    this.user = user;
  }

  public long available() {
    return balance - reservedBalance;
  }

  public void topUp(long amount) {
    if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
    balance = Math.addExact(balance, amount);
  }

  public void reserve(long amount) {
    if (amount < 0 || amount > available())
      throw new IllegalArgumentException("Insufficient credits");
    reservedBalance = Math.addExact(reservedBalance, amount);
  }

  public void charge(long cost, long reservation) {
    if (cost < 0
        || reservation < 0
        || reservation > reservedBalance
        || cost > reservation + available())
      throw new IllegalArgumentException("Insufficient credits");
    // Release the spent part before debiting, preserving invariants in journal snapshots.
    reservedBalance -= Math.min(cost, reservation);
    balance -= cost;
  }

  public void release(long amount) {
    if (amount < 0 || amount > reservedBalance)
      throw new IllegalArgumentException("Invalid release");
    reservedBalance -= amount;
  }
}

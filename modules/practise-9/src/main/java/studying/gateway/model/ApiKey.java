package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@org.hibernate.annotations.DynamicUpdate
@Table(name = "api_keys")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiKey {
  @Id private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private String keyPrefix;
  private String keyHash;
  private String status = "ACTIVE";
  private Instant createdAt = Instant.now();
  private Instant lastUsedAt;
  private Instant expiresAt;

  public ApiKey(User user, String prefix, String hash, Instant expiresAt) {
    this.user = user;
    this.keyPrefix = prefix;
    this.keyHash = hash;
    this.expiresAt = expiresAt;
  }

  public void revoke() {
    status = "REVOKED";
  }

  public boolean valid() {
    return status.equals("ACTIVE")
        && user.getStatus().equals("ACTIVE")
        && (expiresAt == null || expiresAt.isAfter(Instant.now()));
  }

  public void used() {
    lastUsedAt = Instant.now();
  }
}

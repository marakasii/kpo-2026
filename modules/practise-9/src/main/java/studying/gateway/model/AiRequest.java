package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "ai_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiRequest {
  @Id private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "api_key_id")
  private ApiKey apiKey;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  private Model model;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_price_id")
  private ModelPrice modelPrice;

  private String idempotencyKey;
  private String requestHash;
  private String status = "RESERVED";
  private int maxOutputTokens;
  private long reservedCredits;
  private Long actualCost;

  @Column(columnDefinition = "text")
  private String responseContent;

  private Instant createdAt = Instant.now();
  private Instant completedAt;
  private String errorCode;

  public AiRequest(
      User user, ApiKey key, ModelPrice price, String idem, String hash, int max, long reserve) {
    this.user = user;
    apiKey = key;
    model = price.getModel();
    modelPrice = price;
    idempotencyKey = idem;
    requestHash = hash;
    maxOutputTokens = max;
    reservedCredits = reserve;
  }

  public boolean pending() {
    return status.equals("RESERVED") || status.equals("PROCESSING");
  }

  public void processing() {
    status = "PROCESSING";
  }

  public void complete(long cost, String content) {
    status = "COMPLETED";
    actualCost = cost;
    responseContent = content;
    completedAt = Instant.now();
  }

  public void fail(String code, boolean expired) {
    status = expired ? "EXPIRED" : "FAILED";
    errorCode = code;
    completedAt = Instant.now();
  }
}

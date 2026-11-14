package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "usage_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsageRecord {
  @Id private UUID id = UUID.randomUUID();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ai_request_id")
  private AiRequest aiRequest;

  private int promptTokens;
  private int completionTokens;
  private int totalTokens;
  private String modelName;
  private Instant createdAt = Instant.now();

  public UsageRecord(AiRequest request, int input, int output) {
    aiRequest = request;
    promptTokens = input;
    completionTokens = output;
    totalTokens = Math.addExact(input, output);
    modelName = request.getModel().getName();
  }
}

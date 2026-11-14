package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "models")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Model {
  @Id private UUID id = UUID.randomUUID();
  private String name;
  private String status = "ACTIVE";
  private Instant createdAt = Instant.now();

  public Model(String name) {
    this.name = name;
  }
}

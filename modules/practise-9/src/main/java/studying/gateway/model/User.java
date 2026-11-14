package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
  @Id private UUID id = UUID.randomUUID();
  private String email;
  private String status = "ACTIVE";
  private Instant createdAt = Instant.now();

  public User(String email) {
    this.email = email;
  }
}

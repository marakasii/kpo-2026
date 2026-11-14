package studying.gateway.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "model_prices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelPrice {
  @Id private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  private Model model;

  @Column(name = "input_price_per_1000")
  private long inputPricePer1000;

  @Column(name = "output_price_per_1000")
  private long outputPricePer1000;

  private Instant validFrom;
  private Instant validTo;

  public ModelPrice(Model model, long input, long output, Instant from) {
    this.model = model;
    inputPricePer1000 = input;
    outputPricePer1000 = output;
    validFrom = from;
  }
}

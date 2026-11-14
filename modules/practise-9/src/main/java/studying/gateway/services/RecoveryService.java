package studying.gateway.services;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import studying.gateway.repository.GatewayRepository;

@Component
@RequiredArgsConstructor
public class RecoveryService {
  private final GatewayRepository db;
  private final BillingService billing;

  @Value("${app.reservation-timeout-seconds:600}")
  private long timeout;

  @Scheduled(fixedDelayString = "${app.recovery-delay-ms:30000}")
  public void recover() {
    for (var id : db.expired(Instant.now().minusSeconds(timeout)))
      billing.fail(id, "RESERVATION_EXPIRED", true);
  }
}

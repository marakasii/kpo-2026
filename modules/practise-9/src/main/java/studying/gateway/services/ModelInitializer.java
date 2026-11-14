package studying.gateway.services;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import studying.gateway.model.*;
import studying.gateway.repository.GatewayRepository;

@Component
@RequiredArgsConstructor
public class ModelInitializer implements ApplicationRunner {
  private final GatewayRepository db;

  @Value("${app.ollama-model:qwen3:4b}")
  private String name;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!db.modelExists(name)) {
      Model model = new Model(name);
      db.save(model);
      db.save(new ModelPrice(model, 100, 200, Instant.now()));
    }
  }
}

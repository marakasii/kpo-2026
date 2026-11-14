package studying.gateway.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studying.gateway.dto.Api;
import studying.gateway.exception.GatewayException;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final BillingService billing;
  private final OllamaGateway ollama;

  public Api.ChatView chat(String authorization, String idem, Api.Chat command) {
    var reservation = billing.reserve(authorization, idem, command);
    if (!reservation.fresh()) return billing.state(reservation.id());
    try {
      if (!billing.processing(reservation.id())) return billing.state(reservation.id());
      var result = ollama.generate(command);
      var view =
          billing.complete(reservation.id(), result.content(), result.input(), result.output());
      if (!view.status().equals("COMPLETED"))
        throw new GatewayException(
            503, view.errorCode(), "Запрос завершился без списания", reservation.id());
      return view;
    } catch (Exception e) {
      billing.fail(
          reservation.id(),
          e instanceof GatewayException gateway ? gateway.getCode() : "OLLAMA_UNAVAILABLE",
          false);
      if (e instanceof GatewayException gateway) throw gateway;
      throw new GatewayException(
          503, "OLLAMA_UNAVAILABLE", "Ollama временно недоступна", reservation.id());
    }
  }
}

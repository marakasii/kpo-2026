package studying.gateway.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import studying.gateway.dto.Api;
import studying.gateway.exception.GatewayException;

class ChatServiceTest {
  @Test
  void releasesReservationIfProcessingFailsBeforeProviderCall() {
    BillingService billing = mock(BillingService.class);
    OllamaGateway provider = mock(OllamaGateway.class);
    var command = new Api.Chat("model", "Hello", 10);
    UUID id = UUID.randomUUID();
    when(billing.reserve("auth", "idem", command))
        .thenReturn(new BillingService.Reservation(id, true));
    when(billing.processing(id)).thenThrow(new IllegalStateException("failure before call"));
    assertThrows(
        GatewayException.class,
        () -> new ChatService(billing, provider).chat("auth", "idem", command));
    verify(billing).fail(id, "OLLAMA_UNAVAILABLE", false);
    verifyNoInteractions(provider);
  }

  @Test
  void releasesReservationIfFinalTransactionRollsBack() {
    BillingService billing = mock(BillingService.class);
    OllamaGateway provider = mock(OllamaGateway.class);
    var command = new Api.Chat("model", "Hello", 10);
    UUID id = UUID.randomUUID();
    when(billing.reserve("auth", "idem", command))
        .thenReturn(new BillingService.Reservation(id, true));
    when(billing.processing(id)).thenReturn(true);
    when(provider.generate(command)).thenReturn(new OllamaGateway.Result("Answer", 10, 5));
    when(billing.complete(id, "Answer", 10, 5))
        .thenThrow(new IllegalStateException("commit failure"));
    assertThrows(
        GatewayException.class,
        () -> new ChatService(billing, provider).chat("auth", "idem", command));
    verify(billing).fail(id, "OLLAMA_UNAVAILABLE", false);
  }
}

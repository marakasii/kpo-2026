package studying.gateway.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import studying.gateway.dto.Api;

class OllamaGatewayTest {
  @Test
  void rejectsMissingAndEmptyUsage() {
    ChatModel model = mock(ChatModel.class);
    ChatResponse response = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
    when(model.call(any(Prompt.class))).thenReturn(response);
    var gateway = new OllamaGateway(model);
    var command = new Api.Chat("qwen2.5:0.5b", "Hello", 10);
    when(response.getMetadata().getUsage().getPromptTokens()).thenReturn(0);
    assertThrows(IllegalStateException.class, () -> gateway.generate(command));
    when(response.getMetadata().getUsage()).thenReturn(null);
    assertThrows(IllegalStateException.class, () -> gateway.generate(command));
  }
}

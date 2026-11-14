package studying.gateway.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.stereotype.Component;
import studying.gateway.dto.Api;

@Component
@RequiredArgsConstructor
public class OllamaGateway {
  private final ChatModel model;

  public record Result(String content, int input, int output) {}

  public Result generate(Api.Chat command) {
    var response =
        model.call(
            new Prompt(
                command.prompt(),
                OllamaChatOptions.builder()
                    .model(command.model())
                    .numPredict(command.maxTokens())
                    .disableThinking()
                    .build()));
    if (response == null || response.getResult() == null || response.getMetadata() == null)
      throw new IllegalStateException("Missing response metadata");
    var usage = response.getMetadata().getUsage();
    if (usage == null
        || usage.getPromptTokens() == null
        || usage.getCompletionTokens() == null
        || usage.getPromptTokens() <= 0
        || usage.getCompletionTokens() < 0)
      throw new IllegalStateException("Missing or invalid usage");
    return new Result(
        response.getResult().getOutput().getText(),
        usage.getPromptTokens(),
        usage.getCompletionTokens());
  }
}

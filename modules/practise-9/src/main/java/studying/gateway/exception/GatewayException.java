package studying.gateway.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {
  private final int status;
  private final String code;
  private final UUID requestId;

  public GatewayException(int status, String code, String message) {
    this(status, code, message, null);
  }

  public GatewayException(int status, String code, String message, UUID requestId) {
    super(message);
    this.status = status;
    this.code = code;
    this.requestId = requestId;
  }
}

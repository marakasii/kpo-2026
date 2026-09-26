package studying.withsolid.exception;

import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {
    private final String code;

    public ReportException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ReportException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}

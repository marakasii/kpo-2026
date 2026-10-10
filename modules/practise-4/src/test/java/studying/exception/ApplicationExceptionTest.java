package studying.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for application errors. */
@DisplayName("Unit-тесты прикладной ошибки")
class ApplicationExceptionTest {
    @Test
    @DisplayName("Ошибка сохраняет код, сообщение и исходную причину")
    void retainsCodeMessageAndCause() {
        final IOException cause = new IOException("Диск недоступен");

        final ApplicationException error = new ApplicationException(
                ApplicationErrorCode.FILE_WRITE_ERROR,
                "Не удалось сохранить отчёт", cause);

        assertEquals(ApplicationErrorCode.FILE_WRITE_ERROR, error.getCode());
        assertEquals("Не удалось сохранить отчёт", error.getMessage());
        assertSame(cause, error.getCause());
    }
}

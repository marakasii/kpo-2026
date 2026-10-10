package studying.builder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Отдельно проверяем обязательные специфичные поля готовых уведомлений. */
@DisplayName("Специализированные Builder-ы")
class SpecializedNotificationBuilderTest {
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.parse("2026-10-01T10:15:00");

    @Test
    @DisplayName("Email Builder требует тему письма")
    void emailBuilderОтклоняетОтсутствующуюТему() {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailNotificationBuilder()
                        .email("student@example.org")
                        .text("Заказ готов")
                        .createdAt(CREATED_AT)
                        .build());
    }

    @Test
    @DisplayName("Push Builder требует идентификатор устройства")
    void pushBuilderОтклоняетОтсутствующееУстройство() {
        assertThrows(IllegalArgumentException.class,
                () -> new PushNotificationBuilder()
                        .text("Заказ готов")
                        .createdAt(CREATED_AT)
                        .build());
    }

    @Test
    @DisplayName("In-app Builder требует идентификатор пользователя")
    void inAppBuilderОтклоняетОтсутствующегоПользователя() {
        assertThrows(IllegalArgumentException.class,
                () -> new InAppNotificationBuilder()
                        .text("Заказ готов")
                        .createdAt(CREATED_AT)
                        .build());
    }
}

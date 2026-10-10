package studying.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import studying.Main;
import studying.builder.NotificationBuilder;
import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/** Настоящие фабрики и Builder-ы собираются настоящим Spring DI-контекстом. */
@SpringBootTest(classes = Main.class)
@DisplayName("Интеграция Builder, Factory Method и DI")
class NotificationServiceIntegrationTest {
    private static final int HIGH_PRIORITY = 3;

    @Autowired
    private NotificationService service;

    @ParameterizedTest(name = "Полный сценарий: {0}, получатель {1}")
    @CsvSource({
            "EMAIL, student@example.org",
            "PUSH, device-42",
            "IN_APP, user-17"
    })
    @DisplayName("DI выбирает фабрику и создаёт уведомление "
            + "через общий контракт")
    void создаётУведомлениеЧерезDI(final NotificationChannel channel,
                                  final String recipient) {
        LocalDateTime createdAt = LocalDateTime.parse("2026-10-01T10:15:00");
        NotificationDraft draft = new NotificationBuilder()
                .channel(channel)
                .recipient(recipient)
                .text("Ваш заказ передан в доставку")
                .createdAt(createdAt)
                .priority(HIGH_PRIORITY)
                .build();

        Notification notification = service.create(draft);

        // Проверяем общий контракт без приведений к Email/Push/InApp.
        assertAll(
                () -> assertEquals(channel, notification.channel()),
                () -> assertEquals(recipient, notification.recipient()),
                () -> assertEquals(draft.text(), notification.text()),
                () -> assertEquals(createdAt, notification.createdAt()),
                () -> assertEquals(HIGH_PRIORITY, notification.priority()));
    }
}

package studying.factory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import studying.builder.NotificationBuilder;
import studying.model.EmailNotification;
import studying.model.InAppNotification;
import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;
import studying.model.PushNotification;

/** Фабрики тестируются без DI-контейнера, с настоящими Builder-ами. */
@DisplayName("Фабричный метод уведомлений")
class NotificationFactoryTest {
    private static final int HIGH_PRIORITY = 3;
    private static final String SUBJECT = "Уведомление магазина";
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.parse("2026-10-01T10:15:00");

    @Test
    @DisplayName("Email-фабрика создаёт email и добавляет адрес и тему")
    void создаётEmailСАдресомИТемой() {
        NotificationDraft draft = draft(NotificationChannel.EMAIL,
                "student@example.org");
        NotificationFactory factory = new EmailNotificationFactory(SUBJECT);

        Notification result = factory.create(draft);

        // Проверка конкретного продукта нужна тесту фабрики.
        EmailNotification email = assertInstanceOf(EmailNotification.class,
                result);
        assertCommonFields(draft, result);
        assertEquals("student@example.org", email.email());
        assertEquals(SUBJECT, email.subject());
    }

    @Test
    @DisplayName("Push-фабрика создаёт push с идентификатором устройства")
    void создаётPushСИдентификаторомУстройства() {
        NotificationDraft draft = draft(NotificationChannel.PUSH, "device-42");
        NotificationFactory factory = new PushNotificationFactory();

        Notification result = factory.create(draft);

        PushNotification push = assertInstanceOf(PushNotification.class,
                result);
        assertCommonFields(draft, result);
        assertEquals("device-42", push.deviceId());
    }

    @Test
    @DisplayName("In-app фабрика создаёт уведомление "
            + "с идентификатором пользователя")
    void создаётInAppСИдентификаторомПользователя() {
        NotificationDraft draft = draft(NotificationChannel.IN_APP, "user-17");
        NotificationFactory factory = new InAppNotificationFactory();

        Notification result = factory.create(draft);

        InAppNotification inApp = assertInstanceOf(InAppNotification.class,
                result);
        assertCommonFields(draft, result);
        assertEquals("user-17", inApp.userId());
    }

    @ParameterizedTest(name = "Недопустимая тема: «{0}»")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @DisplayName("Email-фабрика отклоняет пустую тему при настройке")
    void отклоняетПустуюТему(final String subject) {
        assertThrows(IllegalArgumentException.class,
                () -> new EmailNotificationFactory(subject));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("factories")
    @DisplayName("Каждая фабрика требует черновик")
    void отклоняетОтсутствующийЧерновик(final NotificationFactory factory) {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(null));
    }

    @ParameterizedTest(name = "{0}, неподходящий канал {1}")
    @MethodSource("wrongChannels")
    @DisplayName("Фабрика отклоняет черновик другого канала")
    void отклоняетНеподходящийКанал(final NotificationFactory factory,
                                  final NotificationChannel wrongChannel) {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(draft(wrongChannel, "recipient-1")));
    }

    private static Stream<Arguments> factories() {
        return Stream.of(
                Arguments.of(Named.of("Email-фабрика",
                        new EmailNotificationFactory(SUBJECT))),
                Arguments.of(Named.of("Push-фабрика",
                        new PushNotificationFactory())),
                Arguments.of(Named.of("In-app фабрика",
                        new InAppNotificationFactory())));
    }

    private static Stream<Arguments> wrongChannels() {
        return Stream.of(
                Arguments.of(Named.of("Email-фабрика",
                                new EmailNotificationFactory(SUBJECT)),
                        NotificationChannel.PUSH),
                Arguments.of(Named.of("Push-фабрика",
                                new PushNotificationFactory()),
                        NotificationChannel.IN_APP),
                Arguments.of(Named.of("In-app фабрика",
                                new InAppNotificationFactory()),
                        NotificationChannel.EMAIL));
    }

    private static NotificationDraft draft(final NotificationChannel channel,
                                           final String recipient) {
        return new NotificationBuilder()
                .channel(channel)
                .recipient(recipient)
                .text("Ваш заказ передан в доставку")
                .createdAt(CREATED_AT)
                .priority(HIGH_PRIORITY)
                .build();
    }

    private static void assertCommonFields(final NotificationDraft draft,
                                           final Notification notification) {
        assertAll(
                () -> assertEquals(draft.channel(), notification.channel()),
                () -> assertEquals(draft.recipient(), notification.recipient()),
                () -> assertEquals(draft.text(), notification.text()),
                () -> assertEquals(draft.createdAt(), notification.createdAt()),
                () -> assertEquals(draft.priority(), notification.priority()));
    }
}

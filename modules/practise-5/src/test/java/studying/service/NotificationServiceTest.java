package studying.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import studying.builder.NotificationBuilder;
import studying.factory.NotificationFactory;
import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/** Моки показывают, что сервис использует переданные через DI фабрики. */
@DisplayName("Сервис создания уведомлений с DI")
class NotificationServiceTest {
    @ParameterizedTest(name = "Выбор фабрики для {0}")
    @EnumSource(NotificationChannel.class)
    @DisplayName("Сервис вызывает только фабрику нужного канала")
    void выбираетПереданнуюФабрикуПоКаналу(final NotificationChannel channel) {
        Map<NotificationChannel, NotificationFactory> factories = Map.of(
                NotificationChannel.EMAIL, mock(NotificationFactory.class),
                NotificationChannel.PUSH, mock(NotificationFactory.class),
                NotificationChannel.IN_APP, mock(NotificationFactory.class));
        NotificationService service = new NotificationService(factories);
        NotificationDraft draft = draft(channel);
        Notification expected = mock(Notification.class);
        NotificationFactory selected = factories.get(channel);
        when(selected.create(draft)).thenReturn(expected);

        Notification result = service.create(draft);

        assertSame(expected, result);
        verify(selected).create(draft);
        // Остальные фабрики не вызывались, а выбранная вызвана ровно один раз.
        verifyNoMoreInteractions(factories.values().toArray());
    }

    @Test
    @DisplayName("Сервис сообщает об отсутствии регистрации фабрики")
    void отклоняетНезарегистрированныйКанал() {
        NotificationService service = new NotificationService(Map.of());

        assertThrows(IllegalArgumentException.class,
                () -> service.create(draft(NotificationChannel.PUSH)));
    }

    @Test
    @DisplayName("Сервис требует черновик")
    void отклоняетОтсутствующийЧерновик() {
        NotificationService service = new NotificationService(Map.of());

        assertThrows(IllegalArgumentException.class,
                () -> service.create(null));
    }

    private static NotificationDraft draft(final NotificationChannel channel) {
        return new NotificationBuilder()
                .channel(channel)
                .recipient("recipient-1")
                .text("Заказ готов")
                .createdAt(LocalDateTime.parse("2026-10-01T10:15:00"))
                .priority(1)
                .build();
    }
}

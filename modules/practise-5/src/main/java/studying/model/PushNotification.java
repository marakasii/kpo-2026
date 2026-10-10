package studying.model;

import java.time.LocalDateTime;

/**
 * Конкретный продукт push-фабрики; получатель — идентификатор устройства.
 *
 * @param deviceId идентификатор устройства
 * @param text текст уведомления
 * @param createdAt явно заданное время создания
 * @param priority приоритет от 1 до 3
 */
public record PushNotification(String deviceId, String text,
                               LocalDateTime createdAt, int priority)
        implements Notification {
    /** Проверяем данные перед созданием push-уведомления. */
    public PushNotification {
        NotificationValidation.validate(NotificationChannel.PUSH, deviceId,
                text, createdAt, priority);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public String recipient() {
        return deviceId;
    }
}

package studying.model;

import java.time.LocalDateTime;

/**
 * Конкретный продукт in-app фабрики; получатель — пользователь приложения.
 *
 * @param userId идентификатор пользователя
 * @param text текст уведомления
 * @param createdAt явно заданное время создания
 * @param priority приоритет от 1 до 3
 */
public record InAppNotification(String userId, String text,
                                LocalDateTime createdAt, int priority)
        implements Notification {
    /** Проверяем данные перед созданием in-app уведомления. */
    public InAppNotification {
        NotificationValidation.validate(NotificationChannel.IN_APP, userId,
                text, createdAt, priority);
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public String recipient() {
        return userId;
    }
}

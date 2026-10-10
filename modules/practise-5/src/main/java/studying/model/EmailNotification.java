package studying.model;

import java.time.LocalDateTime;

/**
 * Конкретный продукт email-фабрики: содержит также тему письма.
 *
 * @param email адрес получателя
 * @param subject тема письма
 * @param text текст уведомления
 * @param createdAt явно заданное время создания
 * @param priority приоритет от 1 до 3
 */
public record EmailNotification(String email, String subject, String text,
                                LocalDateTime createdAt, int priority)
        implements Notification {
    /** Проверяем общие поля и обязательную тему письма. */
    public EmailNotification {
        NotificationValidation.validate(NotificationChannel.EMAIL, email,
                text, createdAt, priority);
        NotificationValidation.requireText(subject, "Тема письма");
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public String recipient() {
        // Общий контракт называет получателя recipient, здесь это email.
        return email;
    }
}

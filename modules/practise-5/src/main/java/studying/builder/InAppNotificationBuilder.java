package studying.builder;

import java.time.LocalDateTime;
import studying.model.InAppNotification;
import studying.model.NotificationValidation;

/** Специализированный Builder для уведомления внутри приложения. */
public final class InAppNotificationBuilder {
    private String userId;
    private String text;
    private LocalDateTime createdAt;
    private int priority = NotificationValidation.DEFAULT_PRIORITY;

    /**
     * Для in-app поле получателя означает идентификатор пользователя.
     *
     * @param value идентификатор пользователя
     * @return этот Builder для продолжения цепочки вызовов
     */
    public InAppNotificationBuilder userId(final String value) {
        userId = value;
        return this;
    }

    /**
     * Устанавливаем текст.
     *
     * @param value текст уведомления
     * @return этот Builder для продолжения цепочки вызовов
     */
    public InAppNotificationBuilder text(final String value) {
        text = value;
        return this;
    }

    /**
     * Переносим явно заданное время.
     *
     * @param value время создания
     * @return этот Builder для продолжения цепочки вызовов
     */
    public InAppNotificationBuilder createdAt(final LocalDateTime value) {
        createdAt = value;
        return this;
    }

    /**
     * Устанавливаем приоритет.
     *
     * @param value приоритет от 1 до 3
     * @return этот Builder для продолжения цепочки вызовов
     */
    public InAppNotificationBuilder priority(final int value) {
        priority = value;
        return this;
    }

    /**
     * Собираем и проверяем готовый продукт in-app фабрики.
     *
     * @return новое неизменяемое уведомление
     */
    public InAppNotification build() {
        return new InAppNotification(userId, text, createdAt, priority);
    }
}

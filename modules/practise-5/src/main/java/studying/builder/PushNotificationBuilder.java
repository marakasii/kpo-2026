package studying.builder;

import java.time.LocalDateTime;
import studying.model.NotificationValidation;
import studying.model.PushNotification;

/** Специализированный Builder для push; его выбирает фабрика, а не клиент. */
public final class PushNotificationBuilder {
    private String deviceId;
    private String text;
    private LocalDateTime createdAt;
    private int priority = NotificationValidation.DEFAULT_PRIORITY;

    /**
     * Для push поле получателя означает идентификатор устройства.
     *
     * @param value идентификатор устройства
     * @return этот Builder для продолжения цепочки вызовов
     */
    public PushNotificationBuilder deviceId(final String value) {
        deviceId = value;
        return this;
    }

    /**
     * Устанавливаем текст.
     *
     * @param value текст уведомления
     * @return этот Builder для продолжения цепочки вызовов
     */
    public PushNotificationBuilder text(final String value) {
        text = value;
        return this;
    }

    /**
     * Переносим явно заданное время.
     *
     * @param value время создания
     * @return этот Builder для продолжения цепочки вызовов
     */
    public PushNotificationBuilder createdAt(final LocalDateTime value) {
        createdAt = value;
        return this;
    }

    /**
     * Устанавливаем приоритет.
     *
     * @param value приоритет от 1 до 3
     * @return этот Builder для продолжения цепочки вызовов
     */
    public PushNotificationBuilder priority(final int value) {
        priority = value;
        return this;
    }

    /**
     * Собираем и проверяем готовый продукт push-фабрики.
     *
     * @return новое неизменяемое уведомление
     */
    public PushNotification build() {
        return new PushNotification(deviceId, text, createdAt, priority);
    }
}

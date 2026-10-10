package studying.builder;

import java.time.LocalDateTime;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;
import studying.model.NotificationValidation;

/**
 * Builder собирает данные по шагам. Пока методы не вызвали build(),
 * изменяется только Builder, а готового черновика ещё нет.
 */
public final class NotificationBuilder {
    private NotificationChannel channel;
    private String recipient;
    private String text;
    private LocalDateTime createdAt;
    private int priority = NotificationValidation.DEFAULT_PRIORITY;

    /**
     * Запоминаем канал и возвращаем this для цепочки вызовов.
     *
     * @param value канал доставки
     * @return этот Builder для продолжения цепочки вызовов
     */
    public NotificationBuilder channel(final NotificationChannel value) {
        channel = value;
        return this;
    }

    /**
     * Запоминаем адрес, идентификатор устройства или пользователя.
     *
     * @param value получатель
     * @return этот Builder для продолжения цепочки вызовов
     */
    public NotificationBuilder recipient(final String value) {
        recipient = value;
        return this;
    }

    /**
     * Запоминаем текст будущего уведомления.
     *
     * @param value текст уведомления
     * @return этот Builder для продолжения цепочки вызовов
     */
    public NotificationBuilder text(final String value) {
        text = value;
        return this;
    }

    /**
     * Время задаёт вызывающий код: системные часы здесь не используются.
     *
     * @param value время создания
     * @return этот Builder для продолжения цепочки вызовов
     */
    public NotificationBuilder createdAt(final LocalDateTime value) {
        createdAt = value;
        return this;
    }

    /**
     * Задаём приоритет от 1 до 3; по умолчанию используется 2.
     *
     * @param value приоритет от 1 до 3
     * @return этот Builder для продолжения цепочки вызовов
     */
    public NotificationBuilder priority(final int value) {
        priority = value;
        return this;
    }

    /**
     * Финальный шаг: конструктор проверяет данные и создаёт новый record.
     * Последующие изменения Builder не затронут уже созданный черновик.
     *
     * @return новый неизменяемый черновик
     */
    public NotificationDraft build() {
        return new NotificationDraft(channel, recipient, text, createdAt,
                priority);
    }
}

package studying.builder;

import java.time.LocalDateTime;
import studying.model.EmailNotification;
import studying.model.NotificationValidation;

/** Специализированный Builder: email-фабрика использует его внутри create(). */
public final class EmailNotificationBuilder {
    private String email;
    private String subject;
    private String text;
    private LocalDateTime createdAt;
    private int priority = NotificationValidation.DEFAULT_PRIORITY;

    /**
     * Поле получателя из черновика становится адресом письма.
     *
     * @param value адрес получателя
     * @return этот Builder для продолжения цепочки вызовов
     */
    public EmailNotificationBuilder email(final String value) {
        email = value;
        return this;
    }

    /**
     * Только у email есть дополнительное поле subject.
     *
     * @param value тема письма
     * @return этот Builder для продолжения цепочки вызовов
     */
    public EmailNotificationBuilder subject(final String value) {
        subject = value;
        return this;
    }

    /**
     * Устанавливаем текст.
     *
     * @param value текст уведомления
     * @return этот Builder для продолжения цепочки вызовов
     */
    public EmailNotificationBuilder text(final String value) {
        text = value;
        return this;
    }

    /**
     * Переносим явно заданное время.
     *
     * @param value время создания
     * @return этот Builder для продолжения цепочки вызовов
     */
    public EmailNotificationBuilder createdAt(final LocalDateTime value) {
        createdAt = value;
        return this;
    }

    /**
     * Устанавливаем приоритет.
     *
     * @param value приоритет от 1 до 3
     * @return этот Builder для продолжения цепочки вызовов
     */
    public EmailNotificationBuilder priority(final int value) {
        priority = value;
        return this;
    }

    /**
     * Конструктор проверяет поля; результат — неизменяемое уведомление.
     *
     * @return новое неизменяемое уведомление
     */
    public EmailNotification build() {
        return new EmailNotification(email, subject, text, createdAt, priority);
    }
}

package studying.model;

import java.time.LocalDateTime;

/**
 * Черновик хранит общие данные, ещё не привязанные к конкретному виду объекта.
 * record автоматически создаёт конструктор и методы чтения полей.
 * Все поля final: после build() черновик нельзя изменить.
 *
 * @param channel канал доставки
 * @param recipient получатель
 * @param text текст уведомления
 * @param createdAt явно заданное время создания
 * @param priority приоритет от 1 до 3
 */
public record NotificationDraft(NotificationChannel channel, String recipient,
                                String text, LocalDateTime createdAt,
                                int priority) {
    /** Проверяем данные перед созданием неизменяемого черновика. */
    public NotificationDraft {
        // Проверка в конструкторе защищает и build(), и прямой вызов new.
        NotificationValidation.validate(channel, recipient, text, createdAt,
                priority);
    }
}

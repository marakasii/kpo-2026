package studying.model;

import java.time.LocalDateTime;

/** Общие проверки: у черновика и готовых уведомлений одинаковые требования. */
public final class NotificationValidation {
    // В этой практике 1 — низкий, 2 — обычный, 3 — высокий приоритет.
    public static final int MIN_PRIORITY = 1;
    public static final int DEFAULT_PRIORITY = 2;
    public static final int MAX_PRIORITY = 3;

    private NotificationValidation() {
    }

    /**
     * Проверяем все общие поля перед созданием неизменяемого объекта.
     *
     * @param channel канал доставки
     * @param recipient получатель
     * @param text текст уведомления
     * @param createdAt явно заданное время создания
     * @param priority приоритет от 1 до 3
     */
    public static void validate(final NotificationChannel channel,
                                final String recipient, final String text,
                                final LocalDateTime createdAt,
                                final int priority) {
        if (channel == null) {
            throw new IllegalArgumentException("Канал обязателен");
        }
        requireText(recipient, "Получатель");
        requireText(text, "Текст");
        if (createdAt == null) {
            throw new IllegalArgumentException("Время создания обязательно");
        }
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("Приоритет должен быть от "
                    + MIN_PRIORITY + " до " + MAX_PRIORITY);
        }
    }

    /**
     * Строка должна содержать хотя бы один непробельный символ.
     *
     * @param value проверяемая строка
     * @param fieldName название поля для сообщения об ошибке
     */
    public static void requireText(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " обязателен");
        }
    }
}

package studying.model;

import java.time.LocalDateTime;

/**
 * Общий контракт продукта фабрики. Клиент читает эти поля, не проверяя,
 * является ли результат email, push или in-app уведомлением.
 */
public interface Notification {
    /**
     * Возвращаем канал доставки.
     *
     * @return канал уведомления
     */
    NotificationChannel channel();

    /**
     * Получаем адрес email, устройство push или пользователя in-app.
     *
     * @return получатель уведомления
     */
    String recipient();

    /**
     * Возвращаем содержимое уведомления.
     *
     * @return текст уведомления
     */
    String text();

    /**
     * Возвращаем время, явно переданное при сборке.
     *
     * @return время создания
     */
    LocalDateTime createdAt();

    /**
     * Возвращаем важность уведомления.
     *
     * @return приоритет от 1 до 3
     */
    int priority();
}

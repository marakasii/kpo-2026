package studying.factory;

import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/**
 * Абстрактный создатель в Factory Method. Метод create() — фабричный метод:
 * каждый наследник сам решает, какой конкретный Notification создать.
 * Клиенту достаточно знать эту фабрику и общий контракт Notification.
 */
public abstract class NotificationFactory {
    /**
     * Фабричный метод: наследник выбирает Builder и создаёт продукт.
     *
     * @param draft проверенный черновик
     * @return готовое уведомление через общий контракт
     */
    public abstract Notification create(NotificationDraft draft);

    /**
     * Защита от передачи черновика не той фабрике.
     *
     * @param draft черновик для проверки
     * @param expected канал, поддерживаемый конкретной фабрикой
     */
    protected final void requireChannel(final NotificationDraft draft,
                                        final NotificationChannel expected) {
        if (draft == null) {
            throw new IllegalArgumentException("Черновик обязателен");
        }
        if (draft.channel() != expected) {
            throw new IllegalArgumentException("Фабрика ожидает канал "
                    + expected + ", получен " + draft.channel());
        }
    }
}

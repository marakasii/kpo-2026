package studying.service;

import java.util.Map;
import studying.factory.NotificationFactory;
import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/** Клиент фабрик: получает зависимости через конструктор (Constructor DI). */
public final class NotificationService {
    private final Map<NotificationChannel, NotificationFactory> factories;

    /**
     * Получаем готовое сопоставление каналов с фабриками через DI.
     *
     * @param factoryRegistry фабрики, зарегистрированные конфигурацией
     */
    public NotificationService(
            final Map<NotificationChannel, NotificationFactory>
                    factoryRegistry) {
        // Копия не даёт внешнему коду изменить регистрации после сборки DI.
        this.factories = Map.copyOf(factoryRegistry);
    }

    /**
     * Создаём уведомление, работая только с общими контрактами.
     *
     * @param draft черновик с каналом и общими данными
     * @return уведомление, созданное выбранной фабрикой
     */
    public Notification create(final NotificationDraft draft) {
        if (draft == null) {
            throw new IllegalArgumentException("Черновик обязателен");
        }
        // Сопоставление задано снаружи: здесь не нужен switch по каналам.
        NotificationFactory factory = factories.get(draft.channel());
        if (factory == null) {
            throw new IllegalArgumentException("Не зарегистрирована фабрика "
                    + "для канала " + draft.channel());
        }
        return factory.create(draft);
    }
}

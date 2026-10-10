package studying.model;

/** Канал нужен конфигурации DI для выбора подходящей фабрики. */
public enum NotificationChannel {
    EMAIL,
    PUSH,
    IN_APP
}

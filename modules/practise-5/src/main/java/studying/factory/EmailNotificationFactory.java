package studying.factory;

import studying.builder.EmailNotificationBuilder;
import studying.model.EmailNotification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;
import studying.model.NotificationValidation;

/** Конкретный создатель: переопределяет фабричный метод для email-продукта. */
public final class EmailNotificationFactory extends NotificationFactory {
    private final String subject;

    /**
     * DI передаёт настройку темы, общую для всех создаваемых писем.
     *
     * @param defaultSubject тема писем этой фабрики
     */
    public EmailNotificationFactory(final String defaultSubject) {
        NotificationValidation.requireText(defaultSubject, "Тема письма");
        this.subject = defaultSubject;
    }

    @Override
    public EmailNotification create(final NotificationDraft draft) {
        requireChannel(draft, NotificationChannel.EMAIL);
        // Фабрика знает нужный Builder и добавляет специфичное поле subject.
        // Клиенту не нужно знать ни Builder, ни класс EmailNotification.
        return new EmailNotificationBuilder()
                .email(draft.recipient())
                .subject(subject)
                .text(draft.text())
                .createdAt(draft.createdAt())
                .priority(draft.priority())
                .build();
    }
}

package studying.factory;

import studying.builder.InAppNotificationBuilder;
import studying.model.InAppNotification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/** Конкретный создатель уведомлений для пользователя приложения. */
public final class InAppNotificationFactory extends NotificationFactory {
    @Override
    public InAppNotification create(final NotificationDraft draft) {
        requireChannel(draft, NotificationChannel.IN_APP);
        // recipient из общего черновика становится конкретным userId.
        return new InAppNotificationBuilder()
                .userId(draft.recipient())
                .text(draft.text())
                .createdAt(draft.createdAt())
                .priority(draft.priority())
                .build();
    }
}

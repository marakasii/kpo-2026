package studying.factory;

import studying.builder.PushNotificationBuilder;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;
import studying.model.PushNotification;

/** Конкретный создатель уведомлений для устройства. */
public final class PushNotificationFactory extends NotificationFactory {
    @Override
    public PushNotification create(final NotificationDraft draft) {
        requireChannel(draft, NotificationChannel.PUSH);
        // recipient из общего черновика становится конкретным deviceId.
        return new PushNotificationBuilder()
                .deviceId(draft.recipient())
                .text(draft.text())
                .createdAt(draft.createdAt())
                .priority(draft.priority())
                .build();
    }
}

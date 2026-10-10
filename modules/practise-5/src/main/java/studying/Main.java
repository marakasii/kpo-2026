package studying;

import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import studying.builder.NotificationBuilder;
import studying.model.Notification;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;
import studying.service.NotificationService;

/** Короткий пример: Builder черновика → сервис с DI → фабрика → уведомление. */
@SpringBootApplication(proxyBeanMethods = false)
public final class Main {
    private static final LocalDateTime DEMO_CREATED_AT =
            LocalDateTime.parse("2026-10-01T10:15:00");
    private static final int DEMO_PRIORITY = 3;

    private Main() {
    }

    /**
     * Запускаем Spring и создаём демонстрационное уведомление.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        try (var context = SpringApplication.run(Main.class, args)) {
            NotificationService service = context.getBean(
                    NotificationService.class);

            // Клиент использует только общий Builder, а не EmailBuilder.
            NotificationDraft draft = new NotificationBuilder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient("student@example.org")
                    .text("Ваш заказ передан в доставку")
                    .createdAt(DEMO_CREATED_AT)
                    .priority(DEMO_PRIORITY)
                    .build();

            // Тип результата общий: клиент не проверяет конкретный класс.
            Notification notification = service.create(draft);
            System.out.println(notification);
        }
    }
}

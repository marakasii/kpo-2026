package studying.configuration;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import studying.factory.EmailNotificationFactory;
import studying.factory.InAppNotificationFactory;
import studying.factory.PushNotificationFactory;
import studying.model.NotificationChannel;
import studying.service.NotificationService;

/**
 * Точка сборки DI: только здесь мы связываем каналы с конкретными фабриками.
 * Spring вызывает @Bean-методы и передаёт готовые зависимости в их параметры.
 */
@Configuration(proxyBeanMethods = false)
public final class ApplicationConfiguration {
    /**
     * Создаём email-фабрику с темой из настроек Spring.
     *
     * @param subject тема всех создаваемых писем
     * @return email-фабрика
     */
    @Bean
    public EmailNotificationFactory emailNotificationFactory(
            @Value("${notification.email.subject:Уведомление магазина}")
            final String subject) {
        // Тема — настройка приложения; фабрика получает её через конструктор.
        return new EmailNotificationFactory(subject);
    }

    /**
     * Создаём фабрику push-уведомлений.
     *
     * @return push-фабрика
     */
    @Bean
    public PushNotificationFactory pushNotificationFactory() {
        return new PushNotificationFactory();
    }

    /**
     * Создаём фабрику уведомлений внутри приложения.
     *
     * @return in-app фабрика
     */
    @Bean
    public InAppNotificationFactory inAppNotificationFactory() {
        return new InAppNotificationFactory();
    }

    /**
     * Связываем каналы с фабриками и передаём зависимости в сервис.
     *
     * @param emailFactory фабрика писем
     * @param pushFactory фабрика уведомлений для устройств
     * @param inAppFactory фабрика уведомлений для пользователей
     * @return сервис с готовыми зависимостями
     */
    @Bean
    public NotificationService notificationService(
            final EmailNotificationFactory emailFactory,
            final PushNotificationFactory pushFactory,
            final InAppNotificationFactory inAppFactory) {
        // Сервис получает готовые фабрики и не создаёт их внутри себя.
        return new NotificationService(Map.of(
                NotificationChannel.EMAIL, emailFactory,
                NotificationChannel.PUSH, pushFactory,
                NotificationChannel.IN_APP, inAppFactory));
    }
}

package studying.ioc.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import studying.service.ReportSaver;
import studying.service.ReportSender;
import studying.service.impl.ReportSaverImpl;
import studying.service.impl.ReportSenderImpl;

/** Declares report application dependencies for the Spring container. */
@Configuration
public class ApplicationConfiguration {
    /** Creates the report sender bean.
     *
     * @return configured report sender
     */
    @Bean
    public ReportSender reportSender() {
        return new ReportSenderImpl();
    }

    /** Creates the report saver bean.
     *
     * @return configured report saver
     */
    @Bean
    public ReportSaver reportSaver() {
        return new ReportSaverImpl();
    }

    /** Creates the workflow with its dependencies injected by Spring.
     *
     * @param sender report sender bean
     * @param saver report saver bean
     * @return configured report workflow
     */
    @Bean
    public ReportService reportService(
            final ReportSender sender,
            final ReportSaver saver
    ) {
        return new ReportService(sender, saver);
    }
}

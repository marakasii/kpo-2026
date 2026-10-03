package studying.ioc.locator;

import java.time.LocalDateTime;
import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;
import studying.service.impl.ReportSaverImpl;
import studying.service.impl.ReportSenderImpl;

/** Starts the report workflow using manually registered services. */
public final class Main {
    private Main() {
    }

    /** Runs the Service Locator example.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        final ServiceLocator locator = new ServiceLocator();
        locator.register(ReportSaver.class, new ReportSaverImpl());
        locator.register(ReportSender.class, new ReportSenderImpl());

        final ReportService reportService = new ReportService(locator);
        final LocalDateTime now = LocalDateTime.now();
        final Report report = Report.builder()
                .title("Отчёт")
                .date(now.toLocalDate())
                .time(now.toLocalTime())
                .carsSold(100)
                .motorcyclesSold(50)
                .build();
        reportService.process(report, "example@example.com");
    }
}

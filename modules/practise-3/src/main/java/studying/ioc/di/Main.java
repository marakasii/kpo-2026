package studying.ioc.di;

import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import studying.model.Report;

/** Starts the report workflow using Spring Dependency Injection. */
public final class Main {
    private Main() {
    }

    /** Runs the Dependency Injection example.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        try (var context = SpringApplication
                .run(ApplicationConfiguration.class)) {
            final ReportService reportService =
                    context.getBean(ReportService.class);
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
}

package studying.withsolid.service;

import studying.withsolid.exception.ReportException;
import studying.withsolid.model.Report;

public class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    public ReportService(ReportSaver reportSaver, ReportSender reportSender) {
        this.reportSaver = reportSaver;
        this.reportSender = reportSender;
    }

    public void process(Report report, String email) {
        validate(report, email);

        reportSaver.save(report);
        reportSender.send(report, email);
    }

    private void validate(Report report, String email) {
        if (report == null) {
            throw new ReportException(
                    "INVALID_REPORT",
                    "Report must not be null"
            );
        }

        if (email == null || email.isBlank()) {
            throw new ReportException(
                    "INVALID_EMAIL",
                    "Email must not be null or blank"
            );
        }

        if (report.getCarsSold() == null || report.getCarsSold() < 0) {
            throw new ReportException(
                    "INVALID_CARS_SOLD",
                    "Number of sold cars must not be negative"
            );
        }

        if (report.getMotorcyclesSold() == null || report.getMotorcyclesSold() < 0) {
            throw new ReportException(
                    "INVALID_MOTORCYCLES_SOLD",
                    "Number of sold motorcycles must not be negative"
            );
        }
    }
}

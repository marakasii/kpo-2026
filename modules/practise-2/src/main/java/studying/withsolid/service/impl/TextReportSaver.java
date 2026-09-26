package studying.withsolid.service.impl;

import studying.withsolid.exception.ReportException;
import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class TextReportSaver implements ReportSaver {
    private static final Path REPORTS_DIRECTORY = Path.of("reports");
    private static final DateTimeFormatter FILE_NAME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Override
    public void save(Report report) {
        var fileName = report.getDate()
                .atTime(report.getTime())
                .format(FILE_NAME_FORMAT) + ".txt";

        var filePath = REPORTS_DIRECTORY.resolve(fileName);

        try {
            Files.createDirectories(REPORTS_DIRECTORY);
            Files.writeString(filePath, formatReport(report));
        } catch (IOException e) {
            throw new ReportException(
                    "REPORT_SAVE_ERROR",
                    "Failed to save report to file: " + filePath,
                    e
            );
        }
    }

    private String formatReport(Report report) {
        return "Title: " + report.getTitle() + System.lineSeparator()
                + "Date: " + report.getDate() + System.lineSeparator()
                + "Time: " + report.getTime() + System.lineSeparator()
                + "Cars sold: " + report.getCarsSold() + System.lineSeparator()
                + "Motorcycles sold: " + report.getMotorcyclesSold()
                + System.lineSeparator();
    }
}

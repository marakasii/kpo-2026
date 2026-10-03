package studying.ioc.di;

import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;

/** Runs the report workflow with dependencies supplied by its constructor. */
public final class ReportService {
    /** Service that delivers reports. */
    private final ReportSender reportSender;
    /** Service that persists reports. */
    private final ReportSaver reportSaver;

    /** Creates the workflow with its required services.
     *
     * @param sender service that delivers reports
     * @param saver service that persists reports
     */
    public ReportService(final ReportSender sender, final ReportSaver saver) {
        this.reportSender = sender;
        this.reportSaver = saver;
    }

    /** Saves and sends a report.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(final Report report, final String email) {
        reportSaver.save(report);
        reportSender.send(report, email);
    }
}

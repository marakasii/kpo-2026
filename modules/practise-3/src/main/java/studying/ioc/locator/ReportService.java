package studying.ioc.locator;

import studying.model.Report;
import studying.service.ReportSaver;
import studying.service.ReportSender;

/** Runs the report workflow by looking up dependencies when it is called. */
public final class ReportService {
    /** Locator used to resolve report workflow dependencies. */
    private final ServiceLocator serviceLocator;

    /** Creates the workflow with a service locator.
     *
     * @param locator container used to find report services
     */
    public ReportService(final ServiceLocator locator) {
        this.serviceLocator = locator;
    }

    /** Saves and sends a report.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(final Report report, final String email) {
        serviceLocator.get(ReportSaver.class).save(report);
        serviceLocator.get(ReportSender.class).send(report, email);
    }
}

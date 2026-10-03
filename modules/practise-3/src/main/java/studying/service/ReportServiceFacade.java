package studying.service;

import lombok.RequiredArgsConstructor;
import studying.model.Report;

@RequiredArgsConstructor
public final class ReportServiceFacade {
    /** Sends generated reports. */
    private final ReportSender reportSender;
    /** Persists generated reports. */
    private final ReportSaver reportSaver;

    /** Saves and sends the supplied report.
     *
     * @param report report to process
     * @param email recipient email address
     */
    public void process(final Report report, final String email) {
        reportSaver.save(report);

        reportSender.send(report, email);
    }
}

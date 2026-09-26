package studying.withsolid.service.impl;

import studying.withsolid.model.Report;
import studying.withsolid.service.ReportSender;

public class EmailReportSender implements ReportSender {
    @Override
    public void send(Report report, String email) {
        System.out.println(
                "Report \"" + report.getTitle() + "\" sent to " + email
        );
    }
}

package studying.withsolid.service;

import studying.withsolid.model.Report;

public interface ReportSender {
    void send(Report report, String email);
}

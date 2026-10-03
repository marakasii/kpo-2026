package studying.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;
import studying.model.Report;

/** Unit tests for the safe in-memory report sender. */
@DisplayName("Unit-тесты отправителя отчётов")
class ReportSenderImplTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 29);
    private static final int CARS_SOLD = 12;
    private static final Report REPORT = Report.builder()
            .title("Продажи")
            .date(DATE)
            .time(LocalTime.NOON)
            .carsSold(CARS_SOLD)
            .build();

    @Test
    @DisplayName("Отправитель запоминает отправленный отчёт и получателя")
    void remembersDeliveredReportAndRecipient() {
        final ReportSenderImpl sender = new ReportSenderImpl();

        sender.send(REPORT, "student@hse.ru");

        assertEquals(REPORT, sender.getLastDelivery().report());
        assertEquals("student@hse.ru", sender.getLastDelivery().email());
    }

    @Test
    @DisplayName("Отправитель отклоняет отсутствующий email")
    void rejectsMissingEmail() {
        final ReportSenderImpl sender = new ReportSenderImpl();

        final ApplicationException exception = assertThrows(
                ApplicationException.class, () -> sender.send(REPORT, null));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
    }

    @Test
    @DisplayName("Отправитель отклоняет отсутствующий отчёт")
    void rejectsMissingReport() {
        final ReportSenderImpl sender = new ReportSenderImpl();

        final ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> sender.send(null, "student@hse.ru"));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                exception.getCode());
        assertNull(sender.getLastDelivery());
    }
}

package studying.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

/** Unit tests for report data and validation. */
@DisplayName("Unit-тесты модели отчёта")
class ReportTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 29);
    private static final LocalTime TIME = LocalTime.of(10, 15, 30, 123_000_000);
    private static final int CARS_SOLD = 12;
    private static final int MOTORCYCLES_SOLD = 7;
    private static final int MIN_SALES = -3;
    private static final int MAX_SALES = 3;

    @Test
    @DisplayName("Корректный отчёт сохраняет все поля")
    void retainsAllFields() {
        final Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);

        assertEquals("Продажи", report.title());
        assertEquals(DATE, report.date());
        assertEquals(TIME, report.time());
        assertEquals(CARS_SOLD, report.carsSold());
        assertEquals(MOTORCYCLES_SOLD, report.motorcyclesSold());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " \t\n "})
    @DisplayName("Пустой заголовок отклоняется")
    void rejectsMissingTitle(final String title) {
        final ApplicationException error = assertThrows(
                ApplicationException.class,
                () -> new Report(title, DATE, TIME, 0, 0));

        assertEquals(ApplicationErrorCode.VALIDATION_ERROR, error.getCode());
    }

    @ParameterizedTest
    @MethodSource("saleCounts")
    @DisplayName("Количество автомобилей проверяется на границе нуля")
    void validatesCarsSold(final int count) {
        assertSaleCount(count, true);
    }

    @ParameterizedTest
    @MethodSource("saleCounts")
    @DisplayName("Количество мотоциклов проверяется на границе нуля")
    void validatesMotorcyclesSold(final int count) {
        assertSaleCount(count, false);
    }

    @Test
    @DisplayName("Текст отчёта содержит фиксированные дату, время и количества")
    void formatsReportText() {
        final Report report = new Report("Продажи", DATE, TIME,
                CARS_SOLD, MOTORCYCLES_SOLD);
        final String separator = System.lineSeparator();

        assertEquals(String.join(separator,
                "Продажи",
                "Дата: 2026-09-29",
                "Время: 10:15:30",
                "--------------------------------",
                "Продано автомобилей: 12 шт.",
                "Продано мотоциклов: 7 шт.",
                "--------------------------------") + separator,
                report.toString());
    }

    private static Stream<Integer> saleCounts() {
        return IntStream.rangeClosed(MIN_SALES, MAX_SALES).boxed();
    }

    private static void assertSaleCount(final int count, final boolean cars) {
        if (count < 0) {
            final ApplicationException error = assertThrows(
                    ApplicationException.class,
                    () -> createReport(count, cars));
            assertEquals(ApplicationErrorCode.VALIDATION_ERROR,
                    error.getCode());
        } else {
            final Report report = createReport(count, cars);
            assertEquals(count, cars ? report.carsSold()
                    : report.motorcyclesSold());
        }
    }

    private static Report createReport(final int count, final boolean cars) {
        return new Report("Продажи", DATE, TIME,
                cars ? count : 0, cars ? 0 : count);
    }
}

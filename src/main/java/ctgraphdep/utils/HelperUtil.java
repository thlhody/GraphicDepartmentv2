package ctgraphdep.utils;

import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.services.AdminTimeService;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelperUtil {

    public static void setupYearComboBox(ComboBox<Integer> yearComboBox) {
        int currentYear = Year.now().getValue();
        IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .boxed()
                .sorted((a, b) -> b.compareTo(a))
                .forEach(yearComboBox.getItems()::add);
        yearComboBox.setValue(currentYear);
    }

    public static void setupMonthComboBox(ComboBox<Month> monthComboBox) {
        monthComboBox.getItems().addAll(Month.values());
        monthComboBox.setValue(Month.of(java.time.LocalDate.now().getMonthValue()));
    }

    public static void updateSelectedUserLabel(Label selectedUserLabel, MonthlyWorkSummary summary, Integer year, Integer month) {
        if (summary == null) {
            Platform.runLater(() -> selectedUserLabel.setText("No data available for the selected period"));
            return;
        }

        Integer daysWorked = summary.getDaysWorked();
        Integer daysRemaining = calculateWorkDays(year, month) - daysWorked - summary.getTimeOffTypes().size();
        Map<String, Long> daysOff = summary.getDaysOffCounts();

        String daysOffString = daysOff.entrySet().stream()
                .map(entry -> entry.getKey() + "-" + entry.getValue())
                .collect(Collectors.joining(" "));

        String totalHoursFormatted = formatHoursToHHMM(summary.getTotalWorkedHours());

        String displayText = String.format("Name: %s | Employee ID: %d | Total Hours Worked: %s | Days Worked: %d | Days Remaining: %d | Days Off: %s",
                summary.getName(), summary.getEmployeeId(), totalHoursFormatted, daysWorked, daysRemaining, daysOffString);

        Platform.runLater(() -> selectedUserLabel.setText(displayText));
    }

    private static int calculateWorkDays(Integer year, Integer month) {
        LocalDate date = LocalDate.of(year, month, 1);
        Integer daysInMonth = date.lengthOfMonth();
        return (int) IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(date::withDayOfMonth)
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }

    private static String formatHoursToHHMM(double hours) {
        Integer totalMinutes = (int) Math.round(hours * 60);
        Integer displayHours = totalMinutes / 60;
        Integer displayMinutes = totalMinutes % 60;
        return String.format("%02d:%02d", displayHours, displayMinutes);
    }
}
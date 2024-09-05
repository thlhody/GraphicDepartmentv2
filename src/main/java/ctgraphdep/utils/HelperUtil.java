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

    public static void updateSelectedUserLabel(Label selectedUserLabel, MonthlyWorkSummary summary, int year, int month) {
        if (summary == null) {
            Platform.runLater(() -> selectedUserLabel.setText("No data available for the selected period"));
            return;
        }

        int totalWorkDays = calculateWorkDays(year, month);
        int daysWorked = summary.getDaysWorked();
        int daysRemaining = totalWorkDays - daysWorked - summary.getTimeOffTypes().size();
        Map<String, Long> daysOff = summary.getDaysOffCounts();

        String daysOffString = daysOff.entrySet().stream()
                .map(entry -> entry.getKey() + "-" + entry.getValue())
                .collect(Collectors.joining(" "));

        String totalHoursFormatted = formatHoursToHHMM(summary.getTotalWorkedHours());

        String displayText = String.format("Name: %s | Employee ID: %d | Total Hours Worked: %s | Days Worked: %d | Days Remaining: %d | Days Off: %s",
                summary.getName(), summary.getEmployeeId(), totalHoursFormatted, daysWorked, daysRemaining, daysOffString);

        Platform.runLater(() -> selectedUserLabel.setText(displayText));
    }



    private static int calculateWorkDays(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        Integer daysInMonth = date.lengthOfMonth();
        return (int) IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(day -> date.withDayOfMonth(day))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }

    public static void refreshWorkTimeData(
            AdminTimeService adminTimeService,
            TableView<MonthlyWorkSummary> workTimeTableView,
            ComboBox<Integer> yearComboBox,
            ComboBox<Month> monthComboBox,
            Label selectedUserLabel,
            Button exportToExcelButton
    ) {
        int selectedYear = yearComboBox.getValue();
        Month selectedMonth = monthComboBox.getValue();

        LoggerUtil.info("Fetching monthly summary for year: " + selectedYear + ", month: " + selectedMonth);
        List<MonthlyWorkSummary> monthlySummaries = adminTimeService.getMonthlyWorkSummary(selectedYear, selectedMonth.getValue());
        LoggerUtil.info("Fetched " + monthlySummaries.size() + " monthly summaries");

        // Filter out the admin user
        List<MonthlyWorkSummary> filteredSummaries = monthlySummaries.stream()
                .filter(summary -> !"Admin".equalsIgnoreCase(summary.getName()))
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            workTimeTableView.getItems().clear();
            workTimeTableView.getItems().addAll(filteredSummaries);

            LoggerUtil.info("Setting up admin work time table");
            TableUtil.setupAdminWorkTimeTable(workTimeTableView, selectedYear, selectedMonth.getValue());

            if (!filteredSummaries.isEmpty()) {
                MonthlyWorkSummary firstSummary = filteredSummaries.get(0);
                updateSelectedUserLabel(selectedUserLabel, firstSummary, selectedYear, selectedMonth.getValue());
                exportToExcelButton.setVisible(true);
            } else {
                selectedUserLabel.setText("No data available for the selected period");
                exportToExcelButton.setVisible(false);
            }

            LoggerUtil.info("Refreshing table view");
            workTimeTableView.refresh();
        });
    }

    private static String formatHoursToHHMM(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int displayHours = totalMinutes / 60;
        int displayMinutes = totalMinutes % 60;
        return String.format("%02d:%02d", displayHours, displayMinutes);
    }
}
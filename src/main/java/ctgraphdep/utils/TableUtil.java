package ctgraphdep.utils;

import ctgraphdep.constants.WorkCode;
import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.models.WorkTimeTable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

public class TableUtil {

    private static final Map<DayOfWeek, String> ROMANIAN_DAY_INITIALS = Map.of(
            DayOfWeek.MONDAY, "L",
            DayOfWeek.TUESDAY, "M",
            DayOfWeek.WEDNESDAY, "M",
            DayOfWeek.THURSDAY, "J",
            DayOfWeek.FRIDAY, "V",
            DayOfWeek.SATURDAY, "S",
            DayOfWeek.SUNDAY, "D"
    );

    public static void setupAdminWorkTimeTable(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        tableView.getColumns().clear();
        tableView.getStyleClass().add("custom-table");

        addNameColumn(tableView);
        addEmployeeIdColumn(tableView);
        addDayColumns(tableView, year, month);
        addTotalHoursColumn(tableView, year, month);
        addOvertimeColumn(tableView, year, month);
        addDaysWorkedColumn(tableView, year, month);
        addTimeOffSummaryColumn(tableView, year, month);

        // Ensure the table uses our custom resize policy
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        LoggerUtil.info(TableUtil.class,"Number of items in TableView after setup: " + tableView.getItems().size());
    }

    private static void addNameColumn(TableView<MonthlyWorkSummary> tableView) {
        TableColumn<MonthlyWorkSummary, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        setFixedColumnWidth(nameColumn, 150);
        nameColumn.getStyleClass().add("column-0");
        tableView.getColumns().add(nameColumn);
    }

    private static void addEmployeeIdColumn(TableView<MonthlyWorkSummary> tableView) {
        TableColumn<MonthlyWorkSummary, Integer> employeeIdColumn = new TableColumn<>("Employee ID");
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        setFixedColumnWidth(employeeIdColumn, 100);
        employeeIdColumn.getStyleClass().add("column-1");
        tableView.getColumns().add(employeeIdColumn);
    }

    private static void addDayColumns(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            addDayColumn(tableView, year, month, day);
        }
    }

    private static void addDayColumn(TableView<MonthlyWorkSummary> tableView, int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        String dayInitial = ROMANIAN_DAY_INITIALS.get(date.getDayOfWeek());

        TableColumn<MonthlyWorkSummary, String> dayColumn = new TableColumn<>(day + "\n" + dayInitial);
        dayColumn.setCellValueFactory(cellData -> {
            Double hours = cellData.getValue().getDay(day);
            String timeOffType = cellData.getValue().getTimeOffType(day);
            if (hours != null && hours > 0) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> formatHoursWithMinutes(hours));
            } else if (timeOffType != null && !timeOffType.isEmpty()) {
                return javafx.beans.binding.Bindings.createStringBinding(() -> timeOffType);
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> "");
            }
        });
        setFixedColumnWidth(dayColumn, 50);
        dayColumn.getStyleClass().add("column-" + (day % 2 + 2));
        tableView.getColumns().add(dayColumn);
    }

    private static void addTotalHoursColumn(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        TableColumn<MonthlyWorkSummary, String> totalHoursColumn = new TableColumn<>("Total Hours");
        totalHoursColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        formatTotalHours(cellData.getValue())
                )
        );
        setFixedColumnWidth(totalHoursColumn, 80);
        totalHoursColumn.getStyleClass().add("column-" + ((YearMonth.of(year, month).lengthOfMonth() + 2) % 2 + 2));
        tableView.getColumns().add(totalHoursColumn);
    }

    private static void addOvertimeColumn(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        TableColumn<MonthlyWorkSummary, String> overtimeColumn = new TableColumn<>("Overtime");
        overtimeColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        formatOvertime(cellData.getValue())
                )
        );
        setFixedColumnWidth(overtimeColumn, 80);
        overtimeColumn.getStyleClass().add("column-" + ((YearMonth.of(year, month).lengthOfMonth() + 3) % 2 + 2));
        tableView.getColumns().add(overtimeColumn);
    }

    private static void addDaysWorkedColumn(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        TableColumn<MonthlyWorkSummary, Integer> daysWorkedColumn = new TableColumn<>("Days Worked");
        daysWorkedColumn.setCellValueFactory(new PropertyValueFactory<>("daysWorked"));
        setFixedColumnWidth(daysWorkedColumn, 80);
        daysWorkedColumn.getStyleClass().add("column-" + ((YearMonth.of(year, month).lengthOfMonth() + 3) % 2 + 2));
        tableView.getColumns().add(daysWorkedColumn);
    }

    private static void addTimeOffSummaryColumn(TableView<MonthlyWorkSummary> tableView, int year, int month) {
        TableColumn<MonthlyWorkSummary, String> timeOffSummaryColumn = new TableColumn<>("Time Off Summary");
        timeOffSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("timeOffSummary"));
        setFixedColumnWidth(timeOffSummaryColumn, 150);
        timeOffSummaryColumn.getStyleClass().add("column-" + ((YearMonth.of(year, month).lengthOfMonth() + 4) % 2 + 2));
        tableView.getColumns().add(timeOffSummaryColumn);
    }

    private static void setFixedColumnWidth(TableColumn<?, ?> column, double width) {
        column.setPrefWidth(width);
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setResizable(false);
    }

    private static String formatHoursWithMinutes(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        int displayHours = totalMinutes / 60;
        int displayMinutes = totalMinutes % 60;
        return String.format("%02d:%02d", displayHours, displayMinutes);
    }

    private static String formatTotalHours(MonthlyWorkSummary summary) {
        double totalRegularHours = 0;
        for (int day = 1; day <= 31; day++) {
            Double dailyHours = summary.getDay(day);
            if (dailyHours != null && dailyHours > 0) {
                totalRegularHours += Math.min(dailyHours, WorkCode.FULL_WORKDAY_HOURS);
            }
        }
        return String.format("%d", (int) Math.floor(totalRegularHours));
    }

    private static String formatOvertime(MonthlyWorkSummary summary) {
        double totalOvertimeHours = 0;
        for (int day = 1; day <= 31; day++) {
            Double dailyHours = summary.getDay(day);
            if (dailyHours != null && dailyHours > WorkCode.FULL_WORKDAY_WITH_OVERTIME) {
                totalOvertimeHours += dailyHours - WorkCode.FULL_WORKDAY_HOURS;
            }
        }
        return String.format("%d", (int) Math.floor(totalOvertimeHours));
    }

    // User table column methods
    public static void setupUserWorkTimeTable(TableView<WorkTimeTable> tableView, Integer year, Integer month) {
        tableView.getColumns().clear();
        tableView.getStyleClass().add("custom-table");

        addUserColumn(tableView, "Date", "workDate", 100, 0, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        addUserColumn(tableView, "Start Time", "firstStartTime", 120, 1, DateTimeFormatter.ofPattern("HH:mm"));
        addUserColumn(tableView, "Breaks", "breaks", 60, 2, null);
        addUserColumn(tableView, "Break Time", "breaksTime", 80, 3, DateTimeFormatter.ofPattern("HH:mm"));
        addUserColumn(tableView, "End", "endTime", 120, 0, DateTimeFormatter.ofPattern("HH:mm"));
        addUserColumn(tableView, "Total Time", "totalWorkedSeconds", 100, 1, null);
        addUserColumn(tableView, "Time Off", "timeOffType", 80, 2, null);

        LoggerUtil.info(TableUtil.class,"User Work Time Table setup completed");
    }

    private static <T> void addUserColumn(TableView<WorkTimeTable> tableView, String title, String property, double width, Integer columnIndex, DateTimeFormatter formatter) {
        TableColumn<WorkTimeTable, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        column.getStyleClass().add("column-" + columnIndex);
        column.setCellFactory(tc -> new TableCell<WorkTimeTable, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (formatter != null && (item instanceof LocalDate || item instanceof LocalDateTime || item instanceof LocalTime)) {
                    setText(formatter.format((TemporalAccessor) item));
                } else if (property.equals("totalWorkedSeconds")) {
                    setText(formatSecondsToHHMM((Long) item));
                } else {
                    setText(item.toString());
                }
            }
        });
        tableView.getColumns().add(column);
    }

    private static String formatSecondsToHHMM(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}

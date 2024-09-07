package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.models.WorkTimeSummary;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.services.UserWorkTimeService;
import ctgraphdep.utils.AlertUtil;
import ctgraphdep.utils.HelperUtil;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.TableUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserTimeController extends BaseController {

    @FXML
    private ImageView logoImage;
    @FXML
    private TableView<WorkTimeTable> workHoursTable;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<Month> monthComboBox;
    @FXML
    private Label totalHoursWorkedLabel;
    @FXML
    private Label overtimeHoursLabel;
    @FXML
    private Label timeOffTakenLabel;

    private UserWorkTimeService userWorkTimeService;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        this.userWorkTimeService = new UserWorkTimeService(serviceFactory.getWorkSessionService());
        setupLogoImage();
        setupYearMonthComboBoxes();
        setupTimeOffControls();
        applyCSS();
        setupTable();
        loadWorkHoursData();
    }

    @FXML
    public void onWorkTimeButton() {
        loadWorkHoursData();
    }

    @FXML
    public void onBackButton() {
        serviceFactory.getNavigationService().toUserPage();
    }

    @FXML
    protected void onSubmitButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String timeOffType = typeComboBox.getValue();

        if (startDate == null || endDate == null || timeOffType == null) {
            AlertUtil.showAlert("Error", "Please fill in all fields.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            AlertUtil.showAlert("Error", "Start date must be before or equal to end date.");
            return;
        }

        try {
            boolean success = serviceFactory.getWorkSessionService().saveTimeOff(startDate, endDate, timeOffType.split(" - ")[0]);
            if (success) {
                AlertUtil.showAlert("Success", "Time off request submitted successfully.");
                loadWorkHoursData();
            } else {
                AlertUtil.showAlert("Error", "Failed to submit time off request.");
            }
        } catch (Exception e) {
            LoggerUtil.error(getClass(),"Error submitting time off request", e);
            AlertUtil.showAlert("Error", "An error occurred while submitting the time off request. Please try again later.");
        }
    }

    private void setupTable() {
        Integer year = yearComboBox.getValue() != null ? yearComboBox.getValue() : YearMonth.now().getYear();
        Month month = monthComboBox.getValue() != null ? monthComboBox.getValue() : YearMonth.now().getMonth();
        TableUtil.setupUserWorkTimeTable(workHoursTable, year, month.getValue());
    }

    private void setupTimeOffControls() {
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
        typeComboBox.setValue("CO - Holiday");
    }

    private void setupYearMonthComboBoxes() {
        HelperUtil.setupYearComboBox(yearComboBox);
        HelperUtil.setupMonthComboBox(monthComboBox);

        // Set default values
        YearMonth currentYearMonth = YearMonth.now();
        yearComboBox.setValue(currentYearMonth.getYear());
        monthComboBox.setValue(currentYearMonth.getMonth());
    }

    private void loadWorkHoursData() {
        try {
            Integer year = yearComboBox.getValue();
            Month month = monthComboBox.getValue();

            List<WorkTimeTable> userWorkHours = userWorkTimeService.loadUserWorkHours(year, month, serviceFactory.getWorkSessionService().getCurrentUser().getUserId());

            workHoursTable.getItems().clear();
            workHoursTable.getItems().addAll(userWorkHours);

            // Update summary labels
            updateSummaryLabels(userWorkHours, year, month);

        } catch (Exception e) {
            LoggerUtil.error(getClass(),"Error loading work hours data", e);
            AlertUtil.showAlert("Error", "Failed to load work hours data. Please try again later.");
        }
    }

    private void updateSummaryLabels(List<WorkTimeTable> workHours, Integer year, Month month) {
        WorkTimeSummary summary = userWorkTimeService.calculateWorkTimeSummary(workHours, year, month);
        totalHoursWorkedLabel.setText(summary.getTotalHoursWorked());
        overtimeHoursLabel.setText(summary.getOvertimeHours());
        timeOffTakenLabel.setText(summary.getTimeOffDays());
    }

    private void applyCSS() {
        String tableStylesPath = Objects.requireNonNull(getClass().getResource(AppPaths.TABLE_STYLES_A)).toExternalForm();
        LoggerUtil.info(getClass(),"Loading table styles from: " + tableStylesPath);
        workHoursTable.getStylesheets().add(tableStylesPath);

        workHoursTable.getStyleClass().add("custom-table");

        LoggerUtil.info(getClass(),"CSS classes added to workHoursTable");
    }
}
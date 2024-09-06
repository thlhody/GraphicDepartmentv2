package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.AlertUtil;
import ctgraphdep.utils.ExportExcelUtil;
import ctgraphdep.utils.HelperUtil;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.TableUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

public class AdminTimeController extends BaseController {

    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<Month> monthComboBox;
    @FXML
    private DatePicker holidayDatePicker;
    @FXML
    private Label selectedUserLabel;
    @FXML
    private TableView<MonthlyWorkSummary> workTimeTableView;
    @FXML
    private Button exportToExcelButton;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        setupUI();
    }

    @FXML
    public void onWorkTimeButton() {
        LoggerUtil.info("Work Time button clicked");
        refreshWorkTimeData();
    }

    @FXML
    public void onAddNationalHoliday() {
        LocalDate selectedDate = holidayDatePicker.getValue();
        boolean success = serviceFactory.getAdminTimeService().addNationalHoliday(selectedDate);
        if (success) {
            AlertUtil.showAlert("Success", "National holiday added successfully.");
            refreshWorkTimeData();
        } else {
            AlertUtil.showAlert("Error", "Failed to add national holiday.");
        }
    }

    @FXML
    public void onExportToExcelButton() {
        int selectedYear = yearComboBox.getValue();
        Month selectedMonth = monthComboBox.getValue();
        Stage stage = (Stage) exportToExcelButton.getScene().getWindow();
        boolean success = ExportExcelUtil.exportMonthlyWorkSummaryToExcel(
                workTimeTableView.getItems(),
                selectedYear,
                selectedMonth.getValue(),
                stage
        );
        if (success) {
            AlertUtil.showAlert("Success", "Data exported to Excel successfully.");
        } else {
            AlertUtil.showAlert("Error", "Failed to export data to Excel.");
        }
    }

    @FXML
    public void onBackButton() {
        serviceFactory.getNavigationService().toAdminPage();
    }

    private void setupUI() {
        LoggerUtil.info("Setting up UI in AdminTimeController");
        HelperUtil.setupYearComboBox(yearComboBox);
        HelperUtil.setupMonthComboBox(monthComboBox);
        holidayDatePicker.setValue(LocalDate.now());
        exportToExcelButton.setVisible(false);
        applyCSS();
        setupTableColumns();

        workTimeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                HelperUtil.updateSelectedUserLabel(selectedUserLabel, newSelection, yearComboBox.getValue(), monthComboBox.getValue().getValue());
            }
        });
    }

    private void setupTableColumns() {
        int year = yearComboBox.getValue();
        int month = monthComboBox.getValue().getValue();
        TableUtil.setupAdminWorkTimeTable(workTimeTableView, year, month);

        // Make the table use available width
        workTimeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void applyCSS() {
        String tableStylesPath = Objects.requireNonNull(getClass().getResource(AppPaths.TABLE_STYLES_A)).toExternalForm();
        LoggerUtil.info("Loading table styles from: " + tableStylesPath);
        workTimeTableView.getStylesheets().add(tableStylesPath);

        workTimeTableView.getStyleClass().add("custom-table");
        selectedUserLabel.getStyleClass().add("selected-user-info");

        LoggerUtil.info("CSS classes added to workTimeTableView and selectedUserLabel");
    }

    private void refreshWorkTimeData() {
        LoggerUtil.info("Refreshing work time data");
        int selectedYear = yearComboBox.getValue();
        int selectedMonth = monthComboBox.getValue().getValue();

        List<MonthlyWorkSummary> summaries = serviceFactory.getAdminTimeService().refreshWorkTimeData(selectedYear, selectedMonth);

        if (summaries.isEmpty()) {
            LoggerUtil.info("No non-admin users found for the selected period");
            workTimeTableView.setItems(FXCollections.observableArrayList());
            exportToExcelButton.setVisible(false);
            selectedUserLabel.setText("No non-admin users found for the selected period");
        } else {
            workTimeTableView.setItems(FXCollections.observableArrayList(summaries));
            exportToExcelButton.setVisible(true);
            MonthlyWorkSummary firstSummary = summaries.get(0);
            HelperUtil.updateSelectedUserLabel(selectedUserLabel, firstSummary, selectedYear, selectedMonth);
        }
    }
}
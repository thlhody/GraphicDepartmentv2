package cottontex.graphdep.controllers;

import cottontex.graphdep.models.WorkTimeTable;
import cottontex.graphdep.services.UserTimeService;
import cottontex.graphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

public class UserTimeController extends BaseController {

    private UserTimeService userTimeService;

    @FXML private TableView<WorkTimeTable> workTimeTableView;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Button submitButton;



    private void setupTypeComboBox() {
        typeComboBox.getItems().addAll("CO - Holiday", "CM - Medical Leave");
        // it should have default CO
    }

    private void setupTable() {
        // it creates a table with AppPathsCSS.TABLE_STYLES_A, "/cottontex/graphdep/css/user-table-styles.css"
        // the table should display from (work_interval.json (workTimeTable)) date column | start time (firstStartTime) HH:mm | end time (endTime)  HH:mm |
        // total breaks  | breaks time HH:mm | TotalWorkedTime / SN-CO-CM depending on what is there instead of worked hours
        LoggerUtil.info("Table setup completed for UserTimeController");
        // the table needs to be up to date
    }

    @FXML
    protected void onSubmitButtonClick() {
        // when the user picks a date from start combobox and end combobox (skipping weekends and SN) it saves in the work_interval.json for that specific userId the TimeOffType

    }

}
package cottontex.graphdep.controllers;

import cottontex.graphdep.models.WorkTimeTable;
import cottontex.graphdep.services.AdminTimeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

public class AdminTimeController extends BaseController {

    private AdminTimeService adminTimeService;

    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<Integer> monthComboBox;
    @FXML
    private Button exportToExcelButton;
    @FXML
    private TableView<WorkTimeTable> workTimeTableView;
    @FXML
    private DatePicker holidayDatePicker;

    @FXML
    protected void onWorkTimeButton() {
        // this based on yearComboBox (year) and monthComboBox ( month) needs to populate a table list with user worked hours/time off (SN-national holiday,CO-time off,CM- medical leave)
        // it should display how much worked time the user registered in the work_interval.json o a specific day for the whole month and year / and should display in the table only this format HH:mm
        // package - TimeOffCodes.NATIONAL_HOLIDAY_CODE = "SN";
        // package - TimeOffCodes.TIME_OFF_CODE = "CO";
        // package - TimeOffCodes.MEDICAL_LEAVE_CODE = "CM";
        // the fxml is correct AdminTimeLayout
        // there is a AdmineTimeService - handles business logic.

    }

    @FXML
    protected void onExportToExcelButtonClick() {
        // this should export to excel the table created by the onWorkTimeButton
        // ExportExcelUtil should handle that

    }

    @FXML
    protected void onAddNationalHolidayClick() {
        // this needs to add  SN to every user in the table on a specific date
        // this needs to update the work_interval.json

    }
}
package cottontex.graphdep.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {

    @FXML
    private Button workIntervalButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button statusButton;

    //all the business logic should be in the AdminService in services package

    @FXML
    public void onStatusButton(){
        // this should open the StatusDialogController
    }

    @FXML
    public void onSettingsButton(){
        // it should open the AdminSettingsController
    }

    @FXML
    public void onWorkIntervalButton(){
        // this should open the AdminTimeController
    }
}

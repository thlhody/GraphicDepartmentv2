package cottontex.graphdep.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatusDialogController extends BaseDialogController {

    @FXML
    private VBox userStatusBox;
    @FXML
    private ScrollPane statusScrollPane;

    public StatusDialogController(VBox userStatusBox) {
        this.userStatusBox = userStatusBox;
    }

    // connection StatusDialogService / StatusDialog.fxml / Users.java (models package)
    // this should create a dialog box that has a refresh icon it can be found AppPathsIMG.REFRESH_ICON
    // and an ok button for closing it
    // in the middle it needs to retrieve the users from the users model and  add a circle red online/green offline next to the name
    // and retrieving by userId the WorkSessionStateUser add on each "date :: timeA(format HH:mm) - Online" if there is a timeB then "date :: timeA(format HH:mm) - timeB(format HH:mm)" this is offline
    // there should be the first timeA and the last time B if there are multiple timeA then pick the first one, if there are multiple timeB pick the last one to display


    @FXML
    public void initialize() {

    }

    @Override
    public void setDialogStage(Stage stage) {

    }

    @Override
    protected void refreshContent() {

    }
}
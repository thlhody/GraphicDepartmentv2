package cottontex.graphdep.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserController extends BaseController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button endButton;
    @FXML
    private Button userSettingsButton;
    @FXML
    private Button userWorkIntervalButton;
    @FXML
    private Label displayTimeInfo;
    @FXML
    private Button statusButton;

    // the UserService should handle the business logic

    private void setWelcomeMessage() {
        //welcomes the user welcome, name of the user!
    }

    private void setupDisplayTimeInfoUpdater() {
        // it displays the current time
    }

    @FXML
    protected void onStartButton() {
        // this saves in work_session_state_user(based on the login it uses the userId to identify the user)  the date and time of when it starts working in timeA
        // it should disable the start button and enable the pause/resume and stop button based on the sessionState here it is STARTED
        // it then saves in work_interval.json  under the firstStartTime under the specific userId
    }

    @FXML
    protected void onPauseButton() {
        // this pauses the start and saves in work_session_state_user(based on the login it uses the userId to identify the user) the date and time of when it pauses in timeB and sessionState is ENDED
        // it should turn into Resume Button and keep the start button disabled and the stop button can remain enabled
    }

    @FXML
    protected void onEndButton() {
        // when this button is pressed it first saves in work_session_state_user(based on the login it uses the userId to identify the user) the date and time in timeB and sessionState is ENDED
        // then it saves the same time in work_interval.json under the endTime.
        // when the endTime is saved it needs to calculate how many worked hours everything should be calculated in HH:mm format and saved in the total worked time and update the userId with that
        // this should disable pause/resume and end buttons and enable the start button

    }

    @FXML
    public void onStatusButton(){
        // this should open the StatusDialogController
    }

    @FXML
    public void onUserSettingsButton(){
        // it should open the UserSettingsController
    }


    @FXML
    protected void onUserWorkIntervalButton() {
        // it should open the UserTimeController
    }
}
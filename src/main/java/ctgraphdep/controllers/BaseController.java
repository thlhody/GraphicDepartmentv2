package cottontex.graphdep.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public class BaseController {

    @FXML
    protected ImageView logoImage;
    @FXML
    protected ImageView mainImage;
    @FXML
    protected Button logoutButton;
    @FXML
    protected Button backButton;

    @FXML
    protected void onLogoutButton() {
        // implement logout button logic
        // this should work on admin and user controller
        // this is in the basecontroller should work like a logout button and close, clear and move to the LauncherController
    }

    @FXML
    protected void onBackButton() {
        // Implement back button logic
        // this should work on all controllers
    }

    @FXML
    public void initialize() {

    }

    //add any other methods that can be used in the rest of the controller so that we can avoid duplicated code
}

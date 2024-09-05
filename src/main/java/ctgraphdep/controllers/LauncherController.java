package ctgraphdep.controllers;

import ctgraphdep.services.*;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.constants.AppPaths;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class LauncherController extends BaseController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button aboutButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label onlineStatusLabel;
    @FXML
    private ProgressIndicator syncProgressIndicator;
    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView mainImage;

    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        if (serviceFactory.isInitialized()) {
            setCurrentFXMLPath(AppPaths.LAUNCHER);
            adjustImagePosition();
            setupLogoImage();
            setupMainImage();
        } else {
            LoggerUtil.error("ServiceFactory is not initialized in LauncherController");
        }
    }

    @FXML
    protected void onLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (serviceFactory.getAuthenticationService().login(username, password)) {
            usernameField.clear();
            passwordField.clear();
        }
    }

    @FXML
    protected void onAboutButton() {
        AboutDialogController.openAboutDialog(serviceFactory);
    }

    private void adjustImagePosition() {
        Platform.runLater(() -> {
            double offset = 12; // Adjust this value to move the image more or less to the left
            mainImage.setTranslateX(-offset);
        });
    }
}
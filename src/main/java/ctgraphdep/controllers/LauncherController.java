package ctgraphdep.controllers;

import ctgraphdep.services.*;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.constants.AppPaths;
import ctgraphdep.utils.WindowUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    private ImageView logoImage;
    @FXML
    private ImageView mainImage;

    private FileAccessibilityService fileAccessibilityService;

    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        if (serviceFactory.isInitialized()) {
            setCurrentFXMLPath(AppPaths.LAUNCHER);
            this.fileAccessibilityService = serviceFactory.getFileAccessibilityService();
            setupLogoImage();
            setupMainImage();
            updateOnlineStatus();
            Platform.runLater(() -> {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                WindowUtil.initializeMainStage(stage);
                WindowUtil.adjustStageSize(stage);
                WindowUtil.centerStage(stage);
            });
        } else {
            LoggerUtil.error("ServiceFactory is not initialized in LauncherController");
        }
    }

    @FXML
    protected void onLoginButton() {

        if (serviceFactory.getAuthenticationService().login(usernameField.getText(), passwordField.getText())) {
            usernameField.clear();
            passwordField.clear();
        }
    }

    @FXML
    protected void onAboutButton() {
        AboutDialogController.openAboutDialog(serviceFactory);
    }

    private void updateOnlineStatus() {
        fileAccessibilityService.updateOnlineStatus(onlineStatusLabel);
    }
}
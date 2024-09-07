package ctgraphdep.controllers;

import ctgraphdep.services.*;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.constants.AppPaths;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private FileAccessibilityService fileAccessibilityService;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        if (serviceFactory.isInitialized()) {
            setCurrentFXMLPath(AppPaths.LAUNCHER);
            this.fileAccessibilityService = serviceFactory.getFileAccessibilityService();
            updateOnlineStatus();
            setupMainImage();
        } else {
            LoggerUtil.error(getClass(),"ServiceFactory is not initialized in LauncherController");
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

    protected void setupMainImage() {
        if (mainImage != null && serviceFactory != null) {
            serviceFactory.getLogoService().setMainImage(mainImage);
            LoggerUtil.info(getClass(),"Main image set successfully in " + getClass().getSimpleName());
        } else {
            LoggerUtil.warn(getClass(),"mainImage is null or serviceFactory is not initialized in " + getClass().getSimpleName());
        }
    }
}
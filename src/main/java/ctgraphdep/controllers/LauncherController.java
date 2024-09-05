package cottontex.graphdep.controllers;

import cottontex.graphdep.models.Users;
import cottontex.graphdep.services.UserService;
import cottontex.graphdep.services.LogoService;
import cottontex.graphdep.utils.LoggerUtil;
import cottontex.graphdep.constants.AppPathsFXML;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static cottontex.graphdep.utils.AlertUtil.showAlert;

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

    private UserService userService;
    private LogoService logoService;

    @FXML
    public void initialize() {
        super.initialize();
        userService = new UserService();
        logoService = new LogoService();
        LoggerUtil.initialize(LauncherController.class, "Launcher view initialized");

        if (logoImage != null) {
            logoService.setHeaderLogo(logoImage);
        } else {
            LoggerUtil.warn("logoImage is null in LauncherController");
        }

        if (mainImage != null) {
            logoService.setMainImage(mainImage);
        } else {
            LoggerUtil.warn("mainImage is null in LauncherController");
        }
    }

    @FXML
    protected void onLoginButton() {
        // ... (existing login logic)
    }

    @FXML
    protected void onAboutButton() {
        LoggerUtil.buttonInfo("About", "N/A");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppPathsFXML.ABOUT_DIALOG));
            Parent root = loader.load();
            AboutDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("About");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            controller.setContent("Creative Time And Task Tracker", "Version 1.0", "© 2024 CottonTex");

            // Set the dialog box image
            logoService.setDialogBoxImage(controller.getDialogImage());

            dialogStage.showAndWait();
        } catch (IOException e) {
            LoggerUtil.error("Error opening About dialog", e);
            showAlert("Error", "Could not open About dialog: " + e.getMessage());
        }
    }
}
package cottontex.graphdep.controllers;

import cottontex.graphdep.services.UserSettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;

public class UserSettingsController extends BaseController {

    @FXML private ImageView logoImage;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private UserSettingsService userSettingsService;

    @FXML
    protected void onChangePassword() {
        // the user changes his password the json file get update for the specific userId with the new password
    }

    private void setStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error-label", "success-label");
        statusLabel.getStyleClass().add(isSuccess ? "success-label" : "error-label");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

}
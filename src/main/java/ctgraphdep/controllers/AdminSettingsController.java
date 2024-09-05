package cottontex.graphdep.controllers;


import cottontex.graphdep.services.AdminSettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminSettingsController extends BaseController {

    private AdminSettingsService adminSettingsService;

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> userComboBox;


    @FXML
    protected void onAddUserButton() {
        // this creates a user in the users.json file
        // and displays a message with the user created using the AlertUtil
        // when clicked it needs to check if the employeeId/username - exists before saving to json file users

    }

    @FXML
    protected void onResetPasswordButtonClick() {
        // it needs a users combobox that takes all the users from the users model and lets it select and reset the password to a generic pasword "cottontex123" updates the json users file.

    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // it uses the same users combobox that takes all the users from the users model and lets it delete the specific(selected) user from the json file.

    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        employeeIdField.clear();
        roleComboBox.setValue("USER");
    }
}
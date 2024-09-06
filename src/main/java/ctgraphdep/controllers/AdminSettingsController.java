package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.Users;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.services.UserManagementService;
import ctgraphdep.utils.AlertUtil;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class AdminSettingsController extends BaseController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField employeeIdField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private Button addUpdateUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private TextField basePathField;
    @FXML
    private Button updateBasePathButton;

    private boolean isEditMode = false;
    private String currentUsername;
    private UserManagementService userManagementService;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        this.serviceFactory = serviceFactory;
        this.userManagementService = serviceFactory.getUserManagementService();
        LoggerUtil.info("AdminSettingsController services initialized: " + serviceFactory.isInitialized());
        setCurrentFXMLPath(AppPaths.ADMIN_SETTING_LAYOUT);
        setupLogoImage();
        populateUserComboBox();
        populateRoleComboBox();
        setupUserComboBoxListener();
        basePathField.setText(JsonPaths.getDataPath());
    }

    @FXML
    protected void onAddUpdateUserButton() {
        if (isEditMode) {
            updateUser();
        } else {
            addNewUser();
        }
    }

    @FXML
    protected void onResetPasswordButton() {
        String selectedUsername = userComboBox.getValue();
        if (selectedUsername != null) {
            userManagementService.resetPassword(selectedUsername);
        } else {
            AlertUtil.showAlert("Error", "Please select a user.");
        }
    }

    @FXML
    protected void onEditUserButton() {
        String selectedUsername = userComboBox.getValue();
        if (selectedUsername != null) {
            Optional<Users> userOptional = userManagementService.getUserByUsername(selectedUsername);
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                nameField.setText(user.getName());
                usernameField.setText(user.getUsername());
                employeeIdField.setText(user.getEmployeeId().toString());
                roleComboBox.setValue(user.getRole());
                passwordField.clear(); // Clear password field for security
                isEditMode = true;
                currentUsername = selectedUsername;
                addUpdateUserButton.setText("Update User");
            } else {
                AlertUtil.showAlert("Error", "Failed to load user data.");
            }
        } else {
            AlertUtil.showAlert("Error", "Please select a user to edit.");
        }
    }

    @FXML
    protected void onClearFieldsButton() {
        clearFields();
        isEditMode = false;
        addUpdateUserButton.setText("Add User");
    }

    @FXML
    protected void onUpdateBasePathButton() {
        String newDataPath = basePathField.getText();
        userManagementService.updateDataPath(newDataPath);
        basePathField.setText(JsonPaths.getDataPath());  // Reset to current path
    }

    @FXML
    protected void onDeleteUserButton() {
        String selectedUsername = userComboBox.getValue();
        if (selectedUsername != null) {
            boolean success = userManagementService.deleteUser(selectedUsername);
            if (success) {
                populateUserComboBox();
                clearFields();
            }
        } else {
            AlertUtil.showAlert("Error", "Please select a user.");
        }
    }

    @FXML
    public void onBackButton() {
        serviceFactory.getNavigationService().toAdminPage();
    }

    private void setupUserComboBoxListener() {
        userComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editUserButton.setDisable(newValue == null);
        });
    }

    private void addNewUser() {
        boolean userAdded = userManagementService.addNewUser(
                nameField.getText(),
                usernameField.getText(),
                passwordField.getText(),
                employeeIdField.getText(),
                roleComboBox.getValue()
        );

        if (userAdded) {
            populateUserComboBox();
            clearFields();
        }
    }

    private void updateUser() {
        boolean success = userManagementService.updateUser(
                currentUsername,
                nameField.getText(),
                usernameField.getText(),
                employeeIdField.getText(),
                roleComboBox.getValue()
        );

        if (success) {
            populateUserComboBox();
            clearFields();
        }
    }

    private void populateUserComboBox() {
        userComboBox.getItems().clear();
        userComboBox.getItems().addAll(userManagementService.getAllUsernames());
    }

    private void populateRoleComboBox() {
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(userManagementService.getRoles());
        roleComboBox.setValue(userManagementService.getDefaultRole());
    }

    private void clearFields() {
        nameField.clear();
        usernameField.clear();
        passwordField.clear();
        employeeIdField.clear();
        roleComboBox.setValue(userManagementService.getDefaultRole());
        isEditMode = false;
        currentUsername = null;
        addUpdateUserButton.setText("Add User");
    }
}
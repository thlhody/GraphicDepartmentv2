package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.constants.AppPaths;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.models.Users;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class UserSettingsController extends BaseController {

    @FXML
    private ImageView logoImage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField employeeIdField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    private Users currentUser;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        LoggerUtil.info("UserSettingsController services initialized: " + serviceFactory.isInitialized());
        setCurrentFXMLPath(AppPaths.USER_SETTINGS_LAYOUT);
        setupLogoImage();
        loadUserData();
    }

    private void loadUserData() {
        if (serviceFactory != null && serviceFactory.getUserService() != null) {
            currentUser = serviceFactory.getWorkSessionService().getCurrentUser();
            if (currentUser != null) {
                nameField.setText(currentUser.getName());
                usernameField.setText(currentUser.getUsername());
                employeeIdField.setText(String.valueOf(currentUser.getEmployeeId()));
            } else {
                LoggerUtil.error("Current user is null");
                setStatusMessage("Error: Unable to load user data", false);
            }
        } else {
            LoggerUtil.error("UserService is not initialized");
            setStatusMessage("Error: Service not initialized", false);
        }
    }

    @FXML
    public void onBackButton() {
        serviceFactory.getNavigationService().toUserPage();
    }

    @FXML
    private void onUpdateProfile() {
        if (serviceFactory != null && serviceFactory.getUserService() != null && currentUser != null) {
            String newName = nameField.getText();
            String newUsername = usernameField.getText();
            String employeeId = employeeIdField.getText();

            boolean success = serviceFactory.getUserService().updateUser(
                    currentUser.getUsername(),
                    newName,
                    newUsername,
                    employeeId,
                    currentUser.getRole(),
                    currentUser.getPersonalFolderPath()
            );

            if (success) {
                setStatusMessage("Profile updated successfully", true);
                loadUserData(); // Reload user data to reflect changes
            } else {
                setStatusMessage("Failed to update profile", false);
            }
        } else {
            LoggerUtil.error("UserService is not initialized or current user is null");
            setStatusMessage("Error: Unable to update profile", false);
        }
    }

    @FXML
    protected void onChangePassword() {
        if (serviceFactory != null && serviceFactory.getUserService() != null) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (newPassword.equals(confirmPassword)) {
                boolean success = serviceFactory.getUserService().changePassword(currentPassword, newPassword);
                if (success) {
                    setStatusMessage("Password changed successfully", true);
                    clearPasswordFields();
                } else {
                    setStatusMessage("Failed to change password", false);
                }
            } else {
                setStatusMessage("New passwords do not match", false);
            }
        } else {
            LoggerUtil.error("UserService is not initialized");
            setStatusMessage("Error: Service not initialized", false);
        }
    }

    private void setStatusMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error-label", "success-label");
        statusLabel.getStyleClass().add(isSuccess ? "success-label" : "error-label");
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}
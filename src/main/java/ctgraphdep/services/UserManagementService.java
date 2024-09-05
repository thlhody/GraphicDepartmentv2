package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.UserStatus;
import ctgraphdep.models.Users;
import ctgraphdep.utils.AlertUtil;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.SaveFilesUtil;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserManagementService {

    private final UserService userService;
    private final Stage stage;

    public UserManagementService(UserService userService, Stage stage) {
        this.userService = userService;
        this.stage = stage;
    }

    public boolean addNewUser(String name, String username, String password, String employeeId, String role) {
        if (userService.isUserExist(username, employeeId)) {
            AlertUtil.showAlert("Error", "User with this username or employee ID already exists.");
            return false;
        }

        String basePath = SaveFilesUtil.selectUserFolder(stage);
        if (basePath == null) {
            AlertUtil.showAlert("Error", "No folder selected. User creation cancelled.");
            return false;
        }

        try {
            String fullPath = SaveFilesUtil.createUserFolder(basePath);
            boolean userAdded = userService.addUser(name, username, password, employeeId, role, fullPath);

            if (userAdded) {
                // Update user status
                updateUserStatus(name, username);
                AlertUtil.showAlert("Success", "User created successfully.");
                return true;
            } else {
                AlertUtil.showAlert("Error", "Failed to create user.");
                return false;
            }
        } catch (RuntimeException e) {
            LoggerUtil.error("Error creating user folder: " + e.getMessage(), e);
            AlertUtil.showAlert("Error", "Failed to create user folder.");
            return false;
        }
    }

    private void updateUserStatus(String name, String username) {
        List<UserStatus> userStatuses = JsonUtils.readUserStatusFromJson(JsonPaths.USER_STATUS_JSON);
        Optional<Users> newUser = userService.getUserByUsername(username);

        if (newUser.isPresent()) {
            UserStatus newStatus = new UserStatus(
                    newUser.get().getUserId(),
                    name,
                    "offline",
                    LocalDateTime.now()
            );
            userStatuses.add(newStatus);
            boolean success = JsonUtils.writeUserStatusToJson(userStatuses, JsonPaths.USER_STATUS_JSON);
            if (!success) {
                LoggerUtil.error("Failed to update user status for new user: " + username);
            }
        } else {
            LoggerUtil.error("Failed to find newly created user: " + username);
        }
    }

    public boolean updateUser(String currentUsername, String name, String newUsername, String employeeId, String role) {
        String basePath = SaveFilesUtil.selectUserFolder(stage);
        if (basePath == null) {
            AlertUtil.showAlert("Error", "No folder selected. User update cancelled.");
            return false;
        }

        try {
            String fullPath = SaveFilesUtil.createUserFolder(basePath);
            boolean success = userService.updateUser(currentUsername, name, newUsername, employeeId, role, fullPath);

            if (success) {
                AlertUtil.showAlert("Success", "User updated successfully.");
                return true;
            } else {
                AlertUtil.showAlert("Error", "Failed to update user.");
                return false;
            }
        } catch (RuntimeException e) {
            LoggerUtil.error("Error creating user folder during update: " + e.getMessage(), e);
            AlertUtil.showAlert("Error", "Failed to create user folder during update.");
            return false;
        }
    }

    public void resetPassword(String username) {
        try {
            boolean success = userService.resetPassword(username, "cottontex123");
            if (success) {
                LoggerUtil.info("Password reset successful for user: " + username);
                AlertUtil.showAlert("Success", "Password reset successfully. New password is: cottontex123 .");
            } else {
                LoggerUtil.error("Failed to reset password for user: " + username);
                AlertUtil.showAlert("Error", "Failed to reset password.");
            }
        } catch (Exception e) {
            LoggerUtil.error("Exception occurred while resetting password: " + e.getMessage(), e);
            AlertUtil.showAlert("Error", "An unexpected error occurred while resetting the password.");
        }
    }

    public boolean deleteUser(String username) {
        boolean success = userService.deleteUser(username);
        if (success) {
            AlertUtil.showAlert("Success", "User deleted successfully.");
            return true;
        } else {
            AlertUtil.showAlert("Error", "Failed to delete user.");
            return false;
        }
    }

    public Optional<Users> getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    public List<String> getAllUsernames() {
        return userService.getAllUsernames();
    }
}
package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.Users;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
public class UserService {

    private List<Users> users;
    private WorkSessionService workSessionService;

    public UserService() {
        loadUsers();
    }

    public boolean isUserExist(String username, String employeeId) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equals(username) || u.getEmployeeId().toString().equals(employeeId));
    }

    public Optional<Users> authenticateUser(String username, String password) {
        loadUsers();
        Optional<Users> authenticatedUser = users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();

        if (authenticatedUser.isPresent()) {
            LoggerUtil.info("User authenticated successfully: " + username);
            if (workSessionService != null) {
                workSessionService.setCurrentUser(authenticatedUser.get());
                workSessionService.loadExistingSession();
            } else {
                LoggerUtil.error("WorkSessionService is null in UserService");
            }
        } else {
            LoggerUtil.warn("Failed authentication attempt for username: " + username);
        }

        return authenticatedUser;
    }

    public Optional<Users> getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public List<Users> getAllUsers() {
        return users;
    }

    public List<String> getAllUsernames() {
        return users.stream()
                .map(Users::getUsername)
                .collect(Collectors.toList());
    }

    private Integer getNextUserId() {
        return users.stream()
                .mapToInt(Users::getUserId)
                .max()
                .orElse(0) + 1;
    }

    //user related actions

    public void reloadUsers() {
        loadUsers();
    }

    private void loadUsers() {
        try {
            users = JsonUtils.readListFromJson(JsonPaths.getJsonUsers(), Users.class);
            LoggerUtil.info("Users loaded successfully. Total users: " + users.size());
        } catch (Exception e) {
            LoggerUtil.error("Error loading users from JSON file", e);
            users = new ArrayList<>();
        }
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        Users currentUser = workSessionService.getCurrentUser();
        if (currentUser == null) {
            LoggerUtil.error("No user currently logged in");
            return false;
        }

        if (!currentUser.getPassword().equals(currentPassword)) {
            LoggerUtil.warn("Failed to change password: incorrect current password");
            return false;
        }

        // Update the password in the current user object
        currentUser.setPassword(newPassword);

        // Find and update the user in the users list
        Optional<Users> userToUpdate = users.stream()
                .filter(u -> u.getUserId().equals(currentUser.getUserId()))
                .findFirst();

        if (userToUpdate.isPresent()) {
            userToUpdate.get().setPassword(newPassword);
            boolean success = saveUsers();
            if (success) {
                LoggerUtil.info("Password changed successfully for user: " + currentUser.getUsername());
            } else {
                LoggerUtil.error("Failed to save new password for user: " + currentUser.getUsername());
            }
            return success;
        } else {
            LoggerUtil.error("User not found in the users list: " + currentUser.getUsername());
            return false;
        }
    }

    private boolean saveUsers() {
        try {
            JsonUtils.writeListToJson(JsonPaths.getJsonUsers(), users);
            return true;
        } catch (Exception e) {
            LoggerUtil.error("Error saving users to JSON file", e);
            return false;
        }
    }

    public boolean resetPassword(String username, String newPassword) {
        Optional<Users> userOptional = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setPassword(newPassword);
            boolean success = saveUsers();
            if (success) {
                LoggerUtil.info("Password reset successfully for user: " + username);
            } else {
                LoggerUtil.error("Failed to save new password for user: " + username);
            }
            return success;
        } else {
            LoggerUtil.warn("User not found for password reset: " + username);
            return false;
        }
    }

    public boolean deleteUser(String username) {
        int initialSize = users.size();
        users = users.stream()
                .filter(user -> !user.getUsername().equals(username))
                .collect(Collectors.toList());

        if (users.size() < initialSize) {
            boolean success = saveUsers();
            if (success) {
                LoggerUtil.info("User deleted successfully: " + username);
            } else {
                LoggerUtil.error("Failed to delete user: " + username);
            }
            return success;
        } else {
            LoggerUtil.warn("User not found for deletion: " + username);
            return false;
        }
    }

    public boolean addUser(String name, String username, String password, String employeeId, String role, String fullPath) {
        if (isUserExist(username, employeeId)) {
            LoggerUtil.warn("Failed to add user: username or employee ID already exists");
            return false;
        }

        Users newUser = new Users(
                getNextUserId(),
                name,
                Integer.parseInt(employeeId),
                username,
                password,
                role,
                fullPath
        );

        users.add(newUser);
        boolean success = saveUsers();
        if (success) {
            LoggerUtil.info("User added successfully: " + username);
        } else {
            LoggerUtil.error("Failed to save new user: " + username);
        }
        return success;
    }

    public boolean updateUser(String oldUsername, String name, String newUsername, String employeeId, String role, String newPath) {
        Optional<Users> userOptional = users.stream()
                .filter(u -> u.getUsername().equals(oldUsername))
                .findFirst();

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setName(name);
            user.setUsername(newUsername);
            user.setEmployeeId(Integer.parseInt(employeeId));
            user.setRole(role);
            user.setPersonalFolderPath(newPath);

            boolean success = saveUsers();
            if (success) {
                LoggerUtil.info("User updated successfully: " + oldUsername);
            } else {
                LoggerUtil.error("Failed to update user: " + oldUsername);
            }
            return success;
        } else {
            LoggerUtil.warn("User not found for update: " + oldUsername);
            return false;
        }
    }
}
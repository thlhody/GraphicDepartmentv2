package cottontex.graphdep.services;

import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.models.Users;
import cottontex.graphdep.utils.JsonUtils;
import cottontex.graphdep.utils.LoggerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private List<Users> users;

    public UserService() {
        loadUsers();
    }

    private void loadUsers() {
        try {
            users = JsonUtils.readListFromJson(JsonPaths.JSON_USERS, Users.class);
            LoggerUtil.info("Users loaded successfully. Total users: " + users.size());
        } catch (IOException e) {
            LoggerUtil.error("Error loading users from JSON file", e);
        }
    }

    public Optional<Users> authenticateUser(String username, String password) {
        Optional<Users> authenticatedUser = users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();

        if (authenticatedUser.isPresent()) {
            LoggerUtil.info("User authenticated successfully: " + username);
        } else {
            LoggerUtil.warn("Failed authentication attempt for username: " + username);
        }

        return authenticatedUser;
    }

    public List<Users> getAllUsers() {
        return users;
    }

    public Optional<Users> getUserById(Integer userId) {
        Optional<Users> user = users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst();

        if (user.isPresent()) {
            LoggerUtil.info("User found with ID: " + userId);
        } else {
            LoggerUtil.warn("No user found with ID: " + userId);
        }

        return user;
    }

    // Add more user-related methods as needed
}
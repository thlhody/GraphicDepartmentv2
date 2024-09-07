package ctgraphdep.services;

import ctgraphdep.controllers.AdminController;
import ctgraphdep.controllers.UserController;
import ctgraphdep.controllers.UserTeamLeaderController;
import ctgraphdep.models.Users;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.AlertUtil;
import lombok.Getter;

import java.util.Optional;

public class AuthenticationService {

    private final UserService userService;
    private final NavigationService navigationService;
    private final WorkSessionService workSessionService;
    @Getter
    private Users currentUser;

    public AuthenticationService(UserService userService, WorkSessionService workSessionService, NavigationService navigationService) {
        this.userService = userService;
        this.navigationService = navigationService;
        this.workSessionService = workSessionService;
    }

    public boolean login(String username, String password) {
        LoggerUtil.logUserAction("Login", username,"Attempt login");

        userService.reloadUsers();

        Optional<Users> authenticatedUser = userService.authenticateUser(username, password);
        if (authenticatedUser.isPresent()) {
            currentUser = authenticatedUser.get();
            workSessionService.setCurrentUser(currentUser); // Set the current user in WorkSessionService
            LoggerUtil.logUserAction("Login", username,"Successful login" );
            navigateBasedOnRole(currentUser);
            return true;
        } else {
            LoggerUtil.warn(getClass(),"Failed login attempt for username: " + username);
            AlertUtil.showAlert("Login Failed", "Invalid username or password.");
            return false;
        }
    }

    private void navigateBasedOnRole(Users user) {
        switch (user.getRole()) {
            case "ADMIN":
                LoggerUtil.logControllerSwitch(AuthenticationService.class, AdminController.class, user.getUsername());
                navigationService.toAdminPage();
                break;
            case "USER":
                LoggerUtil.logControllerSwitch(AuthenticationService.class, UserController.class, user.getUsername());
                navigationService.toUserPage();
                break;
            case "USER TEAM LEADER":
                LoggerUtil.logControllerSwitch(AuthenticationService.class, UserTeamLeaderController.class, user.getUsername());
                navigationService.toUserPage();
                break;
            default:
                LoggerUtil.error(getClass(),"Unknown role for user: " + user.getUsername());
                AlertUtil.showAlert("Login Error", "Unknown user role");
        }
    }
}
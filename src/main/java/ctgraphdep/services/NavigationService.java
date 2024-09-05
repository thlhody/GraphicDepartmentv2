package ctgraphdep.services;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.controllers.BaseController;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationService {

    private Stage primaryStage;

    public NavigationService(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // User navigation
    public void toUserPage() {
        navigateTo(AppPaths.USER_PAGE_LAYOUT, "User Page");
    }

    public void toUserSettings() {
        navigateTo(AppPaths.USER_SETTINGS_LAYOUT, "User Settings");
    }

    public void toUserWorkInterval() {
        navigateTo(AppPaths.USER_WORK_INTERVAL_LAYOUT, "User Work Interval");
    }

    // Admin navigation
    public void toAdminPage() {
        navigateTo(AppPaths.ADMIN_PAGE_LAYOUT, "Admin Page");
    }

    public void toAdminSettings() {
        navigateTo(AppPaths.ADMIN_SETTING_LAYOUT, "Admin Settings");
    }

    public void toAdminWorkInterval() {
        navigateTo(AppPaths.ADMIN_WORK_INTERVAL_LAYOUT, "Admin Work Interval");
    }

    // navigation
    public void goBack(String currentPath){
        if (currentPath.equals(AppPaths.ADMIN_SETTING_LAYOUT) ||
                currentPath.equals(AppPaths.ADMIN_WORK_INTERVAL_LAYOUT)) {
            navigateTo(AppPaths.ADMIN_PAGE_LAYOUT, "Admin Page");
        } else if (currentPath.equals(AppPaths.USER_SETTINGS_LAYOUT) ||
                currentPath.equals(AppPaths.USER_WORK_INTERVAL_LAYOUT)) {
            navigateTo(AppPaths.USER_PAGE_LAYOUT, "User Page");
        } else {
            LoggerUtil.info("Cannot go back from this page");
        }
    }

    public void logout(){
        LoggerUtil.info("User logged out");
        navigateTo(AppPaths.LAUNCHER, "Login");
    }

    // General navigation method
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            // Get the controller and initialize services
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).initializeServices(ServiceFactory.getInstance());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            LoggerUtil.info("Navigated to: " + title);
        } catch (IOException e) {
            LoggerUtil.error("Error navigating to: " + fxmlPath, e);
        }
    }
}

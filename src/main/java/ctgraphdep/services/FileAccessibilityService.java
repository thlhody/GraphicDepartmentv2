package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.utils.LoggerUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.File;

public class FileAccessibilityService {

    public boolean isUsersJsonAccessible() {
        LoggerUtil.info("Checking users.json accessibility in new data path");

        String dataPathUsers = new File(JsonPaths.getDataPath(), "users.json").getAbsolutePath();
        boolean accessibleInDataPath = isFileAccessible(dataPathUsers);

        LoggerUtil.info("Users JSON file accessibility in new path: " + (accessibleInDataPath ? "Accessible" : "Not accessible"));

        return accessibleInDataPath;
    }

    private boolean isFileAccessible(String filePath) {
        File file = new File(filePath);
        boolean exists = file.exists();
        boolean canRead = file.canRead();

        LoggerUtil.info("Checking file: " + filePath);
        LoggerUtil.info("File exists: " + exists);
        LoggerUtil.info("File is readable: " + canRead);

        return exists && canRead;
    }

    public void updateOnlineStatus(Label onlineStatusLabel) {
        LoggerUtil.info("Updating online status");

        boolean isAccessible = isUsersJsonAccessible();

        Platform.runLater(() -> {
            if (onlineStatusLabel == null) {
                LoggerUtil.error("onlineStatusLabel is null");
                return;
            }

            if (isAccessible) {
                setOnlineStatus(onlineStatusLabel);
            } else {
                setOfflineStatus(onlineStatusLabel);
            }
        });
    }

    private void setOnlineStatus(Label label) {
        label.setText("Online");
        label.getStyleClass().remove("offline-status-label");
        label.getStyleClass().add("online-status-label");
        LoggerUtil.info("Status set to Online (using new path)");
    }

    private void setOfflineStatus(Label label) {
        label.setText("Offline");
        label.getStyleClass().remove("online-status-label");
        label.getStyleClass().add("offline-status-label");
        LoggerUtil.info("Status set to Offline (using default path)");
    }
}
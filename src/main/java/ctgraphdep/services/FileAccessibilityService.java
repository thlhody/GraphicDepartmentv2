package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.utils.LoggerUtil;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.File;

public class FileAccessibilityService {

    public boolean isUsersJsonAccessible() {
        LoggerUtil.info(getClass(),"Checking users.json accessibility in new data path");

        String dataPathUsers = new File(JsonPaths.getDataPath(), "users.json").getAbsolutePath();
        boolean accessibleInDataPath = isFileAccessible(dataPathUsers);

        LoggerUtil.info(getClass(),"Users JSON file accessibility in new path: " + (accessibleInDataPath ? "Accessible" : "Not accessible"));

        return accessibleInDataPath;
    }

    private boolean isFileAccessible(String filePath) {
        File file = new File(filePath);
        boolean exists = file.exists();
        boolean canRead = file.canRead();

        LoggerUtil.info(getClass(),"Checking file: " + filePath);
        LoggerUtil.info(getClass(),"File exists: " + exists);
        LoggerUtil.info(getClass(),"File is readable: " + canRead);

        return exists && canRead;
    }

    public void updateOnlineStatus(Label onlineStatusLabel) {
        LoggerUtil.info(getClass(),"Updating online status");

        boolean isAccessible = isUsersJsonAccessible();

        Platform.runLater(() -> {
            if (onlineStatusLabel == null) {
                LoggerUtil.error(getClass(),"onlineStatusLabel is null");
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
        LoggerUtil.info(getClass(),"Status set to Online (using new path)");
    }

    private void setOfflineStatus(Label label) {
        label.setText("Offline");
        label.getStyleClass().remove("online-status-label");
        label.getStyleClass().add("offline-status-label");
        LoggerUtil.info(getClass(),"Status set to Offline (using default path)");
    }
}
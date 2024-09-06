package ctgraphdep.utils;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.constants.JsonPaths;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveFilesUtil {

    public static String selectUserFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select User Folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        return null;
    }

    public static String createUserFolder(String basePath) {
        String fullPath = Paths.get(basePath, AppPaths.CT3_FOLDER).toString();
        Path path = Paths.get(fullPath);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LoggerUtil.error("Error creating CT3 folder: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create CT3 folder", e);
        }
        return fullPath;
    }
}
package ctgraphdep;

import ctgraphdep.utils.LoggerUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            System.setProperty("sun.java2d.uiScale", "1.25");
            LoggerUtil.info("Starting application...");
            checkJavaFXAvailability();
            LoggerUtil.info("JavaFX modules verified. Launching main application...");
            Launcher.main(args);
        } catch (Exception e) {
            LoggerUtil.info("Error occurred while starting the application: "+e.getMessage());
        }
    }

    private static void checkJavaFXAvailability() {
        try {
            Class.forName("javafx.application.Application");
            LoggerUtil.info("JavaFX Application class loaded successfully");
        } catch (ClassNotFoundException e) {
            LoggerUtil.error("Failed to load JavaFX Application class. Make sure JavaFX is properly installed and configured.");
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            LoggerUtil.error("Current directory: " + currentPath);
            LoggerUtil.error("Files in current directory:");
            File[] files = currentPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    LoggerUtil.error(file.getName());
                }
            }
            System.exit(1);
        }
    }
}
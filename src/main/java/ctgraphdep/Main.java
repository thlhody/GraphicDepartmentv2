package ctgraphdep;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting application...");
            checkJavaFXAvailability();
            System.out.println("JavaFX modules verified. Launching main application...");
            Launcher.main(args);
        } catch (Exception e) {
            System.err.println("Error occurred while starting the application:");
            e.printStackTrace();
        }
    }

    private static void checkJavaFXAvailability() {
        try {
            Class.forName("javafx.application.Application");
            System.out.println("JavaFX Application class loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JavaFX Application class. Make sure JavaFX is properly installed and configured.");
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            System.err.println("Current directory: " + currentPath);
            System.err.println("Files in current directory:");
            File[] files = currentPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    System.err.println(file.getName());
                }
            }
            System.exit(1);
        }
    }
}
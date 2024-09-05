package ctgraphdep.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowUtil {
    private static final double DEFAULT_MAIN_WIDTH = 1200;
    private static final double DEFAULT_MAIN_HEIGHT = 900;

    public static void setStageSize(Stage stage, double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public static void resetSize(Stage stage) {
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
    }

    public static void initializeMainStage(Stage stage) {
        setStageSize(stage, DEFAULT_MAIN_WIDTH, DEFAULT_MAIN_HEIGHT);
        //maintainSize(stage);
    }

    public static void updateMainStage(Stage stage, Scene newScene) {
        resetSize(stage);
        stage.setScene(newScene);
        setStageSize(stage, DEFAULT_MAIN_WIDTH, DEFAULT_MAIN_HEIGHT);
        //maintainSize(stage);
    }
}
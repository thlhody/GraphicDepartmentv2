package ctgraphdep.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowUtil {
    private static final double SCREEN_WIDTH_PERCENTAGE = 0.9;
    private static final double SCREEN_HEIGHT_PERCENTAGE = 0.9;
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 600;
    private static final double PADDING = 20;
    private static final double HEADER_MAX_WIDTH = 1200; // Maximum width for header content

    public static void initializeMainStage(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.min(visualBounds.getWidth() * SCREEN_WIDTH_PERCENTAGE, visualBounds.getWidth() - PADDING);
        double height = Math.min(visualBounds.getHeight() * SCREEN_HEIGHT_PERCENTAGE, visualBounds.getHeight() - PADDING);

        width = Math.max(width, MIN_WIDTH);
        height = Math.max(height, MIN_HEIGHT);

        stage.setWidth(width);
        stage.setHeight(height);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        centerStage(stage);
    }

    public static void adjustStageSize(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = Math.min(visualBounds.getWidth() * SCREEN_WIDTH_PERCENTAGE, visualBounds.getWidth() - PADDING);
        double maxHeight = Math.min(visualBounds.getHeight() * SCREEN_HEIGHT_PERCENTAGE, visualBounds.getHeight() - PADDING);

        stage.setWidth(Math.min(stage.getWidth(), maxWidth));
        stage.setHeight(Math.min(stage.getHeight(), maxHeight));

        centerStage(stage);
    }

    public static void centerStage(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((visualBounds.getWidth() - stage.getWidth()) / 2 + visualBounds.getMinX());
        stage.setY((visualBounds.getHeight() - stage.getHeight()) / 2 + visualBounds.getMinY());
    }

    public static void updateMainStage(Stage stage, Scene newScene) {
        stage.setScene(newScene);
        adjustStageSize(stage);
        centerStage(stage);
    }

    public static void applyHeaderConstraints(Region headerContent) {
        headerContent.setMaxWidth(HEADER_MAX_WIDTH);
        headerContent.prefWidthProperty().bind(
                headerContent.getScene().widthProperty().multiply(SCREEN_WIDTH_PERCENTAGE)
        );
    }
}
package ctgraphdep.views;

import ctgraphdep.controllers.BaseController;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.WindowUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public abstract class BasePage extends Application {

    protected abstract String getFxmlPath();
    protected abstract String getTitle();

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource(getFxmlPath());
            if (fxmlUrl == null) {
                LoggerUtil.error("FXML file not found: " + getFxmlPath());
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get the controller and initialize it with ServiceFactory
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).initializeServices(ServiceFactory.getInstance());
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.setTitle(getTitle());

            // Set initial size and position
            WindowUtil.initializeMainStage(primaryStage);

            // Show the stage
            primaryStage.show();

            // Adjust size and apply constraints after showing to ensure proper layout
            Platform.runLater(() -> {
                WindowUtil.adjustStageSize(primaryStage);
                WindowUtil.centerStage(primaryStage);

                // Apply constraints to the header content
                Region headerContent = (Region) scene.lookup(".header-content");
                if (headerContent != null) {
                    WindowUtil.applyHeaderConstraints(headerContent);
                }
            });

        } catch (IOException e) {
            LoggerUtil.error("Error BasePage: " + e.getMessage(), e);
        }
    }
}
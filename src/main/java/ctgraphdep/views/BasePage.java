package ctgraphdep.views;

import ctgraphdep.controllers.BaseController;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.WindowUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

            WindowUtil.initializeMainStage(primaryStage);
            WindowUtil.updateMainStage(primaryStage, scene);

            primaryStage.setTitle(getTitle());
            primaryStage.show();
        } catch (IOException e) {
            LoggerUtil.error("Error BasePage: " + e.getMessage(), e);
        }
    }
}
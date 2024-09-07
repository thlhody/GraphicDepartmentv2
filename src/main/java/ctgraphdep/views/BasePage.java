package ctgraphdep.views;

import ctgraphdep.controllers.BaseController;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
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
                LoggerUtil.error(getClass(),"FXML file not found: " + getFxmlPath());
                return; // Exit the method if FXML is not found
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Initialize controller with ServiceFactory
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).initializeServices(ServiceFactory.getInstance());
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(getTitle());
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            LoggerUtil.error(getClass(),"Error in BasePage: " + e.getMessage(), e);
        }
    }
}
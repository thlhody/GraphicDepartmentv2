package cottontex.graphdep.views;

import cottontex.graphdep.utils.LoggerUtil;
import cottontex.graphdep.utils.WindowUtil;
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
                throw new IOException("FXML file not found: " + getFxmlPath());
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
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
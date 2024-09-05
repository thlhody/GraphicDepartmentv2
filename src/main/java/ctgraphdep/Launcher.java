package ctgraphdep;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.views.BasePage;
import javafx.stage.Stage;

public class Launcher extends BasePage {

    @Override
    protected String getFxmlPath() {
        return AppPaths.LAUNCHER;
    }

    @Override
    protected String getTitle() {
        return "Graphic Department Login";
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the ServiceFactory before loading the FXML
        ServiceFactory.getInstance().initialize(primaryStage);

        // Call the parent's start method to load the FXML and set up the scene
        super.start(primaryStage);
    }

    @Override
    public void stop() {
        // Add any cleanup code here if needed
    }

    public static void main(String[] args) {
        launch(args);
    }
}
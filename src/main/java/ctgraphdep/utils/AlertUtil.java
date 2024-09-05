package ctgraphdep.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertUtil {

    public static void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

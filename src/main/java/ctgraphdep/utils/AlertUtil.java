package ctgraphdep.utils;

import ctgraphdep.constants.AppPaths;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Objects;
import java.util.Optional;

public class AlertUtil {

    public static void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource(AppPaths.ALERT_CSS)).toExternalForm());
            alert.showAndWait();
        });
    }

    public static Optional<ButtonType> showConfirmationDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }
}

package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseDialogController extends BaseController {

    protected Stage dialogStage;

    @FXML
    protected Button refreshButton;
    @FXML
    protected ImageView refreshIcon;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        this.serviceFactory = serviceFactory;
        LoggerUtil.info("BaseDialogController services initialized: " + serviceFactory.isInitialized());
    }

    @FXML
    protected void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
            LoggerUtil.info("Dialog window closed!");
        } else {
            LoggerUtil.error("Attempted to close dialog, but dialogStage is null");
        }
    }

    protected Stage openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            BaseDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            controller.setDialogStage(stage);
            controller.initializeServices(serviceFactory);

            // Apply CSS before showing the dialog
            controller.applyCss(scene);

            stage.showAndWait();

            return stage;
        } catch (IOException e) {
            LoggerUtil.error("Error opening dialog: " + e.getMessage(), e);
            return null;
        }
    }

    protected abstract void initializeDialog();

    protected abstract void applyCss(Scene scene);

    public abstract void setDialogStage(Stage stage);

    protected abstract void refreshContent();

    protected void setupRefreshButton() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshContent());
            refreshButton.setTooltip(new Tooltip("Refresh"));
            LoggerUtil.info("Refresh button set up successfully");
        } else {
            LoggerUtil.info("Refresh button not present in this dialog");
        }
    }

    protected void setRefreshButtonImage(String imagePath) {
        if (serviceFactory.isInitialized() && refreshIcon != null) {
            Image refreshImage = serviceFactory.getDialogService().loadImage(imagePath);
            if (refreshImage != null) {
                refreshIcon.setImage(refreshImage);
                LoggerUtil.info("Refresh icon set successfully");
            } else {
                LoggerUtil.error("Failed to load refresh icon image");
            }
        } else {
            LoggerUtil.error("ServiceFactory not initialized or Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }
}
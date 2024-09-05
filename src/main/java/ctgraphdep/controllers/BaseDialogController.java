package cottontex.graphdep.controllers;

import cottontex.graphdep.services.DialogService;
import cottontex.graphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public abstract class BaseDialogController extends BaseController {

    protected Stage dialogStage;
    protected DialogService dialogService;

    @FXML
    protected Button refreshButton;
    @FXML
    protected ImageView refreshIcon;
    @FXML
    protected ImageView dialogImage;

    public abstract void setDialogStage(Stage stage);

    @FXML
    void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
            LoggerUtil.info("Dialog window closed!");
        } else {
            LoggerUtil.error("Attempted to close dialog, but dialogStage is null");
        }
    }

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
        if (refreshIcon != null) {
            Image refreshImage = dialogService.loadImage(imagePath);
            if (refreshImage != null) {
                refreshIcon.setImage(refreshImage);
                LoggerUtil.info("Refresh icon set successfully");
            } else {
                LoggerUtil.error("Failed to load refresh icon image");
            }
        } else {
            LoggerUtil.error("Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }


}
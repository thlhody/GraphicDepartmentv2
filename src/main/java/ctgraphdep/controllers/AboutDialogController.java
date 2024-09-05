package cottontex.graphdep.controllers;

import cottontex.graphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;

public class AboutDialogController extends BaseDialogController {

    @Getter
    @FXML
    private ImageView dialogImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label copyrightLabel;

    @Override
    protected void refreshContent() {
        // No content to refresh in About dialog
        LoggerUtil.info("Refresh not applicable for About dialog");
    }

    public void setContent(String title, String version, String copyright) {
        titleLabel.setText(title);
        versionLabel.setText(version);
        copyrightLabel.setText(copyright);
        LoggerUtil.info("About dialog content set: Title=" + title + ", Version=" + version);
    }

    @Override
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        LoggerUtil.info("Dialog stage set for AboutDialogController");
    }

    @FXML
    @Override
    protected void closeDialog() {
        super.closeDialog();
    }

    @FXML
    public void initialize() {
        super.initialize();
        // Any additional initialization for AboutDialogController
        LoggerUtil.info("AboutDialogController initialized");
    }

}
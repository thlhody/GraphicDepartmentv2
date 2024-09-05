package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class AboutDialogController extends BaseDialogController {

    @FXML
    private ImageView dialogImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label copyrightLabel;

    @FXML
    @Override
    public void closeDialog() {
        LoggerUtil.info("Closing About dialog");
        super.closeDialog();
    }

    @Override
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        LoggerUtil.info("Dialog stage set for AboutDialogController");
    }

    @Override
    protected void refreshContent() {
        LoggerUtil.info("Refresh not applicable for About dialog");
    }

    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        LoggerUtil.info("Services initialized in AboutDialogController");
        initializeDialog();
    }

    @Override
    protected void initializeDialog() {
        LoggerUtil.info("Initializing About dialog");
        setDialogImage(AppPaths.DIALOG_BOX_IMAGE);
        setContent("Creative Time And Task Tracker", "Version 2.0", "© 2024 thlhody");
        setupImageAnimation();
        LoggerUtil.info("AboutDialogController initialized");
    }

    @Override
    protected void applyCss(Scene scene) {
        URL cssResource = getClass().getResource(AppPaths.ABOUT_DIALOG_CSS);
        if (cssResource == null) {
            LoggerUtil.error("CSS file not found: " + AppPaths.ABOUT_DIALOG_CSS);
            return;
        }
        String cssExternalForm = cssResource.toExternalForm();
        LoggerUtil.info("Loading CSS from: " + cssExternalForm);
        scene.getStylesheets().add(cssExternalForm);

    }

    private void setContent(String title, String version, String copyright) {
        if (titleLabel != null) {
            titleLabel.setText(title);
            LoggerUtil.info("Title set: " + title);
        } else {
            LoggerUtil.error("titleLabel is null");
        }

        if (versionLabel != null) {
            versionLabel.setText(version);
            LoggerUtil.info("Version set: " + version);
        } else {
            LoggerUtil.error("versionLabel is null");
        }

        if (copyrightLabel != null) {
            copyrightLabel.setText(copyright);
            LoggerUtil.info("Copyright set: " + copyright);
        } else {
            LoggerUtil.error("copyrightLabel is null");
        }
    }

    private void setDialogImage(String imagePath) {
        if (dialogImage == null) {
            LoggerUtil.error("dialogImage is null. Check FXML file and controller initialization.");
            return;
        }

        if (serviceFactory == null || !serviceFactory.isInitialized()) {
            LoggerUtil.error("ServiceFactory is not initialized. Ensure initializeServices is called.");
            return;
        }

        Image image = serviceFactory.getDialogService().loadImage(imagePath);
        if (image != null) {
            dialogImage.setImage(image);
            // Ensure the image fits within the ImageView
            dialogImage.setFitWidth(100);
            dialogImage.setFitHeight(100);
            dialogImage.setPreserveRatio(true);
            LoggerUtil.info("Dialog image set successfully with path: " + imagePath);
        } else {
            LoggerUtil.error("Failed to load image: " + imagePath);
        }
    }

    public static void openAboutDialog(ServiceFactory serviceFactory) {
        AboutDialogController controller = new AboutDialogController();
        controller.serviceFactory = serviceFactory;
        Stage stage = controller.openDialog(AppPaths.ABOUT_DIALOG, "About");
        if (stage != null) {
            Platform.runLater(() -> {
                controller.applyCss(stage.getScene());
                LoggerUtil.info("Attempted to apply CSS to About dialog");
            });
        } else {
            LoggerUtil.error("Failed to open About dialog");
        }
    }

    private void setupImageAnimation() {
        if (dialogImage != null) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(10), dialogImage);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
            rotateTransition.setAutoReverse(false);
            rotateTransition.play();
            LoggerUtil.info("Image rotation animation set up");
        } else {
            LoggerUtil.error("dialogImage is null, cannot set up animation");
        }
    }
}
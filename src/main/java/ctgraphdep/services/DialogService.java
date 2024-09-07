package ctgraphdep.services;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.controllers.BaseDialogController;
import ctgraphdep.utils.LoggerUtil;
import javafx.animation.RotateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DialogService {
    private final ServiceFactory serviceFactory;

    public DialogService(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public Image loadImage(String imagePath) {
        try {
            LoggerUtil.info(getClass(),"Loading image: " + imagePath);
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                return new Image(imageStream);
            } else {
                LoggerUtil.error(getClass(),"Failed to load image: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            LoggerUtil.error(getClass(),"Error loading image: " + e.getMessage(), e);
            return null;
        }
    }

    public void setDialogImage(ImageView dialogImage, String imagePath) {
        if (dialogImage == null) {
            LoggerUtil.error(getClass(),"dialogImage is null. Check FXML file and controller initialization.");
            return;
        }

        Image image = loadImage(imagePath);
        if (image != null) {
            dialogImage.setImage(image);
            dialogImage.setFitWidth(100);
            dialogImage.setFitHeight(100);
            dialogImage.setPreserveRatio(true);
            LoggerUtil.info(getClass(),"Dialog image set successfully with path: " + imagePath);
        } else {
            LoggerUtil.error(getClass(),"Failed to load image: " + imagePath);
        }
    }

    public void setupImageAnimation(ImageView dialogImage) {
        if (dialogImage != null) {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(10), dialogImage);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
            rotateTransition.setAutoReverse(false);
            rotateTransition.play();
            LoggerUtil.info(getClass(),"Image rotation animation set up");
        } else {
            LoggerUtil.error(getClass(),"dialogImage is null, cannot set up animation");
        }
    }

    public String getAboutDialogCssPath() {
        return AppPaths.ABOUT_DIALOG_CSS;
    }

    public String getDialogBoxImagePath() {
        return AppPaths.DIALOG_BOX_IMAGE;
    }

    public String getAboutDialogPath() {
        return AppPaths.ABOUT_DIALOG;
    }

    public String getStatusDialogPath() {
        return AppPaths.USER_STATUS_DIALOG;
    }

    public void openDialog(String fxmlPath, String title, BaseDialogController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            BaseDialogController dialogController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            dialogController.setDialogStage(stage);
            dialogController.initializeServices(serviceFactory);
            applyCss(scene, dialogController);
            stage.showAndWait();

        } catch (IOException e) {
            LoggerUtil.error(getClass(),"Error opening dialog: " + e.getMessage(), e);
        }
    }

    public void applyCss(Scene scene, BaseDialogController controller) {
        URL cssResource = controller.getClass().getResource(getAboutDialogCssPath());
        if (cssResource == null) {
            LoggerUtil.error(getClass(),"CSS file not found: " + getAboutDialogCssPath());
            return;
        }
        String cssExternalForm = cssResource.toExternalForm();
        LoggerUtil.info(getClass(),"Loading CSS from: " + cssExternalForm);
        scene.getStylesheets().add(cssExternalForm);
    }

    public void setupRefreshButton(Button refreshButton, Runnable refreshAction) {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshAction.run());
            refreshButton.setTooltip(new Tooltip("Refresh"));
            LoggerUtil.info(getClass(),"Refresh button set up successfully");
        } else {
            LoggerUtil.info(getClass(),"Refresh button not present in this dialog");
        }
    }

    public void setRefreshButtonImage(ImageView refreshIcon, String imagePath) {
        if (refreshIcon != null) {
            Image refreshImage = loadImage(imagePath);
            if (refreshImage != null) {
                refreshIcon.setImage(refreshImage);
                LoggerUtil.info(getClass(),"Refresh icon set successfully");
            } else {
                LoggerUtil.error(getClass(),"Failed to load refresh icon image");
            }
        } else {
            LoggerUtil.error(getClass(),"Refresh icon ImageView is null in setRefreshButtonImage method");
        }
    }

    public void closeDialog(Stage dialogStage) {
        if (dialogStage != null) {
            dialogStage.close();
            LoggerUtil.info(getClass(),"Dialog window closed!");
        } else {
            LoggerUtil.error(getClass(),"Attempted to close dialog, but dialogStage is null");
        }
    }

    public void setAboutDialogContent(Label titleLabel, Label versionLabel, Label copyrightLabel) {
        setLabelText(titleLabel, "Creative Time And Task Tracker");
        setLabelText(versionLabel, "Version 2.0");
        setLabelText(copyrightLabel, "© 2024 thlhody");
    }

    private void setLabelText(Label label, String text) {
        if (label != null) {
            label.setText(text);
            LoggerUtil.info(getClass(),label.getId() + " set: " + text);
        } else {
            LoggerUtil.error(getClass(),"Label is null");
        }
    }
}
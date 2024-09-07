package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class AboutDialogController extends BaseDialogController {

    @FXML
    private ImageView dialogImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private Label copyrightLabel;

    @Override
    protected void initializeDialog() {
        LoggerUtil.info(getClass(),"Initializing About dialog");
        serviceFactory.getDialogService().setDialogImage(dialogImage, serviceFactory.getDialogService().getDialogBoxImagePath());
        serviceFactory.getDialogService().setAboutDialogContent(titleLabel, versionLabel, copyrightLabel);
        serviceFactory.getDialogService().setupImageAnimation(dialogImage);
        LoggerUtil.info(getClass(),"AboutDialogController initialized");
    }

    @Override
    protected void refreshContent() {
        LoggerUtil.info(getClass(),"Refresh not applicable for About dialog");
    }

    public static void openAboutDialog(ServiceFactory serviceFactory) {
        serviceFactory.getDialogService().openDialog(serviceFactory.getDialogService().getAboutDialogPath(), "About", new AboutDialogController());
    }
}
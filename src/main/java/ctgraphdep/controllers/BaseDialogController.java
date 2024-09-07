package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public abstract class BaseDialogController {

    protected ServiceFactory serviceFactory;
    protected Stage dialogStage;

    @FXML
    protected Button refreshButton;
    @FXML
    protected ImageView refreshIcon;

    @FXML
    public void initializeServices(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        LoggerUtil.info(getClass(),"BaseDialogController services initialized: " + serviceFactory.isInitialized());
        initializeDialog();
    }

    @FXML
    protected void closeDialog() {
        serviceFactory.getDialogService().closeDialog(dialogStage);
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        LoggerUtil.info(getClass(),"Dialog stage set for " + this.getClass().getSimpleName());
    }

    protected abstract void initializeDialog();

    protected abstract void refreshContent();

    protected void setupRefreshButton() {
        serviceFactory.getDialogService().setupRefreshButton(refreshButton, this::refreshContent);
    }

    protected void setRefreshButtonImage() {
        serviceFactory.getDialogService().setRefreshButtonImage(refreshIcon, ctgraphdep.constants.AppPaths.REFRESH_ICON);
    }
}
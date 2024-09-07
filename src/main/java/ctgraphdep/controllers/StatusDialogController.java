package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class StatusDialogController extends BaseDialogController {

    @FXML private VBox userStatusBox;

    @Override
    protected void initializeDialog() {
        LoggerUtil.info(getClass(),"Initializing Status dialog");
        setupRefreshButton();
        setRefreshButtonImage();
        refreshContent();
        LoggerUtil.info(getClass(),"StatusDialogController initialized");
    }

    @Override
    protected void refreshContent() {
        LoggerUtil.info(getClass(),"Refreshing status content");
        serviceFactory.getStatusDialogService().updateUserStatusList(userStatusBox);
    }

    public static void openStatusDialog(ServiceFactory serviceFactory) {
        serviceFactory.getDialogService().openDialog(serviceFactory.getDialogService().getStatusDialogPath(), "User Status", new StatusDialogController());
    }
}
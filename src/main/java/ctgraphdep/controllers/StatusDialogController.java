package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class StatusDialogController extends BaseDialogController {

    @FXML private VBox userStatusBox;

    @Override
    protected void initializeDialog() {
        LoggerUtil.info("Initializing Status dialog");
        setupRefreshButton();
        setRefreshButtonImage(AppPaths.REFRESH_ICON);
        refreshContent();
        LoggerUtil.info("StatusDialogController initialized");
    }

    @Override
    protected void refreshContent() {
        LoggerUtil.info("Refreshing status content");
        serviceFactory.getStatusDialogService().updateUserStatusList(userStatusBox);
    }

    public static void openStatusDialog(ServiceFactory serviceFactory) {
        serviceFactory.getDialogService().openDialog(
                serviceFactory.getDialogService().getStatusDialogPath(),
                "User Status",
                new StatusDialogController()
        );
    }
}
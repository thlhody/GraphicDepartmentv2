package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController extends BaseController {

    @FXML
    private Button workIntervalButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button statusButton;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        this.serviceFactory = serviceFactory;
        LoggerUtil.error("AdminController service intialized"+ serviceFactory.isInitialized());
        setCurrentFXMLPath(AppPaths.ADMIN_PAGE_LAYOUT);
        setupLogoImage();
    }

    @FXML
    protected void onStatusButton() {
        StatusDialogController.openStatusDialog(serviceFactory);
    }

    @FXML
    public void onSettingsButton() {
        serviceFactory.getNavigationService().toAdminSettings();
    }

    @FXML
    public void onWorkIntervalButton() {
        serviceFactory.getNavigationService().toAdminWorkInterval();
    }
}

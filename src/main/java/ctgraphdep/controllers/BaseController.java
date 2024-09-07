package ctgraphdep.controllers;

import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class BaseController {

    protected ServiceFactory serviceFactory;
    protected String currentFXMLPath;

    @FXML
    protected ImageView logoImage;
    @FXML
    protected ImageView mainImage;

    @FXML
    public void initializeServices(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        setupLogoImage();
        if (!serviceFactory.isInitialized()) {
            LoggerUtil.error(getClass(),"ServiceFactory is not initialized in BaseController");
        }
    }

    @FXML
    protected void onLogoutButton() {
        if (serviceFactory != null) {
            serviceFactory.getNavigationService().logout();
        } else {
            LoggerUtil.error(getClass(),"ServiceFactory is null in BaseController");
        }
    }

    @FXML
    protected void onBackButton() {
        if (serviceFactory != null && currentFXMLPath != null) {
            serviceFactory.getNavigationService().goBack(currentFXMLPath);
        } else {
            LoggerUtil.error(getClass(),"ServiceFactory is null or currentFXMLPath is not set in BaseController");
        }
    }

    protected void setCurrentFXMLPath(String path) {
        this.currentFXMLPath = path;
    }

    protected void setupLogoImage() {
        if (logoImage != null && serviceFactory != null) {
            logoImage.setFitWidth(150);
            logoImage.setFitHeight(70);
            logoImage.setPreserveRatio(true);
            serviceFactory.getLogoService().setHeaderLogo(logoImage);
            LoggerUtil.info(getClass(),"Logo image set successfully in " + getClass().getSimpleName());
        } else {
            LoggerUtil.warn(getClass(),"logoImage is null or serviceFactory is not initialized in " + getClass().getSimpleName());
        }
    }
}

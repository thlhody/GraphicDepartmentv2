package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.services.ServiceFactory;
import ctgraphdep.utils.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.JsonNode;

public class StatusDialogController extends BaseDialogController {

    @FXML private VBox userStatusBox;

    @FXML
    @Override
    public void closeDialog() {
        LoggerUtil.info("Closing Status dialog");
        super.closeDialog();
    }

    @Override
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        LoggerUtil.info("Dialog stage set for StatusDialogController");
    }

    @Override
    protected void refreshContent() {
        LoggerUtil.info("Refreshing status content");
        updateUserStatusList();
    }

    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        LoggerUtil.info("Services initialized in StatusDialogController");
        initializeDialog();
    }

    @Override
    protected void applyCss(Scene scene) {

    }

    @Override
    protected void initializeDialog() {
        LoggerUtil.info("Initializing Status dialog");
        setupRefreshButton();
        setRefreshButtonImage(AppPaths.REFRESH_ICON);
        refreshContent();
        LoggerUtil.info("StatusDialogController initialized");
    }

    private void updateUserStatusList() {
        LoggerUtil.info("Updating user status list");
        if (userStatusBox == null) {
            LoggerUtil.error("userStatusBox is null");
            return;
        }
        userStatusBox.getChildren().clear();
        JsonNode usersStatus = serviceFactory.getStatusDialogService().getUsersStatus();
        LoggerUtil.info("Number of users: " + usersStatus.size());
        for (JsonNode userStatus : usersStatus) {
            String statusText = serviceFactory.getStatusDialogService().formatUserStatus(userStatus);
            HBox userRow = createUserStatusRow(statusText, userStatus.get("status").asText());
            userStatusBox.getChildren().add(userRow);
        }
        LoggerUtil.info("User status list updated. Number of rows: " + userStatusBox.getChildren().size());
    }

    private HBox createUserStatusRow(String statusText, String status) {
        HBox row = new HBox(10);
        row.getStyleClass().add("user-status-row");

        Circle statusCircle = new Circle(5);
        statusCircle.setFill(status.equals("Online") ? Color.GREEN : Color.RED);

        Label statusLabel = new Label(statusText);
        statusLabel.getStyleClass().add("user-status-text");

        row.getChildren().addAll(statusCircle, statusLabel);
        return row;
    }

    public static void openStatusDialog(ServiceFactory serviceFactory) {
        StatusDialogController controller = new StatusDialogController();
        controller.serviceFactory = serviceFactory;
        controller.openDialog(AppPaths.USER_STATUS_DIALOG, "User Status");
    }
}
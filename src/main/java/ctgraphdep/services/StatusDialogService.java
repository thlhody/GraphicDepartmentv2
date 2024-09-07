package ctgraphdep.services;

import com.fasterxml.jackson.databind.JsonNode;
import ctgraphdep.constants.JsonPaths;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusDialogService {

    public JsonNode getUsersStatus() {
        JsonNode rootNode = JsonUtils.readJsonNode(JsonPaths.getUserStatusJson());
        LoggerUtil.info(getClass(),"Users status read successfully. Number of users: " + rootNode.size());
        return rootNode;
    }

    public String formatUserStatus(JsonNode userStatus) {

        LocalDateTime activityTime = LocalDateTime.parse(userStatus.get("lastActivity").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String formattedDate = activityTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedTime = activityTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String formattedStatus = String.format("%s (%s) :: %s - %s", userStatus.get("name").asText(), userStatus.get("status").asText(), formattedDate, formattedTime);

        LoggerUtil.info(getClass(),"Formatted user status: " + formattedStatus);
        return formattedStatus;
    }

    public synchronized void updateUserStatus(int userId, String status) {
        boolean success = JsonUtils.updateUserStatusInJson(userId, status, JsonPaths.getUserStatusJson());
        if (success) {
            LoggerUtil.info(getClass(),"Updated status for user " + userId + " to " + status);
        } else {
            LoggerUtil.error(getClass(),"Failed to update status for user " + userId);
        }
    }

    public void updateUserStatusList(VBox userStatusBox) {
        if (userStatusBox == null) {
            LoggerUtil.error(getClass(),"userStatusBox is null");
            return;
        }
        userStatusBox.getChildren().clear();
        JsonNode usersStatus = getUsersStatus();
        LoggerUtil.info(getClass(),"Number of users: " + usersStatus.size());
        for (JsonNode userStatus : usersStatus) {
            String statusText = formatUserStatus(userStatus);
            HBox userRow = createUserStatusRow(statusText, userStatus.get("status").asText());
            userStatusBox.getChildren().add(userRow);
        }
        LoggerUtil.info(getClass(),"User status list updated. Number of rows: " + userStatusBox.getChildren().size());
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
}
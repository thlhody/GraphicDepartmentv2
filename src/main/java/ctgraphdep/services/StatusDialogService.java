package ctgraphdep.services;

import com.fasterxml.jackson.databind.JsonNode;
import ctgraphdep.constants.JsonPaths;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusDialogService {

    public JsonNode getUsersStatus() {
        JsonNode rootNode = JsonUtils.readJsonNode(JsonPaths.USER_STATUS_JSON);
        LoggerUtil.info("Users status read successfully. Number of users: " + rootNode.size());
        return rootNode;
    }

    public String formatUserStatus(JsonNode userStatus) {
        String name = userStatus.get("name").asText();
        String status = userStatus.get("status").asText();
        String lastActivity = userStatus.get("lastActivity").asText();

        LocalDateTime activityTime = LocalDateTime.parse(lastActivity, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String formattedDate = activityTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedTime = activityTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        String formattedStatus = String.format("%s (%s) :: %s - %s", name, status, formattedDate, formattedTime);
        LoggerUtil.info("Formatted user status: " + formattedStatus);
        return formattedStatus;
    }

    public synchronized void updateUserStatus(int userId, String status) {
        boolean success = JsonUtils.updateUserStatusInJson(userId, status, JsonPaths.USER_STATUS_JSON);
        if (success) {
            LoggerUtil.info("Updated status for user " + userId + " to " + status);
        } else {
            LoggerUtil.error("Failed to update status for user " + userId);
        }
    }
}
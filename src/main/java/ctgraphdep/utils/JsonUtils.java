package ctgraphdep.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctgraphdep.models.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> List<T> readListFromJson(String filePath, Class<T> clazz) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static <T> void writeListToJson(String filePath, List<T> data) throws IOException {
        objectMapper.writeValue(new File(filePath), data);
    }

    public static List<UserStatus> readUserStatusFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info(JsonUtils.class,"User status file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info(JsonUtils.class,"User status file is empty: " + filePath);
                return new ArrayList<>();
            }
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, UserStatus.class));
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"ReadUserStatusFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeUserStatusToJson(List<UserStatus> userStatuses, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), userStatuses);
            return true;
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"WriteUserStatusToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static List<WorkTimeTable> readWorkTimesFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info(JsonUtils.class,"Work times file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info(JsonUtils.class,"Work times file is empty: " + filePath);
                return new ArrayList<>();
            }
            return readListFromJson(filePath, WorkTimeTable.class);
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"ReadWorkTimeFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeWorkTimesToJson(List<WorkTimeTable> workTimes, String filePath) {
        try {
            writeListToJson(filePath, workTimes);
            return true;
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"WriteWorkTimesToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static JsonNode readJsonNode(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info(JsonUtils.class,"JSON file does not exist: " + filePath);
                return objectMapper.createArrayNode();
            }
            return objectMapper.readTree(file);
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"ReadJsonNode error: " + e.getMessage(), e);
            return objectMapper.createArrayNode();
        }
    }

    public static boolean writeJsonNode(JsonNode node, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), node);
            return true;
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"WriteJsonNode error: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean updateUserStatusInJson(int userId, String status, String filePath) {
        try {
            ArrayNode usersStatus = (ArrayNode) readJsonNode(filePath);
            boolean userFound = false;
            for (int i = 0; i < usersStatus.size(); i++) {
                ObjectNode userNode = (ObjectNode) usersStatus.get(i);
                if (userNode.get("userId").asInt() == userId) {
                    userNode.put("status", status);
                    userNode.put("lastActivity", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    userFound = true;
                    break;
                }
            }
            if (!userFound) {
                LoggerUtil.warn(JsonUtils.class,"User not found in status file: " + userId);
                return false;
            }
            return writeJsonNode(usersStatus, filePath);
        } catch (Exception e) {
            LoggerUtil.error(JsonUtils.class,"UpdateUserStatusInJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static PathConfig readPathConfigFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info(JsonUtils.class,"Path config file does not exist: " + filePath);
                return null;
            }
            return objectMapper.readValue(file, PathConfig.class);
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"ReadPathConfigFromJson error: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean writePathConfigToJson(PathConfig pathConfig, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), pathConfig);
            return true;
        } catch (IOException e) {
            LoggerUtil.error(JsonUtils.class,"WritePathConfigToJson error: " + e.getMessage(), e);
            return false;
        }
    }

}
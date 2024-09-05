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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

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

    public static List<Users> readUsersFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("Users file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info("Users file is empty: " + filePath);
                return new ArrayList<>();
            }
            return readListFromJson(filePath, Users.class);
        } catch (IOException e) {
            LoggerUtil.error("ReadUsersFromJson error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean writeUsersToJson(List<Users> users, String filePath) {
        try {
            writeListToJson(filePath, users);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteUsersToJson error: " + e.getMessage());
            return false;
        }
    }

    public static List<UserStatus> readUserStatusFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("User status file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info("User status file is empty: " + filePath);
                return new ArrayList<>();
            }
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, UserStatus.class));
        } catch (IOException e) {
            LoggerUtil.error("ReadUserStatusFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeUserStatusToJson(List<UserStatus> userStatuses, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), userStatuses);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteUserStatusToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static List<WorkTimeTable> readWorkTimesFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("Work times file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info("Work times file is empty: " + filePath);
                return new ArrayList<>();
            }
            return readListFromJson(filePath, WorkTimeTable.class);
        } catch (IOException e) {
            LoggerUtil.error("ReadWorkTimeFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeWorkTimesToJson(List<WorkTimeTable> workTimes, String filePath) {
        try {
            writeListToJson(filePath, workTimes);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteWorkTimesToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static List<LocalDate> readHolidaysFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("Holidays file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info("Holidays file is empty: " + filePath);
                return new ArrayList<>();
            }
            return readListFromJson(filePath, LocalDate.class);
        } catch (IOException e) {
            LoggerUtil.error("ReadHolidaysFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeHolidaysToJson(List<LocalDate> holidays, String filePath) {
        try {
            writeListToJson(filePath, holidays);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteHolidaysToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static WorkSessionStateUser readWorkSessionStateFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("Work session state file does not exist: " + filePath);
                return null;
            }
            if (file.length() == 0) {
                LoggerUtil.info("Work session state file is empty: " + filePath);
                return null;
            }
            return objectMapper.readValue(file, WorkSessionStateUser.class);
        } catch (IOException e) {
            LoggerUtil.error("ReadWorkSessionStateFromJson error: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean writeWorkSessionStateToJson(WorkSessionStateUser workSessionState, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), workSessionState);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteWorkSessionStateToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static List<MonthlyWorkSummary> readMonthlyWorkSummariesFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("Monthly work summaries file does not exist: " + filePath);
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                LoggerUtil.info("Monthly work summaries file is empty: " + filePath);
                return new ArrayList<>();
            }
            return readListFromJson(filePath, MonthlyWorkSummary.class);
        } catch (IOException e) {
            LoggerUtil.error("ReadMonthlyWorkSummariesFromJson error: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static boolean writeMonthlyWorkSummariesToJson(List<MonthlyWorkSummary> summaries, String filePath) {
        try {
            writeListToJson(filePath, summaries);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteMonthlyWorkSummariesToJson error: " + e.getMessage(), e);
            return false;
        }
    }

    public static JsonNode readJsonNode(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LoggerUtil.info("JSON file does not exist: " + filePath);
                return objectMapper.createArrayNode();
            }
            return objectMapper.readTree(file);
        } catch (IOException e) {
            LoggerUtil.error("ReadJsonNode error: " + e.getMessage(), e);
            return objectMapper.createArrayNode();
        }
    }

    public static boolean writeJsonNode(JsonNode node, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), node);
            return true;
        } catch (IOException e) {
            LoggerUtil.error("WriteJsonNode error: " + e.getMessage(), e);
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
                LoggerUtil.warn("User not found in status file: " + userId);
                return false;
            }
            return writeJsonNode(usersStatus, filePath);
        } catch (Exception e) {
            LoggerUtil.error("UpdateUserStatusInJson error: " + e.getMessage(), e);
            return false;
        }
    }
}
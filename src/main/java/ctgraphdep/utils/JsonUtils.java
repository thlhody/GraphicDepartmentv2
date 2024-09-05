package ctgraphdep.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkTimeTable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static <T> List<T> readListFromJson(String filePath, Class<T> clazz) throws IOException {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.readValue(new File(filePath), listType);
    }

    public static <T> void writeListToJson(String filePath, List<T> data) throws IOException {
        objectMapper.writeValue(new File(filePath), data);
    }

    public static List<Users> readUsersFromJson(String filePath) {
        try {
            return readListFromJson(filePath, Users.class);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static boolean writeUsersToJson(List<Users> users, String filePath) {
        try {
            writeListToJson(filePath, users);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<WorkTimeTable> readWorkTimesFromJson(String filePath) {
        try {
            return readListFromJson(filePath, WorkTimeTable.class);
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static boolean writeWorkTimesToJson(List<WorkTimeTable> workTimes, String filePath) {
        try {
            writeListToJson(filePath, workTimes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
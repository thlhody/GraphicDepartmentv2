package ctgraphdep.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ctgraphdep.models.WorkSessionStateUser;
import ctgraphdep.models.Users;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserSessionFileHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    static {
        // Configure LocalDateTime serializer to exclude nanoseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(formatter);
        objectMapper.registerModule(new JavaTimeModule().addSerializer(LocalDateTime.class, localDateTimeSerializer));
    }

    public static void saveUserSession(Users user, WorkSessionStateUser session) {
        String filePath = user.getPersonalFolderPath() + File.separator + "work_session_state_" + user.getUsername() + ".json";
        ensureUserFolderExists(user);

        try {
            objectMapper.writeValue(new File(filePath), session);
            LoggerUtil.info(UserSessionFileHandler.class,"Session saved for user: " + user.getUsername());
        } catch (IOException e) {
            LoggerUtil.error(UserSessionFileHandler.class,"Error saving session for user: " + user.getUsername(), e);
        }
    }

    public static WorkSessionStateUser readUserSession(Users user) {
        String filePath = user.getPersonalFolderPath() + File.separator + "work_session_state_" + user.getUsername() + ".json";
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            LoggerUtil.info(UserSessionFileHandler.class,"No existing session found for user: " + user.getUsername());
            return null;
        }

        try {
            return objectMapper.readValue(file, WorkSessionStateUser.class);
        } catch (IOException e) {
            LoggerUtil.error(UserSessionFileHandler.class,"Error reading session for user: " + user.getUsername(), e);
            return null;
        }
    }

    private static void ensureUserFolderExists(Users user) {
        File folder = new File(user.getPersonalFolderPath());
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                LoggerUtil.info(UserSessionFileHandler.class,"Created personal folder for user: " + user.getUsername());
            } else {
                LoggerUtil.error(UserSessionFileHandler.class,"Failed to create personal folder for user: " + user.getUsername());
            }
        }
    }
}
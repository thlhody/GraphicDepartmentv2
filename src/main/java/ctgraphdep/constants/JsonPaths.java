package ctgraphdep.constants;

import ctgraphdep.models.PathConfig;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;

import java.io.File;
import java.net.URISyntaxException;

public class JsonPaths {
    private static String ADMIN_PATH;
    private static String DATA_PATH;
    public static final String PATH_CONFIG_JSON = "path_config.json";
    private static final String DEFAULT_FOLDER_NAME = "default";
    private static File executableDirectory;

    static {
        initializeExecutableDirectory();
        initializePaths();
    }

    private static void initializeExecutableDirectory() {
        try {
            File jarFile = new File(JsonPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            executableDirectory = jarFile.getParentFile();
            LoggerUtil.info("Executable directory: " + executableDirectory.getAbsolutePath());
        } catch (URISyntaxException e) {
            LoggerUtil.error("Failed to get executable location. Using current directory.", e);
            executableDirectory = new File(System.getProperty("user.dir"));
        }
    }

    private static void initializePaths() {
        initializeAdminPath();
        loadPathConfig();
    }

    private static void initializeAdminPath() {
        File defaultFolder = new File(executableDirectory, DEFAULT_FOLDER_NAME);
        if (!defaultFolder.exists() && !defaultFolder.mkdir()) {
            LoggerUtil.warn("Failed to create default folder. Using executable location for admin path.");
            defaultFolder = executableDirectory;
        }
        ADMIN_PATH = defaultFolder.getAbsolutePath() + File.separator;
        LoggerUtil.info("Set ADMIN_PATH to: " + ADMIN_PATH);
    }

    public static void setDataPath(String newDataPath) {
        File newDataPathFile = new File(newDataPath);
        if (!newDataPathFile.exists() && !newDataPathFile.mkdirs()) {
            LoggerUtil.warn("Failed to create directory at " + newDataPath + ". Data path not changed.");
            return;
        }
        DATA_PATH = newDataPathFile.getAbsolutePath() + File.separator;
        savePathConfig();
        LoggerUtil.info("Data path updated and saved: " + DATA_PATH);
    }

    private static void loadPathConfig() {
        File configFile = new File(executableDirectory, PATH_CONFIG_JSON);
        LoggerUtil.info("Attempting to load path config from: " + configFile.getAbsolutePath());
        if (configFile.exists()) {
            PathConfig config = JsonUtils.readPathConfigFromJson(configFile.getAbsolutePath());
            if (config != null && config.getDataPath() != null && !config.getDataPath().isEmpty()) {
                File configDataPath = new File(config.getDataPath());
                if (configDataPath.exists() && configDataPath.isDirectory()) {
                    DATA_PATH = configDataPath.getAbsolutePath() + File.separator;
                    LoggerUtil.info("Loaded DATA_PATH from config: " + DATA_PATH);
                } else {
                    LoggerUtil.warn("Config specifies non-existent data path. Using admin path as fallback.");
                    DATA_PATH = ADMIN_PATH;
                }
            } else {
                LoggerUtil.warn("Invalid config in " + configFile.getAbsolutePath() + ". Using admin path as fallback.");
                DATA_PATH = ADMIN_PATH;
            }
        } else {
            LoggerUtil.info("No config file found. Using admin path as initial data path.");
            DATA_PATH = ADMIN_PATH;
        }
        LoggerUtil.info("Final DATA_PATH: " + DATA_PATH);
    }


    private static void savePathConfig() {
        PathConfig config = new PathConfig();
        config.setDataPath(DATA_PATH);
        File configFile = new File(executableDirectory, PATH_CONFIG_JSON);
        boolean saved = JsonUtils.writePathConfigToJson(config, configFile.getAbsolutePath());
        if (saved) {
            LoggerUtil.info("Saved path config to: " + configFile.getAbsolutePath());
        } else {
            LoggerUtil.error("Failed to save path config to: " + configFile.getAbsolutePath());
        }
    }

    public static String getAdminPath() {
        return ADMIN_PATH;
    }

    public static String getDataPath() {
        return DATA_PATH;
    }

    public static String getJsonUsers() {
        File dataPathUsers = new File(DATA_PATH, "users.json");
        if (dataPathUsers.exists()) {
            return dataPathUsers.getAbsolutePath();
        } else {
            LoggerUtil.warn("users.json not found in data path. Using admin path as fallback.");
            return ADMIN_PATH + "users.json";
        }
    }

    public static String getWorkIntervalJson() {
        return DATA_PATH + "work_interval.json";
    }

    public static String getUserStatusJson() {
        return DATA_PATH + "users_status.json";
    }
}
package ctgraphdep.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class LoggerUtil {

    private LoggerUtil() {
        // Private constructor to prevent instantiation
    }

    public static void initialize(Class<?> clazz, String additionalInfo) {
        Logger logger = LoggerFactory.getLogger(clazz);
        String message = "Initializing " + clazz.getSimpleName();
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            message += ": " + additionalInfo;
        }
        logger.info(message);
    }

    public static void logUserAction(String action, String username, String details) {
        Logger logger = LoggerFactory.getLogger("UserActions");
        MDC.put("username", username);
        try {
            logger.info("{} - {}", action, details);
        } finally {
            MDC.remove("username");
        }
    }

    public static void logControllerSwitch(Class<?> fromController, Class<?> toController, String username) {
        Logger logger = LoggerFactory.getLogger("ControllerSwitches");
        MDC.put("username", username);
        try {
            logger.info("Switching from {} to {}", fromController.getSimpleName(), toController.getSimpleName());
        } finally {
            MDC.remove("username");
        }
    }

    public static void info(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).info(message);
    }

    public static void warn(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).warn(message);
    }

    public static void error(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).error(message);
    }

    public static void error(Class<?> clazz, String message, Throwable throwable) {
        LoggerFactory.getLogger(clazz).error(message, throwable);
    }

    public static void debug(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).debug(message);
    }

    public static void trace(Class<?> clazz, String message) {
        LoggerFactory.getLogger(clazz).trace(message);
    }

    public static void logException(Class<?> clazz, String message, Exception e) {
        LoggerFactory.getLogger(clazz).error(message, e);
    }

    public static void logAndThrow(Class<?> clazz, String message, Exception e) throws RuntimeException {
        LoggerFactory.getLogger(clazz).error(message, e);
        throw new RuntimeException(message, e);
    }

    public static void error(String s) {

    }

    public static void info(String javaFXApplicationClassLoadedSuccessfully) {
    }
}
package ctgraphdep.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeProcessingUtils {

    public static String minutesToHHmm(int minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        long hours = duration.toHours();
        long remainingMinutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, remainingMinutes);
    }

    public static int calculateDurationInMinutes(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        return (int) duration.toMinutes();
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static int hhmmToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}
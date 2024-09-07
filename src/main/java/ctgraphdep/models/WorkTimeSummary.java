package ctgraphdep.models;

import java.time.Duration;

public class WorkTimeSummary {
    private final Duration totalWorked;
    private final Duration overtime;
    private final long timeOffDays;

    public WorkTimeSummary(Duration totalWorked, Duration overtime, long timeOffDays) {
        this.totalWorked = totalWorked;
        this.overtime = overtime;
        this.timeOffDays = timeOffDays;
    }

    public String getTotalHoursWorked() {
        return formatDuration(totalWorked);
    }

    public String getOvertimeHours() {
        return formatDuration(overtime);
    }

    public String getTimeOffDays() {
        return String.valueOf(timeOffDays);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }
}
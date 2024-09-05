package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import ctgraphdep.constants.WorkCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class MonthlyWorkSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("employeeId")
    private Integer employeeId;

    @JsonProperty("dailyHours")
    private Map<Integer, Double> dailyHours; // Day of month -> Hours worked

    @JsonProperty("timeOffTypes")
    private Map<Integer, String> timeOffTypes; // Day of month -> Time off type

    @JsonProperty("totalWorkedHours")
    private Double totalWorkedHours;

    @JsonProperty("daysWorked")
    private Integer daysWorked;


    public MonthlyWorkSummary(@JsonProperty("name") String name, @JsonProperty("employeeId") Integer employeeId) {
        this.name = name;
        this.employeeId = employeeId;
        this.dailyHours = new HashMap<>();
        this.timeOffTypes = new HashMap<>();
        this.totalWorkedHours = 0.0;
        this.daysWorked = 0;
    }

    public void addDailyHours(Integer day, Double hours) {
        dailyHours.put(day, hours);
        totalWorkedHours += hours;
        if (hours > 0) {
            daysWorked++;
        }
    }

    public void addTimeOffType(Integer day, String timeOffType) {
        timeOffTypes.put(day, timeOffType);
    }

    public Double getDay(Integer day) {
        return dailyHours.getOrDefault(day, null);
    }

    public String getTimeOffType(Integer day) {
        return timeOffTypes.getOrDefault(day, null);
    }

    public Map<String, Long> getDaysOffCounts() {
        return timeOffTypes.values().stream()
                .collect(Collectors.groupingBy(
                        timeOffType -> timeOffType,
                        Collectors.counting()
                ));
    }

    public String getTimeOffSummary() {
        Map<String, Long> counts = getDaysOffCounts();
        return String.format("%s: %d | %s: %d | %s: %d",
                WorkCode.TIME_OFF_CODE, counts.getOrDefault(WorkCode.TIME_OFF_CODE, 0L),
                WorkCode.MEDICAL_LEAVE_CODE, counts.getOrDefault(WorkCode.MEDICAL_LEAVE_CODE, 0L),
                WorkCode.NATIONAL_HOLIDAY_CODE, counts.getOrDefault(WorkCode.NATIONAL_HOLIDAY_CODE, 0L));
    }
}
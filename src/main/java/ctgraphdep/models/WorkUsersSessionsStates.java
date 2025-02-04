package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkUsersSessionsStates {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("sessionStatus")
    private String sessionStatus;

    @JsonProperty("lastActivity")
    private LocalDateTime lastActivity;

    @JsonProperty("dayStartTime")
    private LocalDateTime dayStartTime;

    @JsonProperty("currentStartTime")
    private LocalDateTime currentStartTime;

    @JsonProperty("lastPauseTime")
    private LocalDateTime lastPauseTime;

    @JsonProperty("totalWorkedMinutes")
    private Integer totalWorkedMinutes;

    @JsonProperty("lunchBreakDeducted")
    private Boolean lunchBreakDeducted;

    @JsonProperty("temporaryStops")
    private List<TemporaryStop> temporaryStops;

    @JsonProperty("totalTemporaryStopMinutes")
    private Integer totalTemporaryStopMinutes;

    @JsonProperty("temporaryStopCount")
    private Integer temporaryStopCount;

    @JsonProperty("workdayCompleted")
    private Boolean workdayCompleted;

    // Getters and setters
    // ...
}
package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkSessionStateUser {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("firstStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstStartTime;

    @JsonProperty("lastEndTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastEndTime;

    @JsonProperty("currentStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime currentStartTime;

    @JsonProperty("lastPauseTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastPauseTime;

    @JsonProperty("totalWorkedSeconds")
    private long totalWorkedSeconds;

    @JsonProperty("totalBreakSeconds")
    private long totalBreakSeconds;

    @JsonProperty("breakCount")
    private int breakCount;

    @JsonProperty("sessionState")
    private String sessionState;

    public void setFirstStartTime(LocalDateTime timeA) {
        this.firstStartTime = timeA != null ? timeA.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    public void setLastEndTime(LocalDateTime timeB) {
        this.lastEndTime = timeB != null ? timeB.truncatedTo(ChronoUnit.SECONDS) : null;
    }

    public void addWorkedTime(long seconds) {
        this.totalWorkedSeconds += seconds;
    }

    public void addBreakTime(long seconds) {
        this.totalBreakSeconds += seconds;
    }

    public void incrementBreakCount() {
        this.breakCount++;
    }
}
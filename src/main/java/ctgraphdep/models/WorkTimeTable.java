package ctgraphdep.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeTable {


    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("firstStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstStartTime;

    @JsonProperty("breaks")
    private Integer breaks;

    @JsonProperty("breaksTime")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime breaksTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("totalWorkedTime")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime totalWorkedTime;

    @JsonProperty("timeOffType")
    private String timeOffType;

    @JsonProperty("workDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    @JsonProperty("totalWorkedSeconds")
    private Long totalWorkedSeconds;

}
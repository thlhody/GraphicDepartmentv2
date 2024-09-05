package cottontex.graphdep.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
public class WorkTimeTable {
    private Integer id;
    private Integer userId;
    private Timestamp firstStartTime;
    private Integer breaks;
    private Time totalBreakTime;
    private Timestamp endStartTime;
    private Time totalWorkedTime;
    private String timeOffType;
    private Date workedDate;
    private Time totalWorkedSeconds;
}

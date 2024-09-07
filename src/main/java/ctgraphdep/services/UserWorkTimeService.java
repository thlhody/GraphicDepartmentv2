package ctgraphdep.services;

import ctgraphdep.models.WorkTimeSummary;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.constants.WorkCode;

import java.time.Duration;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

public class UserWorkTimeService {

    private final WorkSessionService workSessionService;

    public UserWorkTimeService(WorkSessionService workSessionService) {
        this.workSessionService = workSessionService;
    }

    public List<WorkTimeTable> loadUserWorkHours(int year, Month month, Integer userId) {
        List<WorkTimeTable> allWorkHours = workSessionService.getWorkTimeData(year, month.getValue());

        List<WorkTimeTable> userWorkHours = allWorkHours.stream()
                .filter(workTime -> workTime.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());

        LoggerUtil.info(getClass(),"Loaded " + userWorkHours.size() + " work hour entries for user " + userId);

        return userWorkHours;
    }

    public WorkTimeSummary calculateWorkTimeSummary(List<WorkTimeTable> workHours, int year, Month month) {
        Duration totalWorked = Duration.ZERO;
        long timeOffDays = 0;

        for (WorkTimeTable entry : workHours) {
            if (entry.getTotalWorkedSeconds() != null) {
                Duration workedDuration = Duration.ofSeconds(entry.getTotalWorkedSeconds());
                totalWorked = totalWorked.plus(workedDuration);
            }

            if (entry.getTimeOffType() != null && !entry.getTimeOffType().isEmpty()) {
                timeOffDays++;
            }
        }

        // Calculate expected work hours for the month

        Integer workingDaysInMonth = calculateWorkingDaysInMonth(YearMonth.of(year, month));
        Duration expectedMonthlyHours = Duration.ofMinutes((long) (workingDaysInMonth * WorkCode.FULL_WORKDAY_HOURS * 60));

        // Calculate overtime
        Duration overtime = totalWorked.compareTo(expectedMonthlyHours) > 0 ? totalWorked.minus(expectedMonthlyHours) : Duration.ZERO;

        // Adjust total worked hours to not exceed expected monthly hours
        Duration regularHours = totalWorked.compareTo(expectedMonthlyHours) > 0 ? expectedMonthlyHours : totalWorked;

        return new WorkTimeSummary(regularHours, overtime, timeOffDays);
    }

    private int calculateWorkingDaysInMonth(YearMonth yearMonth) {
        return (int) yearMonth.atDay(1).datesUntil(yearMonth.atEndOfMonth().plusDays(1))
                .filter(date -> date.getDayOfWeek().getValue() <= 5)
                .count();
    }
}
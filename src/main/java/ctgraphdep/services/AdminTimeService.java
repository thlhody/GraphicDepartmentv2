package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminTimeService {

    private final UserService userService;

    public AdminTimeService(UserService userService) {
        this.userService = userService;
    }

    public List<MonthlyWorkSummary> getMonthlyWorkSummary(int year, int month) {
        List<WorkTimeTable> workTimes = getWorkTimeData(year, month);
        List<Users> users = userService.getAllUsers();
        Map<Integer, MonthlyWorkSummary> summaryMap = new HashMap<>();

        LoggerUtil.info("Generating monthly summary for " + users.size() + " users");

        for (Users user : users) {
            MonthlyWorkSummary summary = new MonthlyWorkSummary(user.getName(), user.getEmployeeId());
            summaryMap.put(user.getUserId(), summary);
        }

        LoggerUtil.info("Processing " + workTimes.size() + " work time entries");

        for (WorkTimeTable workTime : workTimes) {
            MonthlyWorkSummary summary = summaryMap.get(workTime.getUserId());
            if (summary != null) {
                int day = workTime.getWorkDate().getDayOfMonth();
                double hoursWorked = workTime.getTotalWorkedSeconds() / 3600.0;
                String timeOffType = workTime.getTimeOffType();

                LoggerUtil.info("User " + workTime.getUserId() + " worked " + hoursWorked + " hours on day " + day);

                if (hoursWorked > 0) {
                    summary.addDailyHours(day, hoursWorked);
                } else if (timeOffType != null && !timeOffType.isEmpty()) {
                    summary.addTimeOffType(day, timeOffType);
                }
            } else {
                LoggerUtil.warn("No summary found for user ID: " + workTime.getUserId());
            }
        }

        List<MonthlyWorkSummary> result = new ArrayList<>(summaryMap.values());
        LoggerUtil.info("Generated " + result.size() + " monthly summaries");
        return result;
    }

    public List<WorkTimeTable> getWorkTimeData(int year, int month) {
        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.WORK_INTERVAL_JSON);
        LoggerUtil.info("Total work times read from JSON: " + allWorkTimes.size());

        List<WorkTimeTable> filteredWorkTimes = allWorkTimes.stream()
                .filter(wt -> {
                    LocalDate workDate = wt.getWorkDate();
                    boolean matches = workDate != null && workDate.getYear() == year && workDate.getMonthValue() == month;
                    LoggerUtil.info("Checking entry: " + wt + ", Matches filter: " + matches);
                    return matches;
                })
                .collect(Collectors.toList());

        LoggerUtil.info("Filtered work times: " + filteredWorkTimes.size());
        return filteredWorkTimes;
    }

    public boolean addNationalHoliday(LocalDate date) {
        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.WORK_INTERVAL_JSON);
        List<Users> allUsers = userService.getAllUsers();

        for (Users user : allUsers) {
            WorkTimeTable holiday = new WorkTimeTable(
                    user.getUserId(),
                    null,  // firstStartTime
                    0,     // breaks
                    null,  // breaksTime
                    null,  // endTime
                    null,  // totalWorkedTime
                    "SN",  // timeOffType
                    date,  // workDate
                    0L     // totalWorkedSeconds
            );

            // Remove any existing entry for this user on this date
            allWorkTimes.removeIf(wt -> wt.getUserId().equals(user.getUserId()) && wt.getWorkDate().equals(date));

            // Add the new holiday entry
            allWorkTimes.add(holiday);
        }

        boolean success = JsonUtils.writeWorkTimesToJson(allWorkTimes, JsonPaths.WORK_INTERVAL_JSON);
        if (success) {
            LoggerUtil.info("National holiday added successfully for date: " + date);
        } else {
            LoggerUtil.error("Failed to add national holiday for date: " + date);
        }
        return success;
    }
}
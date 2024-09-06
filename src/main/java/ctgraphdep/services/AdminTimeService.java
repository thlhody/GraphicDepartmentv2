package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AdminTimeService {

    private final UserService userService;
    private static final Set<String> NON_ADMIN_ROLES = new HashSet<>(Arrays.asList("USER", "USER_TEAM_LEADER"));

    public AdminTimeService(UserService userService) {
        this.userService = userService;
    }

    public List<MonthlyWorkSummary> getMonthlyWorkSummary(int year, int month) {
        List<WorkTimeTable> workTimes = getWorkTimeData(year, month);
        List<Users> nonAdminUsers = getNonAdminUsers();
        Map<Integer, MonthlyWorkSummary> summaryMap = new HashMap<>();

        LoggerUtil.info("Generating monthly summary for " + nonAdminUsers.size() + " non-admin users");

        for (Users user : nonAdminUsers) {
            MonthlyWorkSummary summary = new MonthlyWorkSummary(user.getName(), user.getEmployeeId());
            summaryMap.put(user.getUserId(), summary);
        }

        LoggerUtil.info("Processing " + workTimes.size() + " work time entries");

        for (WorkTimeTable workTime : workTimes) {
            MonthlyWorkSummary summary = summaryMap.get(workTime.getUserId());
            if (summary != null) {
                processWorkTimeEntry(workTime, summary);
            } else {
                LoggerUtil.warn("No summary found for user ID: " + workTime.getUserId() + ". This may be an admin or an unrecognized user.");
            }
        }

        List<MonthlyWorkSummary> result = new ArrayList<>(summaryMap.values());
        LoggerUtil.info("Generated " + result.size() + " monthly summaries (excluding admin)");
        return result;
    }

    private List<Users> getNonAdminUsers() {
        List<Users> nonAdminUsers = userService.getAllUsers().stream()
                .filter(user -> !isAdminUser(user))
                .collect(Collectors.toList());
        LoggerUtil.info("Retrieved " + nonAdminUsers.size() + " non-admin users");
        return nonAdminUsers;
    }

    private void processWorkTimeEntry(WorkTimeTable workTime, MonthlyWorkSummary summary) {
        int day = workTime.getWorkDate().getDayOfMonth();
        double hoursWorked = workTime.getTotalWorkedSeconds() / 3600.0;
        String timeOffType = workTime.getTimeOffType();

        LoggerUtil.info("Processing work time for User " + workTime.getUserId() + ": " + hoursWorked + " hours on day " + day);

        if (hoursWorked > 0) {
            summary.addDailyHours(day, hoursWorked);
        } else if (timeOffType != null && !timeOffType.isEmpty()) {
            summary.addTimeOffType(day, timeOffType);
        }
    }

    public List<WorkTimeTable> getWorkTimeData(int year, int month) {
        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());
        LoggerUtil.info("Total work times read from JSON: " + allWorkTimes.size());

        List<Integer> nonAdminUserIds = getNonAdminUsers().stream()
                .map(Users::getUserId)
                .collect(Collectors.toList());

        List<WorkTimeTable> filteredWorkTimes = allWorkTimes.stream()
                .filter(wt -> nonAdminUserIds.contains(wt.getUserId()))
                .filter(wt -> {
                    LocalDate workDate = wt.getWorkDate();
                    return workDate != null && workDate.getYear() == year && workDate.getMonthValue() == month;
                })
                .collect(Collectors.toList());

        LoggerUtil.info("Filtered work times (excluding admin): " + filteredWorkTimes.size());
        return filteredWorkTimes;
    }

    private boolean isAdminUser(Users user) {
        return !NON_ADMIN_ROLES.contains(user.getRole());
    }

    public List<MonthlyWorkSummary> refreshWorkTimeData(int year, int month) {
        List<MonthlyWorkSummary> summaries = getMonthlyWorkSummary(year, month);
        LoggerUtil.info("Refreshed work time data for " + year + "-" + month + ": " + summaries.size() + " entries (excluding admin)");
        return summaries;
    }


    public boolean addNationalHoliday(LocalDate date) {
        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());
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

        boolean success = JsonUtils.writeWorkTimesToJson(allWorkTimes, JsonPaths.getWorkIntervalJson());
        if (success) {
            LoggerUtil.info("National holiday added successfully for date: " + date);
        } else {
            LoggerUtil.error("Failed to add national holiday for date: " + date);
        }
        return success;
    }

}
package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.MonthlyWorkSummary;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AdminTimeService {

    private final UserService userService;
    private static final Set<String> NON_ADMIN_ROLES = new HashSet<>(Arrays.asList("USER", "USER_TEAM_LEADER"));

    public AdminTimeService(UserService userService) {
        this.userService = userService;
    }

    public List<MonthlyWorkSummary> getMonthlyWorkSummary(Integer year, Integer month) {
        Map<Integer, MonthlyWorkSummary> summaryMap = new HashMap<>();
        LoggerUtil.info(getClass(),"Generating monthly summary for " + getNonAdminUsers().size() + " non-admin users");

        for (Users user : getNonAdminUsers()) {
            MonthlyWorkSummary summary = new MonthlyWorkSummary(user.getName(), user.getEmployeeId());
            summaryMap.put(user.getUserId(), summary);
        }

        for (WorkTimeTable workTime : getWorkTimeData(year, month)) {
            MonthlyWorkSummary summary = summaryMap.get(workTime.getUserId());
            if (summary != null) {
                processWorkTimeEntry(workTime, summary);
            } else {
                LoggerUtil.warn(getClass(),"No summary found for user ID: " + workTime.getUserId() + ". This may be an admin or an unrecognized user.");
            }
        }

        List<MonthlyWorkSummary> result = new ArrayList<>(summaryMap.values());
        LoggerUtil.info(getClass(),"Generated " + result.size() + " monthly summaries (excluding admin)");
        return result;
    }

    private List<Users> getNonAdminUsers() {
        List<Users> nonAdminUsers = userService.getAllUsers().stream().filter(user -> !isAdminUser(user)).collect(Collectors.toList());
        LoggerUtil.info(getClass(),"Retrieved " + nonAdminUsers.size() + " non-admin users");
        return nonAdminUsers;
    }

    private void processWorkTimeEntry(WorkTimeTable workTime, MonthlyWorkSummary summary) {
        Integer day = workTime.getWorkDate().getDayOfMonth();
        Double hoursWorked = workTime.getTotalWorkedSeconds() / 3600.0;
        String timeOffType = workTime.getTimeOffType();

        LoggerUtil.info(getClass(),"Processing work time for User " + workTime.getUserId() + ": " + hoursWorked + " hours on day " + day);

        if (hoursWorked > 0) {
            summary.addDailyHours(day, hoursWorked);
        } else if (timeOffType != null && !timeOffType.isEmpty()) {
            summary.addTimeOffType(day, timeOffType);
        }
    }

    public List<WorkTimeTable> getWorkTimeData(Integer year, Integer month) {
        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());
        List<Integer> nonAdminUserIds = getNonAdminUsers().stream().map(Users::getUserId).toList();

        List<WorkTimeTable> filteredWorkTimes = allWorkTimes.stream()
                .filter(wt -> nonAdminUserIds.contains(wt.getUserId()))
                .filter(wt -> {LocalDate workDate = wt.getWorkDate();
                    return workDate != null && workDate.getYear() == year && workDate.getMonthValue() == month;
                })
                .collect(Collectors.toList());

        LoggerUtil.info(getClass(),"Filtered work times (excluding admin): " + filteredWorkTimes.size());
        return filteredWorkTimes;
    }

    private boolean isAdminUser(Users user) {
        return !NON_ADMIN_ROLES.contains(user.getRole());
    }

    public List<MonthlyWorkSummary> refreshWorkTimeData(Integer year, Integer month) {
        List<MonthlyWorkSummary> summaries = getMonthlyWorkSummary(year, month);
        LoggerUtil.info(getClass(),"Refreshed work time data for " + year + "-" + month + ": " + summaries.size() + " entries (excluding admin)");
        return summaries;
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public boolean addNationalHoliday(LocalDate date) {
        if (isWeekend(date)) {
            LoggerUtil.warn(getClass(), "Attempted to add a national holiday on a weekend: " + date);
            return false;
        }

        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());

        for (Users user : userService.getAllUsers()) {
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
            LoggerUtil.info(getClass(),"National holiday added successfully for date: " + date);
        } else {
            LoggerUtil.error(getClass(),"Failed to add national holiday for date: " + date);
        }
        return success;
    }
}
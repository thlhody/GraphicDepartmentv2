package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkSessionStateUser;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.UserSessionFileHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class WorkSessionService {

    private Users currentUser;
    private WorkSessionStateUser currentSession;
    private UserService userService;

    public WorkSessionService() {
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;
        LoggerUtil.info("Current user set: " + user.getName());
    }

    public void startSession() {
        if (currentSession == null || "ENDED".equals(currentSession.getSessionState())) {
            currentSession = new WorkSessionStateUser();
            currentSession.setId(generateNewSessionId());
            currentSession.setUserId(currentUser.getUserId());
            currentSession.setFirstStartTime(LocalDateTime.now());
            currentSession.setCurrentStartTime(LocalDateTime.now());
            currentSession.setSessionState("STARTED");
            saveCurrentSession();
            LoggerUtil.info("Session started for user: " + currentUser.getName());
        } else {
            LoggerUtil.warn("Attempted to start an already active session");
        }
    }

    public void pauseSession() {
        if (currentSession != null && "STARTED".equals(currentSession.getSessionState())) {
            LocalDateTime now = LocalDateTime.now();
            long workedSeconds = ChronoUnit.SECONDS.between(currentSession.getCurrentStartTime(), now);
            currentSession.addWorkedTime(workedSeconds);
            currentSession.setLastPauseTime(now);
            currentSession.setSessionState("Temporary Stop");
            currentSession.incrementBreakCount();
            saveCurrentSession();
            LoggerUtil.info("Session paused for user: " + currentUser.getName());
        } else {
            LoggerUtil.warn("Attempted to pause an inactive session");
        }
    }

    public void resumeSession() {
        if (currentSession != null && "Temporary Stop".equals(currentSession.getSessionState())) {
            LocalDateTime now = LocalDateTime.now();
            long breakSeconds = ChronoUnit.SECONDS.between(currentSession.getLastPauseTime(), now);
            currentSession.addBreakTime(breakSeconds);
            currentSession.setCurrentStartTime(now);
            currentSession.setSessionState("STARTED");
            saveCurrentSession();
            LoggerUtil.info("Session resumed for user: " + currentUser.getName());
        } else {
            LoggerUtil.warn("Attempted to resume a non-paused session");
        }
    }

    public void endSession() {
        if (currentSession != null && !"ENDED".equals(currentSession.getSessionState())) {
            LocalDateTime now = LocalDateTime.now();
            if ("STARTED".equals(currentSession.getSessionState())) {
                long workedSeconds = ChronoUnit.SECONDS.between(currentSession.getCurrentStartTime(), now);
                currentSession.addWorkedTime(workedSeconds);
            } else if ("Temporary Stop".equals(currentSession.getSessionState())) {
                long breakSeconds = ChronoUnit.SECONDS.between(currentSession.getLastPauseTime(), now);
                currentSession.addBreakTime(breakSeconds);
            }
            currentSession.setLastEndTime(now);
            currentSession.setSessionState("ENDED");
            saveCurrentSession();
            updateWorkIntervalJson();
            LoggerUtil.info("Session ended for user: " + currentUser.getName());
        } else {
            LoggerUtil.warn("Attempted to end an inactive session");
        }
    }

    private void updateWorkIntervalJson() {
        WorkTimeTable workTimeEntry = new WorkTimeTable(
                currentUser.getUserId(),
                currentSession.getFirstStartTime(),
                currentSession.getBreakCount(),
                LocalTime.ofSecondOfDay(currentSession.getTotalBreakSeconds()),
                currentSession.getLastEndTime(),
                LocalTime.ofSecondOfDay(currentSession.getTotalWorkedSeconds()),
                null, // timeOffType
                currentSession.getFirstStartTime().toLocalDate(),
                currentSession.getTotalWorkedSeconds()
        );

        List<WorkTimeTable> workTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());
        workTimes.removeIf(wt -> wt.getUserId().equals(currentUser.getUserId()) && wt.getWorkDate().equals(currentSession.getFirstStartTime().toLocalDate()));
        workTimes.add(workTimeEntry);

        boolean success = JsonUtils.writeWorkTimesToJson(workTimes, JsonPaths.getWorkIntervalJson());
        if (success) {
            LoggerUtil.info("Work interval updated successfully for user: " + currentUser.getName());
        } else {
            LoggerUtil.error("Failed to update work interval for user: " + currentUser.getName());
        }
    }

    private void saveCurrentSession() {
        UserSessionFileHandler.saveUserSession(currentUser, currentSession);
    }

    private int generateNewSessionId() {
        WorkSessionStateUser existingSession = UserSessionFileHandler.readUserSession(currentUser);
        if (existingSession != null) {
            return existingSession.getId() + 1;
        }
        return 1;
    }

    public void loadExistingSession() {
        if (currentUser != null) {
            WorkSessionStateUser savedSession = UserSessionFileHandler.readUserSession(currentUser);
            if (savedSession != null && ("STARTED".equals(savedSession.getSessionState()) || "Temporary Stop".equals(savedSession.getSessionState()))) {
                LocalDateTime now = LocalDateTime.now();

                if ("STARTED".equals(savedSession.getSessionState())) {
                    long elapsedSeconds = ChronoUnit.SECONDS.between(savedSession.getCurrentStartTime(), now);
                    savedSession.addWorkedTime(elapsedSeconds);
                    savedSession.setCurrentStartTime(now);
                } else if ("Temporary Stop".equals(savedSession.getSessionState())) {
                    long elapsedBreakSeconds = ChronoUnit.SECONDS.between(savedSession.getLastPauseTime(), now);
                    savedSession.addBreakTime(elapsedBreakSeconds);
                    savedSession.setLastPauseTime(now);
                }

                this.currentSession = savedSession;
                saveCurrentSession();
                LoggerUtil.info("Loaded existing session for user: " + currentUser.getName() + ", State: " + savedSession.getSessionState());
            } else {
                this.currentSession = null;
                LoggerUtil.info("No active session found for user: " + currentUser.getName());
            }
        }
    }

    public boolean saveTimeOff(LocalDate startDate, LocalDate endDate, String timeOffType) {

        if (currentUser == null) {
            LoggerUtil.error("No current user set for saving time off");
            return false;
        }

        List<WorkTimeTable> workTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            if (isWorkingDay(date)) {

                // Check if an entry already exists for this date and user
                LocalDate finalDate = date;
                WorkTimeTable existingEntry = workTimes.stream()
                        .filter(wt -> wt.getUserId().equals(currentUser.getUserId()) && wt.getWorkDate().equals(finalDate))
                        .findFirst()
                        .orElse(null);

                if (existingEntry != null) {

                    // Only update if it's not an SN day
                    if (!"SN".equals(existingEntry.getTimeOffType())) {
                        updateExistingEntry(existingEntry, timeOffType);
                    } else {
                        LoggerUtil.info("Skipping SN day: " + finalDate);
                    }
                } else {
                    // Create new entry
                    WorkTimeTable newEntry = createNewTimeOffEntry(date, timeOffType);
                    workTimes.add(newEntry);
                }
            }
            date = date.plusDays(1);
        }

        boolean success = JsonUtils.writeWorkTimesToJson(workTimes, JsonPaths.getWorkIntervalJson());

        if (success) {
            LoggerUtil.info("Time off saved successfully for user: " + currentUser.getName() + " from " + startDate + " to " + endDate);
        } else {
            LoggerUtil.error("Failed to save time off for user: " + currentUser.getName() + " from " + startDate + " to " + endDate);
        }
        return success;
    }

    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;

    }


    private void updateExistingEntry(WorkTimeTable entry, String timeOffType) {

        entry.setTimeOffType(timeOffType);
        entry.setFirstStartTime(null);
        entry.setBreaks(0);
        entry.setBreaksTime(null);
        entry.setEndTime(null);
        entry.setTotalWorkedTime(LocalTime.of(0, 0));
        entry.setTotalWorkedSeconds(0L);

    }


    private WorkTimeTable createNewTimeOffEntry(LocalDate date, String timeOffType) {

        return new WorkTimeTable(
                currentUser.getUserId(),
                null,  // firstStartTime
                0,     // breaks
                null,  // breaksTime
                null,  // endTime
                LocalTime.of(0, 0),  // totalWorkedTime
                timeOffType,
                date,  // workDate
                0L     // totalWorkedSeconds
        );
    }

    public List<WorkTimeTable> getWorkTimeData(int year, int month) {

        List<WorkTimeTable> allWorkTimes = JsonUtils.readWorkTimesFromJson(JsonPaths.getWorkIntervalJson());
        LoggerUtil.info("Total work times read from JSON: " + allWorkTimes.size());

        List<WorkTimeTable> filteredWorkTimes = allWorkTimes.stream()
                .filter(wt -> {
                    boolean matches = wt.getWorkDate().getYear() == year && wt.getWorkDate().getMonthValue() == month;
                    LoggerUtil.info("Checking entry: " + wt + ", Matches filter: " + matches);
                    return matches;
                })
                .collect(Collectors.toList());
        LoggerUtil.info("Filtered work times: " + filteredWorkTimes.size());
        return filteredWorkTimes;
    }
}
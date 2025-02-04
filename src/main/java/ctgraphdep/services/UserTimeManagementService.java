package ctgraphdep.services;

import ctgraphdep.constants.JsonPaths;
import ctgraphdep.models.TemporaryStop;
import ctgraphdep.models.WorkTimeSummary;
import ctgraphdep.models.WorkTimeTable;
import ctgraphdep.models.WorkUsersSessionsStates;
import ctgraphdep.utils.JsonUtils;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.TimeProcessingUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserTimeManagementService {
//
//
//    private static final int WORK_DAY_MINUTES = 480;
//    private static final int LUNCH_BREAK_MINUTES = 30;
//    private static final int MAX_WORK_DAY_MINUTES = 720;
//
//    public void startDay(Long userId, String userName) {
//        WorkUsersSessionsStates session = new WorkUsersSessionsStates();
//        session.setUserId(userId);
//        session.setName(userName);
//        session.setSessionStatus("Online");
//        session.setDayStartTime(LocalDateTime.now());
//        session.setCurrentStartTime(LocalDateTime.now());
//        session.setTotalWorkedMinutes(0);
//        session.setLunchBreakDeducted(false);
//        session.setWorkdayCompleted(false);
//
//        saveSession(session);
//    }
//
//    public void temporaryStop(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null) {
//            session.setSessionStatus("Temporary Stop");
//            session.setLastPauseTime(LocalDateTime.now());
//            saveSession(session);
//        }
//    }
//
//    public void resumeFromTemporaryStop(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null && "Temporary Stop".equals(session.getSessionStatus())) {
//            LocalDateTime now = LocalDateTime.now();
//            int duration = TimeProcessingUtils.calculateDurationInMinutes(session.getLastPauseTime(), now);
//
//            TemporaryStop stop = new TemporaryStop();
//            stop.setStartTime(session.getLastPauseTime());
//            stop.setEndTime(now);
//            stop.setDuration(duration);
//
//            session.getTemporaryStops().add(stop);
//            session.setTotalTemporaryStopMinutes(session.getTotalTemporaryStopMinutes() + duration);
//            session.setTemporaryStopCount(session.getTemporaryStopCount() + 1);
//            session.setSessionStatus("Online");
//            session.setCurrentStartTime(now);
//
//            saveSession(session);
//        }
//    }
//
//    public void updateWorkTime(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null && "Online".equals(session.getSessionStatus())) {
//            int workedMinutes = TimeProcessingUtils.calculateDurationInMinutes(session.getCurrentStartTime(), LocalDateTime.now());
//            session.setTotalWorkedMinutes(session.getTotalWorkedMinutes() + workedMinutes);
//            session.setCurrentStartTime(LocalDateTime.now());
//
//            checkAndUpdateLunchBreak(session);
//            checkWorkdayCompletion(session);
//
//            saveSession(session);
//        }
//    }
//
//    public void endDay(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null) {
//            updateWorkTime(userId);
//            checkAndUpdateLunchBreak(session);
//            checkWorkdayCompletion(session);
//            session.setSessionStatus("Offline");
//            saveSession(session);
//        }
//    }
//
//    private void checkAndUpdateLunchBreak(WorkUsersSessionsStates session) {
//        if (!session.getLunchBreakDeducted() && session.getTotalWorkedMinutes() >= WORK_DAY_MINUTES + LUNCH_BREAK_MINUTES) {
//            session.setTotalWorkedMinutes(session.getTotalWorkedMinutes() - LUNCH_BREAK_MINUTES);
//            session.setLunchBreakDeducted(true);
//        }
//    }
//
//    private void checkWorkdayCompletion(WorkUsersSessionsStates session) {
//        if (session.getTotalWorkedMinutes() >= WORK_DAY_MINUTES) {
//            session.setWorkdayCompleted(true);
//        }
//        if (session.getTotalWorkedMinutes() >= MAX_WORK_DAY_MINUTES) {
//            session.setLunchBreakDeducted(true);
//            session.setWorkdayCompleted(true);
//        }
//    }
//
//    private WorkUsersSessionsStates getSession(Long userId) {
//        try {
//            List<WorkUsersSessionsStates> sessions = JsonUtils.readWorkSessionsFromJson(JsonPaths.getUsersSessionsStates());
//            Optional<WorkUsersSessionsStates> userSession = sessions.stream()
//                    .filter(s -> s.getUserId().equals(userId))
//                    .findFirst();
//            return userSession.orElse(null);
//        } catch (IOException e) {
//            LoggerUtil.error(getClass(), "Error reading work sessions", e);
//            return null;
//        }
//    }
//
//    private void saveSession(WorkUsersSessionsStates session) {
//        try {
//            List<WorkUsersSessionsStates> sessions = JsonUtils.readWorkSessionsFromJson(JsonPaths.getUsersSessionsStates());
//            sessions.removeIf(s -> s.getUserId().equals(session.getUserId()));
//            sessions.add(session);
//            JsonUtils.writeWorkSessionsToJson((JsonPaths.getUsersSessionsStates()), sessions);
//        } catch (IOException e) {
//            LoggerUtil.error(getClass(), "Error saving work session", e);
//        }
//    }
//
//    public String getTotalWorkedTime(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null) {
//            return TimeProcessingUtils.minutesToHHmm(session.getTotalWorkedMinutes());
//        }
//        return "00:00";
//    }
//
//    public String getTotalBreakTime(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null) {
//            return TimeProcessingUtils.minutesToHHmm(session.getTotalTemporaryStopMinutes());
//        }
//        return "00:00";
//    }
//
//    public int getBreakCount(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        if (session != null) {
//            return session.getTemporaryStopCount();
//        }
//        return 0;
//    }
//
//    public boolean isWorkdayCompleted(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        return session != null && session.getWorkdayCompleted();
//    }
//
//    public List<WorkTimeTable> getUserWorkHours(Long userId, int year, Month month) {
//        List<WorkUsersSessionsStates> allSessions = readAllSessions();
//        List<WorkUsersSessionsStates> userSessions = allSessions.stream()
//                .filter(session -> session.getUserId().equals(userId))
//                .collect(Collectors.toList());
//
//        List<WorkTimeTable> workHours = new ArrayList<>();
//        YearMonth yearMonth = YearMonth.of(year, month);
//
//        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
//            LocalDate date = LocalDate.of(year, month, day);
//            WorkTimeTable entry = createWorkTimeTableEntry(date, userSessions);
//            workHours.add(entry);
//        }
//
//        return workHours;
//    }
//
//    private WorkTimeTable createWorkTimeTableEntry(LocalDate date, List<WorkUsersSessionsStates> userSessions) {
//        WorkTimeTable entry = new WorkTimeTable();
//        entry.setDate(date);
//
//        Optional<WorkUsersSessionsStates> sessionForDay = userSessions.stream()
//                .filter(session -> session.getDayStartTime().toLocalDate().equals(date))
//                .findFirst();
//
//        if (sessionForDay.isPresent()) {
//            WorkUsersSessionsStates session = sessionForDay.get();
//            entry.setStartTime(TimeProcessingUtils.formatTime(session.getDayStartTime()));
//            entry.setEndTime(TimeProcessingUtils.formatTime(session.getCurrentStartTime()));
//            entry.setTotalWorkedHours(TimeProcessingUtils.minutesToHHmm(session.getTotalWorkedMinutes()));
//            entry.setBreakCount(session.getTemporaryStopCount());
//            entry.setTotalBreakTime(TimeProcessingUtils.minutesToHHmm(session.getTotalTemporaryStopMinutes()));
//        } else {
//            entry.setStartTime("--:--");
//            entry.setEndTime("--:--");
//            entry.setTotalWorkedHours("00:00");
//            entry.setBreakCount(0);
//            entry.setTotalBreakTime("00:00");
//        }
//
//        return entry;
//    }
//
//    public WorkTimeSummary calculateWorkTimeSummary(List<WorkTimeTable> workHours, int year, Month month) {
//        int totalWorkedMinutes = workHours.stream()
//                .mapToInt(wh -> TimeProcessingUtils.hhmmToMinutes(wh.getTotalWorkedHours()))
//                .sum();
//
//        int expectedWorkDays = YearMonth.of(year, month).lengthOfMonth();
//        int expectedWorkMinutes = expectedWorkDays * WORK_DAY_MINUTES;
//
//        int overtimeMinutes = Math.max(0, totalWorkedMinutes - expectedWorkMinutes);
//
//        long timeOffDays = workHours.stream()
//                .filter(wh -> wh.getTotalWorkedHours().equals("00:00"))
//                .count();
//
//        WorkTimeSummary summary = new WorkTimeSummary();
//        summary.setTotalHoursWorked(TimeProcessingUtils.minutesToHHmm(totalWorkedMinutes));
//        summary.setOvertimeHours(TimeProcessingUtils.minutesToHHmm(overtimeMinutes));
//        summary.setTimeOffDays(String.valueOf(timeOffDays));
//
//        return summary;
//    }
//
//    public String getSessionStatus(Long userId) {
//        WorkUsersSessionsStates session = getSession(userId);
//        return session != null ? session.getSessionStatus() : "Offline";
//    }
//
//    private List<WorkUsersSessionsStates> readAllSessions() {
//        try {
//            return JsonUtils.readWorkSessionsFromJson(JsonPaths.getUsersSessionsStates());
//        } catch (IOException e) {
//            LoggerUtil.error(getClass(), "Error reading all work sessions", e);
//            return new ArrayList<>();
//        }
//    }
}
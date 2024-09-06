package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkSessionStateUser;
import ctgraphdep.services.*;
import ctgraphdep.utils.LoggerUtil;
import ctgraphdep.utils.UserSessionFileHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class UserController extends BaseController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button endButton;
    @FXML
    private Button userSettingsButton;
    @FXML
    private Button userWorkIntervalButton;
    @FXML
    private Button statusButton;
    @FXML
    private Label currentDateTimeLabel;
    @FXML
    private Label workStartedLabel;
    @FXML
    private Label totalWorkLabel;
    @FXML
    private Label breakCountLabel;
    @FXML
    private Label totalBreakTimeLabel;

    @FXML
    @Override
    public void initializeServices(ServiceFactory serviceFactory) {
        super.initializeServices(serviceFactory);
        this.serviceFactory = serviceFactory;
        setCurrentFXMLPath(AppPaths.USER_PAGE_LAYOUT);
        setupLogoImage();
        setWelcomeMessage();
        loadExistingSession();
        setupDisplayTimeInfoUpdater();
        updateButtonStates();
    }

    @FXML
    protected void onStartButton() {
        serviceFactory.getWorkSessionService().startSession();
        updateUserStatus("Online");
        updateButtonStates();
    }

    @FXML
    protected void onPauseButton() {
        WorkSessionStateUser currentSession = serviceFactory.getWorkSessionService().getCurrentSession();
        if (currentSession != null && "Temporary Stop".equals(currentSession.getSessionState())) {
            serviceFactory.getWorkSessionService().resumeSession();
            updateUserStatus("Online");
            pauseButton.setText("Temporary Stop");
        } else {
            serviceFactory.getWorkSessionService().pauseSession();
            updateUserStatus("Temporary Stop");
            pauseButton.setText("Resume");
        }
        updateButtonStates();
    }

    @FXML
    protected void onEndButton() {
        serviceFactory.getWorkSessionService().endSession();
        updateUserStatus("Offline");
        updateButtonStates();
    }

    @FXML
    protected void onStatusButton() {
        StatusDialogController.openStatusDialog(serviceFactory);
    }

    @FXML
    public void onUserSettingsButton() {
        serviceFactory.getNavigationService().toUserSettings();
    }

    @FXML
    protected void onUserWorkIntervalButton() {
        serviceFactory.getNavigationService().toUserWorkInterval();
    }

    private void setWelcomeMessage() {
        Users currentUser = serviceFactory.getWorkSessionService().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getName();
            welcomeLabel.setText("Welcome, " + userName + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }
    }

    private void setupDisplayTimeInfoUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeDisplay()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTimeDisplay() {
        WorkSessionStateUser currentSession = serviceFactory.getWorkSessionService().getCurrentSession();
        if (currentSession == null) {
            resetDisplayLabels();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String sessionState = currentSession.getSessionState();

        String displayText = formatDateTime(now) + " - " + sessionState;
        currentDateTimeLabel.setText(displayText);
        LoggerUtil.debug("Updated currentDateTimeLabel: " + displayText);

        if ("STARTED".equals(sessionState) || "Temporary Stop".equals(sessionState) || "ENDED".equals(sessionState)) {
            LocalDateTime startTime = currentSession.getFirstStartTime();
            workStartedLabel.setText("Work Started at: " + formatDateTime(startTime));

            long totalWorkedSeconds = currentSession.getTotalWorkedSeconds();
            if ("STARTED".equals(sessionState)) {
                totalWorkedSeconds += ChronoUnit.SECONDS.between(currentSession.getCurrentStartTime(), now);
            }
            totalWorkLabel.setText("Total Work: " + formatDuration(totalWorkedSeconds));

            breakCountLabel.setText("No. of Breaks: " + currentSession.getBreakCount());
            totalBreakTimeLabel.setText("Total Break Time: " + formatDuration(currentSession.getTotalBreakSeconds()));
        } else {
            resetDisplayLabels();
        }
    }

    private void resetDisplayLabels() {
        currentDateTimeLabel.setText(formatDateTime(LocalDateTime.now()) + " - No active session");
        workStartedLabel.setText("Work Started at: --:--:--");
        totalWorkLabel.setText("Total Work: 00:00");
        breakCountLabel.setText("No. of Breaks: 0");
        totalBreakTimeLabel.setText("Total Break Time: 00:00");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy :: HH:mm"));
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
//        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    private void updateButtonStates() {
        WorkSessionStateUser currentSession = serviceFactory.getWorkSessionService().getCurrentSession();
        if (currentSession == null) {
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            endButton.setDisable(true);
        } else {
            String sessionState = currentSession.getSessionState();
            startButton.setDisable(!"ENDED".equals(sessionState));
            pauseButton.setDisable("ENDED".equals(sessionState));
            endButton.setDisable("ENDED".equals(sessionState));

            if ("Temporary Stop".equals(sessionState)) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Temporary Stop");
            }
        }
    }

    private void updateUserStatus(String status) {
        Users currentUser = serviceFactory.getWorkSessionService().getCurrentUser();
        if (currentUser != null) {
            serviceFactory.getStatusDialogService().updateUserStatus(currentUser.getUserId(), status);
            LoggerUtil.info("Updated status for user " + currentUser.getUserId() + " to " + status);
        } else {
            LoggerUtil.warn("Cannot update status: current user is null");
        }
    }

    public void loadExistingSession() {
        if (serviceFactory.getWorkSessionService().getCurrentUser() == null) {
            LoggerUtil.warn("Cannot load session: current user is null");
            return;
        }

        WorkSessionStateUser savedSession = UserSessionFileHandler.readUserSession(serviceFactory.getWorkSessionService().getCurrentUser());
        if (savedSession == null) {
            LoggerUtil.info("No saved session found for user: " + serviceFactory.getWorkSessionService().getCurrentUser().getName());
            return;
        }

        if (!isValidSessionState(savedSession.getSessionState())) {
            LoggerUtil.warn("Invalid session state found for user: " + serviceFactory.getWorkSessionService().getCurrentUser().getName() + ", State: " + savedSession.getSessionState());
            return;
        }

        updateSessionTimes(savedSession);

        saveCurrentSession();
        LoggerUtil.info("Loaded existing session for user: " + serviceFactory.getWorkSessionService().getCurrentUser().getName() + ", State: " + savedSession.getSessionState());
    }

    private boolean isValidSessionState(String state) {
        return "STARTED".equals(state) || "Temporary Stop".equals(state);
    }

    private void updateSessionTimes(WorkSessionStateUser session) {
        LocalDateTime now = LocalDateTime.now();
        if ("STARTED".equals(session.getSessionState())) {
            updateStartedSession(session, now);
        } else if ("Temporary Stop".equals(session.getSessionState())) {
            updatePausedSession(session, now);
        }
    }

    private void updateStartedSession(WorkSessionStateUser session, LocalDateTime now) {
        LocalDateTime lastStartTime = session.getCurrentStartTime();
        if (lastStartTime == null) {
            LoggerUtil.warn("CurrentStartTime is null for STARTED session. Using FirstStartTime.");
            lastStartTime = session.getFirstStartTime();
        }
        long elapsedSeconds = ChronoUnit.SECONDS.between(lastStartTime, now);
        session.addWorkedTime(elapsedSeconds);
        session.setCurrentStartTime(now);
        LoggerUtil.info("Updated STARTED session. Added " + elapsedSeconds + " seconds to worked time.");
    }

    private void updatePausedSession(WorkSessionStateUser session, LocalDateTime now) {
        LocalDateTime lastPauseTime = session.getLastPauseTime();
        if (lastPauseTime == null) {
            LoggerUtil.warn("LastPauseTime is null for Temporary Stop session. Using current time.");
            lastPauseTime = now;
        }
        long elapsedBreakSeconds = ChronoUnit.SECONDS.between(lastPauseTime, now);
        session.addBreakTime(elapsedBreakSeconds);
        session.setLastPauseTime(now);
        LoggerUtil.info("Updated Temporary Stop session. Added " + elapsedBreakSeconds + " seconds to break time.");
    }

    private void saveCurrentSession() {
        UserSessionFileHandler.saveUserSession(serviceFactory.getWorkSessionService().getCurrentUser(), serviceFactory.getWorkSessionService().getCurrentSession());
        LoggerUtil.info("Session saved for user: " + serviceFactory.getWorkSessionService().getCurrentUser().getName());
    }

}

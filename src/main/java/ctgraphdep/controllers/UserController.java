package ctgraphdep.controllers;

import ctgraphdep.constants.AppPaths;
import ctgraphdep.models.Users;
import ctgraphdep.models.WorkSessionStateUser;
import ctgraphdep.services.*;
import ctgraphdep.utils.LoggerUtil;
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

        currentDateTimeLabel.setText(formatDateTime(now) + " - " + sessionState);

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

    private void loadExistingSession() {
        WorkSessionStateUser currentSession = serviceFactory.getWorkSessionService().getCurrentSession();
        if (currentSession != null && "STARTED".equals(currentSession.getSessionState())) {
            updateTimeDisplay();
        }
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
}
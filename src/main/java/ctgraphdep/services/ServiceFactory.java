package ctgraphdep.services;

import ctgraphdep.utils.LoggerUtil;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceFactory {

    private static ServiceFactory instance;
    private Stage primaryStage;
    private NavigationService navigationService;
    private AuthenticationService authenticationService;
    private UserService userService;
    private LogoService logoService;
    private WorkSessionService workSessionService;
    private StatusDialogService statusDialogService;
    private DialogService dialogService;
    private AdminTimeService adminTimeService;
    private UserManagementService userManagementService;
    private FileAccessibilityService fileAccessibilityService;

    private ServiceFactory() {
        // Private constructor to prevent direct instantiation
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize services
        navigationService = new NavigationService(primaryStage);
        workSessionService = new WorkSessionService();
        userService = new UserService();

        // se dependencies after creation
        this.workSessionService.setUserService(this.userService);
        this.userService.setWorkSessionService(this.workSessionService);

        authenticationService = new AuthenticationService(this.userService, this.workSessionService,this.navigationService);
        adminTimeService = new AdminTimeService(userService);
        userManagementService = new UserManagementService(userService, primaryStage);

        logoService = new LogoService();
        dialogService = new DialogService(this);
        statusDialogService = new StatusDialogService();
        fileAccessibilityService = new FileAccessibilityService();
    }

    public LogoService getLogoService() {
        if (logoService == null) {
            LoggerUtil.error("ServiceFactory has not been initialized");
        }
        return logoService;
    }

    public FileAccessibilityService getFileAccessibilityService() {
        if (fileAccessibilityService == null) {
            LoggerUtil.error("FileAccessibilityService is null. Make sure initialize() was called.");
        }
        return fileAccessibilityService;
    }

    public UserManagementService getUserManagementService() {
        if (userManagementService == null) {
            LoggerUtil.error("ServiceFactory has not been initialized");
        }
        return userManagementService;
    }

    public boolean isInitialized() {
        return logoService != null && userManagementService != null &&  workSessionService != null && userService != null && authenticationService != null && navigationService != null;
    }
}
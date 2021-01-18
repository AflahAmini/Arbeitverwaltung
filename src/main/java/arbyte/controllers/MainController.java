package arbyte.controllers;

import arbyte.managers.ExecutorServiceManager;
import arbyte.managers.DataManager;
import arbyte.helper.SessionMouseListener;
import arbyte.helper.SceneHelper;
import arbyte.models.Session;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.jnativehook.GlobalScreen;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MainController {
    private static MainController mainController;
    public static MainController getInstance() {
        return mainController;
    }

    // Duration in seconds how long the program should wait
    // before the loading screen shows
    private static final int waitBeforeLoadDuration = 2;

    //#region FXML variables
    @FXML
    AnchorPane mainView;
    @FXML
    Label labelSession;
    @FXML
    VBox containerFlash;
    @FXML
    Label labelStatus;
    //#endregion

    private final DataManager dataManager = DataManager.getInstance();

    @FXML
    Label labelEmail;

    @FXML
    public void initialize() {
        mainController = this;
        loadThenChangeView("fxml/CalendarView.fxml",
                () -> dataManager.getCalendar() != null,
                CalendarViewController::initialize);

        startSessionUpdateSchedule();
        setStatus(true);

        labelEmail.setText(LoginController.getInstance().getEmail());

        // Disables the default logger for GlobalScreen
        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        // Registers SessionMouseListener to manage session pauses upon inactivity
        try {
            GlobalScreen.registerNativeHook();
            SessionMouseListener mouse = new SessionMouseListener();
            GlobalScreen.addNativeMouseMotionListener(mouse);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void changeView(String fxmlPath){
        setMainView(SceneHelper.getParentFromFXML(fxmlPath));
    }

    // Changes view with a callback that allows access to the controller
    // via the callback, given the controller class is known
    public <T> void changeViewAndModify(String fxmlPath, Consumer<T> controllerCallback) {
        FXMLLoader loader = SceneHelper.getFXMLLoader(fxmlPath);

        try {
            setMainView(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        T controller = loader.getController();
        controllerCallback.accept(controller);
    }

    // Runs two scheduled tasks. One waits until the waiting duration then shows a loading screen,
    // and another periodically runs the validator supplier. If the validator returns true, then
    // the view is switched to the desired view while running the callback for the controller.
    public <T> void loadThenChangeView(String fxmlPath, Supplier<Boolean> validator, Consumer<T> controllerCallback) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
        ExecutorServiceManager.register(scheduledThreadPool);

        ScheduledFuture<?> loadViewHandle = scheduledThreadPool.schedule(() ->
                        Platform.runLater(() -> changeView("fxml/LoadingView.fxml")),
                waitBeforeLoadDuration, TimeUnit.SECONDS);

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            if (validator.get()) {
                Platform.runLater(() -> changeViewAndModify(fxmlPath, controllerCallback));

                loadViewHandle.cancel(false);
                scheduledThreadPool.shutdown();
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    // Shows a flash message on main view
    public void flash(String message, boolean isError) {
        Platform.runLater( () -> {
        try {
            FXMLLoader loader;

            if (isError) {
                loader = SceneHelper.getFXMLLoader("fxml/FlashError.fxml");
            } else {
                loader = SceneHelper.getFXMLLoader("fxml/FlashInfo.fxml");
            }

            Parent flashWindow = loader.load();
            containerFlash.getChildren().add(flashWindow);
            FlashController controller = loader.getController();
            controller.setMessage(message);

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(
                    () -> Platform.runLater(() -> {
                        // After 2 seconds the message fades out for 1 second
                        FadeTransition ft = new FadeTransition(Duration.seconds(1), flashWindow);
                        ft.setFromValue(1);
                        ft.setToValue(0);

                        // then is deleted after the fade out ends
                        ft.setOnFinished(actionEvent -> {
                            containerFlash.getChildren().remove(0);
                            service.shutdown();
                        });
                        ft.play();
                    }),
                    2, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        });
    }

    public void setStatus(boolean isActive){
        labelStatus.setText("Status : " + (isActive ? "Active" : "Inactive") );
    }

    // Starts a ScheduledExecutorService which updates the session label every minute
    private void startSessionUpdateSchedule() {
        labelSession.setText("Duration - 00:00");

        ScheduledExecutorService sessionService = Executors.newSingleThreadScheduledExecutor();
        ExecutorServiceManager.register(sessionService);
        sessionService.scheduleAtFixedRate(() -> {
            String sessionMessage = "Duration - " + getSessionDuration();
            Platform.runLater(() -> labelSession.setText(sessionMessage));
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void setMainView(Parent p) {
        mainView.getChildren().clear();
        mainView.getChildren().add(p);

        // Ensures the parent stretches to the bounds of the containing anchor pane
        AnchorPane.setTopAnchor(p, 0.0);
        AnchorPane.setBottomAnchor(p, 0.0);
        AnchorPane.setLeftAnchor(p, 0.0);
        AnchorPane.setRightAnchor(p, 0.0);
    }

    // Returns the session duration in the format hh:mm
    private String getSessionDuration() {
        Session s = dataManager.getSession();

        if (s == null) return "--:--";
        long sessionSeconds = s.getActiveDuration().getSeconds();
        return String.format("%02d:%02d", sessionSeconds / 3600, sessionSeconds / 60);
    }
}
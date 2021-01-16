package arbyte.controllers;

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

public class MainController {
    private static MainController mainController;
    public static MainController getInstance() {
        return mainController;
    }

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

    private Session curSession;

    @FXML
    Label labelEmail;

    @FXML
    public void initialize() {
        mainController = this;
        curSession = new Session();
        changeView("fxml/CalendarView.fxml");

        startSessionUpdateSchedule();
        setStatus(true);

        labelEmail.setText(LoginController.getInstance().getEmail());

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

    public Session getCurSession() {
        return curSession;
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

    // Shows a flash message on main view
    public void flash(String message, boolean isError) {
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
                        ft.setOnFinished(actionEvent -> containerFlash.getChildren().remove(0));
                        ft.play();
                    }),
                    2, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatus(boolean isActive){
        labelStatus.setText("Status : " + (isActive ? "Active" : "Inactive") );
    }

    // Starts a ScheduledExecutorService which updates the session label every minute
    private void startSessionUpdateSchedule() {
        labelSession.setText("Duration - 00:00");

        ScheduledExecutorService sessionService = Executors.newSingleThreadScheduledExecutor();
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
        long sessionSeconds = curSession.getActiveDuration().getSeconds();
        return String.format("%02d:%02d", sessionSeconds / 3600, sessionSeconds / 60);
    }
}

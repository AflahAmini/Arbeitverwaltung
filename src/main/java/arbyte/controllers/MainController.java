package arbyte.controllers;

import arbyte.helper.GlobalMouseListener;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {
    private static MainController mainController;

    public static MainController getInstance() {
        return mainController;
    }

    private Session curSession;

    @FXML
    AnchorPane mainView;
    @FXML
    Label labelSession;
    @FXML
    VBox containerFlash;
    @FXML
    Label labelStatus;

    @FXML
    public void initialize() {
        mainController = this;
        curSession = new Session();
        changeView("fxml/CalendarView.fxml");

        labelSession.setText("Duration - 00:00");

        ScheduledExecutorService sessionService = Executors.newSingleThreadScheduledExecutor();
        sessionService.scheduleAtFixedRate(() -> {
            String sessionMessage = "Duration - " + getSessionDuration();
            Platform.runLater(() -> labelSession.setText(sessionMessage));
        }, 0, 1, TimeUnit.MINUTES);

        labelStatus.setText("Status : Active");
        try {
            GlobalScreen.registerNativeHook();
            GlobalMouseListener mouse = new GlobalMouseListener();
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
        Parent parent = SceneHelper.getParentFromFXML(fxmlPath);
        mainView.getChildren().clear();

        mainView.getChildren().add(parent);
        AnchorPane.setTopAnchor(parent, 0.0);
        AnchorPane.setBottomAnchor(parent, 0.0);
        AnchorPane.setLeftAnchor(parent, 0.0);
        AnchorPane.setRightAnchor(parent, 0.0);
    }

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
                        FadeTransition ft = new FadeTransition(Duration.seconds(1), flashWindow);
                        ft.setFromValue(1);
                        ft.setToValue(0);

                        ft.setOnFinished(actionEvent -> containerFlash.getChildren().remove(0));
                        ft.play();
                    }),
                    2, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns the session duration in the format hh:mm
    private String getSessionDuration() {
        long sessionSeconds = curSession.getActiveDuration().getSeconds();
        return String.format("%02d:%02d", sessionSeconds / 3600, sessionSeconds / 60);
    }

    public void setStatus(boolean isActive){
        labelStatus.setText("Status : " + (isActive ? "Active" : "Inactive") );
    }
}

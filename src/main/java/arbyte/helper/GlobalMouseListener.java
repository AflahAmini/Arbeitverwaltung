package arbyte.helper;

import arbyte.controllers.MainController;
import arbyte.models.Session;
import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GlobalMouseListener implements NativeMouseMotionListener {
    private ScheduledFuture<?> timerHandle;
    private final Session session = MainController.getInstance().getCurSession();
    private final long countdownTime = 5;

    public GlobalMouseListener(){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        logger.setUseParentHandlers(false);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        if(timerHandle != null) {
            timerHandle.cancel(false);
        }
        if(session.isPaused()){
            session.toggleSessionPause();
            Platform.runLater(() -> MainController.getInstance().setStatus(true));
        }
        else{
            timer();
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }

    private void timer(){

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        timerHandle = scheduler.schedule(() -> {
            session.toggleSessionPause();
            System.out.println("Session is paused");
            Platform.runLater(() -> MainController.getInstance().setStatus(false));

        },  countdownTime, TimeUnit.SECONDS);

    }

}

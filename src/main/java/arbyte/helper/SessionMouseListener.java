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

public class SessionMouseListener implements NativeMouseMotionListener {
    private ScheduledFuture<?> timerHandle;
    private final Session session = MainController.getInstance().getCurSession();
    private final long countdownTime = 5;

    public SessionMouseListener(){
        // Disables the default logger
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        logger.setUseParentHandlers(false);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        // Interrupt the timer handle once the mouse starts moving
        if(timerHandle != null)
            timerHandle.cancel(false);

        if(session.isPaused()){
            session.toggleSessionPause();
            Platform.runLater(() -> MainController.getInstance().setStatus(true));
        } else
            startTimer();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) { }

    // Starts a timer that expires after countdownTime seconds.
    // Upon expiring the timer pauses the session.
    private void startTimer(){
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        timerHandle = scheduler.schedule(() -> {
            session.toggleSessionPause();
            System.out.println("Session is paused");
            Platform.runLater(() -> MainController.getInstance().setStatus(false));

        },  countdownTime, TimeUnit.SECONDS);
    }
}

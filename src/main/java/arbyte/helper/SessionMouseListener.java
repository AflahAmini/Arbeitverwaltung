package arbyte.helper;

import arbyte.application.ExecutorServiceManager;
import arbyte.controllers.MainController;
import arbyte.models.Session;
import javafx.application.Platform;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SessionMouseListener implements NativeMouseMotionListener {
    private final long countdownTime = 5;

    private Session session;

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> timerHandle;

    public SessionMouseListener(){
        // Starts the timer upon initialization, ensures the timer handle exists
        // even when mouse isn't moved from the start
        startTimer();

        fetchSession();
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        handleMouseMovement();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        handleMouseMovement();
    }

    private void handleMouseMovement() {
        if (session == null) return;

        // Interrupt the timer handle once the mouse starts moving
        if(timerHandle != null)
            timerHandle.cancel(false);

        if(session.isPaused()){
            session.toggleSessionPause();
            System.out.println("Session is resumed");

            Platform.runLater(() -> MainController.getInstance().setStatus(true));
        } else {
            executorService.shutdown();
            startTimer();
        }
    }

    // Starts a timer that expires after countdownTime seconds.
    // Upon expiring the timer pauses the session.
    private void startTimer(){
        executorService = Executors.newSingleThreadScheduledExecutor();

        timerHandle = executorService.schedule(() -> {
            session.toggleSessionPause();
            System.out.println("Session is paused");
            Platform.runLater(() -> MainController.getInstance().setStatus(false));

        }, countdownTime, TimeUnit.SECONDS);
    }

    private void fetchSession() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorServiceManager.register(executorService);

        executorService.scheduleAtFixedRate(() -> {
            session = DataManager.getInstance().getSession();

            if (session != null)
                executorService.shutdown();
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}

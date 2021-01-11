package arbyte.helper;

import arbyte.controllers.MainController;
import arbyte.models.Session;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class GlobalMouseListener implements NativeMouseMotionListener {
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private Session session = MainController.getInstance().getCurSession();
    private int i = 0;

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        session.falseSessionPause();

        i=0;
        timer();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }

    private void timer(){
        final Runnable checkMouseMove = new Runnable() {
            @Override
            public void run() {
                    System.out.println(session.isPaused());
                    session.toggleSessionPause();
                    System.out.println(session.isPaused());
                    System.out.println(i);
                    i++;
                try {
                    Thread.sleep(10*10*10*10*10*10*10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        final ScheduledFuture<?> timerHandle =
                scheduler.scheduleAtFixedRate(checkMouseMove,5,60 * 60 * 60, TimeUnit.SECONDS);

    }

}

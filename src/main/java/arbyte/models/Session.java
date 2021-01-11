package arbyte.models;

import java.time.Duration;
import java.time.Instant;

public class Session {
    private final int userId;
    private Instant lastActiveInstant;
    private Instant lastPausedInstant;

    private Duration totalActiveDuration;
    private Duration totalPausedDuration;

    private boolean isPaused;

    public Session(int userId) {
        this.userId = userId;
        this.lastActiveInstant = Instant.now();
        this.lastPausedInstant = Instant.now();

        this.totalActiveDuration = Duration.ZERO;
        this.totalPausedDuration = Duration.ZERO;

        this.isPaused = false;

        System.out.println("Session has started at " + lastActiveInstant);
    }


    public int getUserId() {
        return userId;
    }

    public boolean isPaused() {
        return isPaused;
    }

    // Returns the total active duration of the current session
    public Duration getActiveDuration() {
        // Add to the duration then reassign the last recorded instant as now
        if (!isPaused) {
            totalActiveDuration = totalActiveDuration.plus(Duration.between(lastActiveInstant, Instant.now()));
            lastActiveInstant = Instant.now();
        }

        return totalActiveDuration;
    }

    // Returns the total paused duration of the current session
    public Duration getPausedDuration() {
        // Similar to getActiveDuration but while paused
        if (isPaused) {
            totalPausedDuration = totalPausedDuration.plus(Duration.between(lastPausedInstant, Instant.now()));
            lastPausedInstant = Instant.now();
        }

        return totalPausedDuration;
    }

    // Pauses or unpauses the session
    public void toggleSessionPause() {
        isPaused = !isPaused;

        if (isPaused) {
            // Stores the total active duration right until the session is paused
            totalActiveDuration = totalActiveDuration.plus(Duration.between(lastActiveInstant, Instant.now()));
            lastPausedInstant = Instant.now();
        } else {
            // Stores the total paused duration right until the session is unpaused
            totalPausedDuration = totalPausedDuration.plus(Duration.between(lastPausedInstant, Instant.now()));
            lastActiveInstant = Instant.now();
        }
    }

    public void falseSessionPause(){
        isPaused = false;
    }
}

package arbyte.models;

import java.time.Duration;
import java.time.Instant;

public class Session {
    private Instant lastRecordedInstant;
    private Duration totalDuration;

    private boolean isPaused;

    public Session() {
        this.lastRecordedInstant = Instant.now();
        this.totalDuration = Duration.ZERO;

        this.isPaused = false;

        System.out.println("Session has started at " + lastRecordedInstant);
    }

    public boolean isPaused() {
        return isPaused;
    }

    // Returns the total duration of the current session
    public Duration getDuration() {
        // Add to the duration then reassign the last recorded instant as now
        if (!isPaused) {
            totalDuration = totalDuration.plus(Duration.between(lastRecordedInstant, Instant.now()));
            lastRecordedInstant = Instant.now();
        }

        return totalDuration;
    }

    // Pauses or unpauses the session
    public void toggleSessionPause() {
        isPaused = !isPaused;

        if (isPaused) {
            // Stores the total duration right until the session is paused
            totalDuration = totalDuration.plus(Duration.between(lastRecordedInstant, Instant.now()));
        } else {
            // Otherwise set the lastRecordedInstant to the point when the session is unpaused
            lastRecordedInstant = Instant.now();
        }
    }
}

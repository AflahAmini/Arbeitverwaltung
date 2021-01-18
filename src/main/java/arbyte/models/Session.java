package arbyte.models;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Session {
    private Instant lastActiveInstant;
    private Instant lastPausedInstant;

    private Duration totalActiveDuration;
    private Duration totalPausedDuration;

    private boolean isPaused;

    public Session() {
        init();

        System.out.println("Session has started at " + lastActiveInstant);
    }

    // Session constructor with active- and pausedSeconds params to
    // store as durations. Should be used when resuming a session
    public Session(long activeSeconds, long pausedSeconds) {
        init();

        this.totalActiveDuration = Duration.ofSeconds(activeSeconds);
        this.totalPausedDuration = Duration.ofSeconds(pausedSeconds);

        System.out.println("Session resumed at " + lastActiveInstant);
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

    public String toJson() {
        LocalDate date = LocalDate.from(lastActiveInstant);

        return String.format("{ \"date\": \"%s\", \"activeDuration\": %d, \"inactiveDuration\": %d }",
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                totalActiveDuration.getSeconds(),
                totalPausedDuration.getSeconds());
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

    private void init() {
        this.lastActiveInstant = Instant.now();
        this.lastPausedInstant = Instant.now();

        this.totalActiveDuration = Duration.ZERO;
        this.totalPausedDuration = Duration.ZERO;

        this.isPaused = false;
    }
}

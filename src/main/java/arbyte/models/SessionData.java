package arbyte.models;

public class SessionData {
    private final int dayOfWeek;
    private final long activeDuration;
    private final long inactiveDuration;

    public SessionData(int dayOfWeek, long activeDuration, long inactiveDuration) {
        this.dayOfWeek = dayOfWeek;
        this.activeDuration = activeDuration;
        this.inactiveDuration = inactiveDuration;
    }

    public int getDayOfWeek() { return dayOfWeek; }
    public long getActiveDuration() { return activeDuration; }
    public long getInactiveDuration() { return inactiveDuration; }
}

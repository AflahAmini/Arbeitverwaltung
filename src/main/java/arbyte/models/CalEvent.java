package arbyte.models;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class CalEvent {
    private final String name;
    private final ZonedDateTime startTime;
    private final ZonedDateTime endTime;

    private final boolean isValid;

    // Getters
    public String getName(){ return name; }
    public ZonedDateTime getStartTime() { return startTime; }
    public ZonedDateTime getEndTime() { return endTime; }
    public boolean isValid() {
        return isValid;
    }

    public CalEvent (String name, ZonedDateTime startTime, ZonedDateTime endTime){
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;

        this.isValid = validate();
    }

    private boolean validate() {
        return !name.isEmpty() && startTime != null && endTime != null && startTime.compareTo(endTime) < 0;
    }

    // Returns the monthYear string from the event in the format mm-yyyy
    public String getMonthYear(){
        if (!isValid())
            return "";

        int month = getStartTime().getMonthValue();
        int year = getStartTime().getYear();
        return String.format("%02d-%d", month, year);
    }

}
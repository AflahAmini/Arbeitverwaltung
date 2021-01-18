package arbyte.models;

import com.google.gson.annotations.Expose;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CalEvent {
    @Expose
    private final String name;
    @Expose
    private final ZonedDateTime startTime;
    @Expose
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

    public String getFormattedStartTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm");
        return startTime.format(dtf);
    }

    public String getFormattedEndTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm");
        return endTime.format(dtf);
    }
}
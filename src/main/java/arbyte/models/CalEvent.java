package arbyte.models;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class CalEvent {
    private String name;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public String getName(){
        return name;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public CalEvent (String name, ZonedDateTime startTime, ZonedDateTime endTime){
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public boolean isValid() {
        return !name.isEmpty() && startTime != null && endTime != null && startTime.compareTo(endTime) < 0;
    }

    public String getMonthYear(){
        int month = getStartTime().getMonthValue();
        int year = getStartTime().getYear();
        return String.format("%02d-%d", month, year);
    }

}
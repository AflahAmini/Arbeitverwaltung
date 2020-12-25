package arbyte.models;

import java.util.ArrayList;
import java.util.List;

public class Month {
    private String monthYear;
    private List<CalEvent> events;

    public String getMonthYear() {
        return monthYear;
    }

    public List<CalEvent> getEvents() {
        return events;
    }

    public Month (String monthYear){
        this.monthYear = monthYear;
        this.events = new ArrayList<>();
    }

    public void addEventAt(CalEvent c, int index){
        events.add(index, c);
    }

    public void removeEvent(CalEvent c){
        events.remove(c);
    }




}

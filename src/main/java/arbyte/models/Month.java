package arbyte.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Month {
    @Expose
    private final String monthYear;
    @Expose
    private final List<CalEvent> events;

    public String getMonthYear() { return monthYear; }
    public List<CalEvent> getEvents() { return events; }

    public Month (String monthYear){
        this.monthYear = monthYear;
        this.events = new ArrayList<>();
    }

    public void addEventAt(CalEvent c, int index){
        if (index == events.size()) {
            events.add(c);
        } else {
            events.add(index, c);
        }
    }

    public void removeEvent(CalEvent c){ events.remove(c); }
}

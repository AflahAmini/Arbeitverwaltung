package arbyte.models;

import arbyte.helper.ZonedDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Calendar {
    // Hash map maps the monthYear string to the corresponding Month object
    public HashMap<String, Month> monthHashMap;

    public Calendar(){
        monthHashMap = new HashMap<>();
    }

    // Should only be used in testing
    public List<Month> getMonths(){
        return new ArrayList<>(monthHashMap.values());
    }

    public List<CalEvent> getEventsOfMonth(String monthYear) {
        Month m = monthHashMap.get(monthYear);

        // If month does not exist, then there are no events in that month
        if (m == null) {
            return new ArrayList<>();
        }

        return m.getEvents();
    }

    // Adds the event to the corresponding month object.
    // Throws an error if the event is invalid or the event overlaps other events of that month
    public void addEvent(CalEvent calEvent) throws Exception {
        if(!calEvent.isValid()) {
            throw new Exception("Event is invalid!");
        }
        if(overlapsOtherEvents(calEvent)) {
            throw new Exception("Event would overlap other events!");
        }

        String monthYear = calEvent.getMonthYear();
        Month m = monthHashMap.getOrDefault(monthYear, null);

        if(m == null){
            m = new Month(monthYear);
            monthHashMap.put(monthYear, m);
        }

        int i = binSearchRecur(calEvent,m.getEvents(),0,m.getEvents().size() - 1);
        m.addEventAt(calEvent, i);
    }

    // Replaces oldEvent with newEvent.
    // If unsuccessful then the oldEvent would not be deleted,
    // and an exception would be thrown
    public void updateEvent(CalEvent oldEvent, CalEvent newEvent) throws Exception {
        deleteEvent(oldEvent);
        // If add event fails then the old event should be re-added
        try {
            addEvent(newEvent);
        } catch (Exception e) {
            addEvent(oldEvent);
            throw e;
        }
    }

    public void deleteEvent(CalEvent calEvent){
        String monthYear = calEvent.getMonthYear();
        Month m = monthHashMap.getOrDefault(monthYear, null);

        m.removeEvent(calEvent);

        if(m.getEvents().size() == 0){
            monthHashMap.remove(monthYear);
        }
    }

    // Json (de-)serialization methods
    public String toJson(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
                .create();
        return gson.toJson(this);
    }

    public static Calendar fromJson(String json){
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
                .create();
        return gson.fromJson(json, Calendar.class);
    }

    // Runs a binary search in eventList for calEvent and returns the index where calEvent should be inserted at
    private int binSearchRecur(CalEvent calEvent, List<CalEvent> eventList, int s, int e){
        if(eventList.size() == 0)
            return 0;

        if(s == e){
            if(calEvent.getStartTime().compareTo(eventList.get(s).getStartTime())>0){
                return s + 1;
            }
            return s;
        }

        int mid = (s+e)/2;
        if(calEvent.getStartTime().compareTo(eventList.get(mid).getStartTime())>0)
            return binSearchRecur(calEvent,eventList, mid+1, e);
        else
            return binSearchRecur(calEvent,eventList, s, mid);
    }

    private boolean overlapsOtherEvents(CalEvent calEvent){
        Month m = monthHashMap.getOrDefault(calEvent.getMonthYear(), null);

        // If the month does no exist in the first place then there should be no intersections
        if (m == null)
            return false;

        for(CalEvent y : m.getEvents()){
            int sitA = calEvent.getStartTime().compareTo(y.getEndTime()); // should be negative if calEventStart is earlier than yEnd
            if (sitA >= 0)
                continue;       // Skip unnecessary second comparison
            int sitB = y.getStartTime().compareTo(calEvent.getEndTime()); // should be negative if yStart is earlier then calEventEnd
            if (sitB >= 0)
                continue;

            return true;
        }

        return false;
    }
}

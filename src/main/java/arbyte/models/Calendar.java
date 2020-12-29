package arbyte.models;

import arbyte.helper.ZonedDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Calendar {
    private List<Month> months;

    public Calendar(){
        months = new ArrayList<>();
    }

    public List<Month> getMonths() {
        return months;
    }

    public void addEventToMonth(CalEvent calEvent){
        Month n = null;
        if(!calEvent.isValid() || isIntersect(calEvent)){
            System.out.println("Event is invalid");
            return;
        }
        for(Month m : getMonths()){
            if(m.getMonthYear().equals(calEvent.getMonthYear())){
                n = m;
                break;
            }
        }
        if(n == null){
            n = new Month(calEvent.getMonthYear());
            months.add(n);
        }
        int i = binSearchRecur(calEvent,n.getEvents(),0,n.getEvents().size());
        n.addEventAt(calEvent, i);


    }

    public void editEvent(CalEvent oldEvent, CalEvent newEvent){
        deleteEvent(oldEvent);
        addEventToMonth(newEvent);
    }

    public void deleteEvent(CalEvent calEvent){
        int indexMonth = -1;
        for(Month m : getMonths()){
            if(m.getMonthYear().equals(calEvent.getMonthYear())){
                indexMonth = getMonths().indexOf(m);
                break;
            }
        }
        getMonths().get(indexMonth).removeEvent(calEvent);
        if(getMonths().get(indexMonth).getEvents().size() == 0){
            getMonths().remove(getMonths().get(indexMonth));
        }
    }

    public String toJson(){
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
                .create();
        return gson.toJson(this);
    }

    public static Calendar fromJson(String json){
        Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
                .create();
        return gson.fromJson(json, Calendar.class);
    }


    private int binSearchRecur(CalEvent calEvent,List<CalEvent> eventList, int s, int e){
        if(s == e){
            if(eventList.size() == 0){
                return 0;
            }
            if(calEvent.getStartTime().compareTo(eventList.get(s).getStartTime())>0){
                return s+1;
            }
            return s;
        }
        int mid = (s+e)/2;
        if(calEvent.getStartTime().compareTo(eventList.get(mid).getStartTime())>0){
            return binSearchRecur(calEvent,eventList, mid+1, e);
        }
        else{
            return binSearchRecur(calEvent,eventList, s, mid);
        }
    }

    private boolean isIntersect(CalEvent calEvent){
        for(Month x : getMonths()){
            if(x.getMonthYear().equals(calEvent.getMonthYear())){
                for(CalEvent y : x.getEvents()){
                    System.out.println("yeet");
                    int sitA =calEvent.getStartTime().compareTo(y.getEndTime()); // should negative if calEventStart is earlier than yEnd
                    int sitB =y.getStartTime().compareTo(calEvent.getEndTime()); // should negative if yStart is earlier then calEventEnd
                    System.out.println(sitA + " " + sitB);
                    if(sitA < 0 && sitB < 0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

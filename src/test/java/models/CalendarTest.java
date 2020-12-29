package models;

import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarTest {
    @Test
    void listOfMonthEmptyOnStart(){
        Calendar calendar = new Calendar();

        Assertions.assertEquals(0, calendar.getMonths().size());

    }

    @Test
    void shouldContainListOfMonths() {
        Calendar calendar = new Calendar();
        CalEvent calEvent = new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20211218T18:00"));
        CalEvent calEvent2 = new CalEvent("benis", dateTime("20201118T18:00"), dateTime("20211218T18:00"));
        calendar.addEventToMonth(calEvent);

        // add new event,
        Assertions.assertEquals(calendar.getMonths().size(), 1);

        // add new event, size of Month should stay the same coz Event has same MonthYear
        calendar.addEventToMonth(new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20211218T18:00")));
        Assertions.assertEquals(calendar.getMonths().size(), 1);

        // size of Month should be 2
        calendar.addEventToMonth(calEvent2);
        Assertions.assertEquals(calendar.getMonths().size() , 2);

    }

    @Test
    void canAddEvent(){
        Calendar calendar = new Calendar();
        CalEvent calEvent = new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20211218T18:00"));
        CalEvent calEvent2 = new CalEvent("benis1", dateTime("20201219T18:00"), dateTime("20211218T18:00"));
        CalEvent calEvent3 = new CalEvent("benis2", dateTime("20201220T18:00"), dateTime("20211218T18:00"));
        calendar.addEventToMonth(calEvent3);
        calendar.addEventToMonth(calEvent);
        calendar.addEventToMonth(calEvent2);



        Assertions.assertEquals(calendar.getMonths().size(), 1);
        Assertions.assertEquals(calendar.getMonths().get(0).getEvents().get(0).getName(), "benis");
        Assertions.assertEquals(calendar.getMonths().get(0).getEvents().get(1).getName(), "benis1");
        Assertions.assertEquals(calendar.getMonths().get(0).getEvents().get(2).getName(), "benis2");
    }

    @Test
    void canDeleteEvent(){
        Calendar calendar = new Calendar();
        CalEvent event1 = new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20201219T18:00"));
        CalEvent event2 = new CalEvent("benis1", dateTime("20211218T18:00"), dateTime("20221218T18:00"));
        calendar.addEventToMonth(event1);
        calendar.addEventToMonth(event1);
        calendar.addEventToMonth(event2);
        calendar.deleteEvent(event1);
        calendar.deleteEvent(event2);
        calendar.deleteEvent(event1);

        Assertions.assertEquals(calendar.getMonths().size() , 0);
    }

    @Test
    void canUpdateEvent(){
        Calendar calendar = new Calendar();
        CalEvent event2 = new CalEvent("benis1", dateTime("20211218T18:00"), dateTime("20221218T18:00"));
        CalEvent event3 = new CalEvent("benis1", dateTime("20201218T18:00"), dateTime("20221218T18:00"));
        calendar.addEventToMonth(event2);
        calendar.editEvent(event2, event3);

        Assertions.assertEquals(event3.getMonthYear(), "12-2020");
    }

    @Test
    void calEventIsValid(){
        CalEvent calEvent = new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20211218T18:00"));
        Assertions.assertTrue(calEvent.isValid());
    }

    @Test
    void calEventIsInvalid(){
        // startTime n endTime is same
        CalEvent calEvent = new CalEvent("benis", dateTime("20201218T18:00"), dateTime("20201218T18:00"));
        // startTime is after endTime
        CalEvent calEvent2 = new CalEvent("benis", dateTime("20211218T18:00"), dateTime("20201218T18:00"));
        // name is empty
        CalEvent calEvent3 = new CalEvent("", dateTime("20201218T18:00"), dateTime("20211218T18:00"));

        Assertions.assertFalse(calEvent.isValid());
        Assertions.assertFalse(calEvent2.isValid());
        Assertions.assertFalse(calEvent3.isValid());
    }

    @Test
    void checkDateTime(){
        ZonedDateTime m ;
        m = dateTime("20200912T18:00");

        Assertions.assertEquals(m, ZonedDateTime.of(2020,9, 12, 18, 0, 0, 0, ZoneId.of("+01:00")));
    }

    @Test
    void checkIntersectmethod(){
        Calendar calendar = new Calendar();
        CalEvent event2 = new CalEvent("benis1", dateTime("20211218T16:00"), dateTime("20211218T17:00"));
        CalEvent event3 = new CalEvent("benis1", dateTime("20211218T16:00"), dateTime("20211218T18:00"));
        //CalEvent event4 = new CalEvent("benis1", dateTime("20211218T17:00"), dateTime("20211218T20:00"));
        calendar.addEventToMonth(event2);
        calendar.addEventToMonth(event3);

        //Assertions.assertEquals(kalendar.isIntersect(event3), true);
        //Assertions.assertEquals(kalendar.isIntersect(event4), false);

    }


    // yyyymmddThh:mm
    private ZonedDateTime dateTime(String dateTime){
        if(dateTime.length() != 14){
            System.out.println("Error Wrong dateTime Format");
            return ZonedDateTime.now();
        }
        String time = dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8)
                + "T" + dateTime.substring(9, 11) + ":" + dateTime.substring(12, 14) + ":00+01:00";
        return ZonedDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    }
}

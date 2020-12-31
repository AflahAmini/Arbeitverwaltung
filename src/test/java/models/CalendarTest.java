package models;

import arbyte.helper.test.TestHelper;
import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class CalendarTest {
    @Test
    void listOfMonthEmptyOnStart(){
        Calendar calendar = new Calendar();

        Assertions.assertEquals(0, calendar.getMonths().size());
    }

    @Test
    void shouldContainListOfMonths() throws Exception {
        Calendar calendar = new Calendar();
        CalEvent calEvent = new CalEvent("event1", TestHelper.dateTimeFrom("20200101T00:00"), TestHelper.dateTimeFrom("20200101T03:00"));
        calendar.addEvent(calEvent);

        // A new month object should be created to contain the event
        Assertions.assertEquals(calendar.getMonths().size(), 1);

        // Months list stay the same after an event of the same monthYear is added
        calendar.addEvent(new CalEvent("event2", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T11:00")));
        Assertions.assertEquals(calendar.getMonths().size(), 1);

        // There should be 2 months in the list
        calendar.addEvent(new CalEvent("event3", TestHelper.dateTimeFrom("20200201T05:00"), TestHelper.dateTimeFrom("20200201T08:00")));
        Assertions.assertEquals(calendar.getMonths().size(), 2);
    }

    @Test
    void canAddEvent() throws Exception {
        Calendar calendar = new Calendar();
        CalEvent calEvent = new CalEvent("event1", TestHelper.dateTimeFrom("20200101T00:00"), TestHelper.dateTimeFrom("20200101T03:00"));
        CalEvent calEvent2 = new CalEvent("event2", TestHelper.dateTimeFrom("20200101T05:00"), TestHelper.dateTimeFrom("20200101T07:00"));
        CalEvent calEvent3 = new CalEvent("event3", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T11:00"));
        CalEvent calEvent4 = new CalEvent("event4", TestHelper.dateTimeFrom("20200102T09:00"), TestHelper.dateTimeFrom("20200102T11:00"));

        calendar.addEvent(calEvent3);
        calendar.addEvent(calEvent);
        calendar.addEvent(calEvent2);
        calendar.addEvent(calEvent4);

        // Events in a month should be ordered according to the start time
        Assertions.assertEquals(calendar.getMonths().size(), 1);

        List<CalEvent> events = calendar.getEventsOfMonth("01-2020");
        Assertions.assertEquals(events.size(), 4);
        Assertions.assertEquals(events.get(0).getName(), "event1");
        Assertions.assertEquals(events.get(1).getName(), "event2");
        Assertions.assertEquals(events.get(2).getName(), "event3");
        Assertions.assertEquals(events.get(3).getName(), "event4");

        // Should throw an exception if the event is invalid or overlaps
        CalEvent calEvent5 = new CalEvent("event5", TestHelper.dateTimeFrom("20200101T02:00"), TestHelper.dateTimeFrom("20200101T04:00"));
        CalEvent calEvent6 = new CalEvent("", TestHelper.dateTimeFrom("20200102T02:00"), TestHelper.dateTimeFrom("20200102T04:00"));

        Assertions.assertThrows(Exception.class, () -> calendar.addEvent(calEvent5));
        Assertions.assertThrows(Exception.class, () -> calendar.addEvent(calEvent6));
    }

    @Test
    void canDeleteEvent() throws Exception {
        Calendar calendar = new Calendar();
        CalEvent calEvent = new CalEvent("event1", TestHelper.dateTimeFrom("20200101T00:00"), TestHelper.dateTimeFrom("20200101T03:00"));
        CalEvent calEvent2 = new CalEvent("event2", TestHelper.dateTimeFrom("20200101T05:00"), TestHelper.dateTimeFrom("20200101T07:00"));
        CalEvent calEvent3 = new CalEvent("event3", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T11:00"));

        calendar.addEvent(calEvent);
        calendar.addEvent(calEvent2);
        calendar.addEvent(calEvent3);

        calendar.deleteEvent(calEvent);
        calendar.deleteEvent(calEvent2);
        Assertions.assertEquals(calendar.getMonths().size() , 1);
        Assertions.assertEquals(calendar.getEventsOfMonth("01-2020").size(), 1);

        // Month object should be deleted once the last event of the month is deleted
        calendar.deleteEvent(calEvent3);
        Assertions.assertEquals(calendar.getMonths().size() , 0);
    }

    @Test
    void canUpdateEvent() throws Exception {
        Calendar calendar = new Calendar();
        CalEvent event1 = new CalEvent("event1", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T11:00"));
        CalEvent event2 = new CalEvent("event2", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T11:50"));
        calendar.addEvent(event1);
        calendar.updateEvent(event1, event2);

        List<CalEvent> events = calendar.getEventsOfMonth(event2.getMonthYear());

        Assertions.assertEquals(events.size(), 1);
        Assertions.assertEquals(events.get(0).getEndTime().getMinute(), 50);

        // Should throw an exception if the new event is invalid, and the old event should stay unchanged
        CalEvent event3 = new CalEvent("event3", TestHelper.dateTimeFrom("20200101T09:00"), TestHelper.dateTimeFrom("20200101T00:50"));
        Assertions.assertThrows(Exception.class, () -> calendar.updateEvent(event2, event3));

        events = calendar.getEventsOfMonth(event2.getMonthYear());

        Assertions.assertEquals(events.size(), 1);
        Assertions.assertEquals(events.get(0).getEndTime().getMinute(), 50);
    }

    @Test
    void checkDateTime(){
        ZonedDateTime m ;
        m = TestHelper.dateTimeFrom("20200912T18:00");

        Assertions.assertEquals(m, ZonedDateTime.of(2020,9, 12, 18, 0, 0, 0, ZoneId.of("+01:00")));
    }
}

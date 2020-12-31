package models;

import arbyte.helper.test.TestHelper;
import arbyte.models.CalEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalEventTest {
    @Test
    void shouldBeValid() {
        CalEvent calEvent = new CalEvent("event", TestHelper.dateTimeFrom("20200101T00:00"), TestHelper.dateTimeFrom("20200101T03:00"));

        Assertions.assertTrue(calEvent.isValid());
    }

    @Test
    void shouldNotBeValid() {
        CalEvent calEvent = new CalEvent("event", TestHelper.dateTimeFrom("20200101T00:00"), null);
        CalEvent calEvent2 = new CalEvent("event2", TestHelper.dateTimeFrom("20200101T03:00"), TestHelper.dateTimeFrom("20200101T00:00"));

        Assertions.assertFalse(calEvent.isValid());
        Assertions.assertFalse(calEvent2.isValid());
    }

    @Test
    void shouldReturnCorrectMonthYear() {
        CalEvent calEvent = new CalEvent("event", TestHelper.dateTimeFrom("20200101T00:00"), TestHelper.dateTimeFrom("20200101T03:00"));

        Assertions.assertEquals(calEvent.getMonthYear(), "01-2020");
    }

    @Test
    void shouldReturnBlankMonthYearIfInvalid() {
        CalEvent calEvent2 = new CalEvent("event2", TestHelper.dateTimeFrom("20200101T03:00"), TestHelper.dateTimeFrom("20200101T00:00"));

        Assertions.assertEquals(calEvent2.getMonthYear(), "");
    }
}

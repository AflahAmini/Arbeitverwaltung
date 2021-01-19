package models;

import arbyte.models.WeekYear;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WeekYearTest {
    @Test
    void shouldReturnsFormattedString() {
        WeekYear wy = new WeekYear(2, 2025);
        WeekYear wy2 = new WeekYear(35, 2025);

        Assertions.assertEquals(wy.toString(), "02-2025");
        Assertions.assertEquals(wy2.toString(), "35-2025");
    }

    @Test
    void shouldIncrement() {
        WeekYear wy = new WeekYear(1, 2012);
        WeekYear wy2 = new WeekYear(52, 2021);

        wy.next();
        wy2.next();

        Assertions.assertEquals(wy.toString(), "02-2012");
        Assertions.assertEquals(wy2.toString(), "01-2022");
    }

    @Test
    void shouldDecrement() {
        WeekYear wy = new WeekYear(23, 2042);
        WeekYear wy2 = new WeekYear(1, 2021);

        wy.previous();
        wy2.previous();

        Assertions.assertEquals(wy.toString(), "22-2042");
        Assertions.assertEquals(wy2.toString(), "53-2020");
    }
}

package arbyte.models;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;

public class WeekYear {
    private int week;
    private int year;

    public WeekYear(int week, int year) {
        this.week = week;
        this.year = year;
    }

    public static WeekYear now() {
        LocalDate date = LocalDate.now();
        int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        int year = LocalDate.now().getYear();

        return new WeekYear(week, year);
    }

    public void next() {
        int maxWeeks = getMaxWeeks();

        week++;
        if (week > maxWeeks) {
            week = 1;
            year++;
        }
    }

    public void previous() {
        week--;
        if (week < 1) {
            year--;

            week = getMaxWeeks();
        }
    }

    @Override
    public String toString() {
        return String.format("%02d-%d", week, year);
    }

    private int getMaxWeeks() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c.getActualMaximum(Calendar.WEEK_OF_YEAR);
    }
}

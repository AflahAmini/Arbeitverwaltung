package arbyte.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

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

    public LocalDate getStartDate() {
        return LocalDate.now()
                .withYear(year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public LocalDate getEndDate() {
        return LocalDate.now()
                .withYear(year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    private int getMaxWeeks() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c.getActualMaximum(Calendar.WEEK_OF_YEAR);
    }
}

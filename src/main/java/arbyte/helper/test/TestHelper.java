package arbyte.helper.test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestHelper {

    // Returns a ZonedDateTime from a string of format yyyymmddThh:mm
    public static ZonedDateTime dateTimeFrom(String dateTime){
        if(dateTime.length() != 14){
            System.out.println("Error Wrong dateTime Format");
            return ZonedDateTime.now();
        }
        String time = dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8)
                + "T" + dateTime.substring(9, 11) + ":" + dateTime.substring(12, 14) + ":00+01:00";
        return ZonedDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

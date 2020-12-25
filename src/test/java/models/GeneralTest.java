package models;

import arbyte.models.CalEvent;
import arbyte.models.Kalendar;
import arbyte.models.User;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GeneralTest {
    public static void main(String[] args) {
        Kalendar kalendar = new Kalendar();
        CalEvent event2 = new CalEvent("benis1", dateTime("20211218T16:00"), dateTime("20211218T17:00"));
        CalEvent event3 = new CalEvent("benis1", dateTime("20221218T16:00"), dateTime("20221218T17:00"));
        kalendar.addEventToMonth(event2);
        kalendar.addEventToMonth(event3);

        System.out.println(kalendar.toJson());
        kalendar = Kalendar.fromJson("{\"months\":[{\"monthYear\":\"12-2021\",\"events\":[{\"name\":\"benis1\",\"startTime\":\"2021-12-18T16:00:00+01:00\",\"endTime\":\"2021-12-18T17:00:00+01:00\"}]},{\"monthYear\":\"12-2022\",\"events\":[{\"name\":\"benis1\",\"startTime\":\"2022-12-18T16:00:00+01:00\",\"endTime\":\"2022-12-18T17:00:00+01:00\"}]}]}");
        System.out.println(kalendar.toJson());

        User user = new User("test@gmail.com", "123", "123");
        System.out.println(user.toJson());
    }

    private  static ZonedDateTime dateTime(String dateTime){
        if(dateTime.length() != 14){
            System.out.println("Error Wrong dateTime Format");
            return ZonedDateTime.now();
        }
        String time = dateTime.substring(0, 4) + "-" + dateTime.substring(4, 6) + "-" + dateTime.substring(6, 8)
                + "T" + dateTime.substring(9, 11) + ":" + dateTime.substring(12, 14) + ":00+01:00";
        return ZonedDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    }
}

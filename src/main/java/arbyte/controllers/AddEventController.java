package arbyte.controllers;

import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class AddEventController {
    //#region FXML variables
    @FXML
    TextField eventName;
    @FXML
    JFXTimePicker eventStartTime;
    @FXML
    JFXTimePicker eventEndTime;
    @FXML
    JFXButton addEventButton;
    @FXML
    JFXButton cancelButton;
    //#endregion

    private LocalDate date;

    public void initialize(LocalDate date){
        eventStartTime.set24HourView(true);
        eventEndTime.set24HourView(true);

        this.date = date;
    }

    public void addEventButton() {
        try {
            String name = eventName.getText();
            String startTime = getFullDateTime(eventStartTime);
            String endTime = getFullDateTime(eventEndTime);

            ZonedDateTime eStartTime = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            ZonedDateTime eEndTime = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            System.out.println(startTime);

            CalEvent calEvent = new CalEvent(name, eStartTime, eEndTime);

            Calendar calendar = new Calendar();
            calendar.addEvent(calEvent);
            calendar.toJson();
            MainController.getInstance().changeView("fxml/CalendarView.fxml");
        } catch (Exception e) {
            MainController.getInstance().flash(e.getMessage(), true);
        }
    }

    public void cancelButton() {
        MainController.getInstance().changeViewAndModify("fxml/EventView.fxml",
                (Consumer<EventViewController>) controller -> controller.initialize(date));
    }

    private String getFullDateTime(JFXTimePicker timePicker) throws Exception {
        if (timePicker.getValue() == null)
            throw new Exception("Time cannot be empty!");

        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return (dateString + "T" + timePicker.getValue() + ":00" + OffsetDateTime.now().getOffset());
    }
}

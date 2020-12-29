package arbyte.controllers;

import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AddEventController {
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

    public void addEventButton(ActionEvent Event){
        String name = eventName.getName();
        ZonedDateTime eStartTime = ZonedDateTime.parse(eventStartTime.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        ZonedDateTime eEndTime = ZonedDateTime.parse(eventStartTime.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);



        CalEvent calEvent = new CalEvent(name, eStartTime, eEndTime);

        Calendar calendar = new Calendar();
        calendar.addEventToMonth(calEvent);
        calendar.toJson();
    }

    public void cancelButton(ActionEvent action){

    }


}

package arbyte.controllers;

import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class AddEventController {
    private static AddEventController addEventController;
    private int date;
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

    @FXML
    public void initialize(){
        eventStartTime.set24HourView(true);
        eventEndTime.set24HourView(true);
        addEventController = this;
    }

    public void addEventButton(ActionEvent Event) throws Exception {

        String name = eventName.getText();
        String startTime = intoString(eventStartTime);
        String endTime = intoString(eventEndTime);
        System.out.println(eventStartTime.getValue());

        if(startTime.length() != 25 || endTime.length() != 25){
            System.out.println("Error Wrong dateTime Format");
            System.out.println(startTime.length());

        }
        else {
            ZonedDateTime eStartTime = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            ZonedDateTime eEndTime = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            System.out.println(startTime);

            CalEvent calEvent = new CalEvent(name, eStartTime, eEndTime);

            if(calEvent.isValid()) {
                Calendar calendar = new Calendar();
                calendar.addEvent(calEvent);
                calendar.toJson();
                MainController.getInstance().changeView("fxml/CalendarView.fxml");
            }
            else{
                MainController.getInstance().flash("Invalid Event!", true);
            }
        }
    }

    public void cancelButton(ActionEvent action){
        MainController.getInstance().changeView("fxml/EventView.fxml");
    }

    public void setDate(String date){
        this.date = Integer.parseInt(date);
    }

    public static AddEventController getInstance(){
        return addEventController;
    }

    private String intoString(JFXTimePicker timePicker){
        String date = String.format("%02d",EventViewController.getInstance().getDate() );

        return (CalendarViewController.getInstance().yearMonth()+ "-" + date  + "T" + timePicker.getValue() +
                ":00" + OffsetDateTime.now().getOffset());

    }

}

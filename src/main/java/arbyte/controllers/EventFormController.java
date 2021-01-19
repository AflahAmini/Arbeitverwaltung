package arbyte.controllers;

import arbyte.managers.DataManager;
import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import arbyte.models.ui.FlashMessage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class EventFormController {
    //#region FXML variables
    @FXML
    Label labelTitle;
    @FXML
    TextField eventName;
    @FXML
    JFXTimePicker eventStartTime;
    @FXML
    JFXTimePicker eventEndTime;
    @FXML
    JFXButton buttonMain;
    @FXML
    JFXButton cancelButton;
    //#endregion

    private LocalDate date;
    private CalEvent event;
    private boolean isEdit;

    @FXML
    void initialize() {
        eventStartTime.set24HourView(true);
        eventEndTime.set24HourView(true);
    }

    public void initializeAdd(LocalDate date){
        this.date = date;
        isEdit = false;

        labelTitle.setText("Add Event");
        buttonMain.setText("Add");
    }

    public void initializeEdit(LocalDate date, CalEvent event) {
        this.date = date;
        this.event = event;
        isEdit = true;

        labelTitle.setText("Edit Event");
        buttonMain.setText("Update");

        eventName.setText(event.getName());
        eventStartTime.setValue(LocalTime.from(event.getStartTime()));
        eventEndTime.setValue(LocalTime.from(event.getEndTime()));
    }

    public void onAccept() {
        try {
            String name = eventName.getText();
            String startTime = getFullDateTime(eventStartTime);
            String endTime = getFullDateTime(eventEndTime);

            ZonedDateTime eStartTime = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            ZonedDateTime eEndTime = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            System.out.println(startTime);

            CalEvent newEvent = new CalEvent(name, eStartTime, eEndTime);
            Calendar calendar = DataManager.getInstance().getCalendar();
            if(!isEdit)
                calendar.addEvent(newEvent);
            else
                calendar.updateEvent(event, newEvent);

            MainController.getInstance().changeView("fxml/CalendarView.fxml");
        } catch (Exception e) {
            FlashMessage fm = new FlashMessage(e.getMessage(), true);
            MainController.getInstance().flash(fm);
        }
    }

    public void cancel() {
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
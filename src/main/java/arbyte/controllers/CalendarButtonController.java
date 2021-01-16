package arbyte.controllers;

import arbyte.models.CalEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class CalendarButtonController {

    @FXML
    Text dateNumber;
    @FXML
    HBox eventCount;

    private static CalendarButtonController calendarButtonController;
    private List<CalEvent> calEvents;

    void initInfo(int date, List<CalEvent> calEvents) {
        dateNumber.setText(String.valueOf(date));
        this.calEvents = calEvents;

        final int maxEventCount = 4;
        final double eventCircleRadius = 3;
        final Color eventCircleFill = Color.web("#ed8021");

        // Add circles by numOfEvents times but not exceeding maxEventCount
        for (int i = 0; i < Math.min(maxEventCount, calEvents.size()); i++) {
            Circle eventCircle = new Circle();
            eventCircle.setRadius(eventCircleRadius);
            eventCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(eventCircle);
        }

        // Add a smaller circle at the end if numOfEvents exceeds maxEventCount
        if (calEvents.size() > maxEventCount) {
            Circle smallerCircle = new Circle();
            smallerCircle.setRadius(eventCircleRadius / 2.5);
            smallerCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(smallerCircle);
        }
    }

    public void addButton() {
        CalendarViewController.getInstance().setDate(Integer.parseInt(getDateNumber()));
        MainController.getInstance().changeView("fxml/EventView.fxml");
    }
  
    public static CalendarButtonController getInstance(){
        return calendarButtonController;
    }
  
    public String getDateNumber(){
        return this.dateNumber.getText();
    }

}

package arbyte.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.function.Consumer;

public class CalendarButtonController {
    @FXML
    Text dateNumber;
    @FXML
    HBox eventCount;

    private LocalDate date;

    public void initialize(LocalDate date, int numOfEvents) {
        this.date = date;

        dateNumber.setText(String.format("%02d", date.getDayOfMonth()));

        final int maxEventCount = 4;
        final double eventCircleRadius = 3;
        final Color eventCircleFill = Color.web("#ed8021");

        // Add circles by numOfEvents times but not exceeding maxEventCount
        for (int i = 0; i < Math.min(maxEventCount, numOfEvents); i++) {
            Circle eventCircle = new Circle();
            eventCircle.setRadius(eventCircleRadius);
            eventCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(eventCircle);
        }

        // Add a smaller circle at the end if numOfEvents exceeds maxEventCount
        if (numOfEvents > maxEventCount) {
            Circle smallerCircle = new Circle();
            smallerCircle.setRadius(eventCircleRadius / 2.5);
            smallerCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(smallerCircle);
        }
    }

    public void switchToEventView() {
        MainController.getInstance().changeViewAndModify("fxml/EventView.fxml",
                (Consumer<EventViewController>) controller -> controller.initialize(date));
    }
}
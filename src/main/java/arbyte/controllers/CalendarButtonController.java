package arbyte.controllers;

import arbyte.helper.SceneHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class CalendarButtonController {

    @FXML
    Text dateNumber;

    @FXML
    HBox eventCount;

    void initInfo(int date, int numOfEvents) {
        dateNumber.setText(String.valueOf(date));

        final int maxEventCount = 4;
        final double eventCircleRadius = 3;
        final Color eventCircleFill = Color.web("#ed8021");

        for (int i = 0; i < Math.min(maxEventCount, numOfEvents); i++) {
            Circle eventCircle = new Circle();
            eventCircle.setRadius(eventCircleRadius);
            eventCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(eventCircle);
        }

        if (numOfEvents > maxEventCount) {
            Circle smallerCircle = new Circle();
            smallerCircle.setRadius(eventCircleRadius / 2);
            smallerCircle.setFill(eventCircleFill);

            eventCount.getChildren().add(smallerCircle);
        }
    }

    public void addButton(ActionEvent Event){
        SceneHelper.showAddEvent();
    }

}

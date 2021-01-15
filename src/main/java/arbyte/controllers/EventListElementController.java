package arbyte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EventListElementController {

    @FXML
    Label labelFrom;
    @FXML
    Label labelTo;
    @FXML
    Label labelEventName;

    public void setValues(String from, String to, String eventName){
        labelFrom.setText(from);
        labelTo.setText(to);
        labelEventName.setText(eventName);
    }
}

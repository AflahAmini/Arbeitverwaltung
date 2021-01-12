package arbyte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FlashController {

    @FXML
    private Label labelMessage;

    public void setMessage(String message) {
        labelMessage.setText(message);
    }
}

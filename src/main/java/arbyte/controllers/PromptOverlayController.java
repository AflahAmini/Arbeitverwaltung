package arbyte.controllers;

import arbyte.helper.SceneHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PromptOverlayController {
    @FXML
    AnchorPane promptContainer;

    @FXML
    void initialize(){
        try {
            FXMLLoader loader = SceneHelper.getFXMLLoader("fxml/PromptConfirmation.fxml");
            Parent prompt = loader.load();
            promptContainer.getChildren().add(prompt);
            AnchorPane.setTopAnchor(prompt, 0.0);
            AnchorPane.setBottomAnchor(prompt, 0.0);
            AnchorPane.setRightAnchor(prompt, 0.0);
            AnchorPane.setLeftAnchor(prompt, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }    }
}

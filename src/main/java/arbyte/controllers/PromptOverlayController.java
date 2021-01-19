package arbyte.controllers;

import arbyte.helper.SceneHelper;
import arbyte.models.ui.PromptType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class PromptOverlayController {

    @FXML
    BorderPane root;

    void initialize(PromptType promptType, Runnable onConfirm, Runnable onCancel) {
        try {
            FXMLLoader loader = SceneHelper.getFXMLLoader(getFxmlPathFromType(promptType));
            Parent prompt = loader.load();
            root.setCenter(prompt);

            PromptController controller = loader.getController();
            controller.initialize(onConfirm, onCancel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFxmlPathFromType(PromptType promptType) throws Exception {
        switch (promptType) {
            case CONFIRMATION:
                return "fxml/prompts/PromptConfirmation.fxml";
            case LOGOUT:
                return "fxml/prompts/PromptLogOut.fxml";
            default:
                throw new Exception("Invalid prompt type!");
        }
    }
}

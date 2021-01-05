package arbyte.controllers;

import arbyte.helper.SceneHelper;
import arbyte.models.Session;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainController {

    private Session curSession;

    @FXML
    AnchorPane mainView;

    @FXML
    Label labelMonthYear;

    @FXML
    public void initialize() {
        curSession = new Session();
        changeView("fxml/CalendarView.fxml");

    }

    public void changeView( String fxmlpath ){
        Parent parent = SceneHelper.getParentFromFXML(fxmlpath);
        mainView.getChildren().clear();

        mainView.getChildren().add(parent);
        AnchorPane.setTopAnchor(parent, 0.0);
        AnchorPane.setBottomAnchor(parent, 0.0);
        AnchorPane.setLeftAnchor(parent, 0.0);
        AnchorPane.setRightAnchor(parent, 0.0);
    }
}

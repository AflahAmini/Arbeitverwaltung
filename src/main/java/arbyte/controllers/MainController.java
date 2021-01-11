package arbyte.controllers;

import arbyte.helper.GlobalMouseListener;
import arbyte.helper.SceneHelper;
import arbyte.models.Session;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.jnativehook.GlobalScreen;

import java.time.Instant;

public class MainController {

    private Session curSession;
    private static MainController mainController;
    @FXML
    AnchorPane mainView;

    @FXML
    Label labelMonthYear;

    @FXML
    public void initialize() {
        curSession = new Session(1);
        changeView("fxml/CalendarView.fxml");
        mainController = this;
        try {
            GlobalScreen.registerNativeHook();
            GlobalMouseListener mouse = new GlobalMouseListener();
            GlobalScreen.addNativeMouseMotionListener(mouse);


        }
        catch(Exception e){
            e.printStackTrace();
        }
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
    public static MainController getInstance(){
        return mainController;
    }
    public Session getCurSession() {
        return curSession;
    }
}

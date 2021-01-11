package arbyte.controllers;

import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class EventViewController {
    @FXML
    JFXListView<HBox> listView;

    @FXML
    JFXButton addEventButton;

    @FXML
    JFXButton editButton;

    @FXML
    JFXButton backButton;

    @FXML
    Label labelDate;

    @FXML
    Label labelMonth;

    @FXML
    public void initialize(){
        FXMLLoader loader =  SceneHelper.getFXMLLoader("fxml/EventListElement.fxml");
        try {
            listView.getItems().add(loader.load());
            EventListElementController controller = loader.getController();

            controller.setValues("123", "123", "123");

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}

package arbyte.controllers;

import arbyte.managers.DataManager;
import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.function.Consumer;

public class EventViewController {

    //#region FXML variables
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
    //#endregion

    private LocalDate date;

    public void initialize(LocalDate date) {
        this.date = date;

        labelMonth.setText(String.format("%02d", date.getMonthValue()));
        labelDate.setText(String.format("%02d", date.getDayOfMonth()));

        //get the Event that has same date and monthYear,then add to listView
        DataManager.getInstance().lastEventsList.forEach(calEvent -> {
            FXMLLoader loader =  SceneHelper.getFXMLLoader("fxml/EventListElement.fxml");
            try {
                listView.getItems().add(loader.load());
                EventListElementController controller = loader.getController();
                controller.setValues(calEvent.getFormattedStartTime(), calEvent.getFormattedEndTime(),
                        calEvent.getName());
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public void addEventButton(){
        MainController.getInstance().changeViewAndModify("fxml/EventAdd.fxml",
                (Consumer<AddEventController>) controller -> controller.initialize(date));
    }

    public void backButton(){
        MainController.getInstance().changeView("fxml/CalendarView.fxml");
    }
}

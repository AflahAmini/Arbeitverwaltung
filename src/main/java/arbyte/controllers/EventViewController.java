package arbyte.controllers;

import arbyte.managers.DataManager;
import arbyte.helper.SceneHelper;
import arbyte.models.CalEvent;
import arbyte.models.ui.PromptType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.function.Consumer;

public class EventViewController {

    //#region FXML variables
    @FXML
    JFXListView<HBox> listView;
    @FXML
    JFXButton buttonEdit;
    @FXML
    JFXButton buttonDelete;
    @FXML
    Label labelDate;
    @FXML
    Label labelMonth;
    //#endregion

    private LocalDate date;
    private int selectedIndex = -1;

    public void initialize(LocalDate date) {
        this.date = date;

        labelMonth.setText(String.format("%02d", date.getMonthValue()));
        labelDate.setText(String.format("%02d", date.getDayOfMonth()));

        buttonEdit.setDisable(true);
        buttonDelete.setDisable(true);

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

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectedIndexProperty().addListener((observableValue, before, after) -> {
            selectedIndex = after.intValue();

            if(after.intValue() > -1){
                buttonEdit.setDisable(false);
                buttonDelete.setDisable(false);
            }
            else{
                buttonEdit.setDisable(true);
                buttonDelete.setDisable(true);
            }
        });
    }

    public void addEvent(){
        MainController.getInstance().changeViewAndModify("fxml/EventForm.fxml",
                (Consumer<EventFormController>) controller -> controller.initializeAdd(date));
    }

    public void editEvent(){
        if(selectedIndex == -1) return;

        CalEvent event = DataManager.getInstance().lastEventsList.get(selectedIndex);

        MainController.getInstance().changeViewAndModify("fxml/EventForm.fxml",
                (Consumer<EventFormController>) controller -> controller.initializeEdit(date, event));
    }

    public void deleteEvent(){
        if(selectedIndex == -1) return;

        MainController.getInstance().showPrompt(PromptType.CONFIRMATION, () ->
            Platform.runLater(() -> {
                CalEvent event = DataManager.getInstance().lastEventsList.get(selectedIndex);
                DataManager.getInstance().getCalendar().deleteEvent(event);

                DataManager.getInstance().lastEventsList.remove(selectedIndex);
                listView.getItems().remove(selectedIndex);
            })
        );
    }

    public void back(){
        MainController.getInstance().changeView("fxml/CalendarView.fxml");
    }
}

package arbyte.controllers;

import arbyte.helper.SceneHelper;
import arbyte.helper.test.TestHelper;
import arbyte.models.CalEvent;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;



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
  
    private static EventViewController eventViewController;
    private int date;
    private int month;

    @FXML
    public void initialize(){
        eventViewController = this;

        setDate(String.valueOf(CalendarViewController.getInstance().getDate()));
        setMonth(CalendarViewController.getInstance().yearMonth().substring(5));
        setDateMonth();

        for(int i = 0; i < 10; i++){
            CalEvent event6 = new CalEvent("event", TestHelper.dateTimeFrom("20200103T02:00"),
                    TestHelper.dateTimeFrom("20200103T03:00"));

            addToListView(event6);
        }
    }

    public void addEventButton(ActionEvent e){
        MainController.getInstance().changeView("fxml/EventAdd.fxml");
    }

    public void backButton(ActionEvent e){
        MainController.getInstance().changeView("fxml/CalendarView.fxml");
    }

    public static EventViewController getInstance(){
        return eventViewController;
    }

    public void setDateMonth(){
        labelDate.setText(String.format("%02d",date));
        labelMonth.setText(String.format("%02d",month));
    }

    public void setDate(String date){
        this.date = Integer.parseInt(date);
    }

    public void setMonth(String month){
        this.month = Integer.parseInt(month);
    }

    public int getDate(){
        return date;
    }

    private void addToListView (CalEvent calEvent){
        FXMLLoader loader =  SceneHelper.getFXMLLoader("fxml/EventListElement.fxml");
        try {
            listView.getItems().add(loader.load());
            EventListElementController controller = loader.getController();
            controller.setValues(calEvent.getFormattedStartTime(), calEvent.getFormattedEndTime(), calEvent.getName());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

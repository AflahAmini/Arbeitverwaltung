package arbyte.controllers;

import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;



public class EventViewController {
    private static EventViewController eventViewController;
    private int date;
    private int month;
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
        eventViewController = this;
        FXMLLoader loader =  SceneHelper.getFXMLLoader("fxml/EventListElement.fxml");
        setDate(String.valueOf(CalendarViewController.getInstance().getDate()));
        setMonth(CalendarViewController.getInstance().yearMonth().substring(5,
                CalendarViewController.getInstance().yearMonth().length()));
        setDateMonth();

        try {
            listView.getItems().add(loader.load());
            EventListElementController controller = loader.getController();

            controller.setValues("123", "123", "123");

        }
        catch(Exception e){
            e.printStackTrace();
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
}

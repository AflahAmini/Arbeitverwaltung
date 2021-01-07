package arbyte.controllers;

import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;

public class CalendarViewController {
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private int month = Calendar.getInstance().get(Calendar.MONTH)+1;

    @FXML //CalendarView
    JFXButton btnPrevious;
    @FXML
    JFXButton btnNext;
    @FXML
    Label labelMonthYear;
    @FXML
    GridPane gridCalendar;

    @FXML
    public void initialize(){
        labelMonthYear.setText(month + "/" + year);
        addButtonToCalendarView();
    }

    public void previousButton(ActionEvent Event){
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }
        labelMonthYear.setText(month + "/" + year);
        addButtonToCalendarView();
    }

    public void nextButton(ActionEvent Event){
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
        labelMonthYear.setText(month + "/" + year);
        addButtonToCalendarView();
    }

    private void addButtonToCalendarView() {
        gridCalendar.getChildren().clear();

        YearMonth yearMonthObject = YearMonth.of(year, month); //to get number of days in a month
        LocalDate firstOfMonth = yearMonthObject.atDay( 1 );

        int counter = 0;
        int day = firstOfMonth.getDayOfWeek().getValue();
        int incrementX = day == 7 ? 0 : day;
        int incrementY = 0;

        try {
            while(counter < yearMonthObject.getMonth().maxLength()){
                FXMLLoader btnLoader = SceneHelper.getFXMLLoader("fxml/CalendarButton.fxml");

                if(incrementX < gridCalendar.getColumnConstraints().size()){
                    gridCalendar.add(btnLoader.load(), incrementX, incrementY);

                    CalendarButtonController btnController = btnLoader.getController();
                    btnController.initInfo(counter + 1, 3);

                    incrementX++;
                    counter++;
                }
                else{
                    incrementX = 0;
                    incrementY++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

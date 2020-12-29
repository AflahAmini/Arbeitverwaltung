package arbyte.controllers;

import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.YearMonth;
import java.util.Calendar;

public class CalendarViewController {
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private int month = Calendar.getInstance().get(Calendar.MONTH)+1;
    private int bufferMonth = 0;
    private int bufferYear = 0;

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
        labelMonthYear.setText(month + "//" + year);
        addButtonToCalendarView(year, month);
    }

// TO-DO
// access the month array, get Month Year, then display on labelMonthYear
    public void previousButton(ActionEvent Event){
        bufferMonth--;
        bufferCheck(bufferMonth);
        labelMonthYear.setText((month + bufferMonth) + "//" + (year + bufferYear));
        addButtonToCalendarView((year + bufferYear), (month + bufferMonth));
    }

    public void nextButton(ActionEvent Event){
        bufferMonth++;
        bufferCheck(month + bufferMonth);
        labelMonthYear.setText((month + bufferMonth) + "//" + (year + bufferYear));
        addButtonToCalendarView((year + bufferYear), (month + bufferMonth));
    }

    private void addButtonToCalendarView(int year, int month) {
        gridCalendar.getChildren().clear();

        YearMonth yearMonthObject =  YearMonth.of(year, month); //to get number of days in a month
        int counter = 0;

        outerloop:
        for(int j = 0; j < gridCalendar.getRowConstraints().size(); j++){
            for (int i = 0; i < gridCalendar.getColumnConstraints().size(); i++) {
                Parent parent = SceneHelper.getParentFromFXML("fxml/CalendarButton.fxml");
                gridCalendar.add(parent, i, j);
                counter++;

                if(counter >= yearMonthObject.getMonth().maxLength()){
                    break outerloop;
                }
            }
        }
    }

    private void bufferCheck(int i){
        if(i > 12){
            bufferMonth = bufferMonth -12;
            bufferYear++;
        }
        else if(i <= -12){
            bufferMonth = bufferMonth +12;
            bufferYear--;
        }

    }

}

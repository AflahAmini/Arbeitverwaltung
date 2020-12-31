package arbyte.controllers;

import arbyte.helper.SceneHelper;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
        LocalDate firstOfMonth = yearMonthObject.atDay( 1 );

        int counter = 0;
        int day = firstOfMonth.getDayOfWeek().getValue();
        int startingX = day ==7 ? 0 : day;
        int incrementX = startingX;
        int incrementY = 0;


        while(counter < yearMonthObject.getMonth().maxLength()){
            Parent parent = SceneHelper.getParentFromFXML("fxml/CalendarButton.fxml");
            System.out.println(startingX + " " + counter + " " + yearMonthObject.getMonth().maxLength());
            if(incrementX < gridCalendar.getColumnConstraints().size()){
                gridCalendar.add(parent, incrementX, incrementY);
                incrementX++;
                counter++;
            }
            else{
                incrementX = 0;
                incrementY++;
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

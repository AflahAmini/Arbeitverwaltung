package arbyte.controllers;

import arbyte.helper.DataManager;
import arbyte.helper.SceneHelper;
import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalendarViewController {

    //#region FXML variables
    @FXML
    JFXButton btnPrevious;
    @FXML
    JFXButton btnNext;
    @FXML
    Label labelMonthYear;
    @FXML
    GridPane gridCalendar;
    //#endregion

    private int year = LocalDateTime.now().getYear();
    private int month = LocalDateTime.now().getMonthValue();

    private Calendar calendar;

    @FXML
    public void initialize(){
        calendar = DataManager.getInstance().getCalendar();

        updateCalendarView();
    }

    public void previousButton(){
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }
        updateCalendarView();
    }

    public void nextButton(){
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
        updateCalendarView();
    }

    private void updateCalendarView() {
        labelMonthYear.setText(month + "/" + year);
        generateCalendarButtons();
    }

    private void generateCalendarButtons() {
        gridCalendar.getChildren().clear();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = yearMonth.atDay( 1 );

        int dayCount = 0;
        int firstDay = firstOfMonth.getDayOfWeek().getValue();

        // Transforms firstDay from 1-7 (starting Monday) into 0-6 (starting Sunday)
        int x = firstDay == 7 ? 0 : firstDay;
        int y = 0;

        String monthYear = String.format("%02d-%d", month, year);
        Stack<CalEvent> monthEventStack = new Stack<>();
        List<CalEvent> monthEventList = calendar.getEventsOfMonth(monthYear);

        for(int i = monthEventList.size()-1; i >= 0; i--){
            monthEventStack.push(monthEventList.get(i));
        }

        // Iterate until the last day of the month
        while(dayCount < yearMonth.lengthOfMonth()){
            FXMLLoader btnLoader = SceneHelper.getFXMLLoader("fxml/CalendarButton.fxml");

            if(x < gridCalendar.getColumnConstraints().size()){
                try {
                    gridCalendar.add(btnLoader.load(), x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Sets the values in the calendar button
                CalendarButtonController btnController = btnLoader.getController();

                LocalDate date = LocalDate.of(year, month, dayCount + 1);
                btnController.initialize(date, getEventsOfDay(monthEventStack, dayCount + 1 ).size());

                // Jump to next column, same row
                x++;
                dayCount++;
            }
            else{
                // Jump to next row, first column
                x = 0;
                y++;
            }
        }
    }

    private List<CalEvent> getEventsOfDay(Stack<CalEvent> stack, int desiredDay){
        List<CalEvent> eventsList = new ArrayList<>();

        while(!stack.empty() && stack.peek().getStartTime().getDayOfMonth() == desiredDay){
            eventsList.add(stack.pop());
        }

        return eventsList;
    }
}

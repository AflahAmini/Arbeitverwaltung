package arbyte.controllers;

import arbyte.managers.DataManager;
import arbyte.models.SessionData;
import arbyte.models.WeekYear;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeeklyReportController {
    private static final WeekYear weekYear = WeekYear.now();

    @FXML
    Label labelWeekYear;
    @FXML
    StackedBarChart<String, Number> stackedBarChart;

    private final XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> inactiveSeries = new XYChart.Series<>();

    @FXML
    void initialize() {
        activeSeries.setName("Active");
        inactiveSeries.setName("Inactive");

        for (int i = 0; i < 7; i++) {
            activeSeries.getData().add(new XYChart.Data<>(getDayName(i + 1), 0));
            inactiveSeries.getData().add(new XYChart.Data<>(getDayName(i + 1), 0));
        }

        stackedBarChart.getData().add(activeSeries);
        stackedBarChart.getData().add(inactiveSeries);

        update();
    }

    public void nextWeekYear() {
        weekYear.next();
        update();
    }

    public void prevWeekYear() {
        weekYear.previous();
        update();
    }

    private void update() {
        labelWeekYear.setText(weekYear.toString());

        for (int i = 0; i < 7; i++) {
            activeSeries.getData().get(i).setYValue(0);
            inactiveSeries.getData().get(i).setYValue(0);
        }

        DataManager.getInstance().fetchSessions(weekYear, sessions -> {
            for (SessionData s: sessions) {
                int i = s.getDayOfWeek() - 1;
                double activeHours = s.getActiveDuration() / 3600.0;
                double inactiveHours = s.getInactiveDuration() / 3600.0;

                activeSeries.getData().get(i).setYValue(activeHours);
                inactiveSeries.getData().get(i).setYValue(inactiveHours);
            }
        });
    }

    private String getDayName(int dayOfWeek) {
        DayOfWeek d = DayOfWeek.of(dayOfWeek);
        return d.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}

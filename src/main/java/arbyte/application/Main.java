package arbyte.application;

import arbyte.helper.SceneHelper;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    @Override

    public void start(Stage primaryStage) {
        SceneHelper.initialise(primaryStage);
        SceneHelper.showLogInPage();

        Platform.setImplicitExit(true);

        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}


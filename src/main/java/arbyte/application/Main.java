package arbyte.application;

import arbyte.helper.SceneHelper;
import arbyte.managers.ExecutorServiceManager;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    @Override

    public void start(Stage primaryStage) {
        SceneHelper.initialise(primaryStage);
        SceneHelper.showLogInPage();

        // Makes sure the program exits properly
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest(windowEvent -> {
            ExecutorServiceManager.shutdownAll();

            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}


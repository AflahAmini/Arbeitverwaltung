package arbyte.helper;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class SceneHelper {

    private static Stage currentStage;

    public static void initialise(Stage stage){
        currentStage = stage;
        currentStage.setResizable(false);
    }

    public static void showLogInPage(){
        SceneHelper.switchSceneResource("fxml/LoginPage.fxml", "Log In", 600, 400);

    }

    public static void showRegisterPage(){
        SceneHelper.switchSceneResource("fxml/RegisterPage.fxml", "Register", 600, 400);
    }

    public static void showMainPage(){
        SceneHelper.switchSceneResource("fxml/MainScene.fxml", "Main", 900, 600);
    }

    public static void showAddEvent(){
        SceneHelper.switchSceneResource("fxml/EventAdd.fxml", "Add Event", 600, 400);
    }


    private static void switchSceneResource(String filename, String scenename, int height, int width){

        try{
            ResourceLoader loader = new ResourceLoader();
            Parent source = FXMLLoader.load(loader.getURLFromResource(filename));
            Scene scene = new Scene(source, height, width);
            currentStage.setScene(scene);
            currentStage.show();
        }
        catch(Exception e){
            System.out.println(scenename +" view failed");
            e.printStackTrace();
        }

    }



}

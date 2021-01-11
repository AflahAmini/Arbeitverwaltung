package arbyte.helper;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SceneHelper {

    private static final ResourceLoader resourceLoader = new ResourceLoader();

    private static Stage currentStage;

    public static void initialise(Stage stage){
        currentStage = stage;
        currentStage.setResizable(false);
    }

    public static void showLogInPage(){
        SceneHelper.switchSceneResource("fxml/LoginPage.fxml", 600, 400);
    }

    public static void showRegisterPage(){
        SceneHelper.switchSceneResource("fxml/RegisterPage.fxml",600, 400);
    }

    public static void showMainPage(){
        SceneHelper.switchSceneResource("fxml/MainScene.fxml", 900, 600);
    }

    public static void showAddEvent(){
        SceneHelper.switchSceneResource("fxml/EventAdd.fxml", 600, 400);
    }

    public static Parent getParentFromFXML(String filename) {
        try {
            return FXMLLoader.load(resourceLoader.getURL(filename));
        }
        catch(Exception e){
            System.out.println(filename + " view failed");
            e.printStackTrace();
        }
        return new BorderPane();
    }

    public static FXMLLoader getFXMLLoader(String filename) {
        try {
            return new FXMLLoader(resourceLoader.getURL(filename));
        } catch (Exception e) {
            System.out.println(filename + " getFXMLLoader failed");
        }
        return new FXMLLoader();
    }



    private static void switchSceneResource(String filename,  int height, int width){
            Scene scene = new Scene(getParentFromFXML(filename), height, width);
            currentStage.setScene(scene);
            currentStage.show();
    }
}

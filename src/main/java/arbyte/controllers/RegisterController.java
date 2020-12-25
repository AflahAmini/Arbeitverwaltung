package arbyte.controllers;

import arbyte.helper.SceneHelper;
import arbyte.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;



public class RegisterController {
    @FXML
    Text error;

    @FXML
    TextField emailField;

    @FXML
    PasswordField passField;

    @FXML
    PasswordField conpassField;

    @FXML
    public void initialize(){
        error.setText("");
    }

    public void registerButton(ActionEvent Event){
        User user = new User(emailField.getText(), passField.getText(), conpassField.getText());

        if(user.registerUser()){
            System.out.println("You have been successfully registered!");
            setError("You have been successfully registered!");
            SceneHelper.showMainPage(); // patut nanti terus logIn bila dah register
        }
        else{
            setError("");
            setError("Registration failed");
        }
    }

    public void switchToLogIn(ActionEvent Event){
        SceneHelper.showLogInPage();
    }


    private void setError(String msg){
        error.setText(msg);
    }



}

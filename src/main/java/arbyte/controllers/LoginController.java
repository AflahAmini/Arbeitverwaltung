package arbyte.controllers;

import arbyte.helper.ResourceLoader;
import arbyte.helper.SceneHelper;
import arbyte.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {

    @FXML
    Text error;

    @FXML
    TextField emailField;

    @FXML
    PasswordField passField;

    @FXML
    public void initialize(){
        error.setText("");

    }

    // method to pass userInfo into programme
    public void buttonLogin(ActionEvent Event){
        User user = new User(emailField.getText(), passField.getText(), passField.getText());

        if(user.authenticate()){
            System.out.println("Successful!");
            setError("You have been successfully logged in!");
            //SceneHelper.showMainPage();
            SceneHelper.showAddEvent();
        }
        else{
            setError("Username or Password is wrong!");
        }
    }

    public void switchToRegister(ActionEvent Event){
        SceneHelper.showRegisterPage();
    }


    // nak guna method ni ke ? sebab apa ? apa beza ngan .setText()?
    private void setError(String msg){
        error.setText(msg);
    }

}

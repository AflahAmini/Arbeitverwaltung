package arbyte.controllers;

import arbyte.helper.HttpRequestHandler;
import arbyte.helper.HttpUtils;
import arbyte.helper.SceneHelper;
import arbyte.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    Button btnRegister;

    @FXML
    Button btnCancel;

    @FXML
    public void initialize(){
        error.setText("");
    }

    public void registerButton(ActionEvent Event){
        User user = new User(emailField.getText(), passField.getText(), conpassField.getText());

        if (user.isValid()) {
            btnCancel.setDisable(true);
            btnRegister.setDisable(true);

            HttpRequestHandler.postRequest("/register", user.toJson())
                .thenAcceptAsync((response) -> {
                    btnCancel.setDisable(false);
                    btnRegister.setDisable(false);

                    boolean success = HttpUtils.getBodyProperty("success", response).getAsBoolean();

                    if (success) {
                        String accessToken = HttpUtils.getBodyProperty("accessToken", response).getAsString();

                        System.out.println(accessToken);
                    } else {
                        String message = HttpUtils.getBodyProperty("message", response).getAsString();

                        setError(message);
                    }
                }).exceptionally( e -> {
                    setError("Unable to connect to the server");

                    btnCancel.setDisable(false);
                    btnRegister.setDisable(false);

                    return null;
                });
        } else {
            setError("Fields cannot be empty!");
        }
    }

    public void switchToLogIn(ActionEvent Event){
        SceneHelper.showLogInPage();
    }


    private void setError(String msg){
        error.setText(msg);
    }



}

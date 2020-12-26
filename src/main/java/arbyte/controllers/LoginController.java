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

import java.util.concurrent.*;

public class LoginController {

    @FXML
    Text error;

    @FXML
    TextField emailField;

    @FXML
    PasswordField passField;

    @FXML
    Button btnLogin;

    @FXML
    Button btnRegister;

    @FXML
    public void initialize(){
        error.setText("");

    }

    // Sends a POST request to /login and waits until a response is given.
    // While waiting the login and register buttons should be disabled.
    public void buttonLogin(ActionEvent Event){
        User user = new User(emailField.getText(), passField.getText(), passField.getText());

        if (user.isValid()) {
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);

            HttpRequestHandler.postRequest("/login", user.toJson())
            .thenAcceptAsync((response) -> {
                btnLogin.setDisable(false);
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

                btnLogin.setDisable(false);
                btnRegister.setDisable(false);

                return null;
            } );
        } else {
            setError("Fields cannot be empty!");
        }
    }

    public void switchToRegister(ActionEvent Event){
        SceneHelper.showRegisterPage();
    }

    private void setError(String msg){
        error.setText(msg);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> error.setText(""), 3, TimeUnit.SECONDS);
    }

}

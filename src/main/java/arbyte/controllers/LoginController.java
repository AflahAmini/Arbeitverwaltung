package arbyte.controllers;

import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import arbyte.helper.SceneHelper;
import arbyte.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
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
    public void buttonLogin(){
        User user = new User(emailField.getText(), passField.getText(), passField.getText());

        if (user.isValid()) {
            // Disable the buttons upon clicking
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);

            HttpRequestHandler reqHandler = HttpRequestHandler.getInstance();

            reqHandler.request(RequestType.POST, "/login", user.toJson())
                .thenAccept((response) -> {
                    btnLogin.setDisable(false);
                    btnRegister.setDisable(false);

                    JsonObject responseBody = new Gson().fromJson(response.body(), JsonObject.class);
                    boolean success = responseBody.get("success").getAsBoolean();

                    if (success) {
                        String accessToken = responseBody.get("accessToken").getAsString();
                        String refreshToken = responseBody.get("refreshToken").getAsString();

                        reqHandler.setAccessToken(accessToken);
                        reqHandler.setRefreshToken(refreshToken);

                        Platform.runLater(SceneHelper::showMainPage);
                    } else {
                        String message = responseBody.get("message").getAsString();

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

    public void switchToRegister(){
        SceneHelper.showRegisterPage();
    }

    private void setError(String msg){
        error.setText(msg);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> error.setText(""), 3, TimeUnit.SECONDS);
    }

}

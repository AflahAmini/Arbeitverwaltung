package arbyte.controllers;

import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import arbyte.helper.SceneHelper;
import arbyte.helper.Hasher;
import arbyte.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
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

            reqHandler.request(RequestType.POST, "/register", user.toJson())
            .thenAccept((response) -> {
                JsonObject responseBody = null;
                try {
                    responseBody = reqHandler.getResponseBodyJson(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert responseBody != null;

                if (response.getStatusLine().getStatusCode() == 200) {
                    String accessToken = responseBody.get("accessToken").getAsString();
                    String refreshToken = responseBody.get("refreshToken").getAsString();

                    reqHandler.setAccessToken(accessToken);
                    reqHandler.setRefreshToken(refreshToken);

                    Hasher.storeCredentials("userInfo/userInfo.txt",
                            emailField.getText(),
                            passField.getText());

                    Platform.runLater(() -> {
                        SceneHelper.showMainPage();
                        MainController.getInstance().flash("Login successful!", false);
                    });
                } else {
                    String message = responseBody.get("error").getAsString();

                    setError(message);
                }
            }).exceptionally(e -> {
                if (e instanceof AssertionError) {
                    setError("Error while parsing response JSON!");
                } else {
                    if(Hasher.getEmailPasswordHash("userInfo/userInfo.txt", emailField.getText(), passField.getText())){
                        Platform.runLater(() -> {
                            SceneHelper.showMainPage();
                            MainController.getInstance().flash("Logged in using last known credentials.", false);
                        });
                    }
                    else {
                        setError("Unable to connect to the server");

                        btnLogin.setDisable(false);
                        btnRegister.setDisable(false);
                    }
                }
                return null;
            }).whenComplete((m, t) -> {
                btnLogin.setDisable(false);
                btnRegister.setDisable(false);
            });
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

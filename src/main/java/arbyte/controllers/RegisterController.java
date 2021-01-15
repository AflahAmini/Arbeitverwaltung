package arbyte.controllers;

import arbyte.helper.DataManager;
import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import arbyte.helper.Hasher;
import arbyte.helper.SceneHelper;
import arbyte.models.User;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.concurrent.*;


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

    public void registerButton(){
        User user = new User(emailField.getText(), passField.getText(), conpassField.getText());

        if (user.isValid()) {
            btnCancel.setDisable(true);
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

                    int id = responseBody.get("id").getAsInt();

                    reqHandler.setAccessToken(accessToken);
                    reqHandler.setRefreshToken(refreshToken);

                    Hasher.storeCredentials("userInfo/userInfo.txt",
                            emailField.getText(),
                            passField.getText());

                    Platform.runLater(() -> {
                        user.clearPasswords();
                        user.id = id;
                        DataManager.getInstance().initialize(user, true);

                        SceneHelper.showMainPage();
                        MainController.getInstance().flash("Register successful!", false);
                    });
                } else {
                    String message = responseBody.get("error").getAsString();

                    setError(message);
                }
            }).exceptionally(e -> {
                if (e instanceof AssertionError) {
                    setError("Error while parsing response JSON!");
                } else {
                    setError("Unable to connect to the server");
                    e.printStackTrace();
                }
                return null;
            }).whenComplete((m, t) -> {
                btnCancel.setDisable(false);
                btnRegister.setDisable(false);
            });
        } else {
            setError("Fields cannot be empty!");
        }
    }

    public void switchToLogIn(){
        SceneHelper.showLogInPage();
    }

    private void setError(String msg){
        error.setText(msg);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> error.setText(""), 3, TimeUnit.SECONDS);
    }
}

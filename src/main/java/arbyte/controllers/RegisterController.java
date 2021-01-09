package arbyte.controllers;

import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import arbyte.helper.Hasher;
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
                    btnCancel.setDisable(false);
                    btnRegister.setDisable(false);

                    JsonObject responseBody = new Gson().fromJson(response.body(), JsonObject.class);

                    if (response.statusCode() == 200) {
                        String accessToken = responseBody.get("accessToken").getAsString();
                        String refreshToken = responseBody.get("refreshToken").getAsString();

                        reqHandler.setAccessToken(accessToken);
                        reqHandler.setRefreshToken(refreshToken);

                        Hasher.storeCredentials("userInfo/userInfo.txt", emailField.getText(), passField.getText());

                        Platform.runLater(SceneHelper::showMainPage);
                    } else {
                        String message = responseBody.get("error").getAsString();

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

    public void switchToLogIn(){
        SceneHelper.showLogInPage();
    }

    private void setError(String msg){
        error.setText(msg);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> error.setText(""), 3, TimeUnit.SECONDS);
    }
}

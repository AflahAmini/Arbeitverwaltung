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

import java.io.IOException;
import java.util.concurrent.*;

public class RegisterController {

    //#region FXML variables
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
    //#endregion

    @FXML
    public void initialize(){
        error.setText("");
    }

    // Attempts a user registration using the inputted credentials.
    public void registerButton(){
        User user = new User(emailField.getText(), passField.getText(), conpassField.getText());

        if (user.isValid()) {
            // Disable the buttons while waiting for response
            btnCancel.setDisable(true);
            btnRegister.setDisable(true);

            HttpRequestHandler reqHandler = HttpRequestHandler.getInstance();

            // Sends a POST request to /register with the user json
            reqHandler.request(RequestType.POST, "/register", user.toJson())
            .thenAccept((response) -> {
                JsonObject responseBody = null;
                try {
                    responseBody = reqHandler.getResponseBodyJson(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Assertion error if json cannot be parsed
                assert responseBody != null;

                if (response.getStatusLine().getStatusCode() == 200) {
                    String accessToken = responseBody.get("accessToken").getAsString();
                    String refreshToken = responseBody.get("refreshToken").getAsString();

                    int id = responseBody.get("id").getAsInt();

                    // Set access and refresh tokens for auth requests
                    reqHandler.setAccessToken(accessToken);
                    reqHandler.setRefreshToken(refreshToken);

                    // Store the credentials as a hash
                    Hasher.storeCredentials(emailField.getText(), passField.getText());

                    Platform.runLater(() -> {
                        user.clearPasswords();
                        user.id = id;
                        DataManager.getInstance().initialize(user, true);

                        // Switch to main view with a flash message
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
                }
                // Show the usual connection failed error
                else {
                    setError("Unable to connect to the server");
                    e.printStackTrace();
                }
                return null;
            }).whenComplete((m, t) -> {
                // Re-enable the buttons after request is finished
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

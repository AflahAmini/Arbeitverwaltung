package arbyte.controllers;

import arbyte.managers.DataManager;
import arbyte.models.FlashMessage;
import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import arbyte.helper.SceneHelper;
import arbyte.helper.Hasher;
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

public class LoginController {
    //#region FXML variables
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
    //#endregion

    @FXML
    public void initialize(){
        error.setText("");
    }

    // Attempts a user login using the inputted credentials. If server is unreachable
    // then it attempts an offline login using last known credentials
    public void buttonLogin(){
        User user = new User(emailField.getText(), passField.getText(), passField.getText());

        if (user.isValid()) {
            // Disable the buttons while waiting for response
            btnLogin.setDisable(true);
            btnRegister.setDisable(true);

            HttpRequestHandler reqHandler = HttpRequestHandler.getInstance();

            // Send a POST request to /login with the user json
            reqHandler.request(RequestType.POST, "/login", user.toJson(),
                    response -> {
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
                                user.id = id;
                                DataManager.getInstance().initialize(user, true);

                                // Switch to main view with a flash message
                                SceneHelper.showMainPage();
                                FlashMessage fm = new FlashMessage("Login successful!", false);
                                MainController.getInstance().flash(fm);
                            });
                        } else {
                            String message = responseBody.get("error").getAsString();

                            setError(message);
                        }

                        return null;
                    }).exceptionally(e -> {
                if (e instanceof AssertionError) {
                    setError("Error while parsing response JSON!");
                }
                // If credentials match last known credentials, then log into app in offline mode
                else if(Hasher.compareLastCredentials(emailField.getText(), passField.getText())){
                    Platform.runLater(() -> {
                        DataManager.getInstance().initialize(user, false);

                        SceneHelper.showMainPage();
                        FlashMessage fm = new FlashMessage("Offline mode enabled", false);
                        MainController.getInstance().flash(fm);
                    });
                }
                // Otherwise show the usual connection failed error
                else {
                    setError("Unable to connect to the server");

                    btnLogin.setDisable(false);
                    btnRegister.setDisable(false);
                }
                return null;
            }).whenComplete((m, t) -> {
                // Re-enable the buttons after request is finished
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
        executorService.schedule(() -> {
            error.setText("");
            executorService.shutdown();
        }, 3, TimeUnit.SECONDS);
    }
}
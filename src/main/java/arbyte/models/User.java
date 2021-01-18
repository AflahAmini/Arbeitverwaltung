package arbyte.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {
    public int id;
    private final String email;
    private String password;
    private String passwordConfirmation;

    public User(String email, String password, String passwordConfirmation) {
        this.id = 0;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getEmail() { return email; }

    public boolean isValid(){
        return !(email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) ;
    }

    public void clearPasswords() {
        password = "";
        passwordConfirmation = "";
    }

    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
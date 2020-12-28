package arbyte.models;

import arbyte.helper.Hasher;
import arbyte.helper.ResourceLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private String email;
    private String password;
    private String passwordConfirmation;

    public User(String email, String password, String passwordConfirmation) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    public boolean isValid(){
        return !(email.isBlank() || password.isBlank() || passwordConfirmation.isBlank()) ;
    }

    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);

    }
}
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

    private static final String filepath = "userInfo/userInfo.txt";


    public User(String email, String password, String passwordConfirmation) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    public boolean registerUser(){
        BufferedWriter bw ;
        ResourceLoader rl = new ResourceLoader();

        try {
            File file = rl.getFileFromResource(filepath);
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);

            String passwordHash = Hasher.getDigestFrom(password);

            // checks if the file exist
            // create file if not exist?
            if(file.exists()) {
                if (password.equals(passwordConfirmation) && !alreadyExists() && isValid()) {
                    bw.write(email + "," + passwordHash +",");
                    bw.newLine();
                    bw.close();
                    return true;
                }
                else{
                    if(!isValid()){
                        System.out.println("Error: Invalid Username or Password!");
                        return false;
                    }
                    System.out.println("Error : User already exist!");
                    return false;
                }
            }
            else{
                System.out.println("Error : File does not exist");
                return false;
            }

        }
        catch(Exception e){
            System.out.println("Error inputting User Info into File");
            e.printStackTrace();
        }

        return false;
    }

    // checks if username is already used
    public boolean alreadyExists(){
        return !getLineOfEmail().equals("");
    }

    public boolean isValid(){
        return !(email.isBlank() || password.isBlank() || passwordConfirmation.isBlank() || !isEmailValid()) ;
    }

    public boolean authenticate(){
        try {
            String passwordHash = Hasher.getDigestFrom(password);

            String userLine = getLineOfEmail();

            if(userLine.isBlank()){
                return false;
            }

            Scanner scanner = new Scanner(userLine);
            scanner.useDelimiter(",");

            scanner.next();

            return scanner.next().equals(passwordHash);
        }
        catch(Exception e){
            System.out.println("authenticate method failed");
            e.printStackTrace();
        }

        return false;
    }

    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);

    }

    private String getLineOfEmail(){
        try {
            ResourceLoader loader = new ResourceLoader();
            File textFile = loader.getFileFromResource(filepath);
            Scanner scanner = new Scanner(textFile);

            // loop through each line
            while (scanner.hasNextLine() ){
                String line = scanner.nextLine();

                Scanner subscanner = new Scanner(line);
                subscanner.useDelimiter(",");

                // returns line if email matches
                if(email.equals(subscanner.next())){
                    return line;
                }
            }
        }
        catch(Exception e){
            System.out.println("getLineOfEmail method failed");
            e.printStackTrace();

        }

        // otherwise return empty string
        return "";
    }

    private boolean isEmailValid(){
        Pattern pattern = Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$", Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }
}
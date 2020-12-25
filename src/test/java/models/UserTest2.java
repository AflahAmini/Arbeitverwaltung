package models;

import arbyte.helper.ResourceLoader;
import arbyte.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class UserTest2 {
    @Test
    void shouldNotRegister() {
        User user1 = new User("", "", "");
        User user2 = new User("aflah@gmail.com", "", "");
        User user3 = new User("aflah@gmail.com", "lain", "");
        User user4 = new User("aflah@gmailcoome", "sama", "sama");

        clearInfo();
        Assertions.assertFalse(user1.registerUser());
        Assertions.assertFalse(user2.registerUser());
        Assertions.assertFalse(user3.registerUser());
        Assertions.assertFalse(user4.registerUser());
        clearInfo();
    }

    @Test
    void shouldRegister(){
        User user1 = new User("aflah1@gmail.com", "abc", "abc");
        User user2 = new User("aflah1234@gmail.com", "abc", "abc");

        clearInfo();
        Assertions.assertTrue(user1.registerUser());
        Assertions.assertTrue(user2.registerUser());
        clearInfo();
    }

    @Test
    void shouldNotLogIn() {
        User user1 = new User("aflah1@gmail.com", "123", "123");
        User user2 = new User("aflah1@gmail.com", "1234", "1234");
        User user3 = new User("aflah2@gmail.com", "123", "123");

        clearInfo();
        Assertions.assertTrue(user1.registerUser());
        Assertions.assertFalse(user2.authenticate());
        Assertions.assertFalse(user3.authenticate());
        clearInfo();
    }

    void clearInfo(){
        BufferedWriter bw ;
        ResourceLoader rl = new ResourceLoader();
        try {
            File file = rl.getFileFromResource("userInfo/userInfo.txt");
            FileWriter fw = new FileWriter(file, false);
            bw = new BufferedWriter(fw);
            bw.write("");
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

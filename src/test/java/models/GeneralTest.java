package models;


import arbyte.helper.HttpRequestHandler;
import arbyte.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

public class GeneralTest {
    public static void main(String[] args) {
        try {
            User user = new User("refreshtest@example.com", "password", "password");

            var responseFuture
                    = HttpRequestHandler.postRequest("/login", user.toJson());
            System.out.println("Getting resource");
            long t1 = System.currentTimeMillis();

            var response = responseFuture.get();
            long t2 = System.currentTimeMillis();

            System.out.println(response.statusCode());
            System.out.println(response.body());
            System.out.println(t2 - t1);

            Gson gson = new GsonBuilder().create();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);

            System.out.println(jsonObject.get("message"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

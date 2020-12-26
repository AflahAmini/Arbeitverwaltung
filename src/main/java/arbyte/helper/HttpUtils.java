package arbyte.helper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.http.HttpResponse;

public class HttpUtils {

    private static Gson gson = new Gson();

    // Returns the JsonElement object from the HttpResponse object's JSON that has the property 'name'
    public static JsonElement getBodyProperty(String name, HttpResponse<String> response) {
        JsonObject jsonObj = gson.fromJson(response.body(), JsonObject.class);
        return jsonObj.get(name);
    }
}

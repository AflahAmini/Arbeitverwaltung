package arbyte.helper;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HttpRequestHandler {

    private static HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    public static CompletableFuture<HttpResponse<String>> getRequest(String path) {
        HttpRequest request = defaultBuilder(path)
                            .GET()
                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> postRequest(String path, String content){
        HttpRequest request = defaultBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpRequest.Builder defaultBuilder(String path) {
        // URL params
        String domain = "http://localhost:";
        int port = 3000;

        return  HttpRequest.newBuilder()
                .uri(URI.create(domain + port + path))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
    }
}

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
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class HttpRequestHandler {

    private static HttpRequestHandler instance = null;

    private String accessToken = "";
    private String refreshToken = "";

    private static final Gson gson = new Gson();

    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private HttpRequestHandler() {}

    public static HttpRequestHandler getInstance() {
        if (instance == null)
            instance = new HttpRequestHandler();
        return instance;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public CompletableFuture<HttpResponse<String>> getRequest(String path) {
        HttpRequest request = defaultBuilder(path)
                            .GET()
                            .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getRequestWithAuth(String path) {
        HttpRequest request = defaultBuilder(path)
                            .header("Authorization", "Bearer " + accessToken)
                            .build();

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 401) {
                    response = refreshTokens().get();

                    if (response.statusCode() == 200) {
                        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);

                        setAccessToken(responseJson.get("accessToken").getAsString());
                        setRefreshToken(responseJson.get("refreshToken").getAsString());

                        response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    }
                }

                return response;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        });
    }

    public CompletableFuture<HttpResponse<String>> postRequest(String path, String content){
        HttpRequest request = defaultBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private CompletableFuture<HttpResponse<String>> refreshTokens() {
        return postRequest("/refresh-token",
                String.format("{ \"refreshToken\": \"%s\" }", refreshToken));
    }

    private HttpRequest.Builder defaultBuilder(String path) {
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

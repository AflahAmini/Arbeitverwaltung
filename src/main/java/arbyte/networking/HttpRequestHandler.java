package arbyte.networking;


import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import java.util.concurrent.CompletableFuture;

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

    // Sends a request to the server without authentication.
    // payload is only necessary for a POST/PUT request, otherwise leave an empty string.
    public CompletableFuture<HttpResponse<String>> request(RequestType requestType, String path, String payload) {
        HttpRequest request = generateRequest(defaultBuilder(path), requestType, payload);

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    // Sends a request with authentication.
    // payload is only necessary for a POST/PUT request, otherwise leave an empty string.
    public CompletableFuture<HttpResponse<String>> requestWithAuth(RequestType requestType, String path, String payload) {

        return CompletableFuture.supplyAsync(() -> {
            HttpRequest.Builder reqBuilder = defaultBuilder(path)
                    .header("Authorization", "Bearer " + accessToken);

            HttpRequest request = generateRequest(reqBuilder, requestType, payload);

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 401) {
                    response = refreshTokens().get();

                    if (response.statusCode() == 200) {
                        JsonObject responseJson = gson.fromJson(response.body(), JsonObject.class);

                        setAccessToken(responseJson.get("accessToken").getAsString());
                        setRefreshToken(responseJson.get("refreshToken").getAsString());

                        reqBuilder = defaultBuilder(path)
                                    .header("Authorization", "Bearer " + accessToken);
                        request = generateRequest(reqBuilder, requestType, payload);

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

    // Sends a request to refresh the access and refresh tokens.
    private CompletableFuture<HttpResponse<String>> refreshTokens() {
        System.out.println("Refreshing tokens");
        return request(RequestType.POST, "/refresh-token",
                String.format("{ \"refreshToken\": \"%s\" }", refreshToken));
    }

    // Generates a request from a given builder object and request types along with the content (for POST requests only).
    private HttpRequest generateRequest(HttpRequest.Builder reqBuilder, RequestType requestType, String payload) {
        switch (requestType) {
            case GET:
                return reqBuilder
                        .GET()
                        .build();
            case POST:
                return reqBuilder
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();
            default:
                return null;
        }
    }

    private HttpRequest.Builder defaultBuilder(String path) {
        // URL params
        String domain = "http://localhost:";
        String startingPoint = "/api";
        int port = 3000;
        String subdomain = "/api";

        return  HttpRequest.newBuilder()
                .uri(URI.create(domain + port + startingPoint + path))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
    }
}

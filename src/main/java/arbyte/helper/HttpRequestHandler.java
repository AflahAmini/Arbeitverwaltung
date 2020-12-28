package arbyte.helper;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import java.util.concurrent.CompletableFuture;

public class HttpRequestHandler {
    //static
    private static String domain = "http://localhost:";
    private static int port = 3000;



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

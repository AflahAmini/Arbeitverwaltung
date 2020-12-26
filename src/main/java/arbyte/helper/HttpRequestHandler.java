package arbyte.helper;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HttpRequestHandler {
    //static
    private static String domain = "http://localhost:";
    private static int port = 3000;

    public static void getRequest(String path, Consumer<String> onFinish) throws URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = defaultBuilder(path)
                .GET()
                .build();

        client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(onFinish)
                .join();

        //https://jsonplaceholder.typicode.com/posts

    }
    public static void postRequest(){

        
    }

    private static HttpRequest.Builder defaultBuilder(String path) throws URISyntaxException {

        return  HttpRequest.newBuilder()
                .uri(new URI(domain + String.valueOf(port) + path))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofSeconds(10));
    }

}

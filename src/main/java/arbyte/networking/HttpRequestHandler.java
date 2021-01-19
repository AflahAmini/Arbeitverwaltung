package arbyte.networking;

import arbyte.helper.lambda.SupplierUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.*;
import java.util.concurrent.CompletableFuture;

public class HttpRequestHandler {

    //#region Singleton stuff
    private static HttpRequestHandler instance = null;

    public static HttpRequestHandler getInstance() {
        if (instance == null)
            instance = new HttpRequestHandler();
        return instance;
    }
    //#endregion

    private final Gson gson = new Gson();
    private final String baseUrl = "http://localhost:3000/api";

    private String accessToken = "";
    private String refreshToken = "";

    private final CloseableHttpClient client;

    // Client configuration
    private HttpRequestHandler() {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setConnectTimeout(1000)
                .setSocketTimeout(1000)
                .build();

        client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
    }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    // Sends a request to the server without authentication.
    // payload is only necessary for a POST/PUT request, otherwise leave an empty string.
    public CompletableFuture<Void> request
    (RequestType requestType, String path, String payload, ResponseHandler<Void> handler) {
        return CompletableFuture.supplyAsync(SupplierUtils.wrap(() -> {
            HttpRequestBase request = generateRequest(requestType, path, payload);

            return client.execute(request, handler);
        }));
    }

    // Sends a request with authentication.
    // payload is only necessary for a POST/PUT request, otherwise leave an empty string.
    public CompletableFuture<Void> requestWithAuth
    (RequestType requestType, String path, String payload, ResponseHandler<Void> handler) {
        return CompletableFuture.supplyAsync(SupplierUtils.wrap(() -> {
            HttpRequestBase request = generateRequest(requestType, path, payload);

            processAuthRequest(request, handler);
            return null;
        }));
    }

    // Sends a multipart-form POST request to upload the given file to path
    public CompletableFuture<Void> uploadFileAuth(String path, File file, ResponseHandler<Void> handler) {
        return CompletableFuture.supplyAsync(SupplierUtils.wrap(() -> {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            HttpEntity entity = entityBuilder
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("toUpload", file, ContentType.DEFAULT_BINARY, file.getName())
                    .build();

            HttpPost post = new HttpPost(baseUrl + path);
            post.setEntity(entity);

            processAuthRequest(post, handler);
            return null;
        }));
    }

    public JsonObject getResponseBodyJson(HttpResponse response) throws IOException {
        return gson.fromJson(getResponseBody(response), JsonObject.class);
    }

    public String getResponseBody(HttpResponse response) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        response.getEntity().writeTo(os);
        return os.toString();
    }

    // Sends the given request with auth. If unauthorized then retry after refreshing tokens.
    private void processAuthRequest(HttpRequestBase request, ResponseHandler<Void> handler) throws IOException {
        request.addHeader("Authorization", "Bearer " + accessToken);

        client.execute(request, response -> {
            if (response.getStatusLine().getStatusCode() == 200) {
                handler.handleResponse(response);
                return null;
            }

            // If unauthorized then attempt to refresh tokens
            refreshTokens(() -> {
                try {
                    request.setHeader("Authorization", "Bearer " + accessToken);
                    client.execute(request, handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return null;
        });
    }

    // Sends a request to refresh the access and refresh tokens.
    private void refreshTokens(Runnable onFinish) {
        System.out.println("Refreshing tokens");

        request(RequestType.POST, "/refresh-token",
                String.format("{ \"refreshToken\": \"%s\" }", refreshToken),
                response -> {
                    JsonObject resBodyJson = getResponseBodyJson(response);

                    setAccessToken(resBodyJson.get("accessToken").getAsString());
                    setRefreshToken(resBodyJson.get("refreshToken").getAsString());

                    onFinish.run();
                    return null;
                });
    }

    // Generates a request from the given request type along with the content (for POST requests only).
    private HttpRequestBase generateRequest(RequestType requestType, String path, String payload) {
        HttpRequestBase request;

        switch (requestType) {
            case GET:
                request = new HttpGet(baseUrl + path);
                break;
            case POST:
                HttpPost post = new HttpPost(baseUrl + path);
                post.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));
                request = post;
                break;
            case PUT:
                HttpPut put = new HttpPut(baseUrl + path);
                put.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));
                request = put;
                break;
            default:
                throw new IllegalArgumentException("RequestType is undefined!");
        }

        return request;
    }
}

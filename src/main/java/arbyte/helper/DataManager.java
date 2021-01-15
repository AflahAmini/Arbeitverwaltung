package arbyte.helper;

import arbyte.models.Calendar;
import arbyte.models.User;
import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;

import java.io.*;

public class DataManager {
    //#region Singleton stuff
    private static DataManager instance = null;

    private DataManager() {}
    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    //#endregion

    private static final String calendarPath = "json/calendar.json";

    private User currentUser = null;
    private Calendar calendar = null;

    private boolean online;

    private final ResourceLoader resourceLoader = new ResourceLoader();
    private final HttpRequestHandler reqHandler = HttpRequestHandler.getInstance();

    // Should be run upon successful login or registration
    public void initialize(User currentUser, boolean online) {
        currentUser.clearPasswords();
        this.currentUser = currentUser;
        this.online = online;

        fetchCalendar();
    }

    // Fetches the calendar json from the server if online, otherwise parses
    // from the local json file into the calendar object.
    private void fetchCalendar() {
        if (online) {
            // Fetch the user's json from the server then save it to calendar.json
            reqHandler.requestWithAuth(RequestType.GET, "/calendar/" + currentUser.id, "")
            .thenAccept(response -> {
                try {
                    // Parse the calendar json into the calendar object
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String responseString = reqHandler.getResponseBody(response);
                        calendar = Calendar.fromJson(responseString);
                    }
                    // Throw an exception if the status code is other than 200 and 404
                    else {
                        calendar = new Calendar();
                        saveCalendar();
                        uploadCalendar();

                        if (response.getStatusLine().getStatusCode() != 404)
                            throw new Exception(response.getStatusLine().getReasonPhrase());
                    }

                } catch (Exception e) {
                    System.out.println("fetchCalendar : Something went wrong!");
                    e.printStackTrace();
                }
            }).exceptionally(e -> {
                System.out.println(connectionFailedMessage("fetchCalendar"));

                // Redo fetchCalendar if connection is lost with online false
                online = false;
                fetchCalendar();
                return null;
            });
        } else {
            try {
                // Read from file on calendarPath into a StringBuilder
                File f = resourceLoader.getFileOrCreate(calendarPath);
                BufferedReader br = new BufferedReader(new FileReader(f));

                StringBuilder jsonBuilder = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null)
                    jsonBuilder.append(line);

                br.close();

                // If json is a new file, then create a new blank calendar and save it
                if (jsonBuilder.toString().isBlank()) {
                    System.out.println("Creating new calendar.json file...");
                    calendar = new Calendar();

                    saveCalendar();
                }
                // otherwise parse the json as a Calendar object
                else {
                    calendar = Calendar.fromJson(jsonBuilder.toString());
                }
            } catch (Exception e) {
                System.out.println("Error while getting json file!");
                e.printStackTrace();

                // Set calendar as new calendar by default
                calendar = new Calendar();
            }
        }
    }

    // Uploads the calendar json to the server only when online
    private void uploadCalendar() {
        if (!online) return;

        // Gets the json file
        File f = resourceLoader.getFile(calendarPath);
        if (!f.exists()) {
            System.out.println("Upload cancelled. Calendar file not found!");
            return;
        }

        System.out.println("Uploading calendar...");

        // Sends an upload request to the server
        reqHandler.uploadFileAuth("/calendar/" + currentUser.id, f)
        .thenAccept(response -> {
            if (response.getStatusLine().getStatusCode() != 200)
                System.out.println("Upload failed");
        })
        .exceptionally(e -> {
            System.out.println(connectionFailedMessage("uploadCalendar"));

            online = false;
            e.printStackTrace();
            return null;
        });
    }

    // Saves the current calendar object locally as a json
    private void saveCalendar() {
        try {
            File f = resourceLoader.getFileOrCreate(calendarPath);
            FileWriter fw = new FileWriter(f, false);
            String json = calendar.toJson();
            fw.write(json);

            fw.close();
        } catch (IOException e) {
            System.out.println("Error while writing to calendar json!");
            e.printStackTrace();
        }
    }

    private String connectionFailedMessage(String context) {
        return context + " : Failed to establish connection with the server!";
    }
}

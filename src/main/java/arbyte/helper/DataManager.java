package arbyte.helper;

import arbyte.application.ExecutorServiceManager;
import arbyte.controllers.MainController;
import arbyte.models.CalEvent;
import arbyte.models.Calendar;
import arbyte.models.Session;
import arbyte.models.User;
import arbyte.networking.HttpRequestHandler;
import arbyte.networking.RequestType;
import com.google.gson.JsonObject;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final ResourceLoader resourceLoader = new ResourceLoader();
    private final HttpRequestHandler reqHandler = HttpRequestHandler.getInstance();

    public String lastMonthYear = "";
    public List<CalEvent> lastEventsList = new ArrayList<>();

    private User currentUser = null;
    private Calendar calendar = null;
    private Session session = null;

    private boolean online;

    // Should be run upon successful login or registration
    public void initialize(User currentUser, boolean online) {
        lastMonthYear = String.format("%02d-%d",
                LocalDate.now().getMonthValue(), LocalDate.now().getYear());

        currentUser.clearPasswords();
        this.currentUser = currentUser;
        this.online = online;

        fetchCalendar();

        createSession();
        startSessionSyncSchedule();
    }

    public Calendar getCalendar() { return calendar; }
    public Session getSession() { return session; }

    // Fetches the calendar json from the server if online, otherwise parses
    // from the local json file into the calendar object.
    private void fetchCalendar() {
        if (online) {
            // Fetch the user's json from the server then save it to calendar.json
            reqHandler.requestWithAuth(RequestType.GET, "/calendar/" + currentUser.id, "",
            response -> {
                try {
                    // Parse the calendar json into the calendar object
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String responseString = reqHandler.getResponseBody(response);
                        calendar = Calendar.fromJson(responseString);
                        saveCalendar();

                        return null;
                    }

                    // Throw an exception but don't upload if the status code
                    // is other than 200 and 404
                    calendar = new Calendar();
                    saveCalendar();

                    if (response.getStatusLine().getStatusCode() == 404)
                        uploadCalendar();
                    else
                        throw new Exception(response.getStatusLine().getReasonPhrase());

                } catch (Exception e) {
                    System.out.println("fetchCalendar : Something went wrong!");
                    e.printStackTrace();

                    calendar = new Calendar();
                    saveCalendar();
                }

                return null;
            }).exceptionally(e -> {
                System.out.println(connectionFailedMessage("fetchCalendar"));

                // Redo fetchCalendar if connection is lost with online false
                online = false;
                fetchCalendar();
                return null;
            }).whenComplete(((u, t) -> {
                calendar.setOnChangedCallback(() -> {
                    saveCalendar();
                    uploadCalendar();
                });
            }));
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
            } finally {
                calendar.setOnChangedCallback(this::saveCalendar);
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
        reqHandler.uploadFileAuth("/calendar/" + currentUser.id, f,
        response -> {
            if (response.getStatusLine().getStatusCode() != 200)
                System.out.println("Upload failed");

            return null;
        }).exceptionally(e -> {
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

    // Sends a POST request to /sessions to create a session with today's date.
    // The response should contain a message and the session entry in the db.
    private void createSession() {
        if (!online) {
            session = new Session();
            return;
        }

        LocalDate date = LocalDate.now();
        String payload = String.format("{\"date\": \"%s\"}", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        reqHandler.requestWithAuth(RequestType.POST, "/sessions", payload,
        response -> {
            try {
                JsonObject responseBody = reqHandler.getResponseBodyJson(response);

                if (response.getStatusLine().getStatusCode() == 200) {
                    String message = responseBody.get("message").getAsString();
                    flashMessage(message, false);

                    JsonObject sessionObj = responseBody.get("session").getAsJsonObject();

                    long activeSeconds = sessionObj.get("activeDuration").getAsLong();
                    long pausedSeconds = sessionObj.get("inactiveDuration").getAsLong();

                    if (activeSeconds < 0 || pausedSeconds < 0)
                        throw new Exception("Some durations are negative!");

                    if (activeSeconds > 0 || pausedSeconds > 0)
                        session = new Session(activeSeconds, pausedSeconds);
                    else
                        session = new Session();
                } else {
                    String error = responseBody.get("error").getAsString();
                    flashMessage(error, true);
                }
            } catch (Exception e) {
                System.out.println("createSession : Something went wrong!");
                e.printStackTrace();

                session = new Session();
            }

            return null;
        }).exceptionally(e -> {
            System.out.println(connectionFailedMessage("createSession"));

            online = false;
            return null;
        });
    }

    // Sends a PUT request to /sessions with the session object update the corresponding session.
    private void updateSession() {
        if (!online || session == null)
            return;

        String sessionJson = session.toJson();

        reqHandler.requestWithAuth(RequestType.PUT, "/sessions", sessionJson,
        response -> {
            if (response.getStatusLine().getStatusCode() == 204) {
                System.out.println("Session successfully updated on the server");
            } else {
                System.out.println("Session update failed!");
                System.out.println(response.getStatusLine().getReasonPhrase());
            }

            return null;
        }).exceptionally(e -> {
            System.out.println(connectionFailedMessage("createSession"));

            e.printStackTrace();
            online = false;
            return null;
        });
    }

    private void startSessionSyncSchedule() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorServiceManager.register(executorService);

        executorService.scheduleAtFixedRate(this::updateSession,
                5, 5, TimeUnit.MINUTES);
    }

    private void flashMessage(String message, boolean isError) {
        MainController.getInstance().flash(message, isError);
    }

    private String connectionFailedMessage(String context) {
        return context + " : Failed to establish connection with the server!";
    }
}

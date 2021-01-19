package arbyte.models.ui;

public class FlashMessage {
    private final String message;
    private final boolean isError;

    public FlashMessage(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public String getMessage() { return message; }
    public boolean isError() { return isError; }
}

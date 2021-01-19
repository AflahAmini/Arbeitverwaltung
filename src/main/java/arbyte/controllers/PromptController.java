package arbyte.controllers;

public class PromptController {

    private Runnable onConfirm;
    private Runnable onCancel;

    public void initialize(Runnable onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    public void confirm(){
        onConfirm.run();
        onCancel.run();
    }

    public void cancel(){
        onCancel.run();
    }
}

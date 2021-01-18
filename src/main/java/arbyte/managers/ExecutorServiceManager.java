package arbyte.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceManager {
    private static List<ExecutorService> executorServices = new ArrayList<>();

    public static void register(ExecutorService service) {
        executorServices.add(service);
    }

    public static void shutdownAll() {
        for (ExecutorService e: executorServices) {
            e.shutdown();
        }
    }
}

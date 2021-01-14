package arbyte.helper.lambda;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

// https://stackoverflow.com/questions/14039995/java-8-mandatory-checked-exceptions-handling-in-lambda-expressions-why-mandato/14045585#14045585
public final class SupplierUtils {
    private SupplierUtils() {}

    public static <T> Supplier<T> wrap(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

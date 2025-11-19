package Controller;

import javax.swing.SwingWorker;
import java.util.function.Consumer;
import java.util.function.Supplier;
import Service.ServiceResult;

/**
 * Small helper base class for controllers to run ServiceResult-producing tasks
 * on a background thread and handle completion/failure in concise callbacks.
 */
public class BaseController {

    protected <T> void executeInBackground(
            Supplier<ServiceResult<T>> task,
            Consumer<ServiceResult<T>> onDone,
            Consumer<Throwable> onError,
            Runnable onStart,
            Runnable onFinish
    ) {
        if (onStart != null) {
            try { onStart.run(); } catch (Throwable ignored) {}
        }

        new SwingWorker<ServiceResult<T>, Void>() {
            @Override
            protected ServiceResult<T> doInBackground() {
                return task.get();
            }

            @Override
            protected void done() {
                if (onFinish != null) {
                    try { onFinish.run(); } catch (Throwable ignored) {}
                }

                try {
                    ServiceResult<T> res = get();
                    if (onDone != null) onDone.accept(res);
                } catch (Throwable t) {
                    if (onError != null) onError.accept(t);
                    else t.printStackTrace();
                }
            }
        }.execute();
    }
}

package in.ecomexpress.sruti.ui.login.login;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class DebouncedClickHandler {

    private static final long DEBOUNCE_DELAY = 2000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public void debounce(View.OnClickListener action, View view) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = () -> action.onClick(view);
        handler.postDelayed(runnable, DEBOUNCE_DELAY); // Post the runnable with delay
    }
}

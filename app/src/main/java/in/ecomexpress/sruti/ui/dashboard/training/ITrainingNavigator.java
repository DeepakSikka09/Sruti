package in.ecomexpress.sruti.ui.dashboard.training;

import android.app.Activity;
import android.view.KeyEvent;

public interface ITrainingNavigator {

    void showError(String description);

    void startTrainingWebView(String url);

    Activity getActivityContext();

    void onBackClick();


    boolean onKeyDown(int keyCode, KeyEvent event);
}

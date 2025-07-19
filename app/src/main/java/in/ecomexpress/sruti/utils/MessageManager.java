package in.ecomexpress.sruti.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by parikshittomar on 22-10-2018.
 */

public class MessageManager {

    //    private static boolean logger = !BuildConfig.BUILD_TYPE.contains("release");
    private static boolean logger = false;

    public static void showSampleToast(Context context, String message) {
        if (logger) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

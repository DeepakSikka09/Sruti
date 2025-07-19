package in.ecomexpress.sruti.di.appmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.logging.Logger;

import in.ecomexpress.sruti.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Deepak Sikka on 09-08-2019.
 */

public class LoggingInterceptor implements Interceptor {

    private Context context;

    public LoggingInterceptor(Context context) {
        this.context = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String empCode = sharedPreferences.getString("EMP_CODE", "");

        Request request = chain.request();
        Request requestWithHeaders = request.newBuilder()
                .header("Emp-Code", empCode)
                .build();
        Response response = chain.proceed(requestWithHeaders);
        final String responseString = new String(response.body().bytes());
        if(BuildConfig.DEBUG) {

            Log.e("LoggingInterceptor Req ", request.body() + "" + request.url() + "  " + request.headers().toString());
            Log.e(" LoggingIntercepto Res ", responseString);
        }
        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), responseString))
                .build();
    }
}

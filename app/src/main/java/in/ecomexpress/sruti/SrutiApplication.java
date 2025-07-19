package in.ecomexpress.sruti;

import static in.ecomexpress.sruti.utils.common_files.Constants.Shield_Key;
import static in.ecomexpress.sruti.utils.common_files.Constants.shield_Id;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.ktx.Firebase;
import com.shield.android.Shield;
/*import com.shield.android.Shield;*/

import java.util.HashMap;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import in.ecomexpress.sruti.di.component.DaggerAppComponent;

public class SrutiApplication extends Application implements HasActivityInjector, HasServiceInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install( this);

    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        DaggerAppComponent.builder().application(this).build().inject(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        // Shield documentation implemented
        Shield shield = new Shield.Builder(this,  shield_Id,
               Shield_Key).build();
        Shield.setSingletonInstance(shield);
        String sessionId = Shield.getInstance().getSessionId();
       /* HashMap<String, String> attributes = new HashMap<>();
        attributes.put("key_1", "value_1");
        Shield.getInstance().setDeviceResultStateListener(new Shield.DeviceResultStateListener() {
            @Override
            public void isReady() {
                Shield.getInstance().sendAttributes("Login",attributes);
            }
        });*/


    }


    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

}
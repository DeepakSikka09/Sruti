package in.ecomexpress.sruti.background_service;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.repo.local.db.prefs.IPopPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.repo.remote.RetrofitService;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;
import in.ecomexpress.sruti.utils.common_files.NetworkUtils;

/**
 * Created by deepak on 01/11/19.
 */

public class SrutiSyncService extends Service {
    @Inject
    IPreferenceHelper iPreferenceHelper;

    @Inject
    IPopPreferenceHelper iPopPreferenceHelper;
    @Inject
    RetrofitService retrofitService;
    @Inject
    SrutiDatabase mAppDatabase;
    private SrutiSyncViewModel srutiSyncViewModel;
    Context context = this;
    Timer LiveTrackingTimer;

    private final IBinder mBinder = new SrutiBinder();

    private boolean isGPSDialogVisible = false;
    private GPSTracker gps;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tests_lat", "ok");
        AndroidInjection.inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "sruti_channal";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "sruti", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sruti").setContentText("Sruti service running...").build();

            startForeground(1, notification);
        }
        srutiSyncViewModel = new SrutiSyncViewModel(iPreferenceHelper, retrofitService, mAppDatabase);
        Timer notificationTimer = new Timer();
        notificationTimer.schedule(notificationTask, 5000L, 2 * 60 * 1000L);

        Timer notificationTimer1 = new Timer();
        notificationTimer1.schedule(syncmanifest, 5000L, 30 * 60 * 1000L);

        LiveTrackingTimer = new Timer();
        LiveTrackingTimer.schedule(LiveTrackingTask, 5000L, 2 * 60 * 1000L);


        int delay = 60000; //milliseconds
        Handler gpsHandler = new Handler();
        gpsHandler.postDelayed(new Runnable() {
            public void run() {
                //do something
                gps = new GPSTracker(SrutiSyncService.this, srutiSyncViewModel.getGeoFenceRadius(), (latitude, longitude) -> {
                    if (latitude != 0.0 || longitude != 0.0) {
                        iPreferenceHelper.setCurrentLatitude(String.valueOf(latitude));
                        iPreferenceHelper.setCurrentLongitude(String.valueOf(longitude));
                    }
                });

                //  Toast.makeText(context, "started"+wayLatitude+"stat"+wayLongitude, Toast.LENGTH_SHORT).show();
                gpsHandler.postDelayed(this, delay);
            }
        }, delay);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    public class SrutiBinder extends Binder {

        public SrutiSyncService getService() {
            return SrutiSyncService.this;
        }

    }

    private TimerTask notificationTask = new TimerTask() {
        @Override
        public void run() {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                System.out.println("hello");
                Log.d("check_api","in");
                if (iPopPreferenceHelper.get_pop_enable()) {
                    if (!iPopPreferenceHelper.get_is_child_available()) {
                        srutiSyncViewModel.getPopData(context);
                        Log.d("check_api","ok");
                      /*  signatureViewModel.getPopData(SignatureActivity.this);*/

                    }
                }
                srutiSyncViewModel.getFirstInScanPacket();
                srutiSyncViewModel.getUnSyncImage();
                srutiSyncViewModel.getUnSyncedCommitPacket(context);


            }
        }
    };

    private TimerTask syncmanifest = new TimerTask() {
        @Override
        public void run() {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                System.out.println("hello sync");
                srutiSyncViewModel.combineManifestData();
            }
        }
    };


    private final TimerTask LiveTrackingTask = new TimerTask() {
        @Override
        public void run() {
            try {
                if (iPreferenceHelper.getTripID() > 0) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //  if (isMyServiceRunning(LocationService.class)) {
                        if (!iPreferenceHelper.is_Ecom_Vehicle() &&
                                iPreferenceHelper.get_live_Tracking().equalsIgnoreCase("true") &&
                                iPreferenceHelper.getLiveTrackingId() != (null)) {

                            srutiSyncViewModel.startLiveTracking(context);
                        }
                        //   }

                    } else {
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                    }
                }
            } catch (Exception e) {
                if (!iPreferenceHelper.is_Ecom_Vehicle()) {
                    srutiSyncViewModel.startLiveTracking(context);
                }
            }
        }
    };


}
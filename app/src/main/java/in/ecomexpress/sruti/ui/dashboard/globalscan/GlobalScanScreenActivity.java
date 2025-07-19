package in.ecomexpress.sruti.ui.dashboard.globalscan;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Thread.sleep;

import static in.ecomexpress.sruti.ui.dashboard.globalscan.GlobalScanScreenViewModel.tempManifestNo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityGlobalScanBinding;
import in.ecomexpress.sruti.databinding.BpDialogBoxBinding;
import in.ecomexpress.sruti.databinding.CustomDialogMessageBinding;
import in.ecomexpress.sruti.databinding.WarningDialogBinding;
import in.ecomexpress.sruti.model.ListOfAwbs;
import in.ecomexpress.sruti.model.Manifest_status_details;
import in.ecomexpress.sruti.model.RtoRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalManifestSummaryActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.MyIntentService;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class GlobalScanScreenActivity extends BaseActivity<ActivityGlobalScanBinding, GlobalScanScreenViewModel> implements IScanScreenNavigator, IGlobalAdapterToActivityInterface, in.ecomexpress.barcodelistner.BarcodeResult, AdapterView.OnItemSelectedListener {
    @Inject
    public GlobalScanScreenViewModel globalScanScreenViewModel;
    public ActivityGlobalScanBinding activityGlobalScanBinding;
    private Activity context;
    MediaPlayer mediaPlayer;
    @Inject
    GlobalScanAdapter globalScanAdapter;
    ArrayList<Shipment_Detail> shipmentData;
    private ArrayList<Shipment_Detail> shipment_detail;
    public String awb = null;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;
    private BarcodeHandler barcodeHandler;
    private ScanManager mScanMgr;
    public String device;
    private int outputMode = -1;
    private int i = 0;
    String vehicleType;
    private ArrayList<ListOfAwbs> listOfAwbs;
    private ArrayList<Shipment_Detail> rtoShipmentResponses;
    private ArrayList<Manifest_status_details> manifest_status_details;
    ArrayList<Long> manifest_number;
    public long manifest_id;
    public String locationType="";
    private long lastSyncTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private long storingAwb = 0;
    private String scanFrom = "";
    private int BP_COUNT = 2;
    public String lastTextBp = null;
    private List<ReasonCodeList> reasonCodeLists;
    private boolean isPendingAWbChecked = false;
    public long Current_AWb_FOR_BP_ID = 0;

    private boolean isFlashlightOn = false;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            /*if (!globalScanScreenViewModel.loginAfterServerTime()) {
                if (!isIncorrectTimeDialogOpen) {
                showIncorrectTimeAlert("Cannot proceed due to incorrect device time.\n Do you want to change?", true);
                return;}
            } else */

            if (scanFrom.equalsIgnoreCase(Constants.BP_ID)) {
                if (result.getText() == null || result.getText().equals(lastTextBp) || result.getText().equalsIgnoreCase(awb)) {
                    return;
                } else {
                    lastTextBp = result.getText();
                    //changes here trim
                    Log.d("check_bp", lastTextBp);
                    Log.d("check_awb", String.valueOf(storingAwb));

                    globalScanScreenViewModel.ifBrandPackagingIDexists(storingAwb, lastTextBp);
                }
            } else {
                if (result.getText() == null || result.getText().equals(awb) || result.getText().equals(lastTextBp)) {
                    return;
                } else {

                    awb = result.getText();
                    try {
                        Pattern pattern = Pattern.compile(Constants.REGEX);
                        Matcher matcher = pattern.matcher(awb);
                        if (matcher.matches()) {
                            // Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //   v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                globalScanScreenViewModel.validateAWB(Long.parseLong(awb));
                          /*  beepManager.playBeepSoundAndVibrate();
                            beepManager.playBeepSound();*/
                            } else {
                                // v.vibrate(500);
                                globalScanScreenViewModel.validateAWB(Long.parseLong(awb));
                           /* beepManager.playBeepSoundAndVibrate();
                            beepManager.playBeepSound();*/
                            }
                        } else {
                            showAlert(getString(R.string.regex_vaidation), GlobalScanScreenActivity.this);
                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalScanScreenViewModel.setNavigator(this);
        this.activityGlobalScanBinding = getViewDataBinding();
        reasonCodeLists = new ArrayList<>();
        context = GlobalScanScreenActivity.this;

        activityGlobalScanBinding.flashlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlashlightOn) {
                    barcodeScannerView.setTorchOff();
                    activityGlobalScanBinding.flashlightButton.setImageResource(R.drawable.flashoff);
                    isFlashlightOn = false;
                } else {
                    barcodeScannerView.setTorchOn();
                    activityGlobalScanBinding.flashlightButton.setImageResource(R.drawable.flashon);
                    isFlashlightOn = true;
                }
            }
        });

        device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            activityGlobalScanBinding.flashlightButton.setVisibility(View.GONE);
            activityGlobalScanBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            /*            mScanMgr.enableBeep();*/
            mScanMgr.disableBeep();
            mScanMgr.setScanEnable(true);
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            Map<String, String> settings = mScanMgr.getScanSettings();
            String sOutputMode = settings.get(ScanSettings.Global.OUT_PUT_MODE); //Acquire
            outputMode = ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST;
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {

            activityGlobalScanBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            activityGlobalScanBinding.flashlightButton.setVisibility(View.GONE);
            barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
            barcodeHandler.enableScanner();
            barcodeHandler.setBeepEnabled(false);
        } else {
            activityGlobalScanBinding.flashlightButton.setVisibility(View.VISIBLE);
            beepManager = new BeepManager(this);
            barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
            capture = new CaptureManager(this, barcodeScannerView);
            capture.initializeFromIntent(getIntent(), savedInstanceState);
            barcodeScannerView.decodeContinuous(callback);


        }
        try {
            Bundle extra = getIntent().getBundleExtra("manifest_collection");
            manifest_number = (ArrayList<Long>) extra.getSerializable("manifest_number");

            Iterator itr = manifest_number.iterator();
            while (itr.hasNext()) {
                long x = (Long) itr.next();
                if (x == 0)
                    itr.remove();
            }

        } catch (Exception ee) {
            ee.printStackTrace();
        }
        activityGlobalScanBinding.tvAwbCount.setText("");
        activityGlobalScanBinding.tvAwbCount.setVisibility(View.VISIBLE);
        activityGlobalScanBinding.tvAwbCount.setText(String.valueOf(globalScanAdapter.getItemCount()));

        if (globalScanAdapter.getItemCount() != 0) {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.generic_button);
            buttonDrawable.mutate();
            activityGlobalScanBinding.nextTv.setBackgroundDrawable(buttonDrawable);
        } else {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.login_button);
            buttonDrawable.mutate();
            activityGlobalScanBinding.nextTv.setBackgroundDrawable(buttonDrawable);
        }

        setUp();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;

            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (location.getAccuracy() < globalScanScreenViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        }
                    }
                }
            }
        };


        getLocation();
        globalScanScreenViewModel.getPickupListToView(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT).observe(this, pickupList -> {
            reasonCodeLists.clear();
            reasonCodeLists.addAll(pickupList);
        });

        // Get the click time from the intent
        long clickTime = getIntent().getLongExtra("clickTime", 0);
        long openTime = System.currentTimeMillis();
        long duration = openTime - clickTime;

        // Log the time taken to open this screen
        logScreenOpenTime(duration);

    }
    private void logScreenOpenTime(long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", getScreenName());
        bundle.putLong("open_time", duration);
        FirebaseAnalytics.getInstance(this).logEvent("screen_open_time", bundle);
    }

    @Override
    public GlobalScanScreenViewModel getViewModel() {
        return globalScanScreenViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_global_scan;
    }


    private Thread locationThread;
    private boolean startLocationUpdate = false;

    public void startLocationThread() {
        locationThread = new Thread(() -> {
            try {
                while (startLocationUpdate) {
                    getLocation();
                    if (locationThread != null) {
                        sleep(4000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        locationThread.start();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_START_WORK);
        filter.addAction(Constants.ACTION_STOP_WORK);


        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            //  IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            filter.addAction(ScanManager.ACTION_SEND_SCAN_RESULT);
            // registerReceiver(mResultReceiver, intFilter);
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onResume();
            barcodeHandler.enableScanner();
        } else {
            capture.onResume();
            if (isFlashlightOn) {
                // Turn on the flashlight
                barcodeScannerView.setTorchOff();
                activityGlobalScanBinding.flashlightButton.setImageResource(R.drawable.flashoff);
            }
        }
        super.onResume();
        registerReceiver(mResultReceiver, filter);
        startLocationUpdate = true;
        startLocationThread();


    }

    @Override
    protected void onPause() {
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.stopScan();
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onPause();
        }

        startLocationUpdate = false;
        if (locationThread != null) {
            locationThread.interrupt();
            locationThread = null;
        }
        super.onPause();
        if (isFlashlightOn) {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    protected String getScreenName() {
        return "Global Scan Screen";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.stopScan();
            unregisterReceiver(mResultReceiver);
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
        } else {
            capture.onSaveInstanceState(outState);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else {
            barcodeHandler.onActivityResult(requestCode, resultCode, data);
        }
    }

    private BroadcastReceiver mResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                byte[] bvalue = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                String sValue = intent.getStringExtra("SCAN_BARCODE1");
                try {
                    if (sValue == null && bvalue != null)
                        sValue = new String(bvalue, "GBK");
                    sValue = sValue == null ? "" : sValue;
                    if (sValue != "" && sValue != null) {

                        if (scanFrom.equalsIgnoreCase(Constants.BP_ID)) {

                            lastTextBp = sValue;// changes here trim
                            Log.d("check_bp", lastTextBp);
                            Log.d("check_awb", String.valueOf(storingAwb));

                            globalScanScreenViewModel.ifBrandPackagingIDexists(storingAwb, lastTextBp);

                        } else {
                            Pattern pattern = Pattern.compile(Constants.REGEX);
                            Matcher matcher = pattern.matcher(sValue);
                            if (matcher.matches()) {
                                globalScanScreenViewModel.validateAWB(Long.parseLong(sValue));
                            } else {
                                showAlert(getString(R.string.regex_vaidation), GlobalScanScreenActivity.this);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (action != null) {
                    if (action.equals(Constants.ACTION_START_WORK)) {
                        Log.d("check_progress", "progress_STart");
                        setProgress(VISIBLE);
                    } else if (action.equals(Constants.ACTION_STOP_WORK)) {
                        Log.d("check_progress", "progress_gone");
                        setProgress(GONE);
                    }
                }
            }
        }
    };

    private void setUp() {
        activityGlobalScanBinding.globalAwbRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        activityGlobalScanBinding.globalAwbRecyclerview.setItemAnimator(new DefaultItemAnimator());
        activityGlobalScanBinding.globalAwbRecyclerview.setAdapter(globalScanAdapter);
        globalScanAdapter.setdeleteScanItemListener(this);


        List<String> vehicleNumber = new ArrayList<>();
        if (!globalScanScreenViewModel.getDataManager().is_Ecom_Vehicle()) {
            vehicleType = globalScanScreenViewModel.getDataManager().getVehicleNo();
            vehicleNumber.add(vehicleType);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activityGlobalScanBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);
        } else {
            for (int j = 0; j < globalScanScreenViewModel.getDataManager().getRouteDetail().size(); j++) {
                if (!globalScanScreenViewModel.getDataManager().getRouteDetail().get(j).isDeparted()) {
                    vehicleType = globalScanScreenViewModel.getDataManager().getRouteDetail().get(j).getStart_vehicle_number();
                    vehicleNumber.add(vehicleType);
                }
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activityGlobalScanBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);

            activityGlobalScanBinding.spinnerVehicleType.setOnItemSelectedListener(this);
        }


        globalScanScreenViewModel.getShiListLiveData().observe(GlobalScanScreenActivity.this, new Observer<Shipment_Detail>() {
            @Override
            public void onChanged(@Nullable Shipment_Detail shipment_detailResult) {
                try {
                    if (shipment_detailResult != null) {
                        ThreadGeneric.executeCall(() -> {
                            if (shipment_detailResult.getStatus().equalsIgnoreCase(Constants.RTO_STATUS_1) || shipment_detailResult.getStatus().equalsIgnoreCase(Constants.RTO_STATUS_2)) {
                                showAlert(getString(R.string.shipment_rto_locked), GlobalScanScreenActivity.this);
                            } else if (shipment_detailResult.getStatus().equalsIgnoreCase(Constants.PICKED)) {
                                showAlert(getString(R.string.already_scanned), GlobalScanScreenActivity.this);
                            } else if (shipment_detailResult.getStatus().equalsIgnoreCase(Constants.FAILED)) {
                                showAlert(getString(R.string.failed), GlobalScanScreenActivity.this);
                            } else if (shipment_detailResult.getStatus().equalsIgnoreCase(Constants.PENDING)) {
                                globalScanScreenViewModel.getbp_id_from_awb_no(shipment_detailResult.getAirwaybill_number());
                                /*   globalScanScreenViewModel.getManifestIdFromAwb(shipment_detailResult.getAirwaybill_number(), "");
                                 */
                                setBeepSound();
                            }
                        });
                    } else {
                        showAlert(getString(R.string.advance_validation_for_global), GlobalScanScreenActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            globalScanScreenViewModel.getAllAwbList().observe(GlobalScanScreenActivity.this, new Observer<List<Shipment_Detail>>() {
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void onChanged(@Nullable List<Shipment_Detail> shipment_details) {

                    /*    globalScanScreenViewModel.getPendingAWb().observe(GlobalScanScreenActivity.this, shipment_detail -> {
                     *//* globalScanScreenViewModel.getPendingAWb().observe(GlobalScanScreenActivity.this, new Observer<List<Shipment_Detail>>() {
                        @Override
                        public void onChanged(List<Shipment_Detail> shipment_detail) {*//*


                     *//*  tempManifestNo = 0;*//*





                    });*/

                    shipmentData = new ArrayList<>();
                    shipmentData.addAll(shipment_details);

                    Log.d("check_sss", "ghj");


                    /* checkPendingAWb();*/
                    /*if (!isPendingAWbChecked) {*/
                    checkPendingAWb();
                  /*      isPendingAWbChecked = true;
                    }*/

                }
            });


            globalScanScreenViewModel.getUpdatedTime(manifest_number).observe(this, manifest_list -> {
                lastSyncTime = manifest_list.getLast_sync_time();

                if (isNetworkConnected()) {
                    rtoLockCheck(lastSyncTime);
                } else {
                    showToast(getString(R.string.check_internet));
                }
            });

            // RTO Lock and Shipment updated.
            System.out.println("----RTO  ----");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPendingAWb() {


        // Remove the observer before adding a new one
        globalScanScreenViewModel.getPendingAWb().removeObserver(pendingAWbObserver);

        // Add the observer to getPendingAWb
        globalScanScreenViewModel.getPendingAWb().observe(GlobalScanScreenActivity.this, pendingAWbObserver);

    }

    private Observer<List<Shipment_Detail>> pendingAWbObserver = new Observer<List<Shipment_Detail>>() {
        @Override
        public void onChanged(List<Shipment_Detail> shipment_detail) {
            Log.d("check_ss", "ook");
            ArrayList tempShipmentList = new ArrayList();
            ArrayList tempPickedShipmentList = new ArrayList();
            ArrayList<Shipment_Detail> concatData = new ArrayList();
            Log.d("check_data", String.valueOf(shipment_detail.size()));


            if (shipment_detail.size() > 0) {
                concatData.addAll(shipment_detail);
            }
            if (shipmentData.size() > 0) {

                concatData.addAll(shipmentData);
            }

            for (int i = 0; i < concatData.size(); i++) {
                if (concatData.get(i).getStatus().equalsIgnoreCase(Constants.PICKED) || (concatData.get(i).getStatus().equalsIgnoreCase(Constants.PENDING) && (concatData.get(i).getAirWayBillNumber() == Current_AWb_FOR_BP_ID))) {
                    tempShipmentList.add(concatData.get(i));
                }
                if (concatData.get(i).getStatus().equalsIgnoreCase(Constants.PICKED)) {
                    tempPickedShipmentList.add(concatData.get(i));
                }
            }

            globalScanAdapter.setData(tempShipmentList, Current_AWb_FOR_BP_ID);
            setbutton(tempPickedShipmentList);
        }
    };

    private void setbutton(List<Shipment_Detail> shipment_details) {
        activityGlobalScanBinding.tvAwbCount.setVisibility(View.VISIBLE);
        //  activityGlobalScanBinding.tvAwbCount.setText(String.valueOf(globalScanAdapter.getItemCount()));
        if (shipment_details != null) {
            activityGlobalScanBinding.tvAwbCount.setText(String.valueOf(shipment_details.size()));
            Drawable buttonDrawable;
            if (!shipment_details.isEmpty()) {
                buttonDrawable = getResources().getDrawable(R.drawable.generic_button);
            } else {
                buttonDrawable = getResources().getDrawable(R.drawable.login_button);
            }
            buttonDrawable.mutate();
            activityGlobalScanBinding.nextTv.setBackgroundDrawable(buttonDrawable);
        }
    }

    @Override
    public void onNext() {
        if (globalScanAdapter.getItemCount() != 0) {
            Intent intent = new Intent(GlobalScanScreenActivity.this, GlobalManifestSummaryActivity.class);
            intent.putExtra("vehicleType", vehicleType);
            startActivity(intent);
        } else {
            showAlert(getString(R.string.awb_validate), GlobalScanScreenActivity.this);
        }
    }

    @Override
    public void checkFirstScan(Long isFirst, Long manifest_num, Manifest_List scannedManifest, String came_from, long awb, boolean bp_exist) {
        try {
            if (came_from.equalsIgnoreCase("update")) {


                if (isFirst == 0) {

                    callFirstScanApi(manifest_num, scannedManifest);
                } else {
                    //first scan already done
                }
            } else {

                long start_time = CommonUtils.changeDateTime(scannedManifest.getManifest_details().getLocation().getPickup_slot_time_in());
                long end_time = CommonUtils.changeDateTime(scannedManifest.getManifest_details().getLocation().getPickup_slot_time_out());
                String start = CommonUtils.getTime(scannedManifest.getManifest_details().getLocation().getPickup_slot_time_in());
                String end = CommonUtils.getTime(scannedManifest.getManifest_details().getLocation().getPickup_slot_time_out());

                if (isFirst == 0) {


                    if (CommonUtils.checkTimeslot(start_time, end_time)) {
                        if (bp_exist) {
                            //  stopScan();
                            // showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");

                            globalScanScreenViewModel.get_temp_key(awb);
                        } else {
                            globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0);
                            setBeepSound();
                        }


                    } else {
                        if (scannedManifest.getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("W")) {
                            showManifestWarningDialog("W", start + " - " + end, awb, bp_exist);

                        } else if (scannedManifest.getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("R")) {
                            showManifestWarningDialog("R", start + " - " + end, awb, bp_exist);

                        } else {
                            if (!bp_exist) {
                                globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0);
                                setBeepSound();
                            } else {
                             /*   stopScan();
                                showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
                             */
                                globalScanScreenViewModel.get_temp_key(awb);
                            }
                        }

                    }

                } else {
                    if (CommonUtils.checkTimeslot(start_time, end_time)) {
                       /* globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "",0);
                        setBeepSound();*/
                      /*  stopScan();
                        showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
*/
                        if (bp_exist) {
                            globalScanScreenViewModel.get_temp_key(awb);
                          /*  stopScan();
                            showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
                     */
                        } else {
                            globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0);
                            setBeepSound();
                        }
                    } else {
                        if (scannedManifest.getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("R")) {
                            showManifestWarningDialog("R", start + " - " + end, awb, bp_exist);
                        } else {
                           /* globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "");
                            setBeepSound();*/

                            if (bp_exist) {
                             /*   stopScan();
                                showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
                           */
                                globalScanScreenViewModel.get_temp_key(awb);
                            } else {
                                globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0);
                                setBeepSound();
                            }
                           /* stopScan();
                            showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
*/

                        }
                    }


                    //first scan already done

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isAgentAtWarehouse() {
        Location feLocation = new Location("");
        feLocation.setLatitude(wayLatitude);
        feLocation.setLongitude(wayLongitude);
        Vender_Detail vendorDetails = globalScanScreenViewModel.scannedManifest.getManifest_details();
        if (isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), globalScanScreenViewModel.getGeoFenceRadius())) {
            return true;
        } else {
            return false;
        }
    }

    public void callFirstScanApi(long manifest_num, Manifest_List scannedManifest) {
        try {
            globalScanScreenViewModel.setManifestNumber(manifest_num);
            globalScanScreenViewModel.updateInscanStatus();
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(globalScanScreenViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifest_num);
            firstScanRequest.setEmp_Code(globalScanScreenViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(scannedManifest.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(scannedManifest.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isAgentAtWarehouse() ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, scannedManifest.getManifest_details().getLocation().getLatitude(), scannedManifest.getManifest_details().getLocation().getLongitude()));

            if (globalScanScreenViewModel.getDataManager().getChild() == true) {
                firstScanRequest.setRole("child");
            } else if (globalScanScreenViewModel.getDataManager().getParent() == true) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifest_num);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            globalScanScreenViewModel.updateIsScanStarted(manifest_num);
            globalScanScreenViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                globalScanScreenViewModel.callFirstScanApi(globalScanScreenViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    globalScanScreenViewModel.updateInscanToFirstScanTable(manifest_num);
                    globalScanScreenViewModel.inScanCommitPacket(manifest_num);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResult(String s) {
        i++;
        if (i == 1) {
            // notifyScanStarted();
        }
        Log.d("validateAWB4", "validateAWB");
        globalScanScreenViewModel.validateAWB(Long.parseLong(s));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        vehicleType = String.valueOf(adapterView.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void rtoLockCheck(long lastSyncTime) {
        ProgressDialog dialog = new ProgressDialog(GlobalScanScreenActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        try {
            RtoRequest rtoRequest = new RtoRequest();
            rtoRequest.setManifestIds(manifest_number);
            rtoRequest.setIs_mps(true);
            rtoRequest.setLast_sync_time(lastSyncTime);
            globalScanScreenViewModel.getAllRTOShipmentList(globalScanScreenViewModel.getDataManager().getAuthToken(), rtoRequest).observe(this, rtoLockResponse -> {

                try {
                    if (rtoLockResponse != null) {
                        if (rtoLockResponse.getStatus()) {
                            if (rtoLockResponse.getDescription().equalsIgnoreCase("success")) {
                                dialog.dismiss();
                                showToast("You may scan now");
                            }

                            globalScanScreenViewModel.getDataManager().setLastSyncTime(rtoLockResponse.getResponse().getLast_sync_time());


                            manifest_status_details = new ArrayList<>();
                            manifest_status_details.addAll(rtoLockResponse.getResponse().getManifest_status_details());

                            for (Manifest_status_details manifest_status_detail : manifest_status_details) {
                                listOfAwbs = new ArrayList<>();
                                rtoShipmentResponses = new ArrayList<>();
                                manifest_id = 0;
                                listOfAwbs.addAll(manifest_status_detail.getListOfAwbs());
                                rtoShipmentResponses.addAll(manifest_status_detail.getShipment_details());
                                manifest_id = manifest_status_detail.getManifest_id();
//                                locationType = manifest_status_detail.getManifest_id();

                                if (listOfAwbs.size() > 0) {
                                    for (ListOfAwbs listOfAwbs : listOfAwbs) {
                                        globalScanScreenViewModel.updateRtoShipment(manifest_id, listOfAwbs.getAwb(), listOfAwbs.getStatus_code());
                                    }
                                }
                                if (rtoShipmentResponses.size() > 0) {
                                    for (Shipment_Detail listOfShipments : rtoShipmentResponses) {
                                        listOfShipments.setManifestNoInchild(manifest_id);
                                 //    listOfShipments.setLocation_type(manifest_status_detail.get());
                                    }
                                    globalScanScreenViewModel.insertShipment(rtoShipmentResponses);

//                                    for (Shipment_Detail listOfShipment : rtoShipmentResponses) {
//                                        globalScanScreenViewModel.updateBPID(manifest_id, listOfShipment.getAirwaybill_number(), listOfShipment.getBrand_package_id());
//                                    }

                                }


                            }

                            if (isNetworkConnected()) {
                                CallBPIdApi(manifest_number);

                            } else {
                                showToast(getString(R.string.check_internet));
                            }

                        } else {
                            dialog.dismiss();
                            if (rtoLockResponse.getDescription().equalsIgnoreCase("No shipment found with RTO lock")) {

                            } else {
                                showToast(rtoLockResponse.getDescription());
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
    }

    private void CallBPIdApi(ArrayList<Long> manifest_no) {

        ProgressDialog dialog = new ProgressDialog(GlobalScanScreenActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);

        try {
            UpdatedBPRequest updatedBPRequest = new UpdatedBPRequest();

            /* ArrayList<Long> manifest_number = new ArrayList<>();*/
            //  manifest_number.addAll(manifest_no);
            updatedBPRequest.setManifest_ids(manifest_no);
            updatedBPRequest.setIs_with_mps(true);
            updatedBPRequest.setLast_sync_time(lastSyncTime);
            globalScanScreenViewModel.getUpdatedBpAwbRto(globalScanScreenViewModel.getDataManager().getAuthToken(), updatedBPRequest).observe(this, response -> {
                try {
                    if (response != null) {
                        if (response.getStatus()) {

                            dialog.dismiss();


                            Intent intent = new Intent(context, MyIntentService.class);
                            intent.putExtra("extra_data", response);
                            intent.putExtra("come_from", "global");

                            // Start the IntentService
                            startService(intent);
                        } else {
                            dialog.dismiss();

                            showToast(response.getDescription());

                        }


                    } else {
                        dialog.dismiss();
                        showToast(response.getDescription());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {

        }


    }


    public void showAlert(String message, Context context) {
        try {
            GlobalScanScreenActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
                        mScanMgr.setScanEnable(false);
                        if (!((Activity) context).isFinishing()) {
                            showDialog(message, getString(R.string.newland));

                        }
                     /*   AlertDialog.Builder aBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        AlertDialog dialog = aBuilder.setMessage(message)
                                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mScanMgr.setScanEnable(true);
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.setCancelable(false);
                        dialog.show();*/
                    } else {
                       /* AlertDialog.Builder aBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        AlertDialog dialog = aBuilder.setMessage(message)
                                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.setCancelable(false);
                        dialog.show();*/

                        capture.onPause();
                        if (!((Activity) context).isFinishing()) {
                            showDialog(message, "");
                        }
                    }
                }
            });
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }


    private void getLocation() {
        if (isNetworkConnected()) {
            if (ActivityCompat.checkSelfPermission(GlobalScanScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GlobalScanScreenActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GlobalScanScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.LOCATION_REQUEST);

            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(GlobalScanScreenActivity.this, location -> {
                    if (location != null) {
                        if (location.getAccuracy() < globalScanScreenViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                            }
                        }
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });

            }
        } else {
            LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        if (location.getAccuracy() < globalScanScreenViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = globalScanScreenViewModel.getLastLatitudeFromPref();
                                    wayLongitude = globalScanScreenViewModel.getLastLongitudeFromPref();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }, Looper.getMainLooper());

        }
        if (wayLatitude == 0.0 && wayLongitude == 0.0) {
            wayLatitude = globalScanScreenViewModel.getLastLatitudeFromPref();
            wayLongitude = globalScanScreenViewModel.getLastLongitudeFromPref();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (device.equals(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
        } else {
            capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(GlobalScanScreenActivity.this, location -> {
                        if (location != null) {
                            if (location.getAccuracy() < globalScanScreenViewModel.getGeoFenceRadius()) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = globalScanScreenViewModel.getLastLatitudeFromPref();
                                    wayLongitude = globalScanScreenViewModel.getLastLongitudeFromPref();
                                }
                            }
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void showDialog(String message, String came_from) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        CustomDialogMessageBinding dialogbinding =
                DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.custom_dialog_message, (ViewGroup) activityGlobalScanBinding.getRoot(), false);
        dialog.setContentView(dialogbinding.getRoot());
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogbinding.tvStatus.setText(message);
        dialogbinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
                dialog.dismiss();
            }
        });


        if (message.equalsIgnoreCase(getString(R.string.shipment_rto_locked)) || message.equalsIgnoreCase(getString(R.string.rto_locked)) || message.equalsIgnoreCase(getString(R.string.advance_validation_for_global))) {

            dialogbinding.tvStatus.setTextColor(getResources().getColor(R.color.white));

            dialogbinding.scannedCard.setCardBackgroundColor(getResources().getColor(R.color.shipment_rto_locked));

            setErrorSound();

        } else if (message.equalsIgnoreCase(getString(R.string.regex_vaidation)) || message.equalsIgnoreCase(getString(R.string.failed))) {
            dialogbinding.tvStatus.setTextColor(getResources().getColor(R.color.white));
            dialogbinding.scannedCard.setCardBackgroundColor(getResources().getColor(R.color.shipment_rto_locked));

            setErrorSound();
        } else if (message.equalsIgnoreCase(getString(R.string.alreadyscanned)) || message.equalsIgnoreCase(getString(R.string.already_scanned)) || message.equalsIgnoreCase(getString(R.string.airbill_validate)) || message.equalsIgnoreCase(getString(R.string.already_exits)) || message.equalsIgnoreCase(getString(R.string.Invalid_data)) || message.equalsIgnoreCase(getString(R.string.awb_does_nt_exist))) {

            dialogbinding.tvStatus.setTextColor(getResources().getColor(R.color.white));
            dialogbinding.scannedCard.setCardBackgroundColor(getResources().getColor(R.color.shipment_scanned_already));

            setErrorSound();
            //     beepManager.setVibrateEnabled(true);
        }

        dialog.show();
    }

    public void setErrorSound() {

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bad_beep);

        if (this.mediaPlayer != null && !this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.setVolume(100, 100);
            this.mediaPlayer.start();
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }


    }


    public void setBeepSound() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);

        if (this.mediaPlayer != null && !this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.setVolume(100, 100);

            this.mediaPlayer.start();
        }
    }

    public void showManifestWarningDialog(String validate, String pickup_slot, long awb, boolean bp_exist) {
      /*  if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.setScanEnable(false);
            showPickUpWarningDialog(validate, pickup_slot, awb, Constants._NEWLAND,bp_exist);
        } else {
            capture.onPause();
            showPickUpWarningDialog(validate, pickup_slot, awb, "",bp_exist);
        }
*/
        stopScan();
        showPickUpWarningDialog(validate, pickup_slot, awb, "", bp_exist);

    }

    public void showPickUpWarningDialog(String validate, String pickup_slot, long awb, String came_from, boolean bp_exist) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        WarningDialogBinding warningDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.warning_dialog, (ViewGroup) activityGlobalScanBinding.getRoot(), false);
        dialog.setContentView(warningDialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (validate.equalsIgnoreCase("W")) {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + context.getString(R.string.warning_message));
        } else {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + context.getString(R.string.restriction_message));
        }

        warningDialogBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate.equalsIgnoreCase("W")) {
                    if (bp_exist) {

                        //  showAlertDialogButtonClicked(awb, getString(R.string.scan_brand_packaging), false, "2");
                        globalScanScreenViewModel.get_temp_key(awb);
                    } else {
                        globalScanScreenViewModel.updateShipment(awb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0);
                        setBeepSound();
                        //TODO 15/08
                        scanFrom = "";
                        startScan();
                    }

                    /*if (came_from.equalsIgnoreCase(Constants._NEWLAND)) {
                        mScanMgr.setScanEnable(true);
                        dialog.dismiss();
                    } else {
                        capture.onResume();*/
                    dialog.dismiss();
                    //   }

                } else {
                  /*  if (came_from.equalsIgnoreCase(Constants._NEWLAND)) {
                        mScanMgr.setScanEnable(true);
                        dialog.dismiss();
                    } else {
                        capture.onResume();
                        dialog.dismiss();
                    }*/
                    scanFrom = "";
                    startScan();
                    dialog.dismiss();
                }


            }
        });

        dialog.show();
    }


    public void showAlertDialogButtonClicked(long awb, String msg, boolean count_over, String count) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        BpDialogBoxBinding bpDialogBoxBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.bp_dialog_box,
                (ViewGroup) activityGlobalScanBinding.getRoot(),
                false
        );
        dialog.setContentView(bpDialogBoxBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        bpDialogBoxBinding.tvMsg.setText(msg);
        if (count.equalsIgnoreCase("Over")) {
            bpDialogBoxBinding.tvAttempt.setText(String.format("Attempt:- %s", count));
        } else {

            bpDialogBoxBinding.tvAttempt.setText(String.format("Attempt:- %s Left", count));
        }
        bpDialogBoxBinding.btOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
                if (count_over) {
                    globalScanScreenViewModel.markUndelivered(storingAwb, reasonCodeLists.get(0).getReason_id(), reasonCodeLists.get(0).getReason_code(), "");
                    scanFrom = "";
                    showToast("Reason Code Applied");
                    BP_COUNT = 2;
                } else {
                    storingAwb = awb;
                    scanFrom = Constants.BP_ID;


                }

                dialog.dismiss();


            }
        });

        dialog.show();

    }


    @Override
    public void isBrandPackagingIDisValid(List<Shipment_Detail> shipment_details, String bp_id) {
        try {
            if (shipment_details.size() != 0) {
                if (shipment_details.get(0).getBrand_package_id().equalsIgnoreCase(bp_id)) {
                    globalScanScreenViewModel.updateShipment(storingAwb, Constants.PICKED, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 1);
                    /*   globalScanScreenViewModel.getManifestIdFromAwb(shipment_detailResult.getAirwaybill_number(), "");
                     */
                    Log.d("check_ss", "1st");
                    setBeepSound();
                    scanFrom = "";
                    startScan();
                    BP_COUNT = 2;

                } else {
                    Log.d("check_ss", "2nd");
                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), false, "0");
                    }


                }

            } else {
                if (BP_COUNT == 0) {
                    Log.d("check_ss", "3rd");
                    Log.d("ssss", "apply reason code on");
                    //  scanWarehouseViewModel.markUndelivered(storingAwb, 0, "pickUpList.get(i).getReason_code()", "");
                    //  scanFrom = "";
                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), true, "Over");

                    }

                    setErrorSound();

                } else {
                    Log.d("check_ss", "4th");

                    // showAlertDialogButtonClicked(storingAwb,getString(R.string.Wrong_bp_code));
                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), false, String.valueOf(BP_COUNT));
                    }

                    BP_COUNT--;
                    setErrorSound();

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getBP_ID(String bp_id, long awb_no) {
        if (bp_id.equalsIgnoreCase("")) {

            Log.d("check_sss", "ok");

            globalScanScreenViewModel.getManifestIdFromAwb(awb_no, "", false);

            /*scanFrom = "";*/
        } else {
           /* Log.d("check_sss","ss");
            scanWarehouseViewModel.getbp_id_from_awb_no(awb_no);*/


            globalScanScreenViewModel.getManifestIdFromAwb(awb_no, "", true);

            //  scanFrom = "";
            //  stopScan();
            //   showAlertDialogButtonClicked(awb_no, getString(R.string.scan_brand_packaging), false, "2");
        }
    }

    @Override
    public void getTempKey(long awbNo, Boolean tempValue) {

        Current_AWb_FOR_BP_ID = awbNo;
        storingAwb = awbNo;

        //  globalScanScreenViewModel.updateShipmentTemp(awbNo, Constants.PENDING, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 1, !tempValue);
        globalScanScreenViewModel.updateShipmentTemp(awbNo, Constants.PENDING, vehicleType, Calendar.getInstance().getTimeInMillis(), false, "", 0, !tempValue);
        scanFrom = Constants.BP_ID;
    }


    public void stopScan() {

        if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.setScanEnable(false);

        } else {
            capture.onPause();


        }
    }

    public void startScan() {

        if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.setScanEnable(true);

        } else {
            capture.onResume();

        }
    }
}

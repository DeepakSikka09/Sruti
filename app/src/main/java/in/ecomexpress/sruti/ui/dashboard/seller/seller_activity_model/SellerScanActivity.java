package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import in.ecomexpress.sruti.databinding.ActivityScanVendorBinding;
import in.ecomexpress.sruti.databinding.BpDialogBoxBinding;
import in.ecomexpress.sruti.databinding.CustomDialogMessageBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.handover.IHandOverNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 26/8/19.
 */
public class SellerScanActivity extends BaseActivity<ActivityScanVendorBinding, SellerScanViewModel> implements IHandOverNavigator, in.ecomexpress.barcodelistner.BarcodeResult {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private SellerScanViewModel sellerScanViewModel;
    private ActivityScanVendorBinding activityScanVendorBinding;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;
    ArrayList<Shipment_Detail> shipmentData;
    private SellerScanListAdapter mAdapter;
    public String lastText = null;

    public int awbCount;
    private BarcodeHandler barcodeHandler;
    private ScanManager mScanMgr;
    public String device;
    MediaPlayer mediaPlayer;
    private int outputMode = -1;
    private int i = 0;
    private String vehicle;
    private Long manifest;
    private Manifest_List manifestList;
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
    public boolean Seller_BP_ID = false;
    public long Current_AWb_FOR_BP_ID = 0;

    // private String cameraId;
    private boolean isFlashlightOn = false;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
               /* if (!sellerScanViewModel.loginAfterServerTime()) {
                    if (!isIncorrectTimeDialogOpen) {
                        showIncorrectTimeAlert("Cannot proceed due to incorrect device time.\n Do you want to change?", true);
                        return;
                    }
                } else*/
                if (scanFrom.equalsIgnoreCase(Constants.BP_ID)) {
                    if (result.getText() == null || result.getText().equals(lastTextBp) || result.getText().equalsIgnoreCase(lastText)) {
                        return;
                    } else {
                        lastTextBp = result.getText();
                        Log.d("check_bp", lastTextBp);
                        Log.d("check_awb", String.valueOf(storingAwb));

                        sellerScanViewModel.ifBrandPackagingIDexists(manifest, storingAwb, lastTextBp);
                    }
                } else {
                    if (result.getText() == null || result.getText().equals(lastText) || result.getText().equals(lastTextBp)) {
                        return;
                    } else {

                        lastText = result.getText();
                        Pattern pattern = Pattern.compile(Constants.REGEX);
                        Matcher matcher = pattern.matcher(lastText);
                        if (matcher.matches()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("L")) {
                                    sellerScanViewModel.ifAWBexists(manifest, Long.valueOf(lastText), Constants.PENDING);
                                } else if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("S")) {
                                    if (isNetworkConnected()) {
                                        sellerScanViewModel.callScanApi(lastText, manifestList.getCust_code(), manifestList.getManifest_No(), vehicle, SellerScanActivity.this);
                                    } else {
                                        showToast(getString(R.string.check_internet));
                                    }
                                } else {
                                    sellerScanViewModel.ifAWBexists(manifest, Long.valueOf(lastText), Constants.PENDING);
                                }
                            } else {
                                //deprecated in API 26
                                if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("L")) {
                                    sellerScanViewModel.ifAWBexists(manifest, Long.valueOf(lastText), Constants.PENDING);
                                } else if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("S")) {
                                    if (isNetworkConnected()) {

                                        sellerScanViewModel.callScanApi(lastText, manifestList.getCust_code(), manifestList.getManifest_No(), vehicle, SellerScanActivity.this);
                                    } else {
                                        showToast(getString(R.string.check_internet));
                                    }
                                } else {
                                    sellerScanViewModel.ifAWBexists(manifest, Long.valueOf(lastText), Constants.PENDING);
                                }
                            }
                        } else {
                            showAlert(getString(R.string.regex_vaidation), SellerScanActivity.this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityScanVendorBinding = getViewDataBinding();
        sellerScanViewModel.setNavigator(this);
        manifestList = getIntent().getParcelableExtra("manifest_detail");
        manifest = getIntent().getExtras().getLong("manifest");
        sellerScanViewModel.setManifestNumber(manifest);
        activityScanVendorBinding.flashlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlashlightOn) {
                    barcodeScannerView.setTorchOff();
                    activityScanVendorBinding.flashlightButton.setImageResource(R.drawable.flashoff);
                    isFlashlightOn = false;
                } else {
                    barcodeScannerView.setTorchOn();
                    activityScanVendorBinding.flashlightButton.setImageResource(R.drawable.flashon);
                    isFlashlightOn = true;
                }
            }
        });


        List<String> vehicleNumber = new ArrayList<>();

        reasonCodeLists = new ArrayList<>();
        if (!sellerScanViewModel.getDataManager().is_Ecom_Vehicle()) {
            if (sellerScanViewModel.getDataManager().getVehicleNo() != null) {
                vehicle = sellerScanViewModel.getDataManager().getVehicleNo();
                vehicleNumber.add(vehicle);
            } else {
                showAlert(getString(R.string.Invalid_data), SellerScanActivity.this);
            }

        } else {
            for (int j = 0; j < sellerScanViewModel.getDataManager().getRouteDetail().size(); j++) {
                if (!sellerScanViewModel.getDataManager().getRouteDetail().get(j).isDeparted()) {
                    vehicle = sellerScanViewModel.getDataManager().getRouteDetail().get(j).getStart_vehicle_number();
                    vehicleNumber.add(vehicle);
                }
            }
        }

        device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            activityScanVendorBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            activityScanVendorBinding.flashlightButton.setVisibility(View.GONE);
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.disableBeep();
            mScanMgr.setScanEnable(true);
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            Map<String, String> settings = mScanMgr.getScanSettings();
            String sOutputMode = settings.get(ScanSettings.Global.OUT_PUT_MODE); //Acquire
            outputMode = ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST;
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {

            activityScanVendorBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            activityScanVendorBinding.flashlightButton.setVisibility(View.GONE);
            barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
            barcodeHandler.enableScanner();
            barcodeHandler.setBeepEnabled(false);
        } else {
            activityScanVendorBinding.flashlightButton.setVisibility(View.VISIBLE);
            beepManager = new BeepManager(this);
            barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
            capture = new CaptureManager(this, barcodeScannerView);
            capture.initializeFromIntent(getIntent(), savedInstanceState);
            barcodeScannerView.decodeContinuous(callback);

        }
        mAdapter = new SellerScanListAdapter(this);
        activityScanVendorBinding.tvAwbCount.setText("");
        activityScanVendorBinding.tvAwbCount.setVisibility(View.VISIBLE);
        activityScanVendorBinding.tvAwbCount.setText(String.valueOf(mAdapter.getItemCount()));
        if (mAdapter.getItemCount() != 0) {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.generic_button);
            activityScanVendorBinding.lvBack.setBackgroundDrawable(buttonDrawable);
        } else {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.login_button);
            buttonDrawable.mutate();
            activityScanVendorBinding.lvBack.setBackgroundDrawable(buttonDrawable);
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
                        if (location.getAccuracy() < sellerScanViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        }
                    }
                }
            }
        };


        getLocation();

        sellerScanViewModel.getPickupListToView(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT).observe(this, pickupList -> {
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

    private void setUp() {
        activityScanVendorBinding.vendorRecyclerData.setHasFixedSize(true);
        activityScanVendorBinding.vendorRecyclerData.setItemAnimator(new DefaultItemAnimator());
        activityScanVendorBinding.vendorRecyclerData.setAdapter(mAdapter);
        activityScanVendorBinding.vendorRecyclerData.setLayoutManager(new LinearLayoutManager(this));
        sellerScanViewModel.getScannedVendorStatus().observe(this, shipment_details -> {
            if (shipment_details.size() != 0) {
                awbCount = mAdapter.getItemCount();
                for (int i = 0; i < shipment_details.size(); i++) {
                    sellerScanViewModel.updateShipment(shipment_details.get(i).getAirwaybill_number(), Constants.PICKED, vehicle, false, "", 0);
                }
            } else {
                showAlert(getString(R.string.airbill_validate), SellerScanActivity.this);
            }
        });
        sellerScanViewModel.getAllAwbData(manifest).observe(SellerScanActivity.this, new Observer<List<Shipment_Detail>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable List<Shipment_Detail> shipment_details) {

              /*  ArrayList tempShipmentList = new ArrayList();
                ArrayList tempPickedShipmentList = new ArrayList();
                setAdapter(tempShipmentList, shipment_details, tempPickedShipmentList);
*/

                shipmentData = new ArrayList<>();
                shipmentData.addAll(shipment_details);
                checkPendingAWb();

            }

        });
    }

    private void setAdapter(List<Shipment_Detail> shipment_details) {

        Log.d("check_ss", "ook");
        ArrayList tempShipmentList = new ArrayList();
        ArrayList tempPickedShipmentList = new ArrayList();
        ArrayList<Shipment_Detail> concatData = new ArrayList();


        if (shipment_details.size() > 0) {
            concatData.addAll(shipment_details);
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


        mAdapter.setData(tempShipmentList, Current_AWb_FOR_BP_ID);
        setbutton(tempPickedShipmentList);
    }

    private void setbutton(List<Shipment_Detail> shipment_details) {
        activityScanVendorBinding.tvAwbCount.setVisibility(View.VISIBLE);

        if (shipment_details != null) {
            activityScanVendorBinding.tvAwbCount.setText(String.valueOf(shipment_details.size()));
            Drawable buttonDrawable;
            if (!shipment_details.isEmpty()) {
                buttonDrawable = getResources().getDrawable(R.drawable.generic_button);
            } else {
                buttonDrawable = getResources().getDrawable(R.drawable.login_button);
            }
            buttonDrawable.mutate();
            activityScanVendorBinding.lvBack.setBackgroundDrawable(buttonDrawable);
        }
    }

    @Override
    public SellerScanViewModel getViewModel() {
        sellerScanViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(SellerScanViewModel.class);
        return sellerScanViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_vendor;
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(mResultReceiver, intFilter);
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
            barcodeHandler.onResume();
        } else {
            capture.onResume();
            if (isFlashlightOn) {
                // Turn on the flashlight
                barcodeScannerView.setTorchOff();
                activityScanVendorBinding.flashlightButton.setImageResource(R.drawable.flashoff);
            }
        }
        super.onResume();

        startLocationUpdate = true;
        startLocationThread();


    }

    @Override
    protected void onPause() {
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            Log.d("check_sss", "onPause");
            mScanMgr.stopScan();
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onPause();
        }
        super.onPause();

        startLocationUpdate = false;
        if (locationThread != null) {
            locationThread.interrupt();
            locationThread = null;
        }
        if (isFlashlightOn) {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    protected String getScreenName() {
        return "Seller Scan Screen";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            Log.d("check_sss", "on");
            mScanMgr.stopScan();
            unregisterReceiver(mResultReceiver);
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
        } else {
            capture.onSaveInstanceState(outState);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
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
                        Log.d("check_data", sValue);
                        Log.d("check_data", String.valueOf(bvalue));
                        if (scanFrom.equalsIgnoreCase(Constants.BP_ID)) {
                            lastTextBp = sValue;
                            Log.d("check_bp", lastTextBp);
                            Log.d("check_awb", String.valueOf(storingAwb));

                            sellerScanViewModel.ifBrandPackagingIDexists(manifest, storingAwb, lastTextBp);

                        } else {
                            Pattern pattern = Pattern.compile(Constants.REGEX);
                            Matcher matcher = pattern.matcher(sValue);
                            if (matcher.matches()) {
                                if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("L")) {
                                    sellerScanViewModel.ifAWBexists(manifestList.getManifest_No(), Long.valueOf(sValue), Constants.PENDING);
                                } else if (manifestList.getSetting().getReal_time_soft_data_check().equalsIgnoreCase("S")) {
                                    if (isNetworkConnected()) {
                                        sellerScanViewModel.callScanApi(sValue, manifestList.getCust_code(), manifestList.getManifest_No(), vehicle, SellerScanActivity.this);
                                    } else {
                                        showToast(getString(R.string.check_internet));
                                    }
                                } else {
                                    sellerScanViewModel.ifAWBexists(manifestList.getManifest_No(), Long.valueOf(sValue), Constants.PENDING);
                                }
                            } else {
                                showAlert(getString(R.string.regex_vaidation), SellerScanActivity.this);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNext() {
        finish();
    }

    @Override
    public void showValidationForNull() {
        showAlert(getString(R.string.Invalid_data), SellerScanActivity.this);

    }

    @Override
    public void showErrorMessage(String b) {
        showToast(b);
    }

    @Override
    public void showException(Exception e) {
        showToast(e.getMessage());
    }

    @Override
    public void onResult(String s) {
        i++;
        if (i == 1) {
            // notifyScanStarted();
        }
        sellerScanViewModel.setScannedVendorStatus(manifest, Long.parseLong(s));
    }


    @Override
    public void notifyAdapter() {
        sellerScanViewModel.getAllAwbData(manifest).observe(SellerScanActivity.this, new Observer<List<Shipment_Detail>>() {
            @Override
            public void onChanged(@Nullable List<Shipment_Detail> shipment_details) {


                shipmentData = new ArrayList<>();
                shipmentData.addAll(shipment_details);
                checkPendingAWb();
            }
        });

    }

    private void checkPendingAWb() {


        // Remove the observer before adding a new one
        sellerScanViewModel.getPendingAWb().removeObserver(pendingAWbObserver);

        // Add the observer to getPendingAWb
        sellerScanViewModel.getPendingAWb().observe(SellerScanActivity.this, pendingAWbObserver);

    }

    private Observer<List<Shipment_Detail>> pendingAWbObserver = new Observer<List<Shipment_Detail>>() {
        @Override
        public void onChanged(List<Shipment_Detail> shipment_detail) {

            setAdapter(shipment_detail);
        }
    };

    // Other code
    @Override
    public void isAWBRtoLock(Boolean aBoolean, long awbNumber) {
        int flag = manifestList.getSetting().getAdvance_pickup_check();
        if (aBoolean) {
            showAlert(getString(R.string.shipment_rto_locked), SellerScanActivity.this);
        } else {
            try {
                if (flag == 1) {
                    sellerScanViewModel.isAlreadyScanned(manifest, awbNumber);
                } else {
                    sellerScanViewModel.ifAWBPresent(manifest, awbNumber, "");
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void checkFirstScan(Long value) {
        Log.d("check_ss", "7");
        if (value == 0) {
            Log.d("check_ss", "8");
            callFirstScanApi(manifest);
        } else {
            //first scan already done
        }
    }

    @Override
    public void getShipmentStatus(String status, long awbNumber) {
        if (status.equalsIgnoreCase(Constants.PICKED)) {
            showAlert(getString(R.string.alreadyscanned), SellerScanActivity.this);
        } else if (status.equalsIgnoreCase(Constants.FAILED)) {
            showAlert(getString(R.string.failed), SellerScanActivity.this);
        } else if (status.equalsIgnoreCase(Constants.PENDING)) {

            sellerScanViewModel.getbp_id_from_awb_no(awbNumber);
            setBeepSound();
        } else if (status.equalsIgnoreCase(Constants.RTO_STATUS_1) || status.equalsIgnoreCase(Constants.RTO_STATUS_2)) {
            showAlert(getString(R.string.rto_locked), SellerScanActivity.this);
        }
    }

    @Override
    public void isAlreadyScanned(Boolean aBoolean, Long awbNumber) {
        if (aBoolean) {
            sellerScanViewModel.getShipmentExist(awbNumber, manifest);
        } else {
            try {
                if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
                    sellerScanViewModel.insertAdvanceShipment(manifest, awbNumber, vehicle);
                    sellerScanViewModel.updateShipment(awbNumber, Constants.PICKED, vehicle, true, "", 0);
                    Current_AWb_FOR_BP_ID = 0;
                    setBeepSound();
                } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                        || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                        || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
                    sellerScanViewModel.insertAdvanceShipment(manifest, awbNumber, vehicle);
                    sellerScanViewModel.updateShipment(awbNumber, Constants.PICKED, vehicle, true, "", 0);
                    Current_AWb_FOR_BP_ID = 0;
                    setBeepSound();
                } else {
                    sellerScanViewModel.insertAdvanceShipment(manifest, awbNumber, vehicle);
                    sellerScanViewModel.updateShipment(awbNumber, Constants.PICKED, vehicle, true, "", 0);
                    Current_AWb_FOR_BP_ID = 0;
                    setBeepSound();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void RtoLocked() {
        showAlert(getString(R.string.shipment_rto_locked), SellerScanActivity.this);
    }

    @Override
    public void ifAWBPresent(Boolean aBoolean, long awb) {
        if (aBoolean) {
            sellerScanViewModel.getShipmentStatus(awb, manifest);
        } else {
            showAlert("This awb does not exist", SellerScanActivity.this);
        }
    }

    @Override
    public void getShipmentExist(Boolean manifest_list, long awbNumber) {
        if (manifest_list) {
            sellerScanViewModel.getShipmentStatus(awbNumber, manifest);
        } else {
            showAlert(getString(R.string.already_exits), SellerScanActivity.this);
        }
    }


    @Override
    public void realTimeCheckInvalid(String lastText) {
        if (manifestList.getSetting().getAdvance_pickup_check() == 1) {
            sellerScanViewModel.isAlreadyScanned(manifest, Long.valueOf(lastText));
        } else {
            showAlert(getString(R.string.airbill_validate), SellerScanActivity.this);
        }
    }


    @Override
    public void isAwbValid(List<Shipment_Detail> shipment_details, long awbNumber) {
        if (shipment_details.size() != 0) {
            sellerScanViewModel.isAWBRtoLock(manifest, awbNumber);
        } else {
            if (manifestList.getSetting().getAdvance_pickup_check() == 1) {
                try {
                    sellerScanViewModel.isAlreadyScanned(manifest, awbNumber);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            } else {
                if (shipment_details.size() == 0) {
                    sellerScanViewModel.isAWBRtoLock(manifest, awbNumber);
                }
            }
        }
    }


    public void callFirstScanApi(long manifestNumber) {
        try {
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(sellerScanViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifestNumber);
            firstScanRequest.setEmp_Code(sellerScanViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(manifestList.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(manifestList.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isAgentAtWarehouse() ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, manifestList.getManifest_details().getLocation().getLatitude(), manifestList.getManifest_details().getLocation().getLongitude()));


            if (sellerScanViewModel.getDataManager().getChild() == true) {
                firstScanRequest.setRole("child");
            } else if (sellerScanViewModel.getDataManager().getParent() == true) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifest);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            sellerScanViewModel.updateIsScanStarted(manifestNumber);
            sellerScanViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                sellerScanViewModel.callFirstScanApi(sellerScanViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    sellerScanViewModel.updateInscanStatus();
                    sellerScanViewModel.updateInscanToFirstScanTable(manifestNumber);
                    sellerScanViewModel.inScanCommitPacket(manifestNumber);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showAlert(String message, Context context) {
        if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            mScanMgr.setScanEnable(false);
            showDialog(message, getString(R.string.newland));

        } else {
            capture.onPause();
            showDialog(message, "");

        }
    }

    private void getLocation() {
        if (isNetworkConnected()) {
            if (ActivityCompat.checkSelfPermission(SellerScanActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(SellerScanActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SellerScanActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.LOCATION_REQUEST);

            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(SellerScanActivity.this, location -> {
                    if (location != null) {
                        if (location.getAccuracy() < sellerScanViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = sellerScanViewModel.getLastLatitudeFromPref();
                                    wayLongitude = sellerScanViewModel.getLastLongitudeFromPref();
                                }
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
                        if (location.getAccuracy() < sellerScanViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;

                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = sellerScanViewModel.getLastLatitudeFromPref();
                                    wayLongitude = sellerScanViewModel.getLastLongitudeFromPref();
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
            wayLatitude = sellerScanViewModel.getLastLatitudeFromPref();
            wayLongitude = sellerScanViewModel.getLastLongitudeFromPref();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (device.equalsIgnoreCase(Constants._NEWLAND) || device.equalsIgnoreCase(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
        } else if (device.equalsIgnoreCase(Constants._ZEBRA) || device.equalsIgnoreCase(Constants._95W_IDATA) || device.equalsIgnoreCase(Constants._FREEDOM)
                || device.equalsIgnoreCase(Constants._SEUIC2_CH) || device.equalsIgnoreCase(Constants._SEUIC_CH)
                || device.equalsIgnoreCase(Constants._LUNATE_IDATA)) {
        } else {
            capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(SellerScanActivity.this, location -> {
                        if (location != null) {
                            if (location.getAccuracy() < sellerScanViewModel.getGeoFenceRadius()) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = sellerScanViewModel.getLastLatitudeFromPref();
                                    wayLongitude = sellerScanViewModel.getLastLongitudeFromPref();
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

    private boolean isAgentAtWarehouse() {
        Location feLocation = new Location("");
        feLocation.setLatitude(wayLatitude);
        feLocation.setLongitude(wayLongitude);
        Vender_Detail vendorDetails = manifestList.getManifest_details();
        if (isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), sellerScanViewModel.getGeoFenceRadius())) {
            return true;
        } else {
            return false;
        }
    }

    public void showDialog(String message, String came_from) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        CustomDialogMessageBinding dialogbinding =
                DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.custom_dialog_message, (ViewGroup) activityScanVendorBinding.getRoot(), false);
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


        if (message.equalsIgnoreCase(getString(R.string.shipment_rto_locked)) || message.equalsIgnoreCase(getString(R.string.rto_locked))) {

            dialogbinding.tvStatus.setTextColor(getResources().getColor(R.color.white));

            dialogbinding.scannedCard.setCardBackgroundColor(getResources().getColor(R.color.shipment_rto_locked));

            setErrorSound();

        } else if (message.equalsIgnoreCase(getString(R.string.regex_vaidation)) || message.equalsIgnoreCase(getString(R.string.failed))) {
            dialogbinding.tvStatus.setTextColor(getResources().getColor(R.color.white));
            dialogbinding.scannedCard.setCardBackgroundColor(getResources().getColor(R.color.shipment_rto_locked));

            setErrorSound();
        } else if (message.equalsIgnoreCase(getString(R.string.alreadyscanned)) || message.equalsIgnoreCase(getString(R.string.airbill_validate)) || message.equalsIgnoreCase(getString(R.string.already_exits)) || message.equalsIgnoreCase(getString(R.string.Invalid_data)) || message.equalsIgnoreCase(getString(R.string.awb_does_nt_exist))) {

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

    // This code is also repetitive in both the sellerScan and WarehouseScan activity
    public void showAlertDialogButtonClicked(long awb, String msg, boolean count_over, String count) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        BpDialogBoxBinding bpDialogBoxBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.bp_dialog_box,
                (ViewGroup) activityScanVendorBinding.getRoot(),
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
                    sellerScanViewModel.markUndelivered(storingAwb, reasonCodeLists.get(0).getReason_id(), reasonCodeLists.get(0).getReason_code(), "");
                    scanFrom = "";
                    BP_COUNT = 2;
                    Current_AWb_FOR_BP_ID = 0;
                    showToast("Reason Code Applied");
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

                    Log.d("check_ss", "1");
                    /*  sellerScanViewModel.updateShipmentBPStatus(storingAwb);*/
                    sellerScanViewModel.updateShipment(storingAwb, Constants.PICKED, vehicle, false, "", 1);
                    setBeepSound();
                    startScan();
                    scanFrom = "";
                    BP_COUNT = 2;
                    Current_AWb_FOR_BP_ID = 0;
                    //Log.d("check_status",mScanMgr.getScannerType())
                } else {

                    Log.d("check_ss", "2");
                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), false, "0");
                    }
                }

            } else {
                // This code is also the same in Seller and WareHouse
                if (BP_COUNT == 0) {
                    Log.d("ssss", "apply reason code on");
                    Log.d("check_ss", "3");
                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), true, "Over");
                    }

                    setErrorSound();

                } else {
                    Log.d("check_ss", "4");

                    stopScan();
                    if (!this.isFinishing()) {
                        showAlertDialogButtonClicked(storingAwb, getString(R.string.Wrong_bp_code), false, String.valueOf(BP_COUNT));
                    }

                    setErrorSound();
                    BP_COUNT--;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getBP_ID(String bp_id, long awb_no) {

        if (bp_id.equalsIgnoreCase("")) {
            Current_AWb_FOR_BP_ID = 0;
            sellerScanViewModel.updateShipment(awb_no, Constants.PICKED, vehicle, false, "", 0);
            setBeepSound();
        } else {

            sellerScanViewModel.get_temp_key(awb_no, bp_id);

        }
    }

    @Override
    public void getTempKey(String bpId, long awbNo, Boolean tempKey) {
        Seller_BP_ID = true;
        Current_AWb_FOR_BP_ID = awbNo;
        storingAwb = awbNo;
        /* scanFrom = Constants.BP_ID;*/
        Log.d("check_bp_s", String.valueOf(Seller_BP_ID));
        sellerScanViewModel.updateShipmentTemp(awbNo, Constants.PENDING, vehicle, false, "", 0, !tempKey);
        scanFrom = Constants.BP_ID;

    }


    public void stopScan() {

        if (device.equals(Constants._NEWLAND) || device.equals(Constants._NEW_NEWLAND) || device.equalsIgnoreCase(Constants._NEWLAND_T90)) {
            Log.d("checking", "stop_scan");
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



package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityWarehouseBinding;
import in.ecomexpress.sruti.model.ListOfAwbs;
import in.ecomexpress.sruti.model.RtoRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPRequest;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.CommitDialogValidation;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ChildCallback;
import in.ecomexpress.sruti.ui.dashboard.shipment.ShipmentOtpActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces.IWarehouseAdapterrInterface;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces.IWarehouseNavigator;
import in.ecomexpress.sruti.utils.MyIntentService;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class WarehouseActivity extends BaseActivity<ActivityWarehouseBinding, WarehouseViewModel> implements IWarehouseNavigator, IWarehouseAdapterrInterface, View.OnClickListener, AdapterView.OnItemSelectedListener, ChildCallback {
    public static boolean isParent;
    @Inject
    WarehouseViewModel warehouseViewModel;
    @Inject
    WarehouseShipmentAdapter warehouseShipmentAdapter;
    String vehicleType;
    private List<ReasonCodeList> pickUpList;
    private Manifest_List manifestList;
    private ArrayList<Shipment_Detail> shipment_detail;
    private ArrayList<Shipment_Detail> scanned_shipment_detail;
    private ActivityWarehouseBinding activityWarehouseBinding;
    private long manifestNo;
    private ArrayList<ListOfAwbs> listOfAwbs;
    private ArrayList<Shipment_Detail> rtoShipmentResponses;
    private ArrayList<Recci> recci;
    private int BP_COUNT = 1;
    private long pickup_location_id;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private long manifest_no;
    private String updateShipmentCount;
    private int assignedCount;
    private ArrayList<Shipment_Detail> mps_shipment_detail;
    private Map<Long, List<Shipment_Detail>> mapWithMps;
    private ProgressDialog dialogOnDataUpdate;
    private ArrayList<Long> manifestListCollection;
    private long lastSyncTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private String mdc_GeoLoc_Validation = "";
    private ArrayList<Long> manifestNoArray;
    private ArrayList<Manifest_List> newManifestList;
    String mobile_number_type = "";

    private HashSet<ReasonCodeList> reasonCodeLists;

    private long lastClickTime = 0;
    private long lastClickTimeSyncWarehouse = 0;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, WarehouseActivity.class);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null)
            {
                if(action.equals(Constants.ACTION_START_WORK))
                {
                    setProgress(VISIBLE);
                }
                else if(action.equals(Constants.ACTION_STOP_WORK))
                {
                    setProgress(GONE);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        warehouseViewModel.setNavigator(this);
        this.activityWarehouseBinding = getViewDataBinding();
        reasonCodeLists = new HashSet<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_START_WORK);
        filter.addAction(Constants.ACTION_STOP_WORK);
        registerReceiver(receiver, filter);
        try {
            pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
            recci = getIntent().getParcelableArrayListExtra("RECCIQ");
            manifestList = getIntent().getParcelableExtra("data");
            wayLatitude = getIntent().getExtras().getDouble("wayLatitude");
            wayLongitude = getIntent().getExtras().getDouble("wayLongitude");
            manifest_no = getIntent().getExtras().getLong("manifest_no");
            assignedCount = getIntent().getExtras().getInt("assigned_count");
            mdc_GeoLoc_Validation = warehouseViewModel.getDataManager().get_pickup_geofencing_mode();
            warehouseViewModel.loadManifest().observe(this, manifest_lists -> {
                try {
                    if (manifest_lists != null) {
                        manifestNoArray = new ArrayList<>();
                        newManifestList = new ArrayList<>();
                        for (int i = 0; i < manifest_lists.size(); i++) {
                            if (manifest_lists.get(i).getManifest_details().getLocation_contact_no() == manifestList.getManifest_details().getLocation_contact_no()) {
                                newManifestList.add(manifest_lists.get(i));
                                manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                            }
                        }
                        for (int i = 0; i < newManifestList.size(); i++) {
                            {
                                if (newManifestList.get(i).isSharedManifest()) {
                                    mobile_number_type = newManifestList.get(i).getMobileNoType();
                                    manifestList.setSharedManifest(true);
                                    manifestList.setMobileNoType(mobile_number_type);
                                    warehouseViewModel.updateSharedManifestStatus(manifestNoArray, newManifestList.get(i).getMobileNoType(), true);
                                    break;
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            warehouseViewModel.setManifestDetails(manifestList);
            if (manifestList.getManifest_details().isVerify_geocode()) {
                activityWarehouseBinding.imgFlagFeLocation.setVisibility(VISIBLE);
            } else {
                activityWarehouseBinding.imgFlagFeLocation.setVisibility(View.GONE);
            }

            isParent = warehouseViewModel.getDataManager().getParent() ? true : warehouseViewModel.getDataManager().getAsParentChild();

            if (isParent) {
                activityWarehouseBinding.child.setVisibility(View.GONE);
                activityWarehouseBinding.parent.setVisibility(View.VISIBLE);
                activityWarehouseBinding.status.setVisibility(View.VISIBLE);
            } else {
                activityWarehouseBinding.btnDrop.setVisibility(View.GONE);
                activityWarehouseBinding.selectAll.setVisibility(View.GONE);
                activityWarehouseBinding.child.setVisibility(View.VISIBLE);
                activityWarehouseBinding.parent.setVisibility(View.GONE);
                activityWarehouseBinding.status.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pickUpList = new ArrayList<>();
        scanned_shipment_detail = new ArrayList<>();
        manifestListCollection = new ArrayList<>();
        activityWarehouseBinding.selectAll.setOnClickListener(this);
        activityWarehouseBinding.btnDrop.setOnClickListener(this);
        activityWarehouseBinding.imageViewBack.setOnClickListener(this);

        warehouseViewModel.getAllShipmentList(manifestList.getManifest_No()).observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
                setReasoncode(shipment);
                warehouseShipmentAdapter.setData(shipment, this);
                warehouseShipmentAdapter.setReasonCodeMaster(warehouseViewModel.reasonCodeMaster);
                manifestNo = warehouseViewModel.manifestNo;
                warehouseViewModel.getAllCategoryAssignedCount(manifestNo);
            }
        });

        warehouseViewModel.getPickupListToView(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT).observe(this, pickupList -> {
            reasonCodeLists.clear();
            reasonCodeLists.addAll(pickupList);
            warehouseShipmentAdapter.setBPReasonCode(new ArrayList(reasonCodeLists));

        });

        manifestListCollection.add(manifestList.getManifest_No());

        warehouseViewModel.getUpdatedTime(manifestListCollection).observe(this, manifest_list -> {
            lastSyncTime = manifest_list.getLast_sync_time();
        });
        warehouseViewModel.getCount(manifestList.getManifest_No()).observe(WarehouseActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                updateShipmentCount = String.valueOf(integer);

            }
        });

        // RTO Lock and Shipment updated.
        if (isNetworkConnected()) {
            rtoLockCheck(manifest_no);
        } else {
            showToast(getString(R.string.check_internet));
        }
        warehouseViewModel.getMpsCondition(manifestList.getManifest_No()).observe(WarehouseActivity.this, shipment_details -> {
            if (shipment_details != null && shipment_details.size() > 0) {
                mps_shipment_detail = new ArrayList<>();
                mapWithMps = new HashMap<>();
                mps_shipment_detail.addAll(shipment_details);

                for (Shipment_Detail shipment_detail1 : mps_shipment_detail) {
                    List<Shipment_Detail> mpsList = mapWithMps.get(shipment_detail1.getMaster_airwaybill_number());
                    if (Objects.isNull(mpsList)) {
                        mpsList = new ArrayList<>();
                    }
                    mpsList.add(shipment_detail1);
                    mapWithMps.put(shipment_detail1.getMaster_airwaybill_number(), mpsList);
                }
                System.out.println("Key = " + mapWithMps);

            }
        });

        warehouseShipmentAdapter.setUpdateStaticListener(this);
        try {
            warehouseViewModel.isDataloading().observe(this, aBoolean -> {
                if (aBoolean)
                    WarehouseActivity.this.hideLoading();
                else {
                    WarehouseActivity.this.hideLoading();
                    WarehouseActivity.this.showToast("Data is not proper");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        activityWarehouseBinding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                warehouseShipmentAdapter.getFilter().filter(newText);
                return false;
            }
        });
        setUp();
        warehouseViewModel.isFeAtLocation.setValue(false);

        warehouseViewModel.isFeAtLocation.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    activityWarehouseBinding.imgFlagFeLocation.setImageResource(R.drawable.ic_fe_location_correct);
                } else {
                    activityWarehouseBinding.imgFlagFeLocation.setImageResource(R.drawable.ic_fe_location_wrong);
                }
            }
        });
        initLocation();
        getLocation();


    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(WarehouseActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        new GpsUtils(WarehouseActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
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
                        if (location.getAccuracy() < warehouseViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            warehouseViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());
                        }
                    }
                }
            }
        };
    }

    private void getLocation() {
        if (isNetworkConnected()) {
            if (ActivityCompat.checkSelfPermission(WarehouseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(WarehouseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WarehouseActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.LOCATION_REQUEST);

            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(WarehouseActivity.this, location -> {
                    if (location != null) {
                        if (location.getAccuracy() < warehouseViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = warehouseViewModel.getLastLatitudeFromPref();
                                    wayLongitude = warehouseViewModel.getLastLongitudeFromPref();
                                }
                            }
                            warehouseViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());
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
                        if (location.getAccuracy() < warehouseViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = warehouseViewModel.getLastLatitudeFromPref();
                                    wayLongitude = warehouseViewModel.getLastLongitudeFromPref();
                                }
                            }
                            warehouseViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());
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
            wayLatitude = warehouseViewModel.getLastLatitudeFromPref();
            wayLongitude = warehouseViewModel.getLastLongitudeFromPref();
        }
    }

    private void setReasoncode(List<Shipment_Detail> shipment_details) {
        if (shipment_details != null) {
            // if (warehouseViewModel.checkIfAtLeastOneScan(shipment_detail)) {
            if (warehouseViewModel.checkIfAtLeastScanSuccessfully(shipment_detail)) {
                warehouseViewModel.getPickupListToView(Constants.SHIPMENT_WISE_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    warehouseViewModel.setallReasonCode(pickUpList);

                });
            } else {
                warehouseViewModel.getPickupListToView(Constants.ALL_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    warehouseViewModel.setallReasonCode(pickUpList);

                });
            }
        }
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
        super.onResume();
        startLocationUpdate = true;
        startLocationThread();
       // deleteNumberFromCallLogs(this,mobile_number_type);
    }


    @Override
    protected void onPause() {
        super.onPause();
        startLocationUpdate = false;
        if (locationThread != null) {
            locationThread.interrupt();
            locationThread = null;
        }
    }

    @Override
    protected String getScreenName() {
        return "Warehouse Screen";
    }

    @Override
    public WarehouseViewModel getViewModel() {
        return warehouseViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_warehouse;
    }

    private void setUp() {
        activityWarehouseBinding.shipmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityWarehouseBinding.shipmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityWarehouseBinding.shipmentRecyclerView.setAdapter(warehouseShipmentAdapter);

        List<String> vehicleNumber = new ArrayList<>();
        if (!warehouseViewModel.getDataManager().is_Ecom_Vehicle()) {
            vehicleType = warehouseViewModel.getDataManager().getVehicleNo();
            vehicleNumber.add(vehicleType);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activityWarehouseBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);

        } else {
            for (int j = 0; j < warehouseViewModel.getDataManager().getRouteDetail().size(); j++) {
                if (!warehouseViewModel.getDataManager().getRouteDetail().get(j).isDeparted()) {
                    vehicleType = warehouseViewModel.getDataManager().getRouteDetail().get(j).getStart_vehicle_number();
                    vehicleNumber.add(vehicleType);
                }
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activityWarehouseBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);

            activityWarehouseBinding.spinnerVehicleType.setOnItemSelectedListener(this);
        }

    }

    @Override
    public void onScanClick() {
        // Log the button click event
        logButtonClick("Open Warehouse Scan");
        // Measure time to open a new screen
        long clickTime = System.currentTimeMillis();
        Intent intent = new Intent(WarehouseActivity.this, WarehouseScanActivity.class);
        intent.putExtra("clickTime", clickTime);
        intent.putExtra("vehicle", vehicleType);
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("manifest_detail", manifestList);
        startActivity(intent);
    }

    @Override
    public void onSynClickEvent() {
        // RTO Lock and Shipment updated.
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTimeSyncWarehouse < 3000) {
            showSnackbar("Please wait for 3 seconds");
            return;
        }
        lastClickTimeSyncWarehouse = currentTime;
        if (isNetworkConnected()) {
            rtoLockCheck(manifest_no);
        } else {
            showToast(getString(R.string.check_internet));
        }
    }

    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }

    @Override
    public void notifyAdapter() {
        warehouseShipmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNextClick() {
        if (checkMultiSpace(this, warehouseViewModel.getDataManager())) {
            showMultiSpaceDialog();
        } else {
            // insert
            String count = ("(" + "Picked : " + updateShipmentCount + " /" + "T.Shipment :" + activityWarehouseBinding.txtShipment.getText() + ")");
            warehouseViewModel.updateShipmentCount(manifestList.getManifest_No(), count);

            combineFunction();
        }

    }

    private void combineFunction() {
        if (!isParent) {
            if (mapWithMps != null && mapWithMps.size() > 0) {

                Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = mapWithMps.entrySet().iterator();
                Map<Long, Set<String>> authenticated = new HashMap<>();
                CommitDialogValidation commitDialogValidation = new CommitDialogValidation();

                List<List<Shipment_Detail>> failedCases = new ArrayList<>();
                while (ob.hasNext()) {
                    Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();
                    List<Shipment_Detail> shipmentloc = mapWithMps.get(pair.getKey());
                    Set<String> uniqueStatus = new HashSet<>();
                    for (Shipment_Detail ship : shipmentloc) {
                        uniqueStatus.add(ship.getStatus());
                    }
                    authenticated.put(pair.getKey(), uniqueStatus);
                    if (uniqueStatus.size() > 1) {
                        failedCases.add(shipmentloc);
                    }
                }
                if (failedCases.size() > 0) {
                    commitDialogValidation.ConfirmationDialog(failedCases);
                    commitDialogValidation.show(getSupportFragmentManager(), "dialog");
                } else {
                    elseFunctionOfChild();
                }
            } else {
                elseFunctionOfChild();
            }
        } else {
            if (!warehouseViewModel.isValidToMoveNext(shipment_detail)) {
                showToast(getString(R.string.validate));
                return;
            }
            if (mapWithMps != null && mapWithMps.size() > 0) {

                Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = mapWithMps.entrySet().iterator();
                Map<Long, Set<String>> authenticated = new HashMap<>();
                CommitDialogValidation commitDialogValidation = new CommitDialogValidation();

                List<List<Shipment_Detail>> failedCases = new ArrayList<>();
                while (ob.hasNext()) {
                    Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();
                    List<Shipment_Detail> shipmentloc = mapWithMps.get(pair.getKey());
                    Set<String> uniqueStatus = new HashSet<>();
                    for (Shipment_Detail ship : shipmentloc) {
                        uniqueStatus.add(ship.getStatus());
                    }
                    authenticated.put(pair.getKey(), uniqueStatus);
                    if (uniqueStatus.size() > 1) {
                        failedCases.add(shipmentloc);
                    }
                }
                if (failedCases.size() > 0) {
                    commitDialogValidation.ConfirmationDialog(failedCases);
                    commitDialogValidation.show(getSupportFragmentManager(), "dialog");
                } else {
                    elseFunction();
                }
            } else {
                elseFunction();
            }

        }

    }

    private void elseFunctionOfChild() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(R.string.commit)
                    .setTitle(getString(R.string.alert_title))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<String> statusList = new ArrayList<>();
                                    statusList.add(Constants.PICKED);
                                    statusList.add(Constants.FAILED);
                                    if (warehouseViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                                        warehouseViewModel.getScannedShipmentList(manifestNo, statusList).observe(WarehouseActivity.this, shipment_details -> {
                                            if (shipment_details != null) {

                                                scanned_shipment_detail.addAll(shipment_details);

                                                warehouseViewModel.createCommitPacketNew(scanned_shipment_detail, recci, pickup_location_id, manifestList.getManifest_type(), wayLatitude, wayLongitude);
                                                warehouseViewModel.failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestNo);
                                                warehouseViewModel.failedCommitPacket(1, Constants.PICKED, manifestNo);

                                                if (isParent) {
                                                    warehouseViewModel.inScanCommitPacket(manifestNo);
                                                } else {
                                                    warehouseViewModel.updateCommit_DataTable(manifestNo);
                                                }
                                            }
                                        });
                                    } else {
                                        showToast(getString(R.string.scan_atleast_one));
                                    }
                                }
                            }
                    ).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    private void elseFunction() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(R.string.alert_no_change_afterwards)
                    .setTitle(R.string.alert_title)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    if (warehouseViewModel.checkIfAtLeastOneScan(shipment_detail)) {

                                        if (!manifestList.getSetting().getIs_partial_allow().equalsIgnoreCase("true")) {
                                            //call check status api update
//                                                warehouseViewModel.checkstatusApiCall(manifestList);
                                            if (warehouseViewModel.checkIfAllScan(shipment_detail)) {

                                                if (warehouseViewModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {
                                                    if (warehouseViewModel.checkIfAtLeastScanSuccessfully(shipment_detail)) {
                                                        goToSignatureActivity();

                                                    } else {
                                                        if (manifestList.isSharedManifest()) {
                                                            goToSignatureActivity();
                                                        } else {
                                                            goToShipmentOtpActivity();
                                                        }
                                                    }
                                                } else {
                                                    goToSignatureActivity();
                                                }

                                            } else {
                                                showToast(getString(R.string.pending_shipment));
                                            }
                                        } else {
                                            goToSignatureActivity();
                                        }
                                    } else {
                                        showToast(getString(R.string.scan_atleast_one));
                                    }
                                }
                            }
                    ).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            }
                    )
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    @Override
    public void nextScreen(String manifest_no, String sFileBody) {
        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;

            ThreadGeneric.executeCall(() -> {
                warehouseViewModel.updateFileUrl(filePath, manifest_no);
            });

            Intent intent = new Intent(WarehouseActivity.this, SuccessFailActivity.class);
            intent.putExtra("screen_validation", "success");
            intent.putExtra("manifest_no", manifestNo);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void showDialogBox(String response, ProgressDialog dialogOnDataUpdate) {
        dialogOnDataUpdate.dismiss();
        showCommitFailInfoDialog(response);

    }


    @Override
    public void onCheckStatusClick() {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 3000) {
                showSnackbar("Please wait for 3 seconds");
                return;
            }
            lastClickTime = currentTime;

            if (isNetworkConnected()) {
                dialogOnDataUpdate = new ProgressDialog(WarehouseActivity.this);
                dialogOnDataUpdate.show();
                dialogOnDataUpdate.setCancelable(false);
                dialogOnDataUpdate.setMessage("Fetching Data...");
                dialogOnDataUpdate.setIndeterminate(true);

                ArrayList<Long> manifest_number = new ArrayList<>();
                manifest_number.add(manifestNo);
                warehouseViewModel.getChildApiResponse(manifest_number, dialogOnDataUpdate, shipment_detail, this);
            } else {
                showToast(getString(R.string.check_internet));
            }
        }
        catch (Exception e)
        {
         e.printStackTrace();
        }


    }


    private void showCommitFailInfoDialog(String description) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(description)
                    .setTitle(getString(R.string.alert_title))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    @Override
    public void update(Shipment_Detail shipmentsDetail) {
        try {
            warehouseViewModel.updateShipment(shipmentsDetail.getAirWayBillNumber(), Constants.PENDING, "", false);

            ArrayList<String> shipment = new ArrayList<>();
            for (Shipment_Detail sc : shipment_detail) {
                shipment.add(sc.getStatus());
            }
            if (shipment.get(0).equals(Constants.PENDING)) {
                warehouseViewModel.assignUpdateIsScanStarted(manifestNo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    @Override
    public void delete(Shipment_Detail shipmentsDetail) {
        try {
            warehouseViewModel.onDeleteScanItem(shipmentsDetail);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAll: {
                warehouseViewModel.checkAll(shipment_detail);
                break;
            }
            case R.id.btnDrop: {
                showPopUpWithLocationCondition();
                break;
            }
            case R.id.imageViewBack: {
                finish();
                break;
            }

        }
    }

    private void showPopUpWithLocationCondition() {
        isAgentAtWarehouse();
        if (!activityWarehouseBinding.txtPicked.getText().toString().equalsIgnoreCase("0") || isAgentAtWarehouse()) {
            showPopup();
        } else if (manifestList.getManifest_details().isVerify_geocode()) {
            showFeGeoLocWarningDialog(getFe_distanceFromSeller(), manifestList, mdc_GeoLoc_Validation);
        } else {
            showPopup();
        }
    }

    private boolean isFeGeoLocWarningDialogVisible = false;

    public void showFeGeoLocWarningDialog(String distanceFromWarehouse, Manifest_List manifest, String warningRestriction) {

        if (!isFeGeoLocWarningDialogVisible) {
            isFeGeoLocWarningDialogVisible = true;
            String message = "";
            if (warningRestriction.equalsIgnoreCase("W") || warningRestriction.trim().length() == 0) {
                message = "You are " + distanceFromWarehouse + " mtrs away from Pickup location.\n" + "Do you want to continue?";
            } else if (warningRestriction.equalsIgnoreCase("R")) {
                message = "Not allowed to update reason code as\nactivity is performed " + distanceFromWarehouse + "mtrs\noutside of geofence.";
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (warningRestriction.equalsIgnoreCase("W") && manifest.getManifest_details().isVerify_geocode()) {
                                showPopup();
                            } else if (warningRestriction.equalsIgnoreCase("R") && manifest.getManifest_details().isVerify_geocode()) {
                            } else if (warningRestriction.trim().length() == 0) {
                                showPopup();
                            }
                            dialog.dismiss();
                            isFeGeoLocWarningDialogVisible = false;
                        }
                    })
                    .setCancelable(false);
            alertDialog.show();
        }

    }

    private boolean isAgentAtWarehouse() {
        Location feLocation = new Location("");
        feLocation.setLatitude(wayLatitude);
        feLocation.setLongitude(wayLongitude);
        Vender_Detail vendorDetails = manifestList.getManifest_details();
        if (isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), warehouseViewModel.getGeoFenceRadius())) {
            return true;
        } else {
            return false;
        }
    }


    private void showPopup() {
        try {
            ListPopupWindow popup_new = new ListPopupWindow(WarehouseActivity.this);
            popup_new.setModal(false);
            popup_new.setAnchorView(activityWarehouseBinding.btnDrop);
            popup_new.setWidth(600);

            List<String> rtscodes = new ArrayList<>();
            for (int i = 0; i < pickUpList.size(); i++) {
                rtscodes.add(pickUpList.get(i).getReason_msg());
            }

            popup_new.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtscodes));
            popup_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        if (!warehouseViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                            callFirstScanApi(manifestList.getManifest_No());
                        }
                        warehouseViewModel.markUndelivered(shipment_detail, pickUpList.get(i).getReason_id(), pickUpList.get(i).getReason_code(), "");
                        popup_new.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            popup_new.show();
            // pickUpList.clear();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }


    public void rtoLockCheck(long manifest_no) {

        ProgressDialog dialog = new ProgressDialog(WarehouseActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        try {
            RtoRequest rtoRequest = new RtoRequest();

            ArrayList<Long> manifest_number = new ArrayList<>();
            manifest_number.add(manifest_no);
            rtoRequest.setManifestIds(manifest_number);
            rtoRequest.setIs_mps(true);
            rtoRequest.setLast_sync_time(lastSyncTime);
            warehouseViewModel.getAllRTOShipmentList(warehouseViewModel.getDataManager().getAuthToken(), rtoRequest).observe(this, response -> {
                try {
                    if (response != null) {
                        if (response.getStatus()) {
                            warehouseViewModel.isRtoLockCheck.set(true);
                            if (response.getStatus()) {
                                if (response.getDescription().equalsIgnoreCase("success")) {
                                    dialog.dismiss();
                                    showToast("You may scan now");
                                }
                                listOfAwbs = new ArrayList<>();
                                rtoShipmentResponses = new ArrayList<>();
                                listOfAwbs.addAll(response.getResponse().getManifest_status_details().get(0).getListOfAwbs());
                                rtoShipmentResponses.addAll(response.getResponse().getManifest_status_details().get(0).getShipment_details());
                                if (listOfAwbs.size() > 0) {
                                    for (ListOfAwbs listOfAwbs : listOfAwbs) {
                                        warehouseViewModel.updateRtoShipment(response.getResponse().getManifest_status_details().get(0).getManifest_id(), listOfAwbs.getAwb(), listOfAwbs.getStatus_code());
                                    }
                                }
                                if (rtoShipmentResponses.size() > 0) {
                                    for (Shipment_Detail listOfShipment : rtoShipmentResponses) {
                                        listOfShipment.setManifestNoInchild(response.getResponse().getManifest_status_details().get(0).getManifest_id());
                                    }
                                    warehouseViewModel.insertShipment(rtoShipmentResponses);
                                    // TODO callupdateBpID API

                                    if (isNetworkConnected()) {
                                        CallBPIdApi(manifest_no);
                                    } else {
                                        showToast(getString(R.string.check_internet));
                                    }


//                                    for (Shipment_Detail listOfShipment : rtoShipmentResponses) {
//                                        warehouseViewModel.updateBPID(response.getResponse().getManifest_status_details().get(0).getManifest_id(), listOfShipment.getAirwaybill_number(), listOfShipment.getBrand_package_id());
//
//                                } else {

                                }
                            } else {
                                dialog.dismiss();
                                if (response.getDescription().equalsIgnoreCase("No shipment found with RTO lock")) {

                                } else {
                                    showToast(response.getDescription());
                                }
                            }

                        } else {
                            dialog.dismiss();
                            if (response.getDescription().equalsIgnoreCase("No shipment found with RTO lock")) {

                            } else {
                                showToast(response.getDescription());
                            }
                        }
                    } else {
                        showToast(response.getDescription());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
            dialog.dismiss();
        }
    }

    private void CallBPIdApi(long manifest_no) {

        ProgressDialog dialog = new ProgressDialog(WarehouseActivity.this);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);

        try {
            UpdatedBPRequest updatedBPRequest = new UpdatedBPRequest();

            ArrayList<Long> manifest_number = new ArrayList<>();
            manifest_number.add(manifest_no);
            updatedBPRequest.setManifest_ids(manifest_number);
            updatedBPRequest.setIs_with_mps(true);
            updatedBPRequest.setLast_sync_time(lastSyncTime);
            warehouseViewModel.getUpdatedBpAwbRto(warehouseViewModel.getDataManager().getAuthToken(), updatedBPRequest).observe(this, response -> {
                try {
                    if (response != null) {


                            if (response.getStatus()) {

                                dialog.dismiss();

                                Intent intent = new Intent(this, MyIntentService.class);

                                // Pass data to the IntentService using extras
                                intent.putExtra("extra_data", response.getResponse().get(0));
                                intent.putExtra("come_from", "warehouse");

                                // Start the IntentService
                                startService(intent);
                            } else {
                                dialog.dismiss();

                                showToast(response.getDescription());

                            }


                    }
                    else {
                        dialog.dismiss();
                        showToast(response.getDescription());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        vehicleType = String.valueOf(adapterView.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void callFirstScanApi(long manifestNumber) {
        try {
            warehouseViewModel.updateInscanStatus(manifestNumber);
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(warehouseViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifestNumber);
            firstScanRequest.setEmp_Code(warehouseViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(manifestList.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(manifestList.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isAgentAtWarehouse() ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, manifestList.getManifest_details().getLocation().getLatitude(), manifestList.getManifest_details().getLocation().getLongitude()));


            if (warehouseViewModel.getDataManager().getChild()) {
                firstScanRequest.setRole("child");
            } else if (warehouseViewModel.getDataManager().getParent()) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifestNumber);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            warehouseViewModel.updateIsScanStarted(manifestNumber);
            warehouseViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                warehouseViewModel.callFirstScanApi(warehouseViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    warehouseViewModel.updateInscanStatus(manifestNumber);
                    warehouseViewModel.updateInscanToFirstScanTable(manifestNumber);
                    warehouseViewModel.inScanCommitPacket(manifestNumber);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToSignatureActivity() {
        Intent intent = new Intent(WarehouseActivity.this, SignatureActivity.class);
        intent.putExtra("is_global", false);
        intent.putExtra("advance_shipment_count", activityWarehouseBinding.txtAdvanceShipment.getText().toString());
        intent.putExtra("shipment_count", activityWarehouseBinding.txtShipment.getText().toString());
        intent.putExtra("picked_count", activityWarehouseBinding.txtPicked.getText().toString());
        intent.putExtra("remaining_count", activityWarehouseBinding.txtRemaining.getText().toString());
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("unpicked_count", activityWarehouseBinding.txtUnpicked.getText().toString());
        intent.putExtra("camera_visible", "true");
        intent.putExtra("manifest_count", "1");
        intent.putExtra("RECCIQ", recci);
        intent.putExtra("pickup_location_id", pickup_location_id);
        intent.putExtra("manifest_type_signature", manifestList.getManifest_type());
        intent.putExtra("imageArrayList", "");
        //TODO by SUmit
        intent.putExtra("manifestNoArray", manifestNoArray);
        intent.putExtra("mobile_number_type", mobile_number_type);
        intent.putExtra("registrd_mobile", manifestList.getManifest_details().getLocation_contact_no());
        intent.putExtra("pop_enable", manifestList.getManifest_details().getLocation().isProof_of_pickup_enable());
        intent.putExtra("seller_name", manifestList.getManifest_details().getLocation_name());
        intent.putExtra("signature_pad_visible", manifestList.getManifest_details().isSignature_pad_visible());

        startActivity(intent);

    }

    private void goToShipmentOtpActivity() {
        Intent intent = new Intent(WarehouseActivity.this, ShipmentOtpActivity.class);
        intent.putExtra("manifest_detail", manifestList);
        intent.putExtra("is_global", false);
        intent.putExtra("manifest_count", "1");
        intent.putExtra("advance_shipment_count", activityWarehouseBinding.txtAdvanceShipment.getText().toString());
        intent.putExtra("shipment_count", activityWarehouseBinding.txtShipment.getText().toString());
        intent.putExtra("picked_count", activityWarehouseBinding.txtPicked.getText().toString());
        intent.putExtra("camera_visible", "true");
        intent.putExtra("unpicked_count", activityWarehouseBinding.txtUnpicked.getText().toString());
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("RECCIQ", recci);
        intent.putExtra("pickup_location_id", pickup_location_id);
        intent.putExtra("remaining_count", activityWarehouseBinding.txtRemaining.getText().toString());
        intent.putExtra("manifest_type_signature", manifestList.getManifest_type());
        intent.putExtra("imageArrayList", "");
        startActivity(intent);
    }


}
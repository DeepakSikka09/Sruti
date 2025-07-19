package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

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
import in.ecomexpress.sruti.databinding.ActivitySellerBinding;
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

import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ChildCallback;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ISellerAdapterrInterface;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ISellerNavigator;
import in.ecomexpress.sruti.ui.dashboard.shipment.ShipmentOtpActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.MyIntentService;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class SellerActivity extends BaseActivity<ActivitySellerBinding, SellerViewModel> implements ISellerNavigator, ISellerAdapterrInterface, View.OnClickListener, AdapterView.OnItemSelectedListener, ChildCallback {
    public static boolean isParents;
    @Inject
    SellerViewModel sellerViewModel;
    @Inject
    Context context;
    @Inject
    SellerShipmentAdapter sellerShipmentAdapter;
    long manifestNo = 0l;
    String vehicleType;
    private List<ReasonCodeList> pickUpList;
    private Manifest_List manifestList;
    private ArrayList<Shipment_Detail> shipment_detail;
    private ArrayList<Shipment_Detail> scanned_shipment_detail;
    private ArrayList<Shipment_Detail> mps_shipment_detail;
    private ArrayList<Manifest_List> newManifestList;

    private Map<Long, List<Shipment_Detail>> mapWithMps;
    private ArrayList<ListOfAwbs> listOfAwbs;
    private ArrayList<Shipment_Detail> rtoShipmentResponses;
    private ActivitySellerBinding activitySellerBinding;
    private ArrayList<Recci> recci;
    private long pickup_location_id;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private long manifest_no;
    private String updateShipmentCount;
    private int assignedCount;
    private ProgressDialog dialogOnDataUpdate;
    private ArrayList<Long> manifestListCollection;
    private long lastSyncTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private String mdc_GeoLoc_Validation = "";
    private ArrayList<String> manifestNoArray;
    private ArrayList<Long> manifestNoArrayLong;
    String mobile_number_type = "";
    private HashSet<ReasonCodeList> reasonCodeLists;

    private long lastClickTime = 0;
    private long lastClickTimeforSyncSeller=0;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SellerActivity.class);
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
        sellerViewModel.setNavigator(this);
        this.activitySellerBinding = getViewDataBinding();
        reasonCodeLists = new HashSet<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_START_WORK);
        filter.addAction(Constants.ACTION_STOP_WORK);
        registerReceiver(receiver, filter);

        try {
            pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
            recci = getIntent().getParcelableArrayListExtra("RECCIQ");
            wayLatitude = getIntent().getExtras().getDouble("wayLatitude");
            wayLongitude = getIntent().getExtras().getDouble("wayLongitude");
            manifest_no = getIntent().getExtras().getLong("manifest_no");
            manifestList = getIntent().getParcelableExtra("data");
            assignedCount = getIntent().getExtras().getInt("assigned_count");
            mdc_GeoLoc_Validation = sellerViewModel.getDataManager().get_pickup_geofencing_mode();


            sellerViewModel.loadManifest().observe(this, manifest_lists -> {
                try {
                    if (manifest_lists != null) {
                        manifestNoArray = new ArrayList<>();
                        manifestNoArrayLong = new ArrayList<>();
                        newManifestList = new ArrayList<>();
                        for (int i = 0; i < manifest_lists.size(); i++) {
                            if (manifest_lists.get(i).getManifest_details().getLocation_contact_no() == manifestList.getManifest_details().getLocation_contact_no()) {
                                newManifestList.add(manifest_lists.get(i));
                                manifestNoArray.add(String.valueOf(manifest_lists.get(i).getManifest_No()));
                                manifestNoArrayLong.add(manifest_lists.get(i).getManifest_No());
                            }
                        }
                        for (int i = 0; i < newManifestList.size(); i++) {
                            {
                                if (newManifestList.get(i).isSharedManifest()) {
                                    mobile_number_type = newManifestList.get(i).getMobileNoType();
                                    manifestList.setSharedManifest(true);
                                    manifestList.setMobileNoType(mobile_number_type);
                                    sellerViewModel.updateSharedManifestStatus(manifestNoArrayLong, newManifestList.get(i).getMobileNoType(), true);
                                    break;
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            sellerViewModel.setManifestDetails(manifestList);

            if (manifestList.getManifest_details().isVerify_geocode()) {
                activitySellerBinding.imgFlagFeLocation.setVisibility(VISIBLE);
            } else {
                activitySellerBinding.imgFlagFeLocation.setVisibility(View.GONE);
            }

            isParents = sellerViewModel.getDataManager().getParent() || sellerViewModel.getDataManager().getAsParentChild();
            if (isParents) {
                activitySellerBinding.child.setVisibility(View.GONE);
                activitySellerBinding.parent.setVisibility(VISIBLE);
                activitySellerBinding.btnDrop.setVisibility(VISIBLE);
                activitySellerBinding.status.setVisibility(VISIBLE);
            } else {
                activitySellerBinding.status.setVisibility(View.GONE);
                activitySellerBinding.child.setVisibility(VISIBLE);
                activitySellerBinding.parent.setVisibility(View.GONE);
                activitySellerBinding.btnDrop.setVisibility(View.GONE);
                activitySellerBinding.selectAll.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pickUpList = new ArrayList<>();
        manifestListCollection = new ArrayList<>();
        scanned_shipment_detail = new ArrayList<>();
        activitySellerBinding.selectAll.setOnClickListener(this);
        activitySellerBinding.btnDrop.setOnClickListener(this);
        activitySellerBinding.imageViewBack.setOnClickListener(this);
        setUp();

        sellerViewModel.getPickupListToView(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT).observe(this, pickupList -> {
            reasonCodeLists.clear();
            reasonCodeLists.addAll(pickupList);
            sellerShipmentAdapter.setBPReasonCode(new ArrayList(reasonCodeLists));

        });

        sellerViewModel.getAllShipmentList(manifestList.getManifest_No()).observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
                setReasoncode(shipment);
                sellerShipmentAdapter.setData(shipment);
                sellerShipmentAdapter.setReasonCodeMaster(sellerViewModel.reasonCodeMaster);
                manifestNo = sellerViewModel.manifestNo;
                sellerViewModel.getAllCategoryAssignedCount(manifestNo);
            }
        });

        manifestListCollection.add(manifestList.getManifest_No());
        sellerViewModel.getUpdatedTime(manifestListCollection).observe(this, manifest_list -> lastSyncTime = manifest_list.getLast_sync_time());
        sellerViewModel.getCount(manifestList.getManifest_No()).observe(SellerActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                updateShipmentCount = String.valueOf(integer);
            }
        });

        sellerViewModel.getMpsCondition(manifestList.getManifest_No()).observe(SellerActivity.this, shipment_details -> {
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

        sellerShipmentAdapter.setUpdateStaticListener(this);

        activitySellerBinding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sellerShipmentAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // RTO Lock and Shipment updated.
        if (isNetworkConnected()) {
            rtoLockCheck(manifest_no);
        } else {
            showToast(getString(R.string.check_internet));
        }
        sellerViewModel.isFeAtLocation.setValue(false);
        sellerViewModel.isFeAtLocation.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    activitySellerBinding.imgFlagFeLocation.setImageResource(R.drawable.ic_fe_location_correct);
                } else {
                    activitySellerBinding.imgFlagFeLocation.setImageResource(R.drawable.ic_fe_location_wrong);
                }
            }
        });
        initLocation();
        getLocation();


    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(SellerActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        new GpsUtils(SellerActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
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
                        if (location.getAccuracy() < sellerViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            sellerViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());
                        }
                    }
                }
            }
        };
    }

    private void getLocation() {
        if (isNetworkConnected()) {
            if (ActivityCompat.checkSelfPermission(SellerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(SellerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SellerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.LOCATION_REQUEST);

            } else {

                mFusedLocationClient.getLastLocation().addOnSuccessListener(SellerActivity.this, location -> {
                    if (location != null) {
                        if (location.getAccuracy() < sellerViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = sellerViewModel.getLastLatitudeFromPref();
                                    wayLongitude = sellerViewModel.getLastLongitudeFromPref();
                                }
                            }
                        }
                        sellerViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());

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
                        if (location.getAccuracy() < sellerViewModel.getGeoFenceRadius()) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                                wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                                if (wayLatitude == 0.0 && wayLongitude == 0.0) {
                                    wayLatitude = sellerViewModel.getLastLatitudeFromPref();
                                    wayLongitude = sellerViewModel.getLastLongitudeFromPref();
                                }
                            }
                        }
                        sellerViewModel.isFeAtLocation.postValue(isAgentAtWarehouse());
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
            wayLatitude = sellerViewModel.getLastLatitudeFromPref();
            wayLongitude = sellerViewModel.getLastLongitudeFromPref();
        }
    }


    private void setReasoncode(List<Shipment_Detail> shipment_details) {
        if (shipment_details != null) {
            if (sellerViewModel.checkIfAtLeastScanSuccessfully(shipment_detail)) {
                sellerViewModel.getPickupListToView(Constants.SHIPMENT_WISE_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    sellerViewModel.setallReasonCode(pickUpList);

                });
            } else {
                sellerViewModel.getPickupListToView(Constants.ALL_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    sellerViewModel.setallReasonCode(pickUpList);

                });
            }
        }
    }

    @Override
    public SellerViewModel getViewModel() {
        return sellerViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_seller;
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
        return "Seller Activity";
    }

    private void setUp() {
        activitySellerBinding.shipmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitySellerBinding.shipmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activitySellerBinding.shipmentRecyclerView.setAdapter(sellerShipmentAdapter);
        activitySellerBinding.txtRemaining.getText().toString();

        List<String> vehicleNumber = new ArrayList<>();
        if (!sellerViewModel.getDataManager().is_Ecom_Vehicle()) {
            vehicleType = sellerViewModel.getDataManager().getVehicleNo();
            vehicleNumber.add(vehicleType);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activitySellerBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);


        } else {
            for (int j = 0; j < sellerViewModel.getDataManager().getRouteDetail().size(); j++) {
                if (!sellerViewModel.getDataManager().getRouteDetail().get(j).isDeparted()) {
                    vehicleType = sellerViewModel.getDataManager().getRouteDetail().get(j).getStart_vehicle_number();
                    vehicleNumber.add(vehicleType);
                }
            }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, vehicleNumber);

            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            activitySellerBinding.spinnerVehicleType.setAdapter(spinnerArrayAdapter);

            activitySellerBinding.spinnerVehicleType.setOnItemSelectedListener(this);

        }
    }

    @Override
    public void onScanClick() {
        // Log the button click event
        logButtonClick("Open Seller Scan");

        // Measure time to open a new screen
        long clickTime = System.currentTimeMillis();
        Intent intent = new Intent(SellerActivity.this, SellerScanActivity.class);
        intent.putExtra("clickTime", clickTime);
        intent.putExtra("manifest", manifestNo);
        intent.putExtra("manifest_detail", manifestList);
        startActivity(intent);
    }

    @Override
    public void onSynClickEvent() {
        // RTO Lock and Shipment updated.

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTimeforSyncSeller < 3000) {
            showSnackbar("Please wait for 3 seconds");
            return;
        }
        lastClickTimeforSyncSeller = currentTime;
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
    public void onCheckStatusClick() {


            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 3000) {
                showSnackbar("Please wait for 3 seconds");
                return;
            }
            lastClickTime = currentTime;
            if (isNetworkConnected()) {
                dialogOnDataUpdate = new ProgressDialog(SellerActivity.this);
                dialogOnDataUpdate.show();
                dialogOnDataUpdate.setCancelable(false);
                dialogOnDataUpdate.setMessage("Fetching Data...");
                dialogOnDataUpdate.setIndeterminate(true);

                ArrayList<Long> manifest_number = new ArrayList<>();
                manifest_number.add(manifestNo);
                sellerViewModel.getChildApiResponse(manifest_number, dialogOnDataUpdate, shipment_detail, this);
            } else {
                showToast(getString(R.string.check_internet));
            }




    }

    @Override
    public void notifyAdapter() {
        if (sellerShipmentAdapter != null) {
            sellerShipmentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showDialogBox(String response, ProgressDialog dialogOnDataUpdate) {
        dialogOnDataUpdate.dismiss();
        showCommitFailInfoDialog(response);
    }

    @Override
    public void onNextClick() {
        // insert

//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastClickTime < 3000) {
//                showToast("Please wait for 3 seconds");
//                return;
//            }
//            lastClickTime = currentTime;
            if (checkMultiSpace(this, sellerViewModel.getDataManager())) {
                showMultiSpaceDialog();
            } else {
                String count = ("(" + "Picked : " + updateShipmentCount + " /" + "T.Shipment :" + activitySellerBinding.txtShipment.getText() + ")");
                sellerViewModel.updateShipmentCount(manifestList.getManifest_No(), count);
                combineFunction();
            }



    }

    private void combineFunction() {
        if (!isParents) {
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
            if (!sellerViewModel.isValidToMoveNext(shipment_detail)) {
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

    private void elseFunction() {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(this).setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_no_change_afterwards)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (sellerViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                                if (!manifestList.getSetting().getIs_partial_allow().equalsIgnoreCase("true")) {
                                    //call check status api update
//                                                warehouseViewModel.checkstatusApiCall(manifestList);
                                    if (sellerViewModel.checkIfAllScan(shipment_detail)) {
                                        if (sellerViewModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {
                                            if (sellerViewModel.checkIfAtLeastScanSuccessfully(shipment_detail)) {
                                                goToSignaturePage();
                                            } else {
                                                if (manifestList.isSharedManifest()) {
                                                    goToSignaturePage();
                                                } else {
                                                    goToOtpPage();
                                                }
                                            }
                                        } else {
                                            goToSignaturePage();
                                        }
                                    } else {
                                        showToast(getString(R.string.pending_shipment));
                                    }
                                } else {
                                    goToSignaturePage();

                                }
                            } else {
                                showToast(getString(R.string.scan_atleast_one));
                            }

                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), null);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
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
                                    if (sellerViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                                        sellerViewModel.getScannedShipmentList(manifestNo, statusList).observe(SellerActivity.this, shipment_details -> {
                                            if (shipment_details != null) {

                                                scanned_shipment_detail.addAll(shipment_details);
                                                sellerViewModel.createCommitPacketNew(scanned_shipment_detail, recci, pickup_location_id, manifestList.getManifest_type(), wayLatitude, wayLongitude);
                                                sellerViewModel.failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestNo);
                                                sellerViewModel.failedCommitPacket(1, Constants.PICKED, manifestNo);

                                                if (isParents) {
                                                    sellerViewModel.inScanCommitPacket(manifestNo);
                                                } else {
                                                    sellerViewModel.updateCommit_DataTable(manifestNo);
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

    @Override // File Creation
    public void nextScreen(String manifest_no, String sFileBody) {

        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;

            ThreadGeneric.executeCall(() -> {
                sellerViewModel.updateFileUrl(filePath, manifest_no);
            });

            Intent intent = new Intent(SellerActivity.this, SuccessFailActivity.class);
            intent.putExtra("screen_validation", "success");
            intent.putExtra("manifest_no", manifestNo);
            startActivity(intent);

        } catch (Exception e) {
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

    // RTO Lock Check and new Shipment add
    public void rtoLockCheck(long manifest_no) {
        ProgressDialog dialog = new ProgressDialog(SellerActivity.this);
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
            sellerViewModel.getAllRTOShipmentList(sellerViewModel.getDataManager().getAuthToken(), rtoRequest).observe(this, response -> {
                try {
                    if (response != null) {
                        if (response.getStatus()) {
                            sellerViewModel.isRtoLockCheck.set(true);
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
                                        sellerViewModel.updateRtoShipment(response.getResponse().getManifest_status_details().get(0).getManifest_id(), listOfAwbs.getAwb(), listOfAwbs.getStatus_code());
                                    }

                                }

                                if (rtoShipmentResponses.size() > 0) {
                                    for (Shipment_Detail listOfShipment : rtoShipmentResponses) {
                                        listOfShipment.setManifestNoInchild(response.getResponse().getManifest_status_details().get(0).getManifest_id());
                                    }
                                    sellerViewModel.insertShipment(rtoShipmentResponses);
//                                  TODO update bp_id code
                                    if (isNetworkConnected()) {
                                      CallBPIdApi(manifest_no);
                                    } else {
                                        showToast(getString(R.string.check_internet));
                                    }

                                }
                            } else {
                                dialog.dismiss();
                                if (!response.getDescription().equalsIgnoreCase("No shipment found with RTO lock")) {
                                    showToast(response.getDescription());
                                }
                            }
                        } else {
                            dialog.dismiss();
                            if(!response.getDescription().equalsIgnoreCase("No shipment found with RTO lock")) {
                                showToast(response.getDescription());
                            }
                        }
                    }
                    else {
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

        ProgressDialog dialog = new ProgressDialog(SellerActivity.this);
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
            sellerViewModel.getUpdatedBpAwbRto(sellerViewModel.getDataManager().getAuthToken(), updatedBPRequest).observe(this, response -> {
                try {
                    if (response != null) {

                            if (response.getStatus()) {
                                // Here we are going to update the data in  Local DataBase
                                dialog.dismiss();


                                Intent intent = new Intent(context, MyIntentService.class);

                                // Pass data to the IntentService using extras
                                intent.putExtra("extra_data", response.getResponse().get(0));
                                intent.putExtra("come_from", "seller");

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
            e.printStackTrace();
        }


    }


    @Override
    public void update(Shipment_Detail shipmentsDetail) {
        try {
            sellerViewModel.updateShipment(shipmentsDetail.getAirWayBillNumber(), Constants.PENDING, "", Calendar.getInstance().getTimeInMillis(), false);

            ArrayList<String> shipment = new ArrayList<>();
            for (Shipment_Detail sc : shipment_detail) {
                shipment.add(sc.getStatus());
            }
            if (shipment.get(0).equals(Constants.PENDING)) {
                sellerViewModel.assignUpdateIsScanStarted(manifest_no);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    @Override
    public void delete(Shipment_Detail shipmentsDetail) {
        try {
            sellerViewModel.onDeleteScanItem(shipmentsDetail);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectAll: {
                if (shipment_detail != null) {
                    sellerViewModel.checkAll(shipment_detail);
                }
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
        if (!activitySellerBinding.txtPicked.getText().toString().equalsIgnoreCase("0") || isAgentAtWarehouse()) {
            //   if (!sellerViewModel.pickedShipment.toString().equalsIgnoreCase("0") || isAgentAtWarehouse()) {
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
        if (isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), sellerViewModel.getGeoFenceRadius())) {
            return true;
        } else {
            return false;
        }
    }


    // Reason Code Pop-UP
    private void showPopup() {
        try {
            ListPopupWindow popup_new = new ListPopupWindow(SellerActivity.this);
            popup_new.setModal(false);
            popup_new.setAnchorView(activitySellerBinding.btnDrop);
            popup_new.setWidth(600);
            List<String> rtscodes = new ArrayList<>();

            for (int i = 0; i < pickUpList.size(); i++) {
                rtscodes.add(pickUpList.get(i).getReason_msg());
                System.out.println("reason_code" + pickUpList.get(i).getReason_code());
                System.out.println("reason_in" + pickUpList.get(i).getReason_id());
            }

            popup_new.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtscodes));
            popup_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {

                        if (!sellerViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                            callFirstScanApi(manifestList.getManifest_No());
                        }
                        sellerViewModel.markUndelivered(shipment_detail, pickUpList.get(i).getReason_id(), pickUpList.get(i).getReason_code(), "");
                        popup_new.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            popup_new.show();

        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
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
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(sellerViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifestNumber);
            firstScanRequest.setEmp_Code(sellerViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(manifestList.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(manifestList.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isAgentAtWarehouse() ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, manifestList.getManifest_details().getLocation().getLatitude(), manifestList.getManifest_details().getLocation().getLongitude()));

            if (sellerViewModel.getDataManager().getChild()) {
                firstScanRequest.setRole("child");
            } else if (sellerViewModel.getDataManager().getParent()) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifest_no);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            sellerViewModel.updateIsScanStarted(manifestNumber);
            sellerViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                sellerViewModel.callFirstScanApi(sellerViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    sellerViewModel.updateInscanStatus(manifestNumber);
                    sellerViewModel.updateInscanToFirstScanTable(manifestNumber);
                    sellerViewModel.inScanCommitPacket(manifestNumber);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSignaturePage() {
        Intent intent = new Intent(SellerActivity.this, SignatureActivity.class);
        intent.putExtra("is_global", false);
        intent.putExtra("advance_shipment_count", activitySellerBinding.txtAdvanceShipment.getText().toString());
        intent.putExtra("shipment_count", activitySellerBinding.txtShipment.getText().toString());
        intent.putExtra("picked_count", activitySellerBinding.txtPicked.getText().toString());
        intent.putExtra("remaining_count", activitySellerBinding.txtRemaining.getText().toString());
        intent.putExtra("unpicked_count", activitySellerBinding.txtUnpicked.getText().toString());
        intent.putExtra("camera_visible", "true");
        intent.putExtra("RECCIQ", recci);
        intent.putExtra("pickup_location_id", pickup_location_id);
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("imageArrayList", "");
        intent.putExtra("manifest_count", "1");
        intent.putExtra("manifest_type_signature", manifestList.getManifest_type());

        //TODO by SUmit
        intent.putExtra("manifestNoArray", manifestNoArrayLong);
        intent.putExtra("mobile_number_type", mobile_number_type);
        intent.putExtra("registrd_mobile", manifestList.getManifest_details().getLocation_contact_no());
        intent.putExtra("pop_enable", manifestList.getManifest_details().getLocation().isProof_of_pickup_enable());
        intent.putExtra("seller_name", manifestList.getManifest_details().getLocation_name());
        intent.putExtra("signature_pad_visible", manifestList.getManifest_details().isSignature_pad_visible());

        startActivity(intent);
    }

    public void goToOtpPage() {
        Intent intent = new Intent(SellerActivity.this, ShipmentOtpActivity.class);
        intent.putExtra("manifest_detail", manifestList);
        intent.putExtra("is_global", false);
        intent.putExtra("advance_shipment_count", activitySellerBinding.txtAdvanceShipment.getText().toString());
        intent.putExtra("shipment_count", activitySellerBinding.txtShipment.getText().toString());
        intent.putExtra("picked_count", activitySellerBinding.txtPicked.getText().toString());
        intent.putExtra("remaining_count", activitySellerBinding.txtRemaining.getText().toString());
        intent.putExtra("unpicked_count", activitySellerBinding.txtUnpicked.getText().toString());
        intent.putExtra("camera_visible", "true");
        intent.putExtra("RECCIQ", recci);
        intent.putExtra("pickup_location_id", pickup_location_id);
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("imageArrayList", "");
        intent.putExtra("manifest_count", "1");
        intent.putExtra("manifest_type_signature", manifestList.getManifest_type());
        startActivity(intent);
    }
}
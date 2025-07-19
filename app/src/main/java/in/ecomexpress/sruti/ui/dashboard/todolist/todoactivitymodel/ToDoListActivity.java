package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;


import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityTodoListViewNewBinding;
import in.ecomexpress.sruti.databinding.WarningDialogBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Address;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ManifestLevelReasonCodeActivity;
import in.ecomexpress.sruti.ui.dashboard.globalscan.GlobalScanScreenActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.switchnumber.SwitchNumberDialog;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.DRSremarksInterface;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IToDoListNavigator;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.TodoFragmentToActivityLIstener;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.common_files.progress_bar.ProgressItem;

import static in.ecomexpress.sruti.utils.common_files.Helper.updateLocationWithData;
import static java.lang.Thread.sleep;

public class ToDoListActivity extends BaseActivity<ActivityTodoListViewNewBinding, ToDoListViewModel> implements IToDoListNavigator, HasSupportFragmentInjector, TodoFragmentToActivityLIstener, ToDoListFragment.DRSListRemarkListener, DRSremarksInterface {
    public double wayLatitude = 0.0, wayLongitude = 0.0;
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    ArrayList<Long> manifest_number;
    //For Reason Code
    ListView lv_languages;
    Button cancelbtn;
    BottomSheetDialog bottomSheetDialog;
    ArrayAdapter list_adapter;

    String contact_number1="";
    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;
    private boolean isfilterApplied = false;
    private  int  selectedManifestPosition=0;
    private ExpListViewAdapterWithCheckbox expListViewAdapterWithCheckbox;
    private ArrayList<String> listDataHeader;
    private LinkedHashMap<String, List<String>> listDataChild;
    private SwitchNumberDialog switchNumberDialog;
    private List<Manifest_List> getManifestList = new ArrayList<>();
    private List<String> manifestTypeItems;
    private List<String> manifestStatusItems;
    private boolean isGlobalAllowed = false;
    private ToDoListViewModel toDoListViewModel;
    public ActivityTodoListViewNewBinding activityTodoListMainLayout;
    private boolean ismapview;
    private ToDoListFragment toDoListFragment;
    private DrawerLayout mDrawer;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    RelativeLayout rl_status;
    private ArrayList<ReasonCodeList> pickUpList;
    private ArrayList<Shipment_Detail> shipment_detail;
    private Manifest_List manifest_list;
    String itemValue;
    int post;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityTodoListMainLayout = getViewDataBinding();

        replaceFragment(ToDoListFragment.getInstance());
        activityTodoListMainLayout.setActivitybinding(this);
        activityTodoListMainLayout.count.setVisibility(View.GONE);
        toDoListViewModel.setNavigator(this);
        ismapview = false;
        searchManifest();
        setUpNavigation();

        manifest_number = new ArrayList<>();
        pickUpList = new ArrayList<>();
        try {
            manifestTypeItems = Arrays.asList(getResources().getStringArray(R.array.manifestTypeItems));
            manifestStatusItems = Arrays.asList(getResources().getStringArray(R.array.manifestStatusItems));

        } catch (Exception e) {
            e.printStackTrace();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ToDoListActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(ToDoListActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
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
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    }
                }
            }
        };

        getLocation();
        toDoListViewModel.getAllShipmentTODO().observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
                setReasoncode(shipment);
            }
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

    private void setReasoncode(List<Shipment_Detail> shipment_details) {
        if (shipment_details != null) {
            if (toDoListViewModel.checkIfAtLeastScanSuccessfully(shipment_details)) {
                toDoListViewModel.getPickupListToView(Constants.SHIPMENT_WISE_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    toDoListViewModel.setallReasonCode(pickUpList);
                    openManiestReason(selectedManifestPosition, pickUpList);
                });
            } else {
                toDoListViewModel.getPickupListToView(Constants.ALL_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    toDoListViewModel.setallReasonCode(pickUpList);
                    openManiestReason(selectedManifestPosition, pickUpList);

                });
            }
        }
    }

    private void openManiestReason(int selectedManifestPosition, ArrayList<ReasonCodeList> pickUpList) {

        ArrayList<String> rtscodes = new ArrayList<>();
        for (int i = 0; i < pickUpList.size(); i++) {
            rtscodes.add(pickUpList.get(i).getReason_msg());
        }


        Address address1 = getManifestList.get(selectedManifestPosition).getManifest_details().getAddress();
        String add1 = address1.getLine1() + ", " + address1.getLine2() + ", " + address1.getLine3() + ", " + address1.getState() + ", " + address1.getCity() + ", " + address1.getPincode();
        String fullAddress = add1.replaceAll(", null", "");
        String fullAdd = fullAddress.replaceAll("null,", "");
        String var = TextUtils.isEmpty(getManifestList.get(selectedManifestPosition).getManifest_details().getConcernedPersonName()) ? "" : getManifestList.get(selectedManifestPosition).getManifest_details().getConcernedPersonName() + ":";
        String consta = TextUtils.isEmpty(String.valueOf(getManifestList.get(selectedManifestPosition).getManifest_details().getLocation_contact_no())) ? "" : getManifestList.get(selectedManifestPosition).getManifest_details().getLocation_contact_no() + "";
        String contactdetail = fullAdd.replaceAll("null,", "") + "(" + var + consta + ")";
        contact_number1=consta;


        Intent intent = new Intent(this, ManifestLevelReasonCodeActivity.class);
        intent.putParcelableArrayListExtra("reason", pickUpList);
        intent.putExtra("reason_code", pickUpList.get(post).getReason_code());
        intent.putExtra("reason_id", "" + pickUpList.get(post).getReason_id());
        intent.putExtra("manifest_detail", getManifestList.get(selectedManifestPosition));
        intent.putExtra("full_address", contactdetail);
        startActivity(intent);
    }


    private void getLocation() {
        if (isNetworkConnected()) {
            if (ActivityCompat.checkSelfPermission(ToDoListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ToDoListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ToDoListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.LOCATION_REQUEST);

            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(ToDoListActivity.this, location -> {
                    if (location != null) {
//                        in.ecomexpress.geolocations.Constants.latitude=0.0;
//                        in.ecomexpress.geolocations.Constants.longitude=0.0;
//
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                            wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                            wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = toDoListViewModel.getLastLatitudeFromPref();
                                wayLongitude = toDoListViewModel.getLastLongitudeFromPref();
                            }
                        }
                        try {
                            toDoListFragment.setLatLng(wayLatitude, wayLongitude);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });

            }
        } else {
            LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                            wayLatitude = in.ecomexpress.geolocations.Constants.latitude;
                            wayLongitude = in.ecomexpress.geolocations.Constants.longitude;
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = toDoListViewModel.getLastLatitudeFromPref();
                                wayLongitude = toDoListViewModel.getLastLongitudeFromPref();
                            }
                        }
                        try {
                            toDoListFragment.setLatLng(wayLatitude, wayLongitude);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        if (wayLatitude == 0.0 || wayLongitude == 0.0) {
            wayLatitude = toDoListViewModel.getLastLatitudeFromPref();
            wayLongitude = toDoListViewModel.getLastLongitudeFromPref();
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
        if(toDoListViewModel.vendorContactNumber.get() == null){
            toDoListViewModel.vendorContactNumber.set("");
        }

        if(!Objects.requireNonNull(toDoListViewModel.vendorContactNumber.get()).equalsIgnoreCase("")){
            CommonUtils.deleteNumberFromCallLogs(toDoListViewModel.vendorContactNumber.get(), this);
        }


        startLocationUpdate = true;
        startLocationThread();
        try {
            updateCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return "ToDOList Screen";
    }

    @Override
    public void onIndicatorClick() {
        if (activityTodoListMainLayout.count.getVisibility() == View.VISIBLE) {
            toDoListViewModel.setImage(true);
            activityTodoListMainLayout.count.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.count.setVisibility(View.VISIBLE);
            toDoListViewModel.setImage(false);
        }

    }

    @Override
    public void onGettingManifest(List<Manifest_List> manifest_lists) {
        try {
            this.getManifestList = manifest_lists;
            if (manifest_lists.size() > 0) {

                updateCount();
                enableExpandableList();
                for (Manifest_List manifest_list : getManifestList) {
                    manifest_number.add(manifest_list.getManifest_No());

                }

                for (Manifest_List manifest_list : getManifestList) {
                    toDoListViewModel.getAllShipmentCount(manifest_list);
                    if ((manifest_list.getSetting().getAdvance_pickup_check() == Constants.ADVANCE_SETTING && manifest_list.getCommit_status() == 0)
                            || (manifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_PICKUP_RECCE) && manifest_list.getCommit_status() == 0)
                            || (manifest_list.getManifest_type().equalsIgnoreCase("R") && manifest_list.getCommit_status() == 0)
                            || (manifest_list.getSetting().getQr_code_check() == 1 && manifest_list.getCommit_status() == 0)
                            || (manifest_list.getFlags().isOtp_required() == true && manifest_list.getCommit_status() == 0)
                    ) {
                        isGlobalAllowed = false;
                        break;

                    } else {
                        isGlobalAllowed = true;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateCount() {
        try {
            toDoListFragment.setListRemarkListener(this);
            toDoListViewModel.getAllCategoryAssignedCount();
            toDoListViewModel.getAllCategoryPickedCount();
            toDoListViewModel.getAllCategoryUnPickedCount();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initDataToSeekbar();
                }
            }, 1000);

            hideLayout();
            setTotalCounts();
//            enableExpandableList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClearFilterClick() {

        try {
            // Hashmap for keeping track of our checkbox check states
            HashMap<Integer, boolean[]> mChildCheckStates;
            mChildCheckStates = expListViewAdapterWithCheckbox.getmyChildCheckStates();

            boolean[] manifestTypeOriginal = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_TYPE);
            boolean[] manifestStatusOriginal = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_STATUS);

            boolean[] manifestType = Arrays.copyOf(manifestTypeOriginal, manifestTypeOriginal.length);
            boolean[] manifestStatus = Arrays.copyOf(manifestStatusOriginal, manifestStatusOriginal.length);

            manifestType[0] = true;
            manifestType[1] = true;
            manifestType[2] = true;
            manifestType[3] = true;
            manifestType[4] = true;
            manifestStatus[0] = true;
            manifestStatus[1] = true;
            manifestStatus[2] = true;


            toDoListViewModel.applyFilterOnList(getManifestList, manifestType, manifestStatus);
            mDrawer.closeDrawer(Gravity.RIGHT);
            enableExpandableList();

        } catch (Exception ex) {
            ex.printStackTrace();
            mDrawer.closeDrawer(Gravity.RIGHT);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(ToDoListActivity.this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            if (wayLatitude == 0.0 || wayLongitude == 0.0) {
                                wayLatitude = toDoListViewModel.getLastLatitudeFromPref();
                                wayLongitude = toDoListViewModel.getLastLongitudeFromPref();
                            }
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
                } else {
                    Toast.makeText(ToDoListActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 101: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // callPhoneNumber();
                }
                break;

            }
        }
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }

    @Override
    public void updateProgressBar() {
        initDataToSeekbar();
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
                toDoListViewModel.updateFileUrl(filePath, manifest_no);
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void uploadCommitPacket(CommitPacketData commit, String fileName) {

        if (isNetworkConnected()) {
            toDoListViewModel.uploadCommitPacket(toDoListViewModel.getDataManager().getAuthToken(), commit).observe(this, CommitPacketData -> {
                if (CommitPacketData != null) {
                    ThreadGeneric.executeCall(() -> {
                        System.out.println("commit_process" + commit.getManifest_process().size());
                        if (commit.getManifest_process().size() > 0 && (commit.getManifest_process().get(0).getManifest_type().equals("P") || commit.getManifest_process().get(0).getManifest_type().equals("PR"))) {//commit.getManifest_process().get(0).getPickup_location_id() == null
                            //TODO  NULL check
                            if (CommitPacketData.getResponse().getManifestIds() != null) {
                                toDoListViewModel.getDataManager().updateCommitStatus("1", commit.getManifest_process().get(0).getManifest_no() + "_" + commit.getManifest_process().get(0).getPickup_location_id());

                                toDoListViewModel.getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_SERVER_SYNC), CommitPacketData.getResponse().getManifestIds());
                            }
                            if (!toDoListViewModel.getDataManager().is_Ecom_Vehicle()) {
                                updateLocationWithData(ToDoListActivity.this, String.valueOf(commit.getManifest_process().get(0).getManifest_no()), "PICKED", commit.getManifest_process().get(0).getLocation_latitude(), commit.getManifest_process().get(0).getLocation_longitude());
                            }

                            //   toDoListViewModel.deleteFile(this, fileName);
                            //TODO  NULL check
                            if (CommitPacketData.getResponse().getManifestIds() != null) {
                                toDoListViewModel.deleteCommitedShipment(CommitPacketData.getResponse().getManifestIds());
                            }
                        } else {
                            toDoListViewModel.updateCommitStatusWithRecci("1", "0_" + commit.getManifest_process().get(0).getPickup_location_id());
                            toDoListViewModel.updateManifestListWithRecci(String.valueOf(Constants.COMMIT_SERVER_SYNC), "0_" + commit.getManifest_process().get(0).getPickup_location_id());

                        }

                    });
                } else {
                    showToast(getString(R.string.no_network_error));

                }
            });
        }
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onScanClick() {
        if (toDoListViewModel.iscompleteDepart()) {
            showToast("Global Scan Disabled");
            return;
        }
        // Log the button click event
        logButtonClick("Open Global Scan Screen");
        // Measure time to open a new screen
        long clickTime = System.currentTimeMillis();
        Bundle manifest_collection = new Bundle();
        manifest_collection.putSerializable("manifest_number", manifest_number);
        Intent intent = new Intent(ToDoListActivity.this, GlobalScanScreenActivity.class);
        intent.putExtra("clickTime", clickTime);
        intent.putExtra("manifest_collection", manifest_collection);
        startActivity(intent);

    }

    @Override
    public void onSynClickEvent() {
        Toast.makeText(this, "Deepak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFilterClick() {
        mDrawer.openDrawer(Gravity.RIGHT);

    }

    @Override
    public void onApplyClick() {

        if (toDoListViewModel.totalAssignedCount.get().equalsIgnoreCase("0") && toDoListViewModel.totalPickedCount.get().equalsIgnoreCase("0") && toDoListViewModel.totalUnPickedCount.get().equalsIgnoreCase("0")) {
            showSnackbar("No shipment found.");
            mDrawer.closeDrawer(Gravity.RIGHT);

        } else {

            // Hashmap for keeping track of our checkbox check states
            HashMap<Integer, boolean[]> mChildCheckStates;
            mChildCheckStates = expListViewAdapterWithCheckbox.getmyChildCheckStates();

            boolean[] manifestTypeOriginal = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_TYPE);
            boolean[] manifestStatusOriginal = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_STATUS);

            boolean[] manifestType = Arrays.copyOf(manifestTypeOriginal, manifestTypeOriginal.length);
            boolean[] manifestStatus = Arrays.copyOf(manifestStatusOriginal, manifestStatusOriginal.length);


            //1. if all item not selected than show no filter error msg and return
            if (!validateFilters(mChildCheckStates)) {
                return;
            }

            //2. if no manifest type is selected than set all is true
            if (manifestType[0] == false && manifestType[1] == false && manifestType[2] == false && manifestType[3] == false && manifestType[4] == false) {

                manifestType[0] = true;
                manifestType[1] = true;
                manifestType[2] = true;
                manifestType[3] = true;
                manifestType[4] = true;

            }

            //3. if no manifest status is selected than set all is true
            if (manifestStatus[0] == false && manifestStatus[1] == false && manifestStatus[2] == false) {
                manifestStatus[0] = true;
                manifestStatus[1] = true;
                manifestStatus[2] = true;
            }

            toDoListViewModel.applyFilterOnList(getManifestList, manifestType, manifestStatus);
            mDrawer.closeDrawer(Gravity.RIGHT);
        }
    }

    @Override
    public void setFilteredAdapter(List<Manifest_List> filteredManifestTypeList) {
        toDoListFragment.updateFilteredAdapter(filteredManifestTypeList);
    }

    private boolean validateFilters(HashMap<Integer, boolean[]> mChildCheckStates) {

        boolean atleastOnemanifestTypeChecked = false, atleastOnemanifestStatusChecked = false;
        try {
            //for manifest type
            boolean[] manifestType = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_TYPE);
            for (int i = 0; i < manifestType.length; i++) {
                if (manifestType[i]) {
                    atleastOnemanifestTypeChecked = true;
                    break;
                }
            }

            //for manifest status
            boolean[] manifestStatus = mChildCheckStates.get(ExpListViewAdapterWithCheckbox.MANIFEST_STATUS);
            for (int i = 0; i < manifestStatus.length; i++) {
                if (manifestStatus[i]) {
                    atleastOnemanifestStatusChecked = true;
                    break;
                }
            }


            if (!atleastOnemanifestTypeChecked && !atleastOnemanifestStatusChecked) {
                showSnackbar("Please select atleast one filter.");
                return false;
            } else {
                isfilterApplied = true;
                Log.d("TAG", "onApplyClick: ");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setTotalCounts() {
        toDoListViewModel.recceTotalCount.set(Long.valueOf((toDoListViewModel.recciAssignedcount.get())) + Long.valueOf((toDoListViewModel.recciPickedCount.get())) + Long.valueOf((toDoListViewModel.recciUnPickedCount.get())));
        toDoListViewModel.sellerTotalCount.set(Long.valueOf((toDoListViewModel.vendorAssignedCount.get())) + Long.valueOf((toDoListViewModel.vendorPickedCount.get())) + Long.valueOf((toDoListViewModel.vendorUnPickedCount.get())));
        toDoListViewModel.warehouseTotalCount.set(Long.valueOf((toDoListViewModel.warehouseAssignedcount.get())) + Long.valueOf((toDoListViewModel.warehousePickedCount.get())) + Long.valueOf((toDoListViewModel.warehouseUnPickedCount.get())));
        toDoListViewModel.recceSellerTotalCount.set(Long.valueOf((toDoListViewModel.recci_venAssignedcount.get())) + Long.valueOf((toDoListViewModel.recci_venPickedCount.get())) + Long.valueOf((toDoListViewModel.recci_venUnPickedCount.get())));
        toDoListViewModel.recceWarehouseTotalCount.set(Long.valueOf((toDoListViewModel.recci_wrhAssignedcount.get())) + Long.valueOf((toDoListViewModel.recci_wrhPickedCount.get())) + Long.valueOf((toDoListViewModel.recci_wrhUnPickedCount.get())));
    }

    private void setUpNavigation() {
        mDrawer = activityTodoListMainLayout.drawerView;
//        mNavigationView = activityTodoListMainLayout.navigationView;

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(ToDoListActivity.this);
            }
        };

        mDrawer.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    public void enableExpandableList() {
        try {
            listDataHeader = new ArrayList<String>();
            listDataChild = new LinkedHashMap<String, List<String>>();
            ExpandableListView expListView = findViewById(R.id.left_drawer);

            prepareListData(listDataHeader, listDataChild);

            expListViewAdapterWithCheckbox = new ExpListViewAdapterWithCheckbox(this, listDataHeader, listDataChild, toDoListViewModel, getManifestList);
            expListView.setAdapter(expListViewAdapterWithCheckbox);

            for (int i = 0; i < expListViewAdapterWithCheckbox.getGroupCount(); i++)
                expListView.expandGroup(i);

            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    // Toast.makeText(getApplicationContext(),
                    // "Group Clicked " + listDataHeader.get(groupPosition),
                    // Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
               /* Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();*/
                }
            });


            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    Log.d("TAG", "I got clicked childPosition:[" + childPosition + "] groupPosition:[" + groupPosition + "] id:[" + id + "]");

                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void prepareListData(List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        // Adding child data
        listDataHeader.add(Constants.MANIFEST_TYPE);
        listDataHeader.add(Constants.MANIFEST_STATUS);
        listDataChild.put(listDataHeader.get(0), manifestTypeItems); // Header, Child data
        listDataChild.put(listDataHeader.get(1), manifestStatusItems);
    }

    public void hideLayout() {
        //SELLER
        if (Integer.parseInt(toDoListViewModel.vendorPickedCount.get()) + Integer.parseInt(toDoListViewModel.vendorAssignedCount.get()) + Integer.parseInt(toDoListViewModel.vendorUnPickedCount.get()) == 0) {
            activityTodoListMainLayout.seller.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.seller.setVisibility(View.VISIBLE);
        }


        //WAREHOUSE
        if (Integer.parseInt(toDoListViewModel.warehousePickedCount.get()) + Integer.parseInt(toDoListViewModel.warehouseAssignedcount.get()) + Integer.parseInt(toDoListViewModel.warehouseUnPickedCount.get()) == 0) {
            activityTodoListMainLayout.warehouse.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.warehouse.setVisibility(View.VISIBLE);
        }


        //RECCE
        if (Integer.parseInt(toDoListViewModel.recciPickedCount.get()) + Integer.parseInt(toDoListViewModel.recciAssignedcount.get()) + Integer.parseInt(toDoListViewModel.recciUnPickedCount.get()) == 0) {
            activityTodoListMainLayout.recce.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.recce.setVisibility(View.VISIBLE);
        }

        //RECCE + SELLER
        int recSellCount = Integer.parseInt(toDoListViewModel.recci_venPickedCount.get()) + Integer.parseInt(toDoListViewModel.recci_venAssignedcount.get()) + Integer.parseInt(toDoListViewModel.recci_venUnPickedCount.get());
        if (recSellCount == 0) {
            activityTodoListMainLayout.reccVen.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.reccVen.setVisibility(View.VISIBLE);
        }

        //RECCE + WAREHOUSE
        int recWrhCount = Integer.parseInt(toDoListViewModel.recci_wrhPickedCount.get()) + Integer.parseInt(toDoListViewModel.recci_wrhAssignedcount.get()) + Integer.parseInt(toDoListViewModel.recci_wrhUnPickedCount.get());
        if (recWrhCount == 0) {
            activityTodoListMainLayout.recceWrh.setVisibility(View.GONE);
        } else {
            activityTodoListMainLayout.recceWrh.setVisibility(View.VISIBLE);
        }
    }

    private void initDataToSeekbar() {
        try {

            int count_picked_pickup = Integer.parseInt(toDoListViewModel.vendorPickedCount.get()) + Integer.parseInt(toDoListViewModel.warehousePickedCount.get());
            int count_picked_recce = Integer.parseInt(toDoListViewModel.recciPickedCount.get());
            int count_picked_recce_pickup = Integer.parseInt(toDoListViewModel.recci_venPickedCount.get()) + Integer.parseInt(toDoListViewModel.recci_wrhPickedCount.get());
            int count_total_unpicked = Integer.parseInt(toDoListViewModel.totalUnPickedCount.get());
            int count_total_pending = Integer.parseInt(toDoListViewModel.totalAssignedCount.get());

            progressItemList = new ArrayList<ProgressItem>();
            // blue span - recce
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = count_picked_recce;
            mProgressItem.color = R.color.rekitool;
            progressItemList.add(mProgressItem);

            // red span - pickup
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = count_picked_pickup;
            Log.i("Mainactivity", mProgressItem.progressItemPercentage + "");
            mProgressItem.color = R.color.red;
            progressItemList.add(mProgressItem);

            // green span - recce + pickup
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = count_picked_recce_pickup;
            mProgressItem.color = R.color.rptool;
            progressItemList.add(mProgressItem);

            //white span
            mProgressItem = new ProgressItem();

            mProgressItem.progressItemPercentage = count_total_unpicked;
            mProgressItem.color = R.color.yellow;
            progressItemList.add(mProgressItem);

            //white span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = count_total_pending;
            mProgressItem.color = R.color.dashboardtool;
            progressItemList.add(mProgressItem);

            Log.i("Mainactivity", progressItemList + "");
            int totalListitem = count_picked_pickup + count_picked_recce + count_picked_recce_pickup + count_total_unpicked + count_total_pending;
            int actionListcount = count_picked_pickup + count_picked_recce + count_picked_recce_pickup + count_total_unpicked;
            activityTodoListMainLayout.totalcount.setText(actionListcount + " out of " + totalListitem);
            activityTodoListMainLayout.seekBar.initData(progressItemList, totalListitem);
            activityTodoListMainLayout.seekBar.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void switchToMapView() {
        if (ismapview) {
            ismapview = false;
            replaceFragment(ToDoListFragment.getInstance());
        } else {
            showToast("Under Development..");
            //   ismapview = true;
            //  replaceFragment(TodoMapView.getInstance());
        }
    }

    public void switchCallBridgeNumbers() {
        showToast("Under Development..");
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.todo_container, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            toDoListFragment = (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.todo_container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callToFragment(String data) {
        if (toDoListFragment != null && toDoListFragment.isVisible()) {
            toDoListFragment.callToAdapter(data);
        }
    }

    void searchManifest() {
        activityTodoListMainLayout.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callToFragment(newText);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                showToast("QR CODE Cancelled!");
            } else {
                toDoListFragment.validateScanResult(result.getContents());
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Constants.GPS_REQUEST) {
                    isGPS = true;
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public ToDoListViewModel getViewModel() {
        toDoListViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(ToDoListViewModel.class);
        return toDoListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_todo_list_view_new;
    }

    @Override
    public void onBackClick() {
        super.onBackPressed();
    }

    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }

    @Override
    public void addReasonCode(int poistion) {


        showReasonCodeWindow(poistion);
    }


    private void showReasonCodeWindow(int position1) {
        itemValue = null;
        post = 0;
        try {
            if (getManifestList != null) {
                selectedManifestPosition = position1;
                toDoListViewModel.getAllShipmentListTODO(getManifestList.get(position1).getManifest_No(), position1);

            }


      /*      View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            lv_languages = view.findViewById(R.id.lv_languages);
            cancelbtn = view.findViewById(R.id.cancelbtn);
            list_adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, rtscodes) {

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    return textView;
                }

            };
            lv_languages.setAdapter(list_adapter);
            lv_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (shipment_detail != null) {
                        itemValue = (String) lv_languages.getItemAtPosition(position);
                        post = position;
                    }

                }
            });
            bottomSheetDialog = new BottomSheetDialog(ToDoListActivity.this);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
            cancelbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemValue == null) {
                        showToast("Please select reason code to submit.");
                    } else {
                        bottomSheetDialog.setCancelable(false);
                        toDoListViewModel.markUndelivered(shipment_detail, pickUpList.get(post).getReason_id(), pickUpList.get(post).getReason_code(), "");
                        callFirstScanApi(position1);
                        toDoListViewModel.createCommitPacketNew(shipment_detail, null, getManifestList.get(position1).getPickup_location_id(), getManifestList.get(position1).getManifest_type(), wayLatitude, wayLongitude);
                        toDoListViewModel.createCommitPacketNew(shipment_detail, null, getManifestList.get(position1).getPickup_location_id(), getManifestList.get(position1).getManifest_type(), wayLatitude, wayLongitude);
                        bottomSheetDialog.dismiss();
                        itemValue = null;
                        post = 0;

                    }
                }
            });*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setReasonCode(String remarks) {
        toDoListFragment.setReasonCode(remarks);
    }


    public void callFirstScanApi(int position) {
        try {
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(toDoListViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(getManifestList.get(position).getManifest_No());
            firstScanRequest.setEmp_Code(toDoListViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(getManifestList.get(position).getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(getManifestList.get(position).getManifest_details().getLocation().getLongitude());

            firstScanRequest.setInscan_within_geofence(isInManifestRadiusUsingLatLng(wayLatitude, wayLongitude, getManifestList.get(position).getManifest_details().getLocation().getLatitude(), getManifestList.get(position).getManifest_details().getLocation().getLongitude(), toDoListViewModel.getGeoFenceRadius()) ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, getManifestList.get(position).getManifest_details().getLocation().getLatitude(), getManifestList.get(position).getManifest_details().getLocation().getLongitude()));


            if (toDoListViewModel.getDataManager().getChild() == true) {
                firstScanRequest.setRole("child");
            } else if (toDoListViewModel.getDataManager().getParent() == true) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(getManifestList.get(position).getManifest_No());
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            toDoListViewModel.updateIsScanStarted(getManifestList.get(position).getManifest_No());
            toDoListViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                toDoListViewModel.callFirstScanApi(toDoListViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {

                    toDoListViewModel.updateFirstInscanStatus(getManifestList.get(position).getManifest_No());
                    toDoListViewModel.updateInscanStatus(getManifestList.get(position).getManifest_No());
                    toDoListViewModel.updateInscanToFirstScanTable(getManifestList.get(position).getManifest_No());
                    toDoListViewModel.inScanCommitPacket(getManifestList.get(position).getManifest_No());
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
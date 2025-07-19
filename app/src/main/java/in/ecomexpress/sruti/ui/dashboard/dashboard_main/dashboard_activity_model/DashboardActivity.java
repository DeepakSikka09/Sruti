package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.BuildConfig;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.SrutiSyncService;
import in.ecomexpress.sruti.databinding.ActivityDashboardLayoutBinding;
import in.ecomexpress.sruti.databinding.ActivityDashboardNavHeaderMainBinding;
import in.ecomexpress.sruti.model.DashboardBanner;
import in.ecomexpress.sruti.model.Departure.CountManifest;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.selfDrop.SelfDropManifest;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.repo.remote.ErrorResponse;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_adapter.CarouselEffectTransformer;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_adapter.SliderAdapter;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.DepartCallback;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.IDashboardNavigator;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.SliderCallListener;
import in.ecomexpress.sruti.ui.dashboard.profile.ProfileActivity;
import in.ecomexpress.sruti.ui.dashboard.sos.ParentActivityMethodCallbackListner;
import in.ecomexpress.sruti.ui.dashboard.starttrip.StartTripConfirmationDialog;
import in.ecomexpress.sruti.ui.dashboard.starttrip.StartTripDialog;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.stoptrip.StopTripDialogMultiVehicle;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ToDoListActivity;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingActivity;
import in.ecomexpress.sruti.ui.login.changePassword.ChangePasswordActivity;
import in.ecomexpress.sruti.ui.login.login.LoginActivity;
import in.ecomexpress.sruti.utils.CommonUtils;

public class DashboardActivity extends BaseActivity<ActivityDashboardLayoutBinding, DashboardViewModel> implements IDashboardNavigator, HasSupportFragmentInjector, SliderCallListener, ParentActivityMethodCallbackListner, DepartCallback, LifecycleOwner {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10101;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    List<DashboardBanner> getDashboardbanner;
    SliderAdapter sliderAdapter;
    ChangePasswordActivity changePasswordActivity;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    DashboardViewModel dashboardViewModel;
    Timer timer;
    Toolbar mToolbar;
    @Inject
    Context context;
    Animation animation1;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ActivityDashboardLayoutBinding activityDashboardBinding;
    private final String TAG = DashboardActivity.class.getName();
    private int banarecount = 0;
    private final HashMap<Long, String> manifest_type = new HashMap<>();
    private boolean isSynced = false;
    private int iscompletesync;
    private int totalmanifestcount;
    public static String version;
    public static LocationTracker lt;
    private ArrayList<Long> manifestCollection;

    private long lastClickTime = 0;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DashboardActivity.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel.setNavigator(this);
        this.activityDashboardBinding = getViewDataBinding();
        if (BuildConfig.DEBUG) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        setUp();
        version = getResources().getString(R.string.v2_0_0);

        dashboardViewModel.loadManifest().observe(this, manifest_lists -> {
            manifestCollection = new ArrayList<>();
            for (Manifest_List manifest_lists1 : manifest_lists) {
                manifestCollection.add(manifest_lists1.getManifest_No());
            }
        });

        dashboardViewModel.combineData();
        dashboardViewModel.getBannerData().observe(DashboardActivity.this, dashboardBanners -> {
            if (dashboardBanners != null && dashboardBanners.size() > 0) {
                activityDashboardBinding.bannrImg.setVisibility(View.GONE);
                activityDashboardBinding.rlViewPager.setVisibility(View.VISIBLE);
                dashboardBannerList(dashboardBanners);
            } else {
                activityDashboardBinding.bannrImg.setVisibility(View.VISIBLE);
                activityDashboardBinding.rlViewPager.setVisibility(View.GONE);
            }
        });

        activityDashboardBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                banarecount = position;
                slideCount(banarecount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        activityDashboardBinding.viewPager.setPadding(80, 0, 80, 0);
        activityDashboardBinding.viewPager.setClipToPadding(false);
        activityDashboardBinding.viewPager.setPageTransformer(false, new CarouselEffectTransformer(this));

        if (dashboardViewModel.getTripID() > 0 && dashboardViewModel.isParent() && dashboardViewModel.iscompleteDepart()) {
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.stop_trip));
            dashboardViewModel.getDataManager().setLogout(true);
        } else if (dashboardViewModel.getTripID() > 0 && !(dashboardViewModel.isParent())) {
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.stop_trip));
            dashboardViewModel.getDataManager().setLogout(true);
        } else if (dashboardViewModel.getTripID() > 0 && (dashboardViewModel.isParent())) {
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.departed));
            dashboardViewModel.getDataManager().setLogout(true);
        } else
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.start_trip));

        dashboardViewModel.getStartTripResponse().observe(this, startResponse -> {
            hideLoading();
            if (startResponse != null) {
                if (startResponse.getStatus()) {
                    dashboardViewModel.getDataManager().setLogout(true);
                    synkData(true);
                    handleDialogClose(true);
                } else {
                    showToast(startResponse.getDescription());
                }
            }
        });

        dashboardViewModel.getStopTripResponse().observe(this, startResponse -> {
            hideLoading();
            if (startResponse != null) {
                if (startResponse.getStatus()) {
                    dashboardViewModel.getDataManager().setLogout(false);
                    handleDialogClose(true);
                } else {
                    showToast(startResponse.getDescription());
                }
            }
        });


        dashboardViewModel.getFetched().observe(this, s -> {
            System.out.println("DDDDDDDDDD " + s.getDescription());
        });
        dashboardViewModel.getDetartureTime().observe(this, departure_response -> {
            if (departure_response != null && departure_response.isStatus() && dashboardViewModel.iscompleteDepart()) {
                activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.stop_trip));
            }
        });
        dashboardViewModel.getUnpickedmanifest().observe(this, manifest_lists -> {
            if (dashboardViewModel.isParent()) {
                if (manifest_lists != null && manifest_lists.size() > 0) {
                    ManifestCommitDialog ob = new ManifestCommitDialog();
                    ob.ConfirmationDialog(this, manifest_lists);
                    ob.show(getSupportFragmentManager(), "dialog");
                } else {
                    LiveData<Integer> ob = dashboardViewModel.getDataManager().getUnSyncManifestList();
                    ob.observe(this, integer -> {
                        ob.removeObservers(this);
                        if (integer != null && integer > 0) {
                            showToast(getString(R.string.please_wait_data_is_syncing));
                        } else {
                            StopTripDialogMultiVehicle stopTripDialog = StopTripDialogMultiVehicle.newInstance(this);
                            stopTripDialog.show(getSupportFragmentManager(), "Stop");

                        }
                    });
                }
            } else {
                dashboardViewModel.executeDataForManifestScanningForChild();
            }
        });
        dashboardViewModel.getIsmanifestScanningForChild().observe(this, integerscan -> {
            if (integerscan != null && integerscan > 0) {
                showToast(getString(R.string.scanning_is_started_by_child));
            } else {
                LiveData<Integer> ob = dashboardViewModel.getDataManager().getUnSyncManifestList();
                ob.observe(this, integer -> {
                    ob.removeObservers(this);
                    if (integer != null && integer > 0) {
                        showToast(getString(R.string.please_wait_data_is_syncing));
                    } else {
                        StartTripConfirmationDialog startTripConfirmationDialog = new StartTripConfirmationDialog();
                        startTripConfirmationDialog.ConfirmationDialog(this, false);
                        startTripConfirmationDialog.show(getSupportFragmentManager(), null);
                    }
                });
            }
        });
        dashboardViewModel.getUncommit_manifestdata().observe(this, shipment_details -> {
            iscompletesync++;
            System.out.println(totalmanifestcount + "     " + iscompletesync);
            if (totalmanifestcount == iscompletesync) {
                hideLoading();
            }
            if (shipment_details != null && shipment_details.size() > 0) {
                ThreadGeneric.executeCall(() -> {
                    HashMap<Long, List<Shipment_Detail>> shipmentsDetails = new HashMap<>();
                    shipmentsDetails.put(shipment_details.get(0).manifestNoInchild, shipment_details);
                    System.out.println(manifest_type.get(shipment_details.get(0).manifestNoInchild) + " Last Commit " + shipment_details.get(0).manifestNoInchild);
                    dashboardViewModel.createCommitPacketNew(shipmentsDetails, manifest_type.get(shipment_details.get(0).manifestNoInchild));
                });

            }
        });


        dashboardViewModel.getMenifest().observe(this, menifest_data_master -> {
        });

        dashboardViewModel.isDataloading().observe(this, aBoolean -> {
            hideLoading();
            if (!"success".equals(aBoolean)) {
                showToast(aBoolean);
            } else {
                dashboardViewModel.selfDrop(manifestCollection);
                if (isSynced) {
                    isSynced = false;
                }
            }
        });

        dashboardViewModel.getSelfDropManifest().observe(this, selfDropResponse -> {
            if (selfDropResponse.isStatus()) {
                ArrayList<Long> ob = new ArrayList<Long>();
                for (SelfDropManifest selfDropManifest : selfDropResponse.getResponse().getSelfDropManifests()) {
                    ob.add(selfDropManifest.getManifestId());
                }
                dashboardViewModel.updateManifestSelfList(ob);
                openTodo();
            }

        });
        dashboardViewModel.getStartedVehicle().observe(this, startRouteDetails -> {
            if (startRouteDetails != null) {
                List<LoginResponse.StartRouteDetails> oldlist = dashboardViewModel.getDataManager().getRouteDetail();
                List<LoginResponse.StartRouteDetails> newlist = new ArrayList<>();
                for (LoginResponse.StartRouteDetails newvehicle : startRouteDetails) {
                    boolean isadded = false;

                    for (LoginResponse.StartRouteDetails loc : oldlist) {
                        if (loc.getStart_vehicle_number().equals(newvehicle.getStart_vehicle_number())) {
                            isadded = true;
                        }
                    }
                    if (!isadded) newlist.add(newvehicle);
                }
                oldlist.addAll(newlist);

                dashboardViewModel.getDataManager().setRouteDetail(new Gson().toJson(oldlist));
                handleDialogClose(false);
            }

        });

        startService(new Intent(context, SrutiSyncService.class));
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
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUp() {
        mDrawer = activityDashboardBinding.drawerView;
        mToolbar = activityDashboardBinding.toolbar;
        mNavigationView = activityDashboardBinding.navigationView;
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(DashboardActivity.this);
            }
        };


        mDrawer.addDrawerListener(mDrawerToggle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorPrimaryDark));
        } else {
            mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mDrawerToggle.syncState();

        setupNavMenu();

    }

    @Override
    public DashboardViewModel getViewModel() {
        dashboardViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DashboardViewModel.class);
        return dashboardViewModel;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected String getScreenName() {
        return "DashBoard Screen";
    }

    @Override
    public void openTodo() {
        if (dashboardViewModel.getTripID() <= 0) {
            showToast(getString(R.string.please_start_trip_to_access_data));
        } else {
            LiveData<CountManifest> oo = dashboardViewModel.getStatusCommit();
            oo.observe(this, new Observer<CountManifest>() {
                @Override
                public void onChanged(@Nullable CountManifest countManifest) {
                    oo.removeObserver(this);
                    if (countManifest != null && countManifest.getTotal() == 0) {
                        showToast(getString(R.string.please_sync_data));
                    } else {
                        // Log the button click event
                        logButtonClick("Open ToDoList Screen");

                        // Measure time to open a new screen
                        long clickTime = System.currentTimeMillis();
                        Intent intent = new Intent(DashboardActivity.this, ToDoListActivity.class);
                        intent.putExtra("clickTime", clickTime);
                        startActivity(intent);
                    }
                }
            });

        }
    }


    @Override
    public void onHandoverClick() {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 3000) {
                showSnackbar("Please wait for 3 seconds");
                return;
            }
            lastClickTime = currentTime;
            if (isNetworkConnected()) {
                if (dashboardViewModel.getTripID() <= 0) {
                    showSnackbar("Please start Trip to Sync");
                } else {
                    synkData(false);
                }
            } else {
                showAlert(getString(R.string.check_internet), DashboardActivity.this);
            }
        } catch (Exception e) {
            Log.d(TAG, "onHandoverClick: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void synkData(boolean isstart) {
        isSynced = isstart;
        showLoading();
        if (dashboardViewModel.getDataManager().is_Ecom_Vehicle()) {
            dashboardViewModel.fetchStartedVehicle();
        }
        dashboardViewModel.combineManifestData();

    }

    @Override
    public void checkForDepart(ArrayList<String> vehicleid, String ownertype) {
        if (dashboardViewModel.isLastDepart() == 1) {
            dashboardViewModel.getIsmanifestScanning().observe(DashboardActivity.this, integer -> {
                if (integer != null && integer > 0) {
                    showToast(getString(R.string.scanning_is_started_by_child));
                } else {
                    dashboardViewModel.DepartTimeAPICall(vehicleid, ownertype);
                }
            });
            dashboardViewModel.executeDataForManifestScanning();
        } else {
            dashboardViewModel.DepartTimeAPICall(vehicleid, ownertype);
        }
    }

    @Override
    public void startStopDialog(boolean isStartTrip) {
        if (isStartTrip) {
            StartTripRequest req = new StartTripRequest();
            req.setRole("child");
            showLoading();
            dashboardViewModel.submitData(req);
        } else {
            StopTripRequest req = new StopTripRequest();
            req.setRole("child");
            showLoading();
            dashboardViewModel.submitData(req);
        }
    }

    @Override
    public void commitMenifest(List<Manifest_List> manifest_lists) {
        System.out.println("manifest_lists " + manifest_lists.size());
        manifest_type.clear();
        if (manifest_lists != null && manifest_lists.size() > 0) {
            showLoading();
            iscompletesync = 0;
            totalmanifestcount = manifest_lists.size();
        }
        for (Manifest_List ob : manifest_lists) {
            System.out.println("from uncommit  " + ob.getManifest_No());
            if (ob.getManifest_No() > 0) {
                manifest_type.put(ob.getManifest_No(), ob.getManifest_type());
                dashboardViewModel.getAllShipmentlist(ob.getManifest_No());
            } else {
                System.out.println("Recci " + ob.getComposite_Key());

            }
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
                dashboardViewModel.updateFileUrl(filePath, manifest_no);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void checkMultiSpace(ArrayList<String> multipspaceList, String multi_space_allow) {
        if (CommonUtils.checkMultiSpace(this, multi_space_allow, multipspaceList)) {
            if (!(this.isFinishing())) {
                showMultiSpaceDialog(this);
            }
        }
    }

    @Override
    public void openFuelReimburse() {
        animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(1000);
        activityDashboardBinding.fuelReimburse.startAnimation(animation1);
        showToast("Under Development..");
       /* if (!isNetworkConnected()) {
            showToast(getResources().getString(R.string.no_network_error));
            return;
        }
        startActivity(FuelReimburseActivity.getStartIntent(this));*/
    }

    @Override
    public void openstatisticsActivity() {
        animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(1000);
        activityDashboardBinding.stats.startAnimation(animation1);
        showToast("Under Development..");
        /*if (!isNetworkConnected()) {
            showToast(getResources().getString(R.string.no_network_error));
            return;
        }
        startActivity(PerformanceActivity.getStartIntent(this));*/
    }

    @SuppressLint("NewApi")
    @Override
    public void onAttendanceClick() {
        animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(1000);
        activityDashboardBinding.attend.startAnimation(animation1);
        showToast("Under Development..");
       /* if (!isNetworkConnected()) {
            showToast(getResources().getString(R.string.no_network_error));
            return;
        }
        Intent intent = new Intent(DashboardActivity.this, AttendanceActivityView.class);
        startActivity(intent);*/
    }

    @Override
    public void onTrainingClick() {
        try {
            if (!isNetworkConnected()) {
                showSnackbar(getResources().getString(R.string.no_network_found));
            }
            else {
                logButtonClick("Open Dashboard");
                long clickTime = System.currentTimeMillis();
               Intent intent = new Intent(DashboardActivity.this, TrainingActivity.class);
                intent.putExtra("clickTime", clickTime);
               startActivity(intent);
            }
        } catch (Exception e){
           e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onSosClick() {
        animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(1000);
        activityDashboardBinding.sos.startAnimation(animation1);
        showToast("Under Development..");
//        SOSDialog sosDialog = SOSDialog.newInstance(DashboardActivity.this, this);
//        sosDialog.show(getSupportFragmentManager());

    }

    @Override
    public void onStartStopTrip() {
        if (checkMultiSpace(this, dashboardViewModel.getDataManager())) {
            if (!(this.isFinishing())) {
                showMultiSpaceDialog();
            }
            return;
        }
        if (!dashboardViewModel.getDataManager().getParent() && !dashboardViewModel.getDataManager().getChild()) {
            showToast(getString(R.string.route_not_assigned));
            return;
        }
        if (dashboardViewModel.isParent()) {
            if (dashboardViewModel.getTripID() <= 0) {
                dashboardViewModel.deleteDirectory(getApplicationContext());
                FirebaseApp.initializeApp(getApplicationContext());
                StartTripDialog startTripDialog = StartTripDialog.newInstance(this);
                startTripDialog.show(getSupportFragmentManager(), "STARTD");

            } else {
                if (!dashboardViewModel.iscompleteDepart()) {   //!dashboardViewModel.getDataManager().isDepart()
                    if (dashboardViewModel.getDataManager().getParent()) {

                        DashboardConfirmationDialog ob = new DashboardConfirmationDialog();
                        if (!dashboardViewModel.getDataManager().is_Ecom_Vehicle()) {
                            LoginResponse.StartRouteDetails stroot = new LoginResponse.StartRouteDetails();
                            stroot.setStart_vehicle_number(dashboardViewModel.getDataManager().getVehicleNo());

                            stroot.setStart_vehicle_owner("self");
                            ArrayList<LoginResponse.StartRouteDetails> kk = new ArrayList<LoginResponse.StartRouteDetails>();
                            kk.add(stroot);
                            ob.ConfirmationDialog(this, kk);
                            ob.vehicle_OwnerType("self");
                            ob.show(getSupportFragmentManager(), "dialog");
                        } else {
                            ob.ConfirmationDialog(this, dashboardViewModel.getDataManager().getRouteDetail());
                            ob.vehicle_OwnerType("Ecom");
                            ob.show(getSupportFragmentManager(), "dialog");
                        }
                    } else {
                        showToast(getString(R.string.you_do_not_have_permission));
                    }
                } else {
                    dashboardViewModel.getUnPickedManifestList();
                }
            }
        } else {
            if (checkMultiSpace(this, dashboardViewModel.getDataManager())) {
                if (!(this.isFinishing())) {
                    showMultiSpaceDialog();
                }
            } else {

                if (dashboardViewModel.getTripID() <= 0) {
                    dashboardViewModel.deleteDirectory(getApplicationContext());
                    FirebaseApp.initializeApp(getApplicationContext());
                    StartTripConfirmationDialog startTripConfirmationDialog = new StartTripConfirmationDialog();
                    startTripConfirmationDialog.ConfirmationDialog(this, true);
                    startTripConfirmationDialog.show(getSupportFragmentManager(), null);
//                    logButtonClick("Open StartTrip");
//
//

                } else {
                    LiveData<CountManifest> ob = dashboardViewModel.getStatusCommit();
                    ob.observe(this, aLong -> {
                        System.out.println("aLong " + aLong.getTotal() + "  " + aLong.getPendingtotal());
                        ob.removeObservers(this);
                        if (aLong != null && aLong.getTotal() != 0) {
                            dashboardViewModel.getUnPickedManifestList();
                        } else if (dashboardViewModel.getDataManager().getTripID() > 0) {
                            dashboardViewModel.getUnPickedManifestList();
                        } else {
                            showToast(getString(R.string.no_manifest_found));
                        }
                    });
                }
            }
        }

    }

    @Override
    public Context getActivityContext() {
        return DashboardActivity.this;
    }

    @Override
    public void dashboardBannerList(List<DashboardBanner> mydashboardBannerList) {
        getDashboardbanner = mydashboardBannerList;
        sliderAdapter = new SliderAdapter(this, mydashboardBannerList);
        sliderAdapter.setsliderCallListener(this);
        activityDashboardBinding.viewPager.setAdapter(sliderAdapter);

        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 1000, 6000);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void syncManifestData() {
         onHandoverClick();
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void onLogoutClick() {
        if (isNetworkConnected()) {
            showLoading();
            dashboardViewModel.getAuthToken().observe(DashboardActivity.this, logoutResponse -> {
                if (logoutResponse.getCode() == 200) {
                    hideLoading();
                    if (dashboardViewModel.getDataManager().isLogout()) {
                        showToast(getString(R.string.please_stop_trip));
                    } else {
                        showDialog();
                    }
                    LocationTracker.deletetable();
                    stopService(new Intent(DashboardActivity.this, LocationService.class));
                } else if (logoutResponse.getCode() == 107) {
                    dashboardViewModel.getDataManager().clearPrefrence();
                    dashboardViewModel.deleteData();
                    hideLoading();
                    // LocationTracker.deletetable();
                    stopService(new Intent(DashboardActivity.this, SrutiSyncService.class));
                    stopService(new Intent(DashboardActivity.this, LocationService.class));
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                } else {
                    hideLoading();
                    showToast(getString(R.string.please_contact_server_admin));
                }
            });
        } else {
            showToast(getString(R.string.check_internet));
        }
    }

    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setMessage(R.string.logout_message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNetworkConnected()) {
                    showLoading();
                    dashboardViewModel.deleteDataAtLogout();
                    try {
                        dashboardViewModel.getLiveDataMerger().observe(DashboardActivity.this, logoutResponse -> {
                            if (logoutResponse.getCode() == 107 || logoutResponse.isStatus()) {
                                hideLoading();
                                stopService(new Intent(DashboardActivity.this, SrutiSyncService.class));
                                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                            }
                        });
                        dashboardViewModel.logoutRequest();
                    } catch (Exception e) {
                        hideLoading();
                        e.printStackTrace();
                    }
                } else {
                    showToast(getString(R.string.check_internet));
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setupNavMenu() {
        ActivityDashboardNavHeaderMainBinding activityDashboardNavHeaderMainBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_dashboard_nav_header_main, activityDashboardBinding.navigationView, false);
        activityDashboardBinding.navigationView.addHeaderView(activityDashboardNavHeaderMainBinding.getRoot());

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mDrawer.closeDrawer(GravityCompat.START);
            switch (item.getItemId()) {

                case R.id.navItemprofile:
                    openProfileActivity();
                    return true;
                case R.id.navItem_switch:
                    showToast("Under Development..");
                    return true;
                case R.id.navItemLogout:
                    dashboardViewModel.onLogoutClick();
                    return true;
                case R.id.navChangePassword:
                    openChangePasswordActivity();
                    return true;
                case R.id.refer:
                    showAlert("Dear Deepak," + "Here is your personalized info. Share it in your network.", DashboardActivity.this);

                    return true;
                case R.id.navItem_call_it_team:
                    showToast("Under Development..");
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        inItLocationTracker(this);

       /* if (checkMultiSpace(this, dashboardViewModel.getDataManager())) {
            showMultiSpaceDialog();
        }*/
        try {
            TextView txtConsigneeName = activityDashboardBinding.navigationView.getHeaderView(0).findViewById(R.id.feName);
            txtConsigneeName.setText(dashboardViewModel.getDataManager().getName());
            TextView txtConsigneeCode = activityDashboardBinding.navigationView.getHeaderView(0).findViewById(R.id.empcode);
            txtConsigneeCode.setText(dashboardViewModel.getDataManager().getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_notification:
                showToast("Under Development..");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_dashboard_layout;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finishAffinity();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just startTripSyncDrs this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onHandleError(ErrorResponse errorDetails) {
        showToast(errorDetails.getEResponse().getDescription());
    }

    /*@Override
    public void doLogout(String message) {
    }*/


    @Override
    public void openChangePasswordActivity() {
        changePasswordActivity = ChangePasswordActivity.newInstance(dashboardViewModel.getDataManager().getCode());
        changePasswordActivity.isForcePassChange(false);
        changePasswordActivity.show(getSupportFragmentManager());
        changePasswordActivity.setChangePasswordListener(() -> {
            finish();
        });
    }

    @Override
    public void openProfileActivity() {
        startActivity(ProfileActivity.getStartIntent(this));
    }


    @Override
    public void openStopTrip() {

    }


    @Override
    public void noToDo() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void stopTimer(String url) {
        timer.cancel();
        getPopup(url);

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void logout() {

    }


    public void handleDialogClose(boolean isTripStarted) {
        System.out.println("handleDialogClose" + dashboardViewModel.getTripID());
        if (dashboardViewModel.getTripID() <= 0)
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.start_trip));
        else if (dashboardViewModel.isParent() && dashboardViewModel.iscompleteDepart()) {
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.stop_trip));
        } else if (!dashboardViewModel.isParent() && dashboardViewModel.getTripID() > 0) {
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.stop_trip));
        } else if (dashboardViewModel.isParent())
            activityDashboardBinding.tvTrip.setText(getResources().getString(R.string.departed));


    }

    void slideCount(int banarecount) {
        if (banarecount >= getDashboardbanner.size()) {
            banarecount = 0;
        }

        activityDashboardBinding.viewPager.setCurrentItem(banarecount);

    }

    public void getPopup(String url) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_dashboard_banner);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        WebView webview_ads = dialog.findViewById(R.id.webview_popup);
        ProgressBar progressBar = dialog.findViewById(R.id.progress1);
        Button ClosePopup = dialog.findViewById(R.id.close_popup);

        sliderAdapter.startWebView(webview_ads, progressBar, url);
        ClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new SliderTimer(), 1000, 6000);
                }
            }
        });


    }

    public void showAlert(String message, Context context) {
        try {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
            aBuilder.setTitle(R.string.app_name);
            AlertDialog dialog = aBuilder.setMessage(message).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent sendIntent = new Intent();
                    // sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    //    dialog.dismiss();
                }
            }).create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    class SliderTimer extends TimerTask {

        @Override
        public void run() {
            DashboardActivity.this.runOnUiThread(() -> {
                slideCount(banarecount);
                banarecount++;
            });
        }
    }

    public boolean inItLocationTracker(Context context) {
        boolean isPlayStore = true;
        lt = LocationTracker.getInstance(context, this, isPlayStore, false);
        return isPlayStore;
    }

}

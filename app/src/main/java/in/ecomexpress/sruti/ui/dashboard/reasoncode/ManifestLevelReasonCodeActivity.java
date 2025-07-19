package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
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

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityReasonCodeBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IManifestReasonCodeNavigation;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class ManifestLevelReasonCodeActivity extends BaseActivity<ActivityReasonCodeBinding, ReasonCodeViewModel> implements View.OnClickListener, IManifestReasonCodeNavigation {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private ArrayList<ReasonCodeList> reasonCode;
    private String reason_code;
    private String reason_id;
    Manifest_List manifest_list;
    public double wayLatitude = 0.0, wayLongitude = 0.0;
    private ArrayList<Shipment_Detail> shipment_detail;
    private ActivityReasonCodeBinding activityReasonCodeBinding;
    ReasonCodeViewModel reasonCodeModel;
    String selectedReason, fullAddress, selectedReasonCode;
    int selectedReasonID;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private ArrayList<Long> manifestNoArray;
    private ArrayList<Manifest_List> newManifestList;
    String mobile_number_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityReasonCodeBinding = getViewDataBinding();
        reasonCodeModel.setNavigator(this);
        reasonCode = getIntent().getParcelableArrayListExtra("reason");
        reason_code = getIntent().getExtras().getString("reason_code");
        reason_id = getIntent().getExtras().getString("reason_id");
        manifest_list = getIntent().getParcelableExtra("manifest_detail");
        fullAddress = getIntent().getExtras().getString("full_address");
        manageLocation();
        setUiData();
        setAdapter();
        observeData();


    }

    @Override
    protected String getScreenName() {
        return "Manifest Level Reasoncode Screen";
    }

    private void observeData() {

        reasonCodeModel.getOtpVerificationManifestResponseMediatorLiveData().observe(this, OtpVerificationManifestResponse -> {
            if (OtpVerificationManifestResponse.isStatus()) {

            } else {

            }
        });

        reasonCodeModel.loadManifest().observe(this, manifest_lists -> {
            try {
                if (manifest_lists != null) {
                    manifestNoArray = new ArrayList<>();
                    newManifestList = new ArrayList<>();
                    for (int i = 0; i < manifest_lists.size(); i++) {
                        if (manifest_lists.get(i).getManifest_details().getLocation_contact_no() == manifest_list.getManifest_details().getLocation_contact_no()) {
                            newManifestList.add(manifest_lists.get(i));
                            /* String mobile_number_type=*/
                            manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                        }
                    }
                    for (int i = 0; i < newManifestList.size(); i++) {
                        {
                            if (newManifestList.get(i).isSharedManifest()) {
                                mobile_number_type = newManifestList.get(i).getMobileNoType();
                                manifest_list.setSharedManifest(true);
                                manifest_list.setMobileNoType(mobile_number_type);
                                reasonCodeModel.updateSharedManifestStatus(manifestNoArray, newManifestList.get(i).getMobileNoType(), true);
                                break;
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        reasonCodeModel.getAllShipmentListTODO(manifest_list.getManifest_No());
        reasonCodeModel.getAllShipmentTODO().observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
            }
        });

        reasonCodeModel.getOtpVerificationManifestResponseMediatorLiveData().observe(this, otpVerificationManifestResponse -> {
            reasonCodeModel.markUndelivered(shipment_detail, selectedReasonID, selectedReasonCode, "");
            callFirstScanApi(manifest_list.getManifest_No());
            reasonCodeModel.createCommitPacketNew(shipment_detail, null, manifest_list.getPickup_location_id(), manifest_list.getManifest_type(), wayLatitude, wayLongitude);
        });
    }

    private void setAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        activityReasonCodeBinding.recyclerReason.setLayoutManager(layoutManager);
        activityReasonCodeBinding.recyclerReason.setAdapter(new ReasonCodeAdapter(ManifestLevelReasonCodeActivity.this, this, reasonCode));

    }

    private void setUiData() {
        activityReasonCodeBinding.toolbar.tvTitle.setText(manifest_list.getManifest_details().getLocationName());
        activityReasonCodeBinding.tvClientName.setText(manifest_list.getCust_name());
        // String fullAddress= new StringBuilder().append(manifest_list.getManifest_details().getAddress().getLine1()).append(", ").append(manifest_list.getManifest_details().getAddress().getCity()).append(", ").append(manifest_list.getManifest_details().getAddress().getState()).append(", ").append(manifest_list.getManifest_details().getAddress().getPincode()).append("(").append(manifest_list.getManifest_details().getLocation_contact_no()).append(")").toString();
        activityReasonCodeBinding.tvAddress.setText(fullAddress);
        activityReasonCodeBinding.btSubmit.setOnClickListener(this);
        activityReasonCodeBinding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public ReasonCodeViewModel getViewModel() {
        reasonCodeModel = ViewModelProviders.of(this, viewModelProviderRoom).get(ReasonCodeViewModel.class);

        return reasonCodeModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reason_code;
    }

    public void selectedReason(String reason_msg, String reason_code, int reason_id) {
        if (!reason_msg.isEmpty() && reason_msg != null) {
            selectedReason = reason_msg;
            selectedReasonCode = reason_code;
            selectedReasonID = reason_id;
            activityReasonCodeBinding.btSubmit.setBackgroundResource(R.drawable.button_curved_selected_background);
            activityReasonCodeBinding.btSubmit.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:

                    if (checkMultiSpace(this, reasonCodeModel.getDataManager())) {
                        showMultiSpaceDialog();
                    }
                    else {
                        if (reasonCodeModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {
                            if (!manifest_list.isSharedManifest()) {

                                Intent intent = new Intent(ManifestLevelReasonCodeActivity.this, OtpReasonCodeActivity.class);
                                intent.putExtra("selected_reason", selectedReason);
                                intent.putExtra("reason_code", selectedReasonCode);
                                intent.putExtra("reason_id", String.valueOf(selectedReasonID));
                                intent.putExtra("manifest_detail", manifest_list);
                                intent.putExtra("full_address", fullAddress);
                                startActivity(intent);
                            } else {
                                if (isNetworkConnected()) {
                                    reasonCodeModel.hitManifestVerify(manifest_list.getManifest_details().getLocation_contact_no(), manifestNoArray, ManifestLevelReasonCodeActivity.this, mobile_number_type);

                                } else {
                                    showToast(getString(R.string.check_internet));
                                }
                            }
                        } else {
                            // reasonCodeModel.hitManifestVerify(manifest_list.getManifest_details().getLocation_contact_no(), manifestNoArray, ManifestLevelReasonCodeActivity.this, mobile_number_type);

                            reasonCodeModel.markUndelivered(shipment_detail, selectedReasonID, selectedReasonCode, "");
                            callFirstScanApi(manifest_list.getManifest_No());
                            reasonCodeModel.createCommitPacketNew(shipment_detail, null, manifest_list.getPickup_location_id(), manifest_list.getManifest_type(), wayLatitude, wayLongitude);

                        }

                    }



                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void saveobject(String manifest_no, String sFileBody) {
        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;
            ThreadGeneric.executeCall(() -> {
                reasonCodeModel.updateFileUrl(filePath, manifest_no);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextScreen() {
        Intent intent = new Intent(ManifestLevelReasonCodeActivity.this, ManifestStatusActivity.class);
        intent.putExtra("reason_code", selectedReason);
        intent.putExtra("manifest_list", manifest_list);
        intent.putExtra("full_address", fullAddress);
        startActivity(intent);
    }

    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }

    public void callFirstScanApi(long manifestNumber) {
        try {
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(reasonCodeModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifestNumber);
            firstScanRequest.setEmp_Code(reasonCodeModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(manifest_list.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(manifest_list.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isInManifestRadiusUsingLatLng(wayLatitude, wayLongitude, manifest_list.getManifest_details().getLocation().getLatitude(), manifest_list.getManifest_details().getLocation().getLongitude(), reasonCodeModel.getGeoFenceRadius()) ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, manifest_list.getManifest_details().getLocation().getLatitude(), manifest_list.getManifest_details().getLocation().getLongitude()));

            if (reasonCodeModel.getDataManager().getChild() == true) {
                firstScanRequest.setRole("child");
            } else if (reasonCodeModel.getDataManager().getParent() == true) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifestNumber);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            reasonCodeModel.updateIsScanStarted(manifestNumber);
            reasonCodeModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                reasonCodeModel.callFirstScanApi(reasonCodeModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    reasonCodeModel.updateInscanStatus(manifestNumber);
                    reasonCodeModel.updateInscanToFirstScanTable(manifestNumber);
                    reasonCodeModel.inScanCommitPacket(manifestNumber);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ManifestLevelReasonCodeActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(ManifestLevelReasonCodeActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
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
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(ManifestLevelReasonCodeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ManifestLevelReasonCodeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ManifestLevelReasonCodeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(ManifestLevelReasonCodeActivity.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

        }
    }
}


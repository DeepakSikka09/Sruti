package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityOtpVerificationBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IQrNavigator;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.OtpVerficationViewModel;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class OtpVerificationActivity extends BaseActivity<ActivityOtpVerificationBinding, OtpVerficationViewModel> implements View.OnClickListener, IQrNavigator {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ActivityOtpVerificationBinding activityOtpVerificationBinding;
    OtpVerficationViewModel otpVerficationViewModel;
    private List<ReasonCodeList> pickUpList;
    private String otpValidation;
    private String manifestType;
    private String locationType;
    private Manifest_List manifestList;
    private ArrayList<Shipment_Detail> shipment_detail;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private long manifestNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpVerficationViewModel.setNavigator(this);
        this.activityOtpVerificationBinding = getViewDataBinding();
        setUp();
        pickUpList = new ArrayList<>();
    }

    @Override
    protected String getScreenName() {
        return "OTP Verification Screen";
    }

    private void setUp() {
        activityOtpVerificationBinding.btnDrop.setOnClickListener(this);
        activityOtpVerificationBinding.back.setOnClickListener(this);
        activityOtpVerificationBinding.verifyTv.setOnClickListener(this);
        activityOtpVerificationBinding.btnCommit.setOnClickListener(this);

        otpValidation = getIntent().getExtras().getString("otp_validation");
        manifestType = getIntent().getExtras().getString("manifest_type");
        manifestList = getIntent().getParcelableExtra("data");
        locationType = getIntent().getExtras().getString("location_type");
        manifestNo = getIntent().getExtras().getLong("manifest_no");

        otpVerficationViewModel.getPickupListToView().observe(this, pickupList -> {
            pickUpList.addAll(pickupList);
        });

        otpVerficationViewModel.getAllShipmentList(manifestList.getManifest_No()).observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
            }
        });


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
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    }
                }
            }
        };


    }

    @Override
    public OtpVerficationViewModel getViewModel() {
        otpVerficationViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(OtpVerficationViewModel.class);
        return otpVerficationViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_otp_verification;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnDrop: {
                otpVerficationViewModel.checkAll(shipment_detail);
                showPopup();
                break;
            }

            case R.id.btnCommit: {
                getLocation();
                try {
                    if (otpVerficationViewModel.checkIfAtLeastOneReasonCodeSelected(shipment_detail)) {
                        showToast("Please select reason code first.");
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        AlertDialog dialog = alertDialog.setMessage(R.string.commit)
                                .setTitle(getString(R.string.alert_title))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                otpVerficationViewModel.createCommitPacketNew(shipment_detail, null, 11l, manifestType, wayLatitude, wayLongitude);
                                                otpVerficationViewModel.failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestList.getManifest_No());
                                                otpVerficationViewModel.failedCommitPacket(1,Constants.PICKED,manifestList.getManifest_No());
                                               otpVerficationViewModel.updateCommit_DataTable(manifestList.getManifest_No());
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                }


                break;
            }
            case R.id.back: {
                showSnackbar(getString(R.string.cannot_go_back));
                break;
            }
            case R.id.verify_tv: {
                hideKeyboard(OtpVerificationActivity.this);

                if (isNetworkConnected()) {
                    if (activityOtpVerificationBinding.otpEdt.getText().toString().isEmpty()) {
                        showToast(getString(R.string.otp_error));
                    } else {
                        otpVerficationViewModel.updateShipment(Constants.PENDING, "", Calendar.getInstance().getTimeInMillis() + "", false,false,manifestList.getManifest_No());

                        if (activityOtpVerificationBinding.otpEdt.getText().toString().equalsIgnoreCase(otpValidation)) {
                            if (locationType.equalsIgnoreCase("warehouse")) {
                                Intent intent = WarehouseActivity.getStartIntent(OtpVerificationActivity.this);
                                intent.putExtra("data", manifestList);
                                intent.putExtra("manifest_no", manifestNo);
                                startActivity(intent);
                            } else if (locationType.equalsIgnoreCase("seller")) {
                                Intent intent = SellerActivity.getStartIntent(OtpVerificationActivity.this);
                                intent.putExtra("data", manifestList);
                                intent.putExtra("manifest_no", manifestNo);
                                startActivity(intent);
                            }
                        } else {
                            showToast("OTP Not Matched");
                        }
                    }

                } else {
                    showToast(getString(R.string.check_internet));
                }
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
                otpVerficationViewModel.updateFileUrl(filePath, manifest_no);
            });

            Intent intent = new Intent(OtpVerificationActivity.this, SuccessFailActivity.class);
            intent.putExtra("screen_validation", "fail");
            intent.putExtra("manifest_no",manifestNo);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showPopup() {

        try {
            ListPopupWindow popup_new = new ListPopupWindow(OtpVerificationActivity.this);
            popup_new.setModal(false);
            popup_new.setAnchorView(activityOtpVerificationBinding.btnDrop);
            popup_new.setWidth(1000);
            List<String> rtscodes = new ArrayList<>();

            for (int i = 0; i < pickUpList.size(); i++) {
                rtscodes.add(pickUpList.get(i).getReason_msg());
            }

            popup_new.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtscodes));
            popup_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    otpVerficationViewModel.markUndelivered(shipment_detail, pickUpList.get(i).getReason_id(), pickUpList.get(i).getReason_code(), "");
                    popup_new.dismiss();
                }
            });

            popup_new.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }


    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(OtpVerificationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(OtpVerificationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OtpVerificationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(OtpVerificationActivity.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(OtpVerificationActivity.this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back));
    }


}

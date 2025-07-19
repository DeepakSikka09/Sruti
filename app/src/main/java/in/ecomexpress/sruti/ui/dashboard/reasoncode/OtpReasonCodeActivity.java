package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityOtpReasonCodeBinding;
import in.ecomexpress.sruti.databinding.AlternateBottomsheetDialogBinding;
import in.ecomexpress.sruti.databinding.SkipOtpReasonListBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IReasonCodeNavigation;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class OtpReasonCodeActivity extends BaseActivity<ActivityOtpReasonCodeBinding, OtpReasonCodeViewModel> implements IReasonCodeNavigation, View.OnFocusChangeListener, View.OnKeyListener, TextWatcher, View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private String selectedReason, full_address;
    private String reason_code;

    SkipOtpReasonListBinding skipOtpReasonListBinding;
    private String reason_id;
    private CountDownTimer countDownTimers = null;
    private CountDownTimer countDownTimer = null;
    BottomSheetDialog skipReasonDialog;
    private Manifest_List manifest_detail;
    private Manifest_List manifest_list;
    private ArrayList<Shipment_Detail> shipment_detail;
    private ToDoListViewModel toDoListViewModel;
    public double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    String reasonCode = "";
    private ArrayList<Long> manifestNoArray;
    OtpReasonCodeViewModel otpReasonCodViewModel;
    ActivityOtpReasonCodeBinding activityOtpReasonCodeBinding;

    String mobile_number_type = "";
    long count = 0;

    boolean on_max_reach = false;
    boolean came_from_otp = false;
    boolean came_from_skip_otp = false;
    boolean came_from_Alternate_mobile = false;
    boolean otp_verified = false;
    boolean reason_Selected = false;
    boolean perform_click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityOtpReasonCodeBinding = getViewDataBinding();
        otpReasonCodViewModel.setNavigator(this);
        selectedReason = getIntent().getExtras().getString("selected_reason");
        manifest_list = getIntent().getParcelableExtra("manifest_detail");
        reason_code = getIntent().getExtras().getString("reason_code");
        reason_id = getIntent().getExtras().getString("reason_id");
        full_address = getIntent().getExtras().getString("full_address");


        setUiData();
        setPINListeners();
        onClickListner();

        if (!manifest_list.getManifest_details().isIs_valid_contact_no() ) {
            perform_click=true;
            activityOtpReasonCodeBinding.tvAlternate.performClick();

            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);
        }else{
            perform_click=false;

        }
            if (isNetworkConnected()) {
            otpReasonCodViewModel.hitOtpApi(manifest_list.getManifest_details().getLocation_contact_no(), manifest_list.getOtp_pickup_location_id(), this, "R");
        } else {
            showToast(getString(R.string.check_internet));
        }



        otpReasonCodViewModel.getAllShipmentListTODO(manifest_list.getManifest_No());
        manageLocation();
        observeData();


//        activityOtpReasonCodeBinding.tvAlternate.performClick();

    }

    @Override
    protected String getScreenName() {
        return "OTP Reason Code Screen";
    }

    private void setUiData() {
        activityOtpReasonCodeBinding.tvClient.setText(manifest_list.getCust_name());
        //activityOtpReasonCodeBinding.tvAddress.setText(new StringBuilder().append(manifest_list.getManifest_details().getAddress().getLine1()).append(", ").append(manifest_list.getManifest_details().getAddress().getCity()).append(", ").append(manifest_list.getManifest_details().getAddress().getState()).append(", ").append(manifest_list.getManifest_details().getAddress().getPincode()).append("(").append(manifest_list.getManifest_details().getLocation_contact_no()).append(")").toString());
        activityOtpReasonCodeBinding.tvAddress.setText(full_address);
        activityOtpReasonCodeBinding.toolbar.tvTitle.setText(getResources().getString(R.string.verification));

        //  activityOtpReasonCodeBinding.toolbar.tvTitle.setText(manifest_list.getManifest_details().getLocationName());
    }

    private void manageLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OtpReasonCodeActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(OtpReasonCodeActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
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

    private void observeData() {


        otpReasonCodViewModel.getAllShipmentTODO().observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
            }
        });


        otpReasonCodViewModel.getSkipOtpMainResponseMediatorLiveData().observe(this, skipOtpMainResponse -> {
            if (skipOtpMainResponse.getStatus()) {
                openSkipReasonOTP((ArrayList<ReasonList>) skipOtpMainResponse.getResponse().getReasonList());
            } else {
                showSnackbar(skipOtpMainResponse.getDescription());
            }
        });

        otpReasonCodViewModel.getVerifyPickUpOtp().observe(this, verifyPickUpOtpResponse -> {
            if (verifyPickUpOtpResponse.isStatus()) {

                mobile_number_type = verifyPickUpOtpResponse.getResponse().getVerification_source();
                showSuccessSnackbar(verifyPickUpOtpResponse.getResponse().getDescription());
                activityOtpReasonCodeBinding.btVerify.setEnabled(true);
                activityOtpReasonCodeBinding.btVerify.setBackgroundResource(R.drawable.button_curved_selected_background);
                otp_verified = true;
                //   DisableButtonAfterVerify(on_max_reach,otp_verified);

                DisableAlternateButton(came_from_Alternate_mobile, on_max_reach);
                DisableSkipOTP(otp_verified, came_from_otp);
                DisableAll(otp_verified);
                DisableViaSkipOtp(otp_verified, reason_Selected);

            } else {

                activityOtpReasonCodeBinding.btVerify.setEnabled(false);
                activityOtpReasonCodeBinding.btVerify.setBackgroundResource(R.drawable.button_curved_background);
                showSnackbar(verifyPickUpOtpResponse.getDescription());
            }
        });

        otpReasonCodViewModel.getSendPickUpOtp().observe(this, sendPickUpOtpResponse -> {
            if (sendPickUpOtpResponse.isStatus()) {
                setTimer();
                showSuccessSnackbar(sendPickUpOtpResponse.getResponse().getDescription());

            } else {
                if (sendPickUpOtpResponse.getResponse() != null) {


                    if (sendPickUpOtpResponse.getResponse().getMax_reach() != null) {

                        if (sendPickUpOtpResponse.getResponse().getMax_reach()) {
                            showSnackbar(sendPickUpOtpResponse.getDescription());
                            on_max_reach = true;
                            otp_verified = false;
                            came_from_otp = false;
                            DisableButtonOnFirstTimeCame(on_max_reach);
                            DisableAlternateButton(came_from_Alternate_mobile, on_max_reach);
                            // DisableSkipOTP(otp_verified,came_from_otp);
                        } else {
                            showSnackbar(sendPickUpOtpResponse.getDescription());
                        }
                    } else {
                        showSnackbar(sendPickUpOtpResponse.getDescription());
                    }
                } else {
                    showSnackbar(sendPickUpOtpResponse.getDescription());
                }
            }
        });




      /*  otpReasonCodViewModel.getVerifyPickUpOtp().observe(this, verifyPickUpOtpResponse -> {
            if (verifyPickUpOtpResponse.isStatus()) {
                mobile_number_type=verifyPickUpOtpResponse.getResponse().getVerification_source();
                showSuccessSnackbar(verifyPickUpOtpResponse.getResponse().getDescription());
                activityOtpReasonCodeBinding.btVerify.setEnabled(true);
                activityOtpReasonCodeBinding.btVerify.setBackgroundResource(R.drawable.button_curved_selected_background);


            } else {
                showSnackbar(verifyPickUpOtpResponse.getDescription());
                activityOtpReasonCodeBinding.btVerify.setEnabled(false);
                activityOtpReasonCodeBinding.btVerify.setBackgroundResource(R.drawable.button_curved_background);

            }
        });*/

        /*otpReasonCodViewModel.getSendPickUpOtp().observe(this, sendPickUpOtpResponse -> {
            if (sendPickUpOtpResponse.isStatus()) {
                showSuccessSnackbar(sendPickUpOtpResponse.getResponse().getDescription());

            } else {
                showSnackbar(sendPickUpOtpResponse.getDescription());
            }
        });
*/

        otpReasonCodViewModel.loadManifest().observe(this, manifest_lists -> {
            try {
                if (manifest_lists != null) {
                    manifestNoArray = new ArrayList<>();

                    for (int i = 0; i < manifest_lists.size(); i++) {
                        if (manifest_lists.get(i).getManifest_details().getLocation_contact_no() == manifest_list.getManifest_details().getLocation_contact_no()) {
                            manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void onClickListner() {
        activityOtpReasonCodeBinding.tvSkipOtp.setOnClickListener(this);
        activityOtpReasonCodeBinding.btVerify.setOnClickListener(this);
        activityOtpReasonCodeBinding.otpLayout.resendotpTV.setOnClickListener(this);
        activityOtpReasonCodeBinding.toolbar.ivBack.setOnClickListener(this);
        activityOtpReasonCodeBinding.tvAlternate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_verify:
                if (checkMultiSpace(this, otpReasonCodViewModel.getDataManager())) {
                    showMultiSpaceDialog();
                } else {
                    if (isNetworkConnected()) {
                        otpReasonCodViewModel.updateSharedManifestStatus(manifestNoArray, mobile_number_type, true);

                        otpReasonCodViewModel.markUndelivered(shipment_detail, Integer.parseInt(reason_id), reason_code, "");
                        callFirstScanApi(manifest_list.getManifest_No());
                        otpReasonCodViewModel.createCommitPacketNew(shipment_detail, null, manifest_list.getPickup_location_id(), manifest_list.getManifest_type(), wayLatitude, wayLongitude);
                    } else {
                        showToast(getString(R.string.check_internet));
                    }
                }

                break;
            case R.id.resendotpTV:
                if (isNetworkConnected()) {
                    came_from_Alternate_mobile = false;
                    setTimer();

                    otpReasonCodViewModel.hitOtpApi(manifest_list.getManifest_details().getLocation_contact_no(), manifest_list.getOtp_pickup_location_id(), this, "R");
                } else {
                    showToast(getString(R.string.check_internet));
                }
                break;

            case R.id.tv_alternate:
                openAlternateNoBottomSheet();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_skip_otp:
                if (isNetworkConnected()) {
                    otpReasonCodViewModel.hitSkipOtpReasonListAPI(this);
                } else {
                    showToast(getString(R.string.check_internet));
                }
                break;

            case R.id.bt_submit:
                if (isNetworkConnected()) {
                    otpReasonCodViewModel.hitVerifyApi(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().toString(), manifest_list.getManifest_details().getLocation_contact_no(), this, manifestNoArray, reasonCode);
                    reason_Selected = true;
                    skipReasonDialog.dismiss();
                } else {
                    showToast(getString(R.string.check_internet));
                }

                break;

        }
    }

    private void setPINListeners() {
        activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.addTextChangedListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT1ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT2ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT3ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT4ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT5ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT6ET.setOnFocusChangeListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT1ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT2ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT3ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT4ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT5ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.OPT6ET.setOnKeyListener(this);
        activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.setOnKeyListener(this);
    }

    @Override
    public OtpReasonCodeViewModel getViewModel() {
        otpReasonCodViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(OtpReasonCodeViewModel.class);

        return otpReasonCodViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_otp_reason_code;

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
                otpReasonCodViewModel.updateFileUrl(filePath, manifest_no);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextScreen() {
        Intent intent = new Intent(OtpReasonCodeActivity.this, ManifestStatusActivity.class);
        intent.putExtra("reason_code", selectedReason);
        intent.putExtra("manifest_list", manifest_list);
        intent.putExtra("full_address", full_address);
        startActivity(intent);
    }

    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(OtpReasonCodeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(OtpReasonCodeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OtpReasonCodeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(OtpReasonCodeActivity.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

        }
    }

    public void callFirstScanApi(long manifestNumber) {
        try {
            FirstScanRequest firstScanRequest = new FirstScanRequest();
            firstScanRequest.setTrip_Id(otpReasonCodViewModel.getDataManager().getTripID());
            firstScanRequest.setManifest_id(manifestNumber);
            firstScanRequest.setEmp_Code(otpReasonCodViewModel.getDataManager().getCode());
            firstScanRequest.setStart_Time(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            firstScanRequest.setFirst_scan_lat(wayLatitude);
            firstScanRequest.setFirst_scan_lng(wayLongitude);
            firstScanRequest.setVerified_lat(manifest_list.getManifest_details().getLocation().getLatitude());
            firstScanRequest.setVerified_lng(manifest_list.getManifest_details().getLocation().getLongitude());
            firstScanRequest.setInscan_within_geofence(isInManifestRadiusUsingLatLng(wayLatitude, wayLongitude, manifest_list.getManifest_details().getLocation().getLatitude(), manifest_list.getManifest_details().getLocation().getLongitude(), otpReasonCodViewModel.getGeoFenceRadius()) ? 1 : 0);
            firstScanRequest.setDistance_from_pickup_location(getRadialDistanceUsingLatLng(wayLatitude, wayLongitude, manifest_list.getManifest_details().getLocation().getLatitude(), manifest_list.getManifest_details().getLocation().getLongitude()));

            if (otpReasonCodViewModel.getDataManager().getChild() == true) {
                firstScanRequest.setRole("child");
            } else if (otpReasonCodViewModel.getDataManager().getParent() == true) {
                firstScanRequest.setRole("parent");
            }

            FirstInscan firstInscan = new FirstInscan();
            firstInscan.setManifetsId(manifestNumber);
            firstInscan.setStatus(0);
            firstInscan.setRequestData(new ObjectMapper().writeValueAsString(firstScanRequest));
            otpReasonCodViewModel.updateIsScanStarted(manifestNumber);
            otpReasonCodViewModel.insertFirstScanData(firstInscan);

            if (isNetworkConnected()) {
                otpReasonCodViewModel.callFirstScanApi(otpReasonCodViewModel.getDataManager().getAuthToken(), firstScanRequest).observe(this, firstScanResponse -> {
                    otpReasonCodViewModel.updateInscanStatus(manifestNumber);
                    otpReasonCodViewModel.updateInscanToFirstScanTable(manifestNumber);
                    otpReasonCodViewModel.inScanCommitPacket(manifestNumber);
                });

            } else {
                showToast(getString(R.string.no_network_error));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setText("");
        } else if (s.length() == 1) {
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setText(s.charAt(0) + "");
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");
        } else if (s.length() == 2) {

            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setText(s.charAt(1) + "");
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");
        } else if (s.length() == 3) {
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setText(s.charAt(2) + "");
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");

        } else if (s.length() == 4) {
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setText(s.charAt(3) + "");
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText("");
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");
        } else if (s.length() == 5) {
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText(s.charAt(4) + "");
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");
        } else if (s.length() == 6) {
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText(s.charAt(5) + "");
            hideSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.OPT6ET);
            if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().toString().length() == 6) {
                if (isNetworkConnected()) {
                    otpReasonCodViewModel.hitVerifyApi(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().toString(), manifest_list.getManifest_details().getLocation_contact_no(), this, manifestNoArray, "");
                } else {
                    showToast(getString(R.string.check_internet));
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.OPT1ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }
                break;

            case R.id.OPT2ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }
                break;

            case R.id.OPT3ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }
                break;

            case R.id.OPT4ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }
                break;
            case R.id.OPT5ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }

                break;
            case R.id.OPT6ET:
                if (hasFocus) {
                    setFocus(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                    showSoftKeyboard(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.m_hidden_Edit_text:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 6)
                            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setText("");
                        else if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 5)
                            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setText("");
                        else if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 4)
                            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setText("");
                        else if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 3)
                            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setText("");
                        else if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 2)
                            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setText("");
                        else if (activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().length() == 1)
                            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setText("");

//                        if (mPinHiddenEditText.length() > 0)
//                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));
//                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /*
        private void setTimer() {
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.INVISIBLE);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.INVISIBLE);
            activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
            countDownTimer = new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    count = millisUntilFinished / 1000;
                    activityOtpReasonCodeBinding.otpLayout.tvTimer.setText("Resend in" + " " + (count) + " " + "seconds");
                }

                @Override
                public void onFinish() {
                    activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.VISIBLE);
                    activityOtpReasonCodeBinding.otpLayout.tvTimer.setText("");
                    activityOtpReasonCodeBinding.tvAlternate.setEnabled(true);
                    activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(OtpReasonCodeActivity.this, R.color.blue_grey_light));

                }
            };
            countDownTimer.start();
        }*/
    private void setTimer() {
        activityOtpReasonCodeBinding.otpLayout.tvTimer.setVisibility(View.VISIBLE);
        activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.INVISIBLE);
        activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
        activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
        activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
        activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                count = millisUntilFinished / 1000;
                activityOtpReasonCodeBinding.otpLayout.tvTimer.setText("Resend in" + " " + (count) + " " + "seconds");
            }

            @Override
            public void onFinish() {
                if(perform_click){
                    activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.GONE);
                }else {
                    activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.VISIBLE);
                }
                  activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.VISIBLE);
                activityOtpReasonCodeBinding.otpLayout.tvTimer.setText("");
                activityOtpReasonCodeBinding.tvAlternate.setEnabled(true);
                activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(true);
                activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(OtpReasonCodeActivity.this, R.color.blue_grey_light));
                activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(OtpReasonCodeActivity.this, R.color.blue_grey_light));

            }
        };
        countDownTimer.start();
    }

    public void openAlternateNoBottomSheet() {
        try {

            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.GONE);
            Dialog alternatNoDialog = new Dialog(this, R.style.AppCompatAlertDialogStyle);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set the width to match the parent
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set the height to wrap content
            alternatNoDialog.getWindow().setAttributes(layoutParams);

            AlternateBottomsheetDialogBinding alternateBottomsheetDialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.alternate_bottomsheet_dialog,
                    (ViewGroup) activityOtpReasonCodeBinding.getRoot(),
                    false
            );
            alternatNoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alternatNoDialog.setCancelable(false);
            alternatNoDialog.setContentView(alternateBottomsheetDialogBinding.getRoot());
            alternatNoDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alternatNoDialog.setCanceledOnTouchOutside(false);

            if(perform_click){
                activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.GONE);
                activityOtpReasonCodeBinding.otpLayout.resendotpTV.setVisibility(View.GONE);
                alternateBottomsheetDialogBinding.tvSkipVerification.setVisibility(View.GONE);
            }else {

                activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.GONE);
                alternateBottomsheetDialogBinding.tvSkipVerification.setVisibility(View.GONE);

            }
            countDownTimers = new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    alternateBottomsheetDialogBinding.tvCounterTimer.setText(String.format(Locale.getDefault(), "%d seconds", millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    alternateBottomsheetDialogBinding.tvCounterTimer.setVisibility(View.GONE);
                    alternateBottomsheetDialogBinding.tvSkipVerification.setVisibility(View.VISIBLE);

                }
            };
            countDownTimers.start();

            if (alternatNoDialog != null && !alternatNoDialog.isShowing()) {
                alternatNoDialog.show();
            }
            alternateBottomsheetDialogBinding.tvSkipVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isNetworkConnected()) {
                        activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.VISIBLE);
                        otpReasonCodViewModel.hitSkipOtpReasonListAPI(OtpReasonCodeActivity.this);
                    } else {
                        showToast(getString(R.string.check_internet));
                    }
                    alternatNoDialog.hide();

                }
            });

            alternateBottomsheetDialogBinding.btVerifyNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!alternateBottomsheetDialogBinding.etAlternateNo.getText().toString().equals("") && alternateBottomsheetDialogBinding.etAlternateNo.getText().toString().length() >= 10) {
                        if (isNetworkConnected()) {
                            alternatNoDialog.hide();
                            countDownTimers.cancel();
                            came_from_Alternate_mobile = true;
                            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(true);
                            activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.VISIBLE);
                            otpReasonCodViewModel.hitOtpApi(Long.parseLong(alternateBottomsheetDialogBinding.etAlternateNo.getText().toString()), manifest_list.getOtp_pickup_location_id(), OtpReasonCodeActivity.this, "A");
                        } else {
                            showToast(getString(R.string.check_internet));
                        }
                    } else {
                        Toast.makeText(OtpReasonCodeActivity.this, R.string.enter_valid_mob_no, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        if (!otp_verified && !reason_Selected) {
            super.onBackPressed();
        } else {
            showSnackbar(getString(R.string.Cant_go_Back));
        }
    }

    public void selectedReason(String reason_msg, String reason_code) {
        //HitAPI

        if (!reason_code.equalsIgnoreCase("")) {
            skipOtpReasonListBinding.btSubmit.setEnabled(true);
            skipOtpReasonListBinding.btSubmit.setBackgroundResource(R.drawable.button_curved_selected_background);


        }
        // skipReasonDialog.dismiss();
        came_from_skip_otp = true;
        reasonCode = reason_code;

    }


    private void DisableViaSkipOtp(boolean otp_verified, boolean reason_Selected) {
        if (otp_verified && reason_Selected) {
            activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));


           /* activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
*/

          /*  activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
*/
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);

        }
    }

    private void DisableAll(boolean otp_verified) {
        if (otp_verified) {
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));


           /* activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
*/

          /*  activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
*/
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);
        }

    }

    private void DisableAlternateButton(boolean came_from_alternate_mobile, boolean on_max_reach) {
        if (came_from_alternate_mobile && on_max_reach) {
            activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
        } else {

        }
    }

    private void DisableSkipOTP(boolean otp_verified, boolean came_from_otp) {
        if (otp_verified && came_from_otp) {
            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);

        } else {

        }
    }

    private void DisableButtonOnFirstTimeCame(boolean on_first_time_max_reach) {
        if (on_first_time_max_reach) {
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(this, R.color.text_color_dark_grey));

            activityOtpReasonCodeBinding.tvAlternate.setEnabled(true);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(this, R.color.blue_ecom));

            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(true);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(this, R.color.blue_ecom));

            activityOtpReasonCodeBinding.otpLayout.tvTimer.setVisibility(View.GONE);


        }


    }


    private void openSkipReasonOTP(ArrayList<ReasonList> reasonList) {
        try {

            skipReasonDialog = new BottomSheetDialog(this, R.style.videosheetDialogTheme);


            skipOtpReasonListBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.skip_otp_reason_list,
                    (ViewGroup) activityOtpReasonCodeBinding.getRoot(),
                    false
            );
            skipReasonDialog.setContentView(skipOtpReasonListBinding.getRoot());
            /*    alternatNoDialog.setContentView(R.layout.alternate_bottomsheet_dialog);*/
            skipReasonDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            skipReasonDialog.setCanceledOnTouchOutside(true);

            if (skipReasonDialog != null && !skipReasonDialog.isShowing()) {
                skipReasonDialog.show();
            }
            skipOtpReasonListBinding.btSubmit.setOnClickListener(this);


            skipOtpReasonListBinding.ReasonRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            skipOtpReasonListBinding.ReasonRecyclerView.setAdapter(new SkipOtpViaSwipeReasonCodeAdapter(reasonList, this));


        } catch (Exception e) {

        }
    }
}
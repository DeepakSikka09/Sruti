package in.ecomexpress.sruti.ui.dashboard.shipment;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityShipmentOtpBinding;
import in.ecomexpress.sruti.databinding.AlternateBottomsheetDialogBinding;
import in.ecomexpress.sruti.databinding.SkipOtpReasonListBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.attendance.AttendanceRequest;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.masterdata.ReasonList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;

public class ShipmentOtpActivity extends BaseActivity<ActivityShipmentOtpBinding, ShipmentOtpViewModel> implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher, View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ShipmentOtpViewModel shipmentOtpViewModel;
    ActivityShipmentOtpBinding activityOtpReasonCodeBinding;
    private CountDownTimer countDownTimer = null;
    private CountDownTimer countDownTimers = null;
    private Manifest_List manifest_detail;
    long count = 0;
    BottomSheetDialog skipReasonDialog;
    SkipOtpReasonListBinding skipOtpReasonListBinding;
    public static Long manifestNo;
    private String advance_shipment_count;
    private boolean is_global;
    private String manifest_count;

    String reasonCode = "";
    private String shipment_count;
    private List<ReasonList> pickUpList;
    private String picked_count, unpicked_count;
    private String remaining_count;
    private String camera_visible;
    private Bitmap emptyBitmap, well;
    private ArrayList<Recci> recci;
    private long pickup_location_id;
    private ArrayList<Long> manifestNoArray;
    String mobile_number_type = "";
    private List<Manifest_List> manifest_list1 = new ArrayList();
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
        shipmentOtpViewModel.setNavigator(this);

        setUp();
        setPINListeners();
        onClickListner();

        activityOtpReasonCodeBinding.toolbar.tvTitle.setText(getResources().getString(R.string.verification));


        if (!manifest_detail.getManifest_details().isIs_valid_contact_no() ) {
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
            shipmentOtpViewModel.hitOtpApi(manifest_detail.getManifest_details().getLocation_contact_no(), manifest_detail.getOtp_pickup_location_id(), this, "R");
        } else {
            showToast(getString(R.string.check_internet));
        }
        obserLiveData();

    }

    @Override
    protected String getScreenName() {
        return "Shipment OTP Screen";
    }

    private void setUp() {
        manifest_detail = getIntent().getParcelableExtra("manifest_detail");
        manifestNoArray = (ArrayList<Long>) getIntent().getSerializableExtra("manifestNoArray");
        pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
        recci = getIntent().getParcelableArrayListExtra("RECCIQ");
        advance_shipment_count = getIntent().getExtras().getString("advance_shipment_count");
        manifest_count = getIntent().getExtras().getString("manifest_count");

        shipment_count = getIntent().getExtras().getString("shipment_count");
        picked_count = getIntent().getExtras().getString("picked_count");
        unpicked_count = getIntent().getExtras().getString("unpicked_count");
        remaining_count = getIntent().getExtras().getString("remaining_count");
        camera_visible = getIntent().getExtras().getString("camera_visible");
        manifestNo = getIntent().getExtras().getLong("manifestNo");
        is_global = getIntent().getExtras().getBoolean("is_global");
        activityOtpReasonCodeBinding.txtManifest.setText(manifest_count);
        activityOtpReasonCodeBinding.txtShipment.setText(shipment_count);
        activityOtpReasonCodeBinding.txtPicked.setText(picked_count);
        activityOtpReasonCodeBinding.txtUnpicked.setText(unpicked_count);
        activityOtpReasonCodeBinding.txtRemaining.setText(remaining_count);
        activityOtpReasonCodeBinding.txtAdvanceShipment.setText(advance_shipment_count);

    }

    private void obserLiveData() {


        shipmentOtpViewModel.getSkipOtpMainResponseMediatorLiveData().observe(this, skipOtpMainResponse -> {
            if (skipOtpMainResponse.getStatus()) {
                openSkipReasonOTP((ArrayList<ReasonList>) skipOtpMainResponse.getResponse().getReasonList());
            } else {
                showSnackbar(skipOtpMainResponse.getDescription());
            }
        });

        shipmentOtpViewModel.getVerifyPickUpOtp().observe(this, verifyPickUpOtpResponse -> {
            if (verifyPickUpOtpResponse.isStatus()) {

                mobile_number_type = verifyPickUpOtpResponse.getResponse().getVerification_source();
                showSuccessSnackbar(verifyPickUpOtpResponse.getResponse().getDescription());
                activityOtpReasonCodeBinding.btVerify.setEnabled(true);
                activityOtpReasonCodeBinding.btVerify.setBackgroundResource(R.drawable.button_curved_selected_background);
                otp_verified = true;
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

        shipmentOtpViewModel.getSendPickUpOtp().observe(this, sendPickUpOtpResponse -> {
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


        shipmentOtpViewModel.loadManifest().observe(this, manifest_lists -> {
            try {
                if (manifest_lists != null) {
                    manifestNoArray = new ArrayList<>();

                    for (int i = 0; i < manifest_lists.size(); i++) {
                        if (manifest_lists.get(i).getManifest_details().getLocation_contact_no() == manifest_detail.getManifest_details().getLocation_contact_no()) {
                            manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Log.d("check_list", manifest_list1.toString());

    }

    private void DisableViaSkipOtp(boolean otp_verified, boolean reason_Selected) {
        if (otp_verified && reason_Selected) {
            activityOtpReasonCodeBinding.tvAlternate.setEnabled(false);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setEnabled(false);
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
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
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
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
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
        } else {

        }
    }

    private void DisableSkipOTP(boolean otp_verified, boolean came_from_otp) {
        if (otp_verified && came_from_otp) {
            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(false);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));
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
            activityOtpReasonCodeBinding.otpLayout.resendotpTV.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.text_color_dark_grey));

            activityOtpReasonCodeBinding.tvAlternate.setEnabled(true);
            activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.blue_ecom));

            activityOtpReasonCodeBinding.tvSkipOtp.setEnabled(true);
            activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.blue_ecom));

            activityOtpReasonCodeBinding.otpLayout.tvTimer.setVisibility(View.GONE);


        }


    }

    private void onClickListner() {
        activityOtpReasonCodeBinding.btVerify.setOnClickListener(this);
        activityOtpReasonCodeBinding.otpLayout.resendotpTV.setOnClickListener(this);
        activityOtpReasonCodeBinding.tvSkipOtp.setOnClickListener(this);
        activityOtpReasonCodeBinding.toolbar.ivBack.setOnClickListener(this);
        activityOtpReasonCodeBinding.tvAlternate.setOnClickListener(this);

    }


    @Override
    public ShipmentOtpViewModel getViewModel() {
        shipmentOtpViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(ShipmentOtpViewModel.class);
        return shipmentOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shipment_otp;
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
                activityOtpReasonCodeBinding.tvAlternate.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.blue_grey_light));
                activityOtpReasonCodeBinding.tvSkipOtp.setTextColor(ContextCompat.getColor(ShipmentOtpActivity.this, R.color.blue_grey_light));

            }
        };
        countDownTimer.start();
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
                    came_from_otp = true;
                    shipmentOtpViewModel.hitVerifyApi(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().toString(), manifest_detail.getManifest_details().getLocation_contact_no(), this, manifestNoArray, "");

                } else {
                    showToast(getString(R.string.check_internet));
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_verify:

                shipmentOtpViewModel.updateSharedManifestStatus(manifestNoArray, mobile_number_type, true);
                Intent intent = new Intent(this, SignatureActivity.class);
                intent.putExtra("is_global", false);
                intent.putExtra("manifest_count", "1");
                intent.putExtra("advance_shipment_count", advance_shipment_count);
                intent.putExtra("shipment_count", shipment_count);
                intent.putExtra("picked_count", picked_count);
                intent.putExtra("camera_visible", "true");
                intent.putExtra("unpicked_count", unpicked_count);
                intent.putExtra("manifestNo", manifestNo);
                intent.putExtra("RECCIQ", recci);
                intent.putExtra("pickup_location_id", pickup_location_id);
                intent.putExtra("remaining_count", remaining_count);
                intent.putExtra("manifest_type_signature", manifest_detail.getManifest_type());
                intent.putExtra("imageArrayList", "");
                intent.putExtra("coming_from_Otp", true);
                intent.putExtra("registrd_mobile", manifest_detail.getManifest_details().getLocation_contact_no());
                intent.putExtra("pop_enable", manifest_detail.getManifest_details().getLocation().isProof_of_pickup_enable());
                intent.putExtra("seller_name", manifest_detail.getManifest_details().getLocation_name());
                intent.putExtra("signature_pad_visible", manifest_detail.getManifest_details().isSignature_pad_visible());

                startActivity(intent);
                break;
            case R.id.resendotpTV:
                if (isNetworkConnected()) {
                    came_from_Alternate_mobile = false;
                    setTimer();
                    shipmentOtpViewModel.hitOtpApi(manifest_detail.getManifest_details().getLocation_contact_no(), manifest_detail.getOtp_pickup_location_id(), this, "R");
                } else {
                    showToast(getString(R.string.check_internet));
                }
                break;

            case R.id.tv_alternate:
                openAlternateNoBottomSheet();
                break;
            case R.id.tv_skip_otp:
                if (isNetworkConnected()) {
                    shipmentOtpViewModel.hitSkipOtpReasonListAPI(this);
                } else {
                    showToast(getString(R.string.check_internet));
                }

                break;
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.bt_submit:

                if (isNetworkConnected()) {
                    shipmentOtpViewModel.hitVerifyApi(activityOtpReasonCodeBinding.otpLayout.mHiddenEditText.getText().toString(), manifest_detail.getManifest_details().getLocation_contact_no(), this, manifestNoArray, reasonCode);
                    reason_Selected = true;
                    skipReasonDialog.dismiss();
                } else {
                    showToast(getString(R.string.check_internet));
                }
                break;
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
            skipOtpReasonListBinding.ReasonRecyclerView.setAdapter(new SkipOtpReasonCodeAdapter(reasonList, this));


        } catch (Exception e) {

        }
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

                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
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


            if (!alternatNoDialog.isShowing()) {
                alternatNoDialog.show();
            }

            alternateBottomsheetDialogBinding.btVerifyNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkConnected()) {
                        if (!alternateBottomsheetDialogBinding.etAlternateNo.getText().toString().equals("") && alternateBottomsheetDialogBinding.etAlternateNo.getText().toString().length() >= 10) {
                            alternatNoDialog.hide();
                            countDownTimers.cancel();
                            activityOtpReasonCodeBinding.otpLayout.OPT1ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT2ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT3ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT4ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT5ET.setEnabled(true);
                            activityOtpReasonCodeBinding.otpLayout.OPT6ET.setEnabled(true);

                            activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.VISIBLE);
                            came_from_Alternate_mobile = true;
                            shipmentOtpViewModel.hitOtpApi(Long.parseLong(alternateBottomsheetDialogBinding.etAlternateNo.getText().toString()), manifest_detail.getOtp_pickup_location_id(), ShipmentOtpActivity.this, "A");
                        } else {
                            Toast.makeText(ShipmentOtpActivity.this, R.string.enter_valid_mob_no, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        showToast(getString(R.string.check_internet));
                    }
                }
            });
            alternateBottomsheetDialogBinding.tvSkipVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkConnected()) {
                        activityOtpReasonCodeBinding.tvSkipOtp.setVisibility(View.VISIBLE);
                        shipmentOtpViewModel.hitSkipOtpReasonListAPI(ShipmentOtpActivity.this);
                    } else {
                        showToast(getString(R.string.check_internet));
                    }
                    alternatNoDialog.hide();

                }
            });

        } catch (Exception e) {

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

    @Override
    public void onBackPressed() {
        if (!otp_verified && !reason_Selected) {
            super.onBackPressed();
        } else {
            showSnackbar(getString(R.string.Cant_go_Back));
        }
    }
}

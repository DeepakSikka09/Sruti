package in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import in.ecomexpress.sruti.BuildConfig;
import in.ecomexpress.sruti.ui.login.changePassword.ChangePasswordActivity;
import in.ecomexpress.sruti.ui.login.login.LoginActivity;
import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityLoginVerifyOtpBinding;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;

import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.common_files.PermissionManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;



public class LoginVerifyOtpActivity extends BaseActivity<ActivityLoginVerifyOtpBinding, LoginVerifyOtpViewModel>
        implements ILoginVerifyOtpNavigator {
    public static final int REQUEST_CODE_FOR_SMS = 1;
    CounterClass otptimer;
    @Inject
    LoginVerifyOtpViewModel loginVerifyOtpViewModel;
    boolean flag = true;
    boolean manualflag = false;
    ActivityLoginVerifyOtpBinding activityLoginVerifyOtpBinding;
    boolean flagvalue, manualOtpFlag;
    private ChangePasswordActivity changePasswordActivity;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginVerifyOtpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginVerifyOtpViewModel.setNavigator(this);
        if (BuildConfig.DEBUG) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        flagvalue = getIntent().getBooleanExtra("flag", false);
        this.activityLoginVerifyOtpBinding = getViewDataBinding();
        activityLoginVerifyOtpBinding.timerTv.setText("00:10");
        otptimer = new CounterClass(10000, 1000);
        otptimer.start();
        activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.GONE);
        activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.VISIBLE);

        long clickTime = getIntent().getLongExtra("clickTime", 0);
        long openTime = System.currentTimeMillis();
        long duration = openTime - clickTime;
        logScreenOpenTime(duration);

    }

    private void logScreenOpenTime(long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", getScreenName());
        bundle.putLong("open_time", duration);
        FirebaseAnalytics.getInstance(this).logEvent("screen_open_time", bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new PermissionManager().check(this, android.Manifest.permission.RECEIVE_SMS, REQUEST_CODE_FOR_SMS);
    }

    @Override
    public LoginVerifyOtpViewModel getViewModel() {
        return loginVerifyOtpViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_verify_otp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_FOR_SMS) {//response for SMS permission request
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //What to do if User allowed SMS permission
            } else {
                //What to do if user disallowed requested SMS permission
            }
        }
    }

    @Override
    public void onResendOtp() {
        if (manualflag) {
            activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.VISIBLE);
            activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.GONE);
        }
        if (activityLoginVerifyOtpBinding.timerTv.getText().toString().equalsIgnoreCase("Click RESEND if OTP not received.")) {
            activityLoginVerifyOtpBinding.timerTv.setText("00:10");
            otptimer = new CounterClass(10000, 1000);
            otptimer.start();
            manualflag = true;
            Constants.OTP_DELIMITER = "OTP";
            loginVerifyOtpViewModel.resendOtp("none");
            showToast("Otp will be sent shortly..");

        } else {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.login_button);
            buttonDrawable.mutate();
            activityLoginVerifyOtpBinding.resendTv.setBackgroundDrawable(buttonDrawable);
            activityLoginVerifyOtpBinding.resendOtpTv.setBackgroundDrawable(buttonDrawable);

            loginVerifyOtpViewModel.resendOtp("none");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected String getScreenName() {
        return "Login Verify OTP Screen";
    }

    @Override
    public void onHandleError(String description) {
        showToast(description);
    }

    @Override
    public void onNext(String otpDelimiter) {
        if (otpDelimiter.length() == 6) {
            Constants.OTP_DELIMITER = "OTP";
            loginVerifyOtpViewModel.updateUserLoggedInState();
            Intent intent = new Intent(LoginVerifyOtpActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToast("OTP Sent Successfully");
        }
    }

    @Override
    public void onverify() {
        if (containsOnlyNumbers(activityLoginVerifyOtpBinding.otpEdt.getText().toString()) && activityLoginVerifyOtpBinding.otpEdt.getText().toString().length() == 6) {
            loginVerifyOtpViewModel.veryfyOtp(activityLoginVerifyOtpBinding.otpEdt.getText().toString());
            flag = false;
        } else {
            showToast("Enter Valid OTP");
        }
    }

    @Override
    public void logout() {
        hideKeyboard(LoginVerifyOtpActivity.this);
        showToast("Your session has expired. Please log in");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)
            showToast(getString(R.string.http_500_msg));
        else
            showToast(getString(R.string.server_down_msg));
    }

    @Override
    public void onBackClick() {
        onBackPressed();
    }

    @Override
    public void showException(Exception e) {
        showToast(e.getMessage());
    }


    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            activityLoginVerifyOtpBinding.enterOtpLayoutChild3.setVisibility(View.VISIBLE);
            activityLoginVerifyOtpBinding.enterOtpLayoutChild2.setVisibility(View.GONE);
        }


        @Override
        public void onTick(long millisUntilFinished) {

            long millis = millisUntilFinished;

            String hms = String.format(
                    "%02d:%02d",

                    TimeUnit.MILLISECONDS.toMinutes(millis)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(millis)));
            System.out.println(hms);

            activityLoginVerifyOtpBinding.timerTv.setTextColor(getResources().getColor(R.color.colorAccent1));
            activityLoginVerifyOtpBinding.timerTv.setText(hms);

            Drawable buttonDrawable = getResources().getDrawable(R.drawable.login_button);
            buttonDrawable.mutate();
            activityLoginVerifyOtpBinding.resendTv.setBackgroundDrawable(buttonDrawable);
            activityLoginVerifyOtpBinding.resendOtpTv.setBackgroundDrawable(buttonDrawable);

            if (Constants.OTP_DELIMITER.length() != 6) {

            } else {
                if (flag) {
                    if (containsOnlyNumbers(Constants.OTP_DELIMITER) && Constants.OTP_DELIMITER.length() == 6) {
                        loginVerifyOtpViewModel.veryfyOtp(Constants.OTP_DELIMITER);
                        flag = false;
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
}
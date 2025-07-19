package in.ecomexpress.sruti.ui.login.login;

import static in.ecomexpress.sruti.utils.common_files.Constants.ESPER_TOKEN;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.BuildConfig;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GPSTracker;
import in.ecomexpress.sruti.databinding.ActivityLoginBinding;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.ui.login.changePassword.ChangePasswordActivity;
import in.ecomexpress.sruti.ui.login.forget.ForgetActivity;
import in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen.LoginVerifyOtpActivity;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.MessageManager;
import in.ecomexpress.sruti.utils.common_files.Constants;
import io.esper.devicesdk.EsperDeviceSDK;

import com.shield.android.Shield;


public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements ILoginNavigator, HasSupportFragmentInjector {
    @Inject
    IPreferenceHelper iPreferenceHelper;
    private GPSTracker gps;
    public static LocationTracker lt;
    private Boolean esperSDKActivated;


    private Activity activity;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10101;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    LoginViewModel mLoginViewModel;
    ActivityLoginBinding mActivityLoginBinding;
    ChangePasswordActivity changePasswordActivity;
    EsperDeviceSDK sdk;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    public static LoginActivity globalActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        globalActivityContext = LoginActivity.this;
        super.onCreate(savedInstanceState);
        mActivityLoginBinding = getViewDataBinding();
        mLoginViewModel.setNavigator(this);

        sdk = EsperDeviceSDK.getInstance(getApplicationContext());
        activity = LoginActivity.this;


        sdk.activateSDK(ESPER_TOKEN, new EsperDeviceSDK.Callback<Void>() {
            @Override
            public void onResponse(Void response) {
                //Activation was successful
                Log.d("checkstatus", "ok");
                esperSDKActivated = true;
            }

            @Override
            public void onFailure(Throwable t) {
                // t.printStackTrace();
                Log.d("checkstatus", t.toString());
                esperSDKActivated = false;
            }
        });

        initEsperSDKActivationCheck();
        if (isNetworkConnected()) {
            if (BuildConfig.DEBUG) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
            setBottomText();
        } else {
            showToast(getString(R.string.check_internet));
        }
    }

    private void setBottomText() {
        String text = mLoginViewModel.getcopyRightText();
        if (text != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            } else {
                mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(text));
            }
        } else
            mActivityLoginBinding.tvCopyright.setText(Html.fromHtml(getString(R.string.copyright_txt)/*getResources().getString(R.string.copyright_txt)*/));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                mLoginViewModel.decideNextActivity();

                if (!checkPermission(Constants.permissions)) {
                    requestPermission();
                } else {
                    deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                }
            } else {
                deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                mLoginViewModel.decideNextActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String getScreenName() {
        return "Login Screen";
    }

    private void requestPermission() {
        requestPermissionsSafely(Constants.permissions, 100);
    }

    @Override
    public boolean hasPermission(String permission) {
        return super.hasPermission(permission);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 1);
                    }
                } else if (checkPermission(permissions)) {
                    deviceDetails.setDeviceId(CommonUtils.getImei(getApplicationContext()));
                    mLoginViewModel.decideNextActivity();
                } else {
                    showToast(getString(R.string.permission_required));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }

        }
        return true;
    }

    @Override
    public LoginViewModel getViewModel() {
        mLoginViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        return mLoginViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void enableLoginButton(boolean enable) {
        mActivityLoginBinding.btnServerLogin.setEnabled(enable);
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)
            showToast(getString(R.string.http_500_msg));
        else
            showToast(getString(R.string.server_down_msg));
    }

    @Override
    public void noAPKUpdate() {
        showToast(getString(R.string.download_apk));
    }

    @Override
    public void showNotifyChangePasswordAlert(LoginResponse response) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(response.getSResponse().getFlags().getReset_password().getNotify_password_reset_info())
                    .setTitle(getString(R.string.notify_pass_expired))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changePasswordActivity = ChangePasswordActivity.newInstance(mActivityLoginBinding.etEmail.getText().toString());
                                    changePasswordActivity.show(getSupportFragmentManager());
                                    changePasswordActivity.setOTPFlag(mLoginViewModel.isOTPRequiredTrue());
                                    changePasswordActivity.setChangePasswordListener(() -> {
                                        clearStack();
                                        finish();
                                    });
                                }
                            }
                    ).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    try {
                                        if (response.getSResponse().getFlags().getIs_otp_required()) {
                                            showVerifyOtp(response.getSResponse().getFlags().getReset_password().getIs_password_reset_required());
                                            return;
                                        }
                                        if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                                            showAPKForceUpdate(response.getSResponse().getApkUpdateResponse());
                                        } else if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 1) {

                                            if (!response.getSResponse().getFlags().getIs_otp_required()) {
                                                mLoginViewModel.getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                                            }
                                            showAPKSoftUpdate(response, response.getSResponse().getApkUpdateResponse());

                                        } else if (response.getSResponse().getFlags().getIs_otp_required()) {
                                            startLoginVeriyOTPActivity();
                                        } else {
                                            mLoginViewModel.getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                                            onSuccess();
                                        }
                                    } catch (Exception e) {
                                        showToast(e.getMessage());
                                        e.printStackTrace();
                                    }
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
    public void showException(Exception e) {
        showToast(e.getMessage());
    }


    @Override
    public void onCallITSupportClick(String ITExecutiveNo) {
        showToast("Under Development..");
    }

    private void startCallIntent(String ITExecutiveNo) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(ITExecutiveNo)));

        if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            //You already have permission
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.getMessage());
            }
        }
        startActivity(intent);
    }

    @Override
    public void onServerLogin() {
        hideKeyboard(LoginActivity.this);
        String empCode = mActivityLoginBinding.etEmail.getText().toString();
        if (LoginViewModel.isUsenameAndPasswordValid(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString())) {
            if (isNetworkConnected()) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("EMP_CODE", empCode);
                editor.apply();
                showLoading();
                mLoginViewModel.UnSyncCommitList(Constants.SHIPMENT_STATUS).observe(LoginActivity.this, pushApis -> {
                    if (pushApis.size() != 0) {
                        if (pushApis.get(0).getEmpId().equals(mActivityLoginBinding.etEmail.getText().toString())) {
                            inItLocationTracker(this);
                            mLoginViewModel.login(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
                        } else {
                            showAlertUserData("Locally committed data of Emp Code (" + pushApis.get(0).getEmpId() + ") will get deleted if you login.", LoginActivity.this);
                        }
                    } else {
                        inItLocationTracker(this);
                        mLoginViewModel.login(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
                    }
                });
            } else {
                showToast(getString(R.string.check_internet));
            }
        } else {
            if (mActivityLoginBinding.etEmail.getText().toString().isEmpty() || mActivityLoginBinding.etPassword.getText().toString().isEmpty()) {
                showToast(getString(R.string.login_error));
            }
        }
    }

    @Override
    public void onForgetPassword() {
        if (isNetworkConnected()) {

            ForgetActivity forgetActivity = ForgetActivity.newInstance(LoginActivity.this);
            forgetActivity.show(getSupportFragmentManager());
        } else {
            showToast(getString(R.string.check_internet));
        }
    }

    @Override
    public void openDashboardActivity() {
        mLoginViewModel.updateUserLoggedInState();
        logButtonClick("Open Dashboard");
        // Measure time to open a new screen
        long clickTime = System.currentTimeMillis();
        // Create intent for DashboardActivity and pass the click time
        Intent intent = DashboardActivity.getStartIntent(this);
        String email = mActivityLoginBinding.etEmail.getText().toString();
        logSignUpEvent(email);
        intent.putExtra("clickTime", clickTime);
        startActivity(intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onHandleError(String errorResponse) {
        showToast(errorResponse);
    }

    @Override
    public void onHandleErrorAPK() {
        MessageManager.showToast(LoginActivity.this, getString(R.string.please_check_internet));
    }


    @Override
    public void onSuccess() {


        callShieldCode();
        openDashboardActivity();
        String email = mActivityLoginBinding.etEmail.getText().toString();
        logSignUpEvent(email);
    }

    private void callShieldCode() {

        String sessionId = Shield.getInstance().getSessionId();
        HashMap<String, String> attributes = new HashMap<>();
//        attributes.put("sessionId", sessionId);
        attributes.put("user_id", mActivityLoginBinding.etEmail.getText().toString());
        Shield.getInstance().setDeviceResultStateListener(new Shield.DeviceResultStateListener() {
            @Override
            public void isReady() {
                Shield.getInstance().sendAttributes("Login", attributes);
            }
        });
    }


    @Override
    public void showAPKForceUpdate(LoginResponse.APKUpdateResponse apkUpdateResponse) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        String message = apkUpdateResponse.getApk_version_message() != null ? apkUpdateResponse.getApk_version_message() : "Please update your current application.";
        AlertDialog dialog = alertDialog.setMessage(message)
                .setTitle("App Update")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startDownload(apkUpdateResponse);
                            }
                        }

                ).create();


        dialog.setCancelable(false);
        dialog.show();
    }

    private void startDownload(LoginResponse.APKUpdateResponse apkUpdateResponse) {
        mLoginViewModel.downloadAPK(apkUpdateResponse.getApk_url(), sdk, LoginActivity.this, esperSDKActivated);

    }

    @Override
    public void installAPK(File file) {

        new AlertDialog.Builder(LoginActivity.this).setMessage("New Update Available. " + "Press Okay to Update Application.").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                    promptInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(promptInstall);
                } catch (Exception e) {
                    showToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        }).setCancelable(false).show();
    }

    @Override
    public void hideProgressView() {
        hideLoading();
    }


    @Override
    public void showAPKSoftUpdate(LoginResponse loginResponse, LoginResponse.APKUpdateResponse apkUpdateResponse) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        String message = apkUpdateResponse.getApk_version_message() != null ? apkUpdateResponse.getApk_version_message() : "Please update your current application.";
        AlertDialog dialog = alertDialog.setMessage(message)
                .setTitle("App Update")
                .setPositiveButton("Update", (dialogInterface, i) -> startDownload(apkUpdateResponse)

                )
                .setNegativeButton("Later",
                        ((dialogInterface, i) -> {
                            if (loginResponse.getSResponse().getFlags().getIs_otp_required()) {
                                startLoginVeriyOTPActivity();
                                return;
                            }
                            openDashboardActivity();
                        })
                ).create();
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * show a alert dialog to show that your password expired now your have to change your password
     */
    @Override
    public void showChangePassword() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        String message = getString(R.string.message_pass_expired);
        AlertDialog dialog = alertDialog.setMessage(message)
                .setTitle(getString(R.string.title_pass_expired))
                .setPositiveButton(getString(R.string.Continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changePasswordActivity = ChangePasswordActivity.newInstance(mActivityLoginBinding.etEmail.getText().toString());
                                changePasswordActivity.show(getSupportFragmentManager());
                                changePasswordActivity.isForcePassChange(true);
                                // changePasswordActivity.setUsername(mActivityLoginBinding.etEmail.getText().toString());
                                changePasswordActivity.setOTPFlag(mLoginViewModel.isOTPRequiredTrue());
                                changePasswordActivity.setChangePasswordListener(() -> {
                                    clearStack();
                                    finish();
                                });
                            }
                        }
                )
                .create();
        dialog.setCancelable(false);
        dialog.show();

    }

    public void clearStack() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void showVerifyOtp(boolean flag) {
        hideLoading();
        Intent intent = new Intent(LoginActivity.this, LoginVerifyOtpActivity.class);
        intent.putExtra("flag", flag);
        startActivity(intent);
    }

    @Override
    public void startLoginVeriyOTPActivity() {
        hideLoading();
        logButtonClick("Open Login Verify Activity");
        long clickTime = System.currentTimeMillis();
        Intent intent = LoginVerifyOtpActivity.getStartIntent(LoginActivity.this);
        String email = mActivityLoginBinding.etEmail.getText().toString();
        logSignUpEvent(email);
        intent.putExtra("clickTime", clickTime);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setMessage(R.string.exit);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finishAffinity();
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

    public void showAlertUserData(String message, Context mContext) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("COMMIT");
            builder.setMessage(message);
            builder.setCancelable(false);

            builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LocationTracker.deletetable();
                    mLoginViewModel.deleteForcefully();
                    mLoginViewModel.getDataManager().clearPopPrefrence();
                    inItLocationTracker(LoginActivity.this);
                    mLoginViewModel.login(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hideLoading();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    public void inItLocationTracker(Context context) {
        //do something
        gps = new GPSTracker(LoginActivity.this, (latitude, longitude) -> {
            if (latitude != 0.0 || longitude != 0.0) {
                iPreferenceHelper.setCurrentLatitude(String.valueOf(latitude));
                iPreferenceHelper.setCurrentLongitude(String.valueOf(longitude));
            }
        });

    }

    public void initEsperSDKActivationCheck() {
        // Check whether sdk is activated or not

        sdk.isActivated(new EsperDeviceSDK.Callback<Boolean>() {
            @Override
            public void onResponse(@Nullable Boolean isActive) {
                if (isActive) {
                    Log.d("TAG", "isEsperSDKActivated: SDK is activated");
                } else {
                    Log.d("TAG", "isEsperSDKActivated: SDK is not activated");
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("checkstatus", t.toString());
            }
        });

    }

    public void logSignUpEvent(String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "Sruti_Login_User");
        bundle.putString("user_id", userId);
        getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }


}


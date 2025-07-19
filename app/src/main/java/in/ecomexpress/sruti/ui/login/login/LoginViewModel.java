package in.ecomexpress.sruti.ui.login.login;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import in.ecomexpress.sruti.model.DeviceDetails;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.login.LoginRequest;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.repo.DataManager;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.common_files.UpdateAPKInstaller;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.esper.devicesdk.EsperDeviceSDK;
import io.reactivex.functions.Consumer;

public class LoginViewModel extends BaseViewModel<ILoginNavigator> {
  @Inject IPreferenceHelper iPreferenceHelper;
    private boolean OTPRequiredTrue;
    private ProgressDialog pd;
    public LoginViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void decideNextActivity() {
        try {
            if (getDataManager().getCurrentUserLoggedInMode()
                    == DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType()) {

            } else {
                getNavigator().openDashboardActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onHandleError(e.getMessage());
        }

    }

    public String getcopyRightText() {
        return getDataManager().getBottomText();
    }

    public void onServerLoginClick() {
        getNavigator().onServerLogin();

    }






    public void onForgetPasswordClick() {
        getNavigator().onForgetPassword();
    }

    public void onCallITSupportClick() {
        getNavigator().onCallITSupportClick(getDataManager().getCallITExecutiveNo());
    }

    public LiveData<List<PushApi>> UnSyncCommitList(int status) {
        return getDataManager().UnSyncCommitListAtLogin(status);
    }

    public void login(String email, String password, DeviceDetails deviceDetails) {
        if (getDataManager().isDownloadAPKIsInProcess() > 0) {
            getNavigator().onHandleError("An updated apk is in download. please wait for some time. ");
            return;
        }
        setIsLoading(true);
        final long timeStamp = System.currentTimeMillis();
        try {
            LoginRequest loginRequest = new LoginRequest(email, password, deviceDetails);
            deviceDetails.setLatitude(getDataManager().getCurrentLatitude());
            deviceDetails.setLongitude(getDataManager().getCurrentLongitude());

            getCompositeDisposable().add(getDataManager()
                    .doLoginApiCall(loginRequest)
                    .doOnSuccess(new Consumer<LoginResponse>() {
                        @Override
                        public void accept(LoginResponse loginResponse) {
                            Log.d(TAG, loginResponse.toString());
                            getNavigator().hideProgressView();
                            if (loginResponse.isStatus()) {
                                getNavigator().hideProgressView();
                                boolean isPasswordResetRequired = false;
                                boolean isOtpRequiredFlag = false;
                                if (loginResponse.getSResponse().getFlags().getIs_otp_required()) {
                                    isOtpRequiredFlag = true;
                                }
                                OTPRequiredTrue = isOtpRequiredFlag;
                                if (loginResponse.getSResponse().getFlags().getReset_password().getIs_password_reset_required()) {
                                    isPasswordResetRequired = true;
                                }
                                if (loginResponse.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                                    return;
                                }
                                LoginResponse.SResponse response = loginResponse.getSResponse();
                                IDataManager.LoggedInMode loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER;
                                if (isPasswordResetRequired) {
                                    loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
                                }
                                if (isOtpRequiredFlag) {
                                    loggedInMode = DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
                                }
                                LoginViewModel.this.getDataManager()
                                        .updateUserInfo(
                                                loggedInMode,
                                                response.getAuthToken(),
                                                response.getServiceCenter(),
                                                response.getServerTime(),
                                                response.getName(),
                                                response.getDesignation(),
                                                response.getMobile(),
                                                response.getFlags().getIs_ecom_vehicle(),
                                                response.getCode());
                                getDataManager().setRouteDetail(new Gson().toJson(response.getStart_route_details()));
                                getDataManager().isChild(response.getFlags().isIs_child());
                                getDataManager().is_child_available(response.getFlags().isIs_child_available());
                                getDataManager().isParent(response.getFlags().isIs_parent());
                                getDataManager().setRouteID(response.getRoute_id());
                                getDataManager().setDeparted(response.getFlags().isIs_departure());


                                if (response.getStart_route_details() != null && response.getStart_route_details().size() > 0) {
                                    getDataManager().setLiveTrackingId(response.getStart_route_details().get(0).getLive_tracking_id());
                                    getDataManager().setTripID(response.getStart_route_details().get(0).getStart_trip_id());
                                    getDataManager().setVehicleNo(response.getStart_route_details().get(0).getStart_vehicle_number());
                                }
                                if (!response.getFlags().getIs_ecom_vehicle() && response.getStart_route_details() != null && response.getStart_route_details().size() > 0 && response.getStart_route_details().get(0).isDeparted())
                                    getDataManager().setDepartSelfVehicle(true);

                                LoginResponse.scGeoLocation scGeoLocation = loginResponse.getSResponse().getScGeoLocation();
                                if (scGeoLocation != null) {
                                    getDataManager().updateDCDetails(scGeoLocation);
                                }
                            }
                        }


                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<LoginResponse>() {
                        @Override
                        public void accept(LoginResponse response) {
                            try {
                                Log.d(TAG, "login: " + response.toString());
                                LoginViewModel.this.setIsLoading(false);
                                getNavigator().hideProgressView();
                                //apk update functionality working fine already check dummy apk download and install
                                if (response.isStatus()) {

                                    try {
                                        TimeZone tz = TimeZone.getDefault();
                                        Log.d(TAG, "servertime123: " + tz);
                                        if (tz.getID().equalsIgnoreCase("Asia/Kolkata") || tz.getID().equalsIgnoreCase("Asia/Calcutta")) {
                                                String serverTimeinMilisecond1 = response.getSResponse().getServerTime();
                                                long serverTimeinMilisecond = Long.parseLong(serverTimeinMilisecond1);
                                                long currentSysTimeinMilisecond = System.currentTimeMillis();
                                                long timeDifference = currentSysTimeinMilisecond - serverTimeinMilisecond;
                                                long maxDifference = 120000; //2min in milisecond
                                                long minDifference = -120000;
                                                Log.d(TAG, "servertime123: " + serverTimeinMilisecond);
                                                Log.d(TAG, "servertime123: " + currentSysTimeinMilisecond);
                                                Log.d(TAG, "servertime123: " + timeDifference);

                                                if (timeDifference > maxDifference || timeDifference < minDifference) {
                                                    getNavigator().onHandleError("Server Time Mismatch!");
                                                    return;
                                                } else {
                                                    HashMap<String, String> hashMapUrl = new HashMap<String, String>();
                                                    if (response.getSResponse().getApiUrls().getLive_api_url() != null) {
                                                        hashMapUrl = response.getSResponse().getApiUrls().getLive_api_url();
                                                        getDataManager().setURL(new Gson().toJson(hashMapUrl));

                                                    }

                                                    if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 2) {
                                                        getNavigator().showAPKForceUpdate(response.getSResponse().getApkUpdateResponse());
                                                    } else if (response.getSResponse().getApkUpdateResponse().getVersion_status() == 1) {
                                                        if (!response.getSResponse().getFlags().getIs_otp_required()) {
                                                            getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                                                        }
                                                        getNavigator().showAPKSoftUpdate(response, response.getSResponse().getApkUpdateResponse());
                                                    } else if (response.getSResponse().getFlags().getReset_password().getIs_password_reset_required()) {
                                                        getNavigator().showChangePassword();
                                                        return;
                                                    } else {
                                                        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                                                        LoginViewModel.this.getNavigator().onSuccess();

                                                    }
                                                }


                                        } else  { getNavigator().onHandleError("Incorrect Timezone");}
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    //Error message comes in All Caps and this not good at all.
                                    if (response.getSResponse().getDescription().equalsIgnoreCase("Password Changed required")) {
                                        getNavigator().showChangePassword();
                                        return;
                                    } else {
                                        LoginViewModel.this.getNavigator().onHandleError(response.getSResponse().getDescription());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                getNavigator().onHandleError(e.getMessage());
                            }
                        }
                    }, throwable ->

                    {
                        System.out.println("throwable  " + throwable.getMessage());
                        setIsLoading(false);
                        getNavigator().hideProgressView();
                        String error;
                        try {
//                            writeErrors(timeStamp, new Exception(throwable));
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showErrorMessage(true);
                            } else {
//                                getNavigator().startLoginVeriyOTPActivity();
                                getNavigator().showErrorMessage(false);
                            }
                        } catch (Exception e) {
                            getNavigator().showException(e);
                            e.printStackTrace();
                            getNavigator().hideProgressView();

                        }
                    }));
        } catch (Exception e) {
            getNavigator().showException(e);
            getNavigator().hideProgressView();
//            writeErrors(timeStamp, e);
//            Log.e(TAG, e.getCause().getStackTrace().toString());
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
                getNavigator().hideProgressView();
            }
        }
    }
    public void downloadAPK(String url, EsperDeviceSDK sdk, Context context, Boolean esperSDKActivated) {

        if (url != null) {
            pd = new ProgressDialog(context);
            UpdateAPKInstaller downloadAndInstall = new UpdateAPKInstaller();
            pd.setCancelable(false);
            pd.setMessage("Downloading APK File....");
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            downloadAndInstall.setContext(context, pd,sdk,esperSDKActivated);
            downloadAndInstall.execute(url.trim());
        } else {
            getNavigator().noAPKUpdate();
        }
    }

    public static boolean isUsenameAndPasswordValid(String username, String password) {
        //validate username and password
        if (username == null || username.isEmpty()) {
            return false;
        }

        return !(password == null || password.isEmpty());
    }

    public void deleteForcefully() {
        getDataManager().deleteForcefully();
    }


    public boolean isOTPRequiredTrue() {
        return OTPRequiredTrue;
    }


    public void updateUserLoggedInState() {
        getDataManager().updateUserLoggedInState(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
    }
}

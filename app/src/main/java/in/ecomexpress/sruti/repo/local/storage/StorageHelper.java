package in.ecomexpress.sruti.repo.local.storage;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sruti.model.Departure.Departure_Response;
import in.ecomexpress.sruti.model.Departure.Depature_Request;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestRequest;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestResponse;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpRequest;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpResponse;
import in.ecomexpress.sruti.model.OtpRequest.VerifyPickUpOtpRequest;
import in.ecomexpress.sruti.model.OtpRequest.VerifyPickUpOtpResponse;
import in.ecomexpress.sruti.model.RtoLockResponse;
import in.ecomexpress.sruti.model.RtoRequest;
import in.ecomexpress.sruti.model.ScanRequest;
import in.ecomexpress.sruti.model.ScanResponse;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPResponse;
import in.ecomexpress.sruti.model.attendance.AttendanceRequest;
import in.ecomexpress.sruti.model.attendance.AttendanceResponse;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitRequest;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitResponse;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.CommitResponse;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.fuel.FuelReimbursementRequest;
import in.ecomexpress.sruti.model.fuel.response.FuelReimbursementResponse;
import in.ecomexpress.sruti.model.handoverdata.HandOverRequest;
import in.ecomexpress.sruti.model.handoverdata.HandOverResponse;
import in.ecomexpress.sruti.model.login.ChangePasswordRequest;
import in.ecomexpress.sruti.model.login.ForgetPasswordUserRequest;
import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;
import in.ecomexpress.sruti.model.login.LoginRequest;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.login.LogoutRequest;
import in.ecomexpress.sruti.model.login.LogoutResponse;
import in.ecomexpress.sruti.model.login.OTPVerifyWithPasswordRequest;
import in.ecomexpress.sruti.model.masterdata.Master_Data_Response;
import in.ecomexpress.sruti.model.masterdata.SkipOtpMainResponse;
import in.ecomexpress.sruti.model.masterdata.User_Data;
import in.ecomexpress.sruti.model.menifestdata.Menifest_Data_Master;
import in.ecomexpress.sruti.model.performance.PerformanceRequest;
import in.ecomexpress.sruti.model.performance.PerformanceResponse;
import in.ecomexpress.sruti.model.resendOtp.LoginResendOtpRequest;
import in.ecomexpress.sruti.model.selfDrop.SelfDropRequest;
import in.ecomexpress.sruti.model.selfDrop.SelfDropResponse;
import in.ecomexpress.sruti.model.signature.ProofOfPickupRequest;
import in.ecomexpress.sruti.model.signature.ProofOfPickupResponse;
import in.ecomexpress.sruti.model.sos.SOSRequest;
import in.ecomexpress.sruti.model.sos.SOSResponse;
import in.ecomexpress.sruti.model.starttrip.ImageUplaodResponse;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.starttrip.StartTripResponse;
import in.ecomexpress.sruti.model.stoptrip.StopTrip;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpResponse;
import in.ecomexpress.sruti.repo.remote.IRestApiHelper;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingRequest;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingResponse;
import in.ecomexpress.sruti.utils.CommonUtils;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Singleton
public class StorageHelper implements IStorageHelper, IRestApiHelper {
    Context mContext;

    @Inject
    public StorageHelper(Context context) {
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException {
        String strbuffer = CommonUtils.loadJSONFromAsset(mContext, "login_response.json");
        LoginResponse loginResponse = new ObjectMapper().readValue(strbuffer, LoginResponse.class);
        return Single.just(loginResponse);
    }

    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, ChangePasswordRequest changePasswordRequest) {
        try {
            ForgotPasswordResponse loginResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(loginResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(ForgetPasswordUserRequest forgetPasswordUserRequest) {
        try {
            ForgotPasswordResponse forgotPasswordResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(forgotPasswordResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        try {
            ForgotPasswordResponse forgotPasswordResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "forgot_password_response.json"), ForgotPasswordResponse.class);
            return Single.just(forgotPasswordResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, LoginResendOtpRequest loginVerifyOtpRequest) {
        try {
            LoginVerifyOtpResponse loginVerifyOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "common_error_response.json"), LoginVerifyOtpResponse.class);
            return Single.just(loginVerifyOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        try {
            LoginVerifyOtpResponse loginVerifyOtpResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "common_error_response.json"), LoginVerifyOtpResponse.class);
            return Single.just(loginVerifyOtpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String token, FuelReimbursementRequest fuelReimbursementRequest) {
        try {
            FuelReimbursementResponse fuelReimbursementResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "fuel.json"), FuelReimbursementResponse.class);
            return Single.just(fuelReimbursementResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, SOSRequest sosRequest) {
        try {
            SOSResponse sosResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "sosresponse.json"), SOSResponse.class);
            return Single.just(sosResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<StartTripResponse> doStartTrip(String authToken, StartTripRequest startTripRequest) {
        try {
            MutableLiveData<StartTripResponse> mutableLiveData = new MutableLiveData<>();

            StartTripResponse sosResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "startTripResponse.json"), StartTripResponse.class);
            mutableLiveData.setValue(sosResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<ImageUplaodResponse> uploadStartTripImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        try {
            MutableLiveData<ImageUplaodResponse> mutableLiveData = new MutableLiveData<>();
            ImageUplaodResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "startTripImageResponse.json"), ImageUplaodResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<ImageUplaodResponse> uploadCommitImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        try {
            MutableLiveData<ImageUplaodResponse> mutableLiveData = new MutableLiveData<>();
            ImageUplaodResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "startTripImageResponse.json"), ImageUplaodResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<StopTrip> doStopTrip(String authToken, StopTripRequest stopTripRequest) {
        try {
            MutableLiveData<StopTrip> stopTripMutableLiveData = new MutableLiveData<>();
            StopTrip imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "stopTripResponse.json"), StopTrip.class);
            stopTripMutableLiveData.setValue(imageUplaodResponse);
            return stopTripMutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<Master_Data_Response> doToDoList(String authToken, User_Data username) {
        try {
            MutableLiveData<Master_Data_Response> loc = new MutableLiveData<>();
            Master_Data_Response imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "masterdata.json"), Master_Data_Response.class);
            loc.setValue(imageUplaodResponse);
            return loc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LiveData<AttendanceResponse> doAttendanceData(String authToken, AttendanceRequest stopTripRequest) {
        try {
            MutableLiveData<AttendanceResponse> mutableLiveData = new MutableLiveData<>();
            AttendanceResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "attendance_response.json"), AttendanceResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Single<ScanResponse> callScanApi(String authToken, ScanRequest scanRequest) throws IOException {
        String strbuffer = CommonUtils.loadJSONFromAsset(mContext, "scan_response.json");
        ScanResponse loginResponse = new ObjectMapper().readValue(strbuffer, ScanResponse.class);
        return Single.just(loginResponse);
    }

    @Override
    public LiveData<HandOverResponse> getHandOverData(String authToken, HandOverRequest handOverRequest) {
        try {
            MutableLiveData<HandOverResponse> mutableLiveData = new MutableLiveData<>();
            HandOverResponse handOverResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "handover_response.json"), HandOverResponse.class);
            mutableLiveData.setValue(handOverResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<Menifest_Data_Master> getManifestDetail(String authToken, int roid) {
        try {
            MutableLiveData<Menifest_Data_Master> mutableLiveData = new MutableLiveData<>();
            Menifest_Data_Master commitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "manifest_detail.json"), Menifest_Data_Master.class);
            mutableLiveData.setValue(commitResponse);
            return mutableLiveData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public LiveData<CommitResponse> doCommitApiCall(String authToken, CommitPacketData commit) {
        try {
            MutableLiveData<CommitResponse> mutableLiveData = new MutableLiveData<>();
            CommitResponse commitResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "commit_response.json"), CommitResponse.class);
            mutableLiveData.setValue(commitResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest manifest_no) {
        try {
            MutableLiveData<RtoLockResponse> mutableLiveData = new MutableLiveData<>();
            RtoLockResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "rto_lock.json"), RtoLockResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest) {
        try{
            MutableLiveData<UpdatedBPResponse> mutableLiveData = new MutableLiveData<>();
            UpdatedBPResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "rto_lock.json"), UpdatedBPResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        try {
            MutableLiveData<FirstScanResponse> mutableLiveData = new MutableLiveData<>();
            FirstScanResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "first_scan.json"), FirstScanResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest) {
        try {
            MutableLiveData<ChildCommitResponse> mutableLiveData = new MutableLiveData<>();
            ChildCommitResponse imageUplaodResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "child_commit_status.json"), ChildCommitResponse.class);
            mutableLiveData.setValue(imageUplaodResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<Departure_Response> departuredTimeUpdate(String authToken, Depature_Request depature_request) {
        return null;
    }

    @Override
    public LiveData<LogoutResponse> callAuthToken(String authToken) {
        return null;
    }

    @Override
    public LiveData<List<LoginResponse.StartRouteDetails>> fetchStartedVehicle(String authToken, int route_id, String empcode) {
        return null;
    }

    @Override
    public LiveData<SelfDropResponse> selfDrop(String authToken, SelfDropRequest selfDropRequest) {
        try {
            MutableLiveData<SelfDropResponse> mutableLiveData = new MutableLiveData<>();
            SelfDropResponse logoutResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "self_drop.json"), SelfDropResponse.class);
            mutableLiveData.setValue(logoutResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiveData<SendPickUpOtpResponse> sendPickupOtp(String authToken, SendPickUpOtpRequest sendPickUpOtpRequest) {
        return null;
    }

    @Override
    public LiveData<SkipOtpMainResponse> sendReasonList(String authToken) {
        return null;
    }
    @Override
    public LiveData<VerifyPickUpOtpResponse> verifyPickupOtp(String authToken, VerifyPickUpOtpRequest verifyPickUpOtpRequest) {
        return null;
    }

    @Override
    public LiveData<OtpVerificationManifestResponse> otpVerificationManifest(String authToken, OtpVerificationManifestRequest otpVerificationManifestRequest) {
        return null;
    }


    @Override
    public LiveData<ProofOfPickupResponse> SendProofOfPickup(String authToken, ProofOfPickupRequest proofOfPickupRequest) {
        return null;
    }

    @Override
    public LiveData<TrainingResponse> TrainingVideos(String authToken, TrainingRequest trainingRequest) {
        return null;
    }

    @Override
    public LiveData<LogoutResponse> doLogout(String authToken, LogoutRequest logoutRequest) {
        try {
            MutableLiveData<LogoutResponse> mutableLiveData = new MutableLiveData<>();
            LogoutResponse logoutResponse = new ObjectMapper().readValue(CommonUtils.loadJSONFromAsset(mContext, "logout.json"), LogoutResponse.class);
            mutableLiveData.setValue(logoutResponse);
            return mutableLiveData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    // Not used in app
    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, PerformanceRequest request) {
        return null;
    }
}


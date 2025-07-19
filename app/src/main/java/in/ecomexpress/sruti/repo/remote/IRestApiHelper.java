package in.ecomexpress.sruti.repo.remote;


import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import in.ecomexpress.sruti.ui.dashboard.training.TrainingRequest;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingResponse;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface IRestApiHelper {
    // Using reactivex
    Single<ForgotPasswordResponse> doResetPasswordApiCall(String authToken, ChangePasswordRequest changePasswordRequest);

    Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException;

    Single<ForgotPasswordResponse> doForgetPasswordApiCall(ForgetPasswordUserRequest forgetPasswordUserRequest);

    Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest);

    Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, LoginResendOtpRequest loginVerifyOtpRequest);

    Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, LoginVerifyOtpRequest loginVerifyOtpRequest);

    Single<FuelReimbursementResponse> doFuelListApiCall(String token, FuelReimbursementRequest fuelReimbursementRequest);

    Single<PerformanceResponse> doPerformanceApiCall(String authToken, PerformanceRequest request);

    Single<SOSResponse> doSOSApiCall(String authToken, SOSRequest sosRequest);

    Single<ScanResponse> callScanApi(String authToken, ScanRequest scanRequest) throws IOException;

    // Using LiveData
    LiveData<StartTripResponse> doStartTrip(String authToken, StartTripRequest startTripRequest);


    LiveData<ImageUplaodResponse> uploadStartTripImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody);

    LiveData<ImageUplaodResponse> uploadCommitImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody);

    LiveData<StopTrip> doStopTrip(String authToken, StopTripRequest stopTripRequest);

    LiveData<Master_Data_Response> doToDoList(String authToken, User_Data username);

    LiveData<AttendanceResponse> doAttendanceData(String authToken, AttendanceRequest stopTripRequest);

    LiveData<LogoutResponse> doLogout(String authToken, LogoutRequest logoutRequest);

    LiveData<HandOverResponse> getHandOverData(String authToken, HandOverRequest handOverRequest);

    LiveData<Menifest_Data_Master> getManifestDetail(String authToken, int pickupRouteId);


    LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest rtoRequest);

    LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest);

    LiveData<CommitResponse> doCommitApiCall(String authToken, CommitPacketData commit);

    LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse);

    LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest);

    LiveData<Departure_Response> departuredTimeUpdate(String authToken, Depature_Request depature_request);

    LiveData<LogoutResponse> callAuthToken(String authToken);

    LiveData<List<LoginResponse.StartRouteDetails>> fetchStartedVehicle(String authToken, int route_id, String empcode);

    LiveData<SelfDropResponse> selfDrop(String authToken, SelfDropRequest selfDropRequest);

    LiveData<SendPickUpOtpResponse>sendPickupOtp(String authToken, SendPickUpOtpRequest sendPickUpOtpRequest);
    LiveData<SkipOtpMainResponse>sendReasonList(String authToken);
    LiveData<VerifyPickUpOtpResponse>verifyPickupOtp(String authToken, VerifyPickUpOtpRequest verifyPickUpOtpRequest);
    LiveData<OtpVerificationManifestResponse>otpVerificationManifest(String authToken, OtpVerificationManifestRequest otpVerificationManifestRequest);
    LiveData<ProofOfPickupResponse>SendProofOfPickup(String authToken, ProofOfPickupRequest proofOfPickupRequest);
    LiveData<TrainingResponse>TrainingVideos(String authToken, TrainingRequest trainingRequest);

}

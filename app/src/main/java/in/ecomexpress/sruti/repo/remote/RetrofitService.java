package in.ecomexpress.sruti.repo.remote;

import java.util.Map;

import in.ecomexpress.sruti.model.Departure.Departure_Response;
import in.ecomexpress.sruti.model.Departure.Depature_Request;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestResponse;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestRequest;
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
import in.ecomexpress.sruti.model.starttrip.Extra_Vehicle;
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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitService {
    //Login Api
    @POST("login/")
    Single<LoginResponse> doLoginApiCall(@Body LoginRequest request);

    //Login Verify OTP Api
    @POST("loginVerifyOTP/")
    Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(@Header("auth_token") String token, @Body LoginVerifyOtpRequest loginVerifyOtpRequest);

    //Login Resend OTP Api
    @POST("resend-otp/")
    Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(@Header("auth_token") String token, @Body LoginResendOtpRequest loginVerifyOtpRequest);

    //Login Reset password Api
    @POST("change-password/")
    Single<ForgotPasswordResponse> doResetPasswordApiCall(@Header("auth_token") String token, @Body ChangePasswordRequest changePasswordRequest);

    //Login Forgot password Api
    @POST("forgot-password/")
    Single<ForgotPasswordResponse> doForgetPasswordApiCall(@Body ForgetPasswordUserRequest forgetPasswordUserRequest);


    //Login Forgot password with otp Api
    @POST("forgot-password-with-otp/")
    Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(@Body OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest);


    @POST
    Single<FuelReimbursementResponse> doFuelListApiCall(@Url String fuel_url, @Header("auth_token") String token, @Body FuelReimbursementRequest fuelReimbursementRequest);

    @POST
    Single<PerformanceResponse> doPerformanceApiCall(@Url String performance_url, @Header("auth_token") String token, @Body PerformanceRequest performanceRequest);

    @POST
    Single<SOSResponse> doSOSApiCall(@Url String sos_url, @Header("auth_token") String authToken, @Body SOSRequest sosRequest);

    @POST
    Call<StartTripResponse> doStartTrip(@Url String startTrip, @Header("auth_token") String authToken, @Body StartTripRequest startTripRequest);

    @Multipart
    @POST
    Call<ImageUplaodResponse> uploadStartTripImage(@Url String image_upload, @Part MultipartBody.Part imageFile, @PartMap Map<String, RequestBody> requestBody);

    @Multipart
    @POST
    Call<ImageUplaodResponse> uploadCommitImage(@Url String image_upload, @Part MultipartBody.Part imageFile, @PartMap Map<String, RequestBody> requestBody);

    @POST
    Call<StopTrip> doStopTrip(@Url String stoptrip, @Header("auth_token") String authToken, @Body StopTripRequest stopTripRequest);

    @POST
    Call<CommitResponse> doCommitApiCall(@Url String commitApi, @Header("auth_token") String authToken, @Body CommitPacketData commitPacketData);


    @POST
    Call<AttendanceResponse> doAttendanceData(@Url String attendance_url, @Header("auth_token") String authToken, @Body AttendanceRequest stopTripRequest);

    @POST
    Call<HandOverResponse> getHandOverData(@Url String handover_url, @Header("auth_token") String authToken, @Body HandOverRequest handOverRequest);

    @POST
    Call<LogoutResponse> doLogoutRequest(@Url String logout_url, @Header("auth_token") String authToken, @Body LogoutRequest logoutRequest);

    @POST
    Call<Master_Data_Response> doToDoList(@Url String master_data, @Header("auth_token") String authToken, @Body User_Data username);

    @GET
    Call<Menifest_Data_Master> getManifestDetail(@Url String manifest, @Header("auth_token") String authToken, @Query("is_with_mps") boolean isMps);//, @Query("pickupRouteId") int pickupRouteId


    @PUT
    Single<ScanResponse> scanApicall(@Url String scanApi, @Header("auth_token") String authToken, @Body ScanRequest scanRequest);

    @POST
    Call<RtoLockResponse> getAllRTOShipmentList(@Url String rtoLockApi, @Header("auth_token") String authToken, @Body RtoRequest rtoRequest);


    @POST
    Call<ChildCommitResponse> checkChildStatusApi(@Url String childStatusApi, @Header("auth_token") String authToken, @Body ChildCommitRequest childCommitRequest);


    @POST
    Call<FirstScanResponse> callFirstScanApi(@Url String rtoLockApi, @Header("auth_token") String authToken, @Body FirstScanRequest firstScanRequest);


    @PUT
    Call<Departure_Response> departuredTimeUpdate(@Url String departurl, @Header("auth_token") String authToken, @Body Depature_Request depature_request);


    @POST
    Call<LogoutResponse> callAuthToken(@Url String rtoLockApi, @Header("auth_token") String authToken);

    @GET
    Call<Extra_Vehicle> fetchtartedVehicle(@Url String rtoLockApi, @Header("auth_token") String authToken, @Query("pickupRouteId") int routeid, @Query("employeeCode") String empcode);


    @GET
    Call<SkipOtpMainResponse> sendReasonList(@Url String url, @Header("auth_token") String authToken);//, @Query("pickupRouteId") int pickupRouteId


    @POST
    Call<SelfDropResponse> fetchSelfDropManifest(@Url String selfDropUrl, @Header("auth_token") String authToken, @Body SelfDropRequest selfDropRequest);

    @POST
    Call<SendPickUpOtpResponse> sendPickupOtp(@Url String sendPickupOtpUrl, @Header("auth_token") String authToken, @Body SendPickUpOtpRequest sendPickUpOtpRequest);

    @POST
    Call<VerifyPickUpOtpResponse> verifyPickupOtp(@Url String verifyPickupOtp, @Header("auth_token") String authToken, @Body VerifyPickUpOtpRequest verifyPickUpOtpRequest);


    @POST
    Call<OtpVerificationManifestResponse> otpVerificationManifest(@Url String otpVerificationManifest, @Header("auth_token") String authToken, @Body OtpVerificationManifestRequest otpVerificationManifestRequest);

    @POST
    Call<ProofOfPickupResponse> proofofpickup(@Url String proofofpickup, @Header("auth_token") String authToken, @Body ProofOfPickupRequest proofOfPickupRequest);

    @POST()
    Call<UpdatedBPResponse> updatedBp(@Url String rtoLockApi, @Header("auth_token") String authToken, @Body UpdatedBPRequest updatedBPRequest);

    @GET
    Call<TrainingResponse> training(@Url String TrainingAPi, @Header("auth_token") String token);



}

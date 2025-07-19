package in.ecomexpress.sruti.repo;


import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sruti.model.DashboardBanner;
import in.ecomexpress.sruti.model.Departure.CountManifest;
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
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.fuel.FuelReimbursementRequest;
import in.ecomexpress.sruti.model.fuel.response.FuelReimbursementResponse;
import in.ecomexpress.sruti.model.handoverdata.HandOverRequest;
import in.ecomexpress.sruti.model.handoverdata.HandOverResponse;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.login.ChangePasswordRequest;
import in.ecomexpress.sruti.model.login.ForgetPasswordUserRequest;
import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;
import in.ecomexpress.sruti.model.login.LoginRequest;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.login.LogoutRequest;
import in.ecomexpress.sruti.model.login.LogoutResponse;
import in.ecomexpress.sruti.model.login.OTPVerifyWithPasswordRequest;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.masterdata.Master_Data_Response;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.masterdata.SkipOtpMainResponse;
import in.ecomexpress.sruti.model.masterdata.User_Data;
import in.ecomexpress.sruti.model.menifestdata.Flags;
import in.ecomexpress.sruti.model.menifestdata.ManifestAndShipment;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Menifest_Data_Master;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.performance.PerformanceRequest;
import in.ecomexpress.sruti.model.performance.PerformanceResponse;
import in.ecomexpress.sruti.model.popData.PopData;
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
import in.ecomexpress.sruti.repo.local.db.IDBHelper;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.repo.local.db.prefs.IPopPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.local.storage.IStorageHelper;
import in.ecomexpress.sruti.repo.remote.IRestApiHelper;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingRequest;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingResponse;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Singleton
public class DataManager implements IDataManager {
    private final Context mContext;
    private final IDBHelper mDbHelper;
    private final IPreferenceHelper mPreferencesHelper;
    private final IPopPreferenceHelper mpopPreferencesHelper;
    private final IRestApiHelper mApiHelper;
    private final IStorageHelper mFileHelper;

    @Inject
    public DataManager(Context mContext,
                       IDBHelper mDbHelper,
                       IPreferenceHelper mPreferencesHelper,
                       IPopPreferenceHelper mpopPreferencesHelper,
                       IRestApiHelper mApiHelper,
                       IStorageHelper mFileHelper) {
        this.mContext = mContext;
        this.mDbHelper = mDbHelper;
        this.mPreferencesHelper = mPreferencesHelper;
        this.mpopPreferencesHelper = mpopPreferencesHelper;
        this.mApiHelper = mApiHelper;
        this.mFileHelper = mFileHelper;
    }


    @Override
    public void updateUserInfo(LoggedInMode loggedInMode,
                               String authToken,
                               String serviceCenter,
                               String ServerTime,
                               String name,
                               String designation,
                               String mobile,
                               boolean is_ecom_vehicle,
                               String code) {

        setCurrentUserLoggedInMode(loggedInMode);
        setAuthToken(authToken);
        setServerTime(ServerTime);
        setServiceCenter(serviceCenter);
        setName(name);
        setDesignation(designation);
        setMobile(mobile);
        setCode(code);
        setecom_vehicle(is_ecom_vehicle);
    }

    @Override
    public Observable<Boolean> deleteAllTables() {
        return mDbHelper.deleteAllTables();
    }


    @Override
    public Observable<List<Shipment_Detail>> ifAWBexists(long manifestNumber, long awbNo, String status) {
        return mDbHelper.ifAWBexists(manifestNumber, awbNo, status);
    }


    @Override
    public LiveData<Shipment_Detail> ifAWBPresent(long manifestNumber, long awbNo, String status) {
        return mDbHelper.ifAWBPresent(manifestNumber, awbNo, status);
    }

    @Override
    public Observable<Boolean> ifAWBPresentNew(long manifestNumber, long awbNo, String status) {
        return mDbHelper.ifAWBPresentNew(manifestNumber, awbNo, status);
    }


    @Override
    public Observable<Long> isFirstScan(long manifestNumber) {
        return mDbHelper.isFirstScan(manifestNumber);
    }

    @Override
    public Observable<Manifest_List> getSingleManifestDetail(long manifestNumber) {
        return mDbHelper.getSingleManifestDetail(manifestNumber);
    }

    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, ChangePasswordRequest changePasswordRequest) {
        return mApiHelper.doResetPasswordApiCall(token, changePasswordRequest);
    }

    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) throws IOException {
        return mApiHelper.doLoginApiCall(request);
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(ForgetPasswordUserRequest forgetPasswordUserRequest) {
        return mApiHelper.doForgetPasswordApiCall(forgetPasswordUserRequest);
    }

    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        return mApiHelper.doOTPVerifyWithPasswordApiCall(otpVerifyWithPasswordRequest);
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, LoginResendOtpRequest loginVerifyOtpRequest) {
        return mApiHelper.doLoginResendOtpApiCall(authToken, loginVerifyOtpRequest);
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return mApiHelper.doLoginVerifyOtpApiCall(authToken, loginVerifyOtpRequest);
    }


    @Override
    public void insertToDoList(List<ReasonCodeList> reasonCodeLists, List<in.ecomexpress.sruti.model.masterdata.Post_option> post_options) {
        mDbHelper.insertToDoList(reasonCodeLists, post_options);
    }

    @Override
    public void updateShipmentCount(long manifestNo, String shipmentCount) {
        mDbHelper.updateShipmentCount(manifestNo, shipmentCount);
    }


    @Override
    public LiveData<List<ReasonCodeList>> getPickupList(String differenceState) {
        return mDbHelper.getPickupList(differenceState);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getAllScanAWBlist(String status, Long manifest_No) {
        return mDbHelper.getAllScanAWBlist(status, manifest_No);
    }
    @Override
    public LiveData<List<Shipment_Detail>> getAllScanAWBlistTemp(List<String> status, Long manifest_No) {
        return mDbHelper.getAllScanAWBlistTemp(status, manifest_No);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlist(String status, int commit_status) {
        return mDbHelper.getAllGlobalScanAWBlist(status, commit_status);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlistTemp(List<String> Status, int commit_status,long tempManifestNo) {
        return mDbHelper.getAllGlobalScanAWBlistTemp(Status, commit_status,tempManifestNo);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getTempGlobalScanAWBlistTemp(String Status, int commit_status,long tempManifestNo) {
        return mDbHelper.getTempGlobalScanAWBlistTemp(Status, commit_status,tempManifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllShipmentlist(long manifest) {
        return mDbHelper.getAllShipmentlist(manifest);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getMpsCondition(long manifestNo) {
        return mDbHelper.getMpsCondition(manifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getMpsConditionUsingGlobalScan(ArrayList<Long> manifestCollection) {
        return mDbHelper.getMpsConditionUsingGlobalScan(manifestCollection);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllParentShipmentlist(long manifest_no) {
        return mDbHelper.getAllParentShipmentlist(manifest_no);
    }

    @Override
    public LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifestNo) {
        return mDbHelper.getSpecificManifestDetail(manifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllScannedShipmentList(long manifest_No, List<String> status) {
        return mDbHelper.getAllScannedShipmentList(manifest_No, status);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getCommitAPICall(String vehcileId) {
        return mDbHelper.getCommitAPICall(vehcileId);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getScannedShipmentList(String picked) {
        return mDbHelper.getScannedShipmentList(picked);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getTotalShipmentList(List<Long> manifest_no) {
        return mDbHelper.getTotalShipmentList(manifest_no);
    }


    @Override
    public void setRouteName(String routeName) {
        mPreferencesHelper.setRouteName(routeName);
    }

    @Override
    public void setVodaOrderNo(String orderNo) {
        mPreferencesHelper.setVodaOrderNo(orderNo);
    }

    @Override
    public String getVehicleNo() {
        return mPreferencesHelper.getVehicleNo();
    }

    @Override
    public void setVehicleNo(String vehicleNo) {
        mPreferencesHelper.setVehicleNo(vehicleNo);
    }

    @Override
    public void setSelfVehicleType(String type) {
        mPreferencesHelper.setSelfVehicleType(type);
    }

    @Override
    public String getSelfVehicleType() {
        return mPreferencesHelper.getSelfVehicleType();
    }

    @Override
    public void setDepartSelfVehicle(boolean departSelfVehicle) {
        mPreferencesHelper.setDepartSelfVehicle(departSelfVehicle);
    }

    @Override
    public boolean getDepartSelfVehicle() {
        return mPreferencesHelper.getDepartSelfVehicle();
    }

    @Override
    public String getAwbCount() {
        return mPreferencesHelper.getAwbCount();
    }

    @Override
    public void setAwbCount(String awbCount) {
        mPreferencesHelper.setAwbCount(awbCount);
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode currentUserLoggedInMode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(currentUserLoggedInMode);
    }

    @Override
    public String getMobile() {
        return mPreferencesHelper.getMobile();
    }

    @Override
    public void setMobile(String mobile) {

        mPreferencesHelper.setMobile(mobile);

    }

    @Override
    public String getServerTime() {
        return mPreferencesHelper.getServerTime();
    }

    @Override
    public void setServerTime(String serverTime) {
        mPreferencesHelper.setServerTime(serverTime);
    }

    @Override
    public void updateUserLoggedInState(LoggedInMode loggedInMode) {
        mPreferencesHelper.updateUserLoggedInState(loggedInMode);

    }

    @Override
    public String getBottomText() {
        return mPreferencesHelper.getBottomText();
    }

    @Override
    public String getCallITExecutiveNo() {
        return mPreferencesHelper.getCallITExecutiveNo();
    }

    @Override
    public void setCallITExecutiveNo(String number) {
        mPreferencesHelper.setCallITExecutiveNo(number);
    }

    @Override
    public void setDownloadAPKIsInProcess(long b) {
        mPreferencesHelper.setDownloadAPKIsInProcess(b);
    }

    @Override
    public long isDownloadAPKIsInProcess() {
        return mPreferencesHelper.isDownloadAPKIsInProcess();
    }

    @Override
    public void updateDCDetails(LoginResponse.scGeoLocation scGeoLocation) {
        mPreferencesHelper.updateDCDetails(scGeoLocation);
    }

    @Override
    public String getName() {
        return mPreferencesHelper.getName();
    }

    @Override
    public void setName(String name) {

        mPreferencesHelper.setName(name);

    }

    @Override
    public String getAuthToken() {
        return mPreferencesHelper.getAuthToken();
    }

    @Override
    public void setAuthToken(String authToken) {
        mPreferencesHelper.setAuthToken(authToken);
    }

    @Override
    public void isChild(boolean is_child) {
        mPreferencesHelper.isChild(is_child);
    }

    @Override
    public void is_child_available(boolean is_child_available) {
        mpopPreferencesHelper.is_child_available(is_child_available);
    }

    @Override
    public boolean get_is_child_available() {
        return mpopPreferencesHelper.get_is_child_available();
    }

    @Override
    public boolean get_pop_enable() {
        return mpopPreferencesHelper.get_pop_enable();
    }

    @Override
    public void is_POP_Enable(boolean is_pop_enable) {
        mpopPreferencesHelper.is_POP_Enable(is_pop_enable);
    }

    @Override
    public void isParent(boolean isParent) {
        mPreferencesHelper.isParent(isParent);
    }

    @Override
    public boolean getChild() {
        return mPreferencesHelper.getChild();
    }

    @Override
    public boolean getParent() {
        return mPreferencesHelper.getParent();
    }

    @Override
    public double getCurrentLatitude() {
        return mPreferencesHelper.getCurrentLatitude();
    }

    @Override
    public String getSOSSMSTemplate() {
        return mPreferencesHelper.getSOSSMSTemplate();
    }

    @Override
    public String getSOSNumbers() {
        return mPreferencesHelper.getSOSNumbers();
    }

    @Override
    public double getCurrentLongitude() {
        return mPreferencesHelper.getCurrentLongitude();
    }

    @Override
    public void setCurrentLatitude(String latitude) {
        mPreferencesHelper.setCurrentLatitude(latitude);
    }

    @Override
    public void setCurrentLongitude(String longitude) {
        mPreferencesHelper.setCurrentLongitude(longitude);
    }

    @Override
    public String getCode() {
        return mPreferencesHelper.getCode();
    }

    @Override
    public void setCode(String code) {

        mPreferencesHelper.setCode(code);

    }

    @Override
    public boolean clearPrefrence() {
        return mPreferencesHelper.clearPrefrence();
    }

    @Override
    public boolean clearPopPrefrence() {
        return mpopPreferencesHelper.clearPopPrefrence();
    }
    @Override
    public void setLocationType(Long locationType) {
        mPreferencesHelper.setLocationType(locationType);
    }

    @Override
    public void setIsUserValided(Boolean isUserValided) {
        mPreferencesHelper.setIsUserValided(isUserValided);
    }

    @Override
    public void setPhotoUrl(String photoUrl) {

        mPreferencesHelper.setPhotoUrl(photoUrl);

    }

    @Override
    public void setAuthPinCode(String authPinCode) {
        mPreferencesHelper.setAuthPinCode(authPinCode);
    }

    @Override
    public List<LoginResponse.StartRouteDetails> getRouteDetail() {
        return mPreferencesHelper.getRouteDetail();
    }

    @Override
    public void setRouteDetail(String routeDetails) {
        mPreferencesHelper.setRouteDetail(routeDetails);
    }

    @Override
    public void setStopTripMeterReading(long reading) {
        mPreferencesHelper.setStopTripMeterReading(reading);

    }

    @Override
    public String getPstnFormat() {
        return mPreferencesHelper.getPstnFormat();
    }

    @Override
    public void setPstnFormat(String format) {

        mPreferencesHelper.setPstnFormat(format);

    }

    @Override
    public List<DashboardBanner> getDashBoardBanner() {
        return mPreferencesHelper.getDashBoardBanner();
    }

    @Override
    public void setDashBoardBanner(String dashBoardBanner) {
        mPreferencesHelper.setDashBoardBanner(dashBoardBanner);
    }

    @Override
    public int getTripID() {
        return mPreferencesHelper.getTripID();
    }

    @Override
    public void setTripID(int tripID) {
        mPreferencesHelper.setTripID(tripID);
    }

    @Override
    public void setLiveTrackingId(String live_tracking_id) {
        mPreferencesHelper.setLiveTrackingId(live_tracking_id);
    }

    @Override
    public String getLiveTrackingId() {
        return mPreferencesHelper.getLiveTrackingId();
    }

    @Override
    public int getRouteID() {
        return mPreferencesHelper.getRouteID();
    }

    @Override
    public void setRouteID(int routeID) {
        mPreferencesHelper.setRouteID(routeID);
    }

    @Override
    public void setCutOffTime(String time) {
        mPreferencesHelper.setCutOffTime(time);
    }

    @Override
    public String getCutOffTime() {
        return mPreferencesHelper.getCutOffTime();
    }

    @Override
    public long getLastSyncTime() {
        return mPreferencesHelper.getLastSyncTime();
    }

    @Override
    public void setLastSyncTime(long lastSyncTime) {
        mPreferencesHelper.setLastSyncTime(lastSyncTime);
    }

    @Override
    public boolean is_Ecom_Vehicle() {
        return mPreferencesHelper.is_Ecom_Vehicle();
    }

    @Override
    public void setecom_vehicle(boolean is_com) {
        mPreferencesHelper.setecom_vehicle(is_com);
    }

    @Override
    public String getServiceCenter() {
        return mPreferencesHelper.getServiceCenter();
    }

    @Override
    public void setServiceCenter(String serviceCenter) {
        mPreferencesHelper.setServiceCenter(serviceCenter);

    }

    @Override
    public String getLocationCode() {
        return mPreferencesHelper.getLocationCode();
    }

    @Override
    public void setLocationCode(String locationCode) {
        mPreferencesHelper.setLocationCode(locationCode);
    }

    @Override
    public String getDesignation() {
        return mPreferencesHelper.getDesignation();
    }

    @Override
    public void setDesignation(String designation) {
        mPreferencesHelper.setDesignation(designation);
    }

    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, PerformanceRequest request) {
        return mApiHelper.doPerformanceApiCall(authToken, request);
    }

    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String authToken, FuelReimbursementRequest request) {
        return mApiHelper.doFuelListApiCall(authToken, request);
    }

    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, SOSRequest sosRequest) {
        return mApiHelper.doSOSApiCall(authToken, sosRequest);
    }

    @Override
    public LiveData<StartTripResponse> doStartTrip(String authToken, StartTripRequest startTripRequest) {
        return mApiHelper.doStartTrip(authToken, startTripRequest);
    }

    @Override
    public LiveData<ImageUplaodResponse> uploadStartTripImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        return mApiHelper.uploadStartTripImage(imageFile, requestBody);
    }

    @Override
    public LiveData<ImageUplaodResponse> uploadCommitImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        return mApiHelper.uploadCommitImage(imageFile, requestBody);
    }


    @Override
    public LiveData<StopTrip> doStopTrip(String authToken, StopTripRequest stopTripRequest) {
        return mApiHelper.doStopTrip(authToken, stopTripRequest);
    }

    @Override
    public LiveData<Master_Data_Response> doToDoList(String authToken, User_Data username) {
        return mApiHelper.doToDoList(authToken, username);
    }

    @Override
    public LiveData<AttendanceResponse> doAttendanceData(String authToken, AttendanceRequest stopTripRequest) {
        return mApiHelper.doAttendanceData(authToken, stopTripRequest);
    }

    @Override
    public LiveData<LogoutResponse> doLogout(String authToken, LogoutRequest logoutRequest) {
        return mApiHelper.doLogout(authToken, logoutRequest);
    }

    @Override
    public LiveData<HandOverResponse> getHandOverData(String authToken, HandOverRequest handOverRequest) {
        return mApiHelper.getHandOverData(authToken, handOverRequest);
    }

    @Override
    public LiveData<Menifest_Data_Master> getManifestDetail(String authToken, int pickupRouteId) {
        return mApiHelper.getManifestDetail(authToken, pickupRouteId);
    }

    @Override
    public Single<ScanResponse> callScanApi(String authToken, ScanRequest scanRequest) throws IOException {
        return mApiHelper.callScanApi(authToken, scanRequest);
    }

    @Override
    public LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest rtoRequest) {
        return mApiHelper.getAllRTOShipmentList(authToken, rtoRequest);
    }

    @Override
    public LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest) {
        return mApiHelper.getUpdatedBpAwbRto(authToken,updatedBPRequest);
    }

    @Override
    public LiveData<CommitResponse> doCommitApiCall(String authToken, CommitPacketData commit) {
        return mApiHelper.doCommitApiCall(authToken, commit);
    }

    @Override
    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return mApiHelper.callFirstScanApi(authToken, firstScanResponse);
    }


    @Override
    public LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest) {
        return mApiHelper.checkChildStatusApi(authToken, childCommitRequest);
    }

    @Override
    public LiveData<Departure_Response> departuredTimeUpdate(String authToken, Depature_Request depature_request) {
        return mApiHelper.departuredTimeUpdate(authToken, depature_request);
    }

    @Override
    public LiveData<List<LoginResponse.StartRouteDetails>> fetchStartedVehicle(String authToken, int route_id, String empcode) {
        return mApiHelper.fetchStartedVehicle(authToken, route_id, empcode);
    }

    @Override
    public LiveData<SelfDropResponse> selfDrop(String authToken, SelfDropRequest selfDropRequest) {
        return mApiHelper.selfDrop(authToken, selfDropRequest);
    }

    @Override
    public LiveData<SendPickUpOtpResponse> sendPickupOtp(String authToken, SendPickUpOtpRequest sendPickUpOtpRequest) {
        return mApiHelper.sendPickupOtp(authToken, sendPickUpOtpRequest);
    }

    @Override
    public LiveData<SkipOtpMainResponse> sendReasonList(String authToken) {
        return mApiHelper.sendReasonList(authToken);
    }


    @Override
    public LiveData<VerifyPickUpOtpResponse> verifyPickupOtp(String authToken, VerifyPickUpOtpRequest verifyPickUpOtpRequest) {
        return mApiHelper.verifyPickupOtp(authToken, verifyPickUpOtpRequest);

    }

    @Override
    public LiveData<OtpVerificationManifestResponse> otpVerificationManifest(String authToken, OtpVerificationManifestRequest otpVerificationManifestRequest) {
        return mApiHelper.otpVerificationManifest(authToken, otpVerificationManifestRequest);

    }


    @Override
    public LiveData<ProofOfPickupResponse> SendProofOfPickup(String authToken, ProofOfPickupRequest proofOfPickupRequest) {
        return mApiHelper.SendProofOfPickup(authToken, proofOfPickupRequest);

    }

    @Override
    public LiveData<TrainingResponse> TrainingVideos(String authToken, TrainingRequest trainingRequest) {
        return mApiHelper.TrainingVideos(authToken, trainingRequest);
    }


    @Override
    public LiveData<LogoutResponse> callAuthToken(String authToken) {
        return mApiHelper.callAuthToken(authToken);
    }

    @Override
    public void setUserAsLoggedOut() {

    }

    @Override
    public Observable<List<Post_option>> getCbPstnOptions() {
        return mDbHelper.getCbPstnOptions();
    }

    @Override
    public void insertManifestShipmentDetail(List<Manifest_List> manifest_lists, List<Shipment_Detail> response) {
        mDbHelper.insertManifestShipmentDetail(manifest_lists, response);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getShipmentDetail() {
        return mDbHelper.getShipmentDetail();
    }

    @Override
    public LiveData<List<Manifest_List>> getManifestDetail() {
        return mDbHelper.getManifestDetail();
    }
/*
    @Override
    public LiveData<List<PopData>> getPopData() {
        return mDbHelper.getPopData();
    }
*/


    @Override
    public LiveData<Manifest_List> getUpdatedTime(ArrayList<Long> manifestNo) {
        return mDbHelper.getUpdatedTime(manifestNo);
    }

    @Override
    public LiveData<List<Manifest_List>> getManifestDetailStatus() {
        return mDbHelper.getManifestDetailStatus();
    }

    @Override
    public void updateScannedAWBStatus(Long awb, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied) {
        mDbHelper.updateScannedAWBStatus(awb, status, vehicle, date_time, isAdvance, reason_code, is_bp_validated,reason_code_applied);
    }
    @Override
    public void updateScannedAWBStatusTemp(Long awb, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied,boolean tempKey) {
        mDbHelper.updateScannedAWBStatusTemp(awb, status, vehicle, date_time, isAdvance, reason_code, is_bp_validated,reason_code_applied,tempKey);
    }

    @Override
    public void updateOtpShipment(String status, String vehicle, String date_time, boolean isAdvance, boolean checked, long manifestNo) {
        mDbHelper.updateOtpShipment(status, vehicle, date_time, isAdvance, checked, manifestNo);

    }


    @Override
    public LiveData<List<Shipment_Detail>> getScannedManifestList(String status) {
        return mDbHelper.getScannedManifestList(status);
    }

    @Override
    public LiveData<List<Manifest_List>> getAllManifestShipments(long manifestids) {
        return mDbHelper.getAllManifestShipments(manifestids);
    }

    @Override
    public Observable<Long> getVendorStatusCount(int status) {
        return mDbHelper.getVendorStatusCount(status);
    }

    @Override
    public Observable<Long> getVendorStatusPickedSyncedCount(int picked, int synced) {
        return mDbHelper.getVendorStatusPickedSyncedCount(picked, synced);
    }

    @Override
    public Observable<Long> getWarehouseStatusPickedSyncedCount(int picked, int synced) {
        return mDbHelper.getWarehouseStatusPickedSyncedCount(picked, synced);
    }

    @Override
    public Observable<Long> getRecciStatusPickedSyncedCount(int picked, int synced) {
        return mDbHelper.getRecciStatusPickedSyncedCount(picked, synced);
    }

    @Override
    public Observable<Long> getRecciVenStatusPickedSyncedCount(int picked, int synced) {
        return mDbHelper.getRecciVenStatusPickedSyncedCount(picked, synced);
    }

    @Override
    public Observable<Long> getRecciWrhStatusPickedSyncedCount(int picked, int synced) {
        return mDbHelper.getRecciWrhStatusPickedSyncedCount(picked, synced);
    }

    @Override
    public Observable<Long> getWarehouseStatusCount(int status) {
        return mDbHelper.getWarehouseStatusCount(status);
    }

    @Override
    public Observable<Long> getRecciStatusCount(int status) {
        return mDbHelper.getRecciStatusCount(status);
    }

    @Override
    public Observable<Long> getRecciVenStatusCount(int status) {
        return mDbHelper.getRecciVenStatusCount(status);
    }

    @Override
    public Observable<Long> getRecciWrhStatusCount(int status) {
        return mDbHelper.getRecciWrhStatusCount(status);
    }

    @Override
    public Flowable<List<Manifest_List>> getLastManifestdata(String status) {
        return mDbHelper.getLastManifestdata(status);
    }

    @Override
    public Observable<Long> getShipmentCount(String status, long manifestNo) {
        return mDbHelper.getShipmentCount(status, manifestNo);
    }

    @Override
    public LiveData<Integer> getScannedShipmentCount(String status, long manifestNo) {
        return mDbHelper.getScannedShipmentCount(status, manifestNo);
    }


    @Override
    public Observable<Long> getGlobalCount(String status) {
        return mDbHelper.getGlobalCount(status);
    }

    @Override
    public Observable<Long> getTotalCount(long manifestNo) {
        return mDbHelper.getTotalCount(manifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> fetchToDoFilterData(String searchdata) {
        return mDbHelper.fetchToDoFilterData(searchdata);
    }

    @Override
    public LiveData<List<Shipment_Detail>> fetchManifestData(List<Long> manifestNumber) {
        return mDbHelper.fetchManifestData(manifestNumber);
    }

    @Override
    public LiveData<List<ManifestAndShipment>> getManifestAndShipment(String searchdata) {
        return mDbHelper.getManifestAndShipment(searchdata);
    }

    @Override
    public LiveData<List<HandOverShipmentList>> getAllAwbData() {
        return mDbHelper.getAllAwbData();
    }


    /**
     * @return mDbHelper Get AWB Data
     */
    @Override
    public void insertAwbNumberList(HandOverShipmentList handOverShipmentList) {
        mDbHelper.insertAwbNumberList(handOverShipmentList);
    }

    /**
     * @param awbNumber Delete AWB
     */
    @Override
    public LiveData<Integer> deleteAwbData(int awbNumber) {
        return mDbHelper.deleteAwbData(awbNumber);
    }


    /**
     * @return Validate AWB number
     */
    @Override
    public LiveData<HandOverShipmentList> isValidateAwb(long awbNumber) {
        return mDbHelper.isValidateAwb(awbNumber);
    }


    @Override
    public LiveData<Shipment_Detail> isScannedAWBValid(long awb) {
        return mDbHelper.isScannedAWBValid(awb);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getScannedVendorStatus(long manifestNumber, long awbNumber) {
        return mDbHelper.getScannedVendorStatus(manifestNumber, awbNumber);
    }

    @Override
    public LiveData<Flags> getFlagDetail() {
        return mDbHelper.getFlagDetail();
    }


    @Override
    public void setRecciQuestion(List<General_Question> recciQuestion) {
        mDbHelper.setRecciQuestion(recciQuestion);
    }

    @Override
    public LiveData<List<General_Question>> getRecciQuestion(List<Integer> recciQuestion) {
        return mDbHelper.getRecciQuestion(recciQuestion);
    }

    @Override
    public Observable<Boolean> markUndelivered(Shipment_Detail... shipmentDetails) {
        return mDbHelper.markUndelivered(shipmentDetails);
    }

    @Override
    public Observable<Boolean> insertAdvanceShipmentNew(Shipment_Detail shipment_detail) {
        return mDbHelper.insertAdvanceShipmentNew(shipment_detail);
    }

    @Override
    public Observable<Boolean> deleteAdvanceShipment(long manifestNoInchild, long awb) {
        return mDbHelper.deleteAdvanceShipment(manifestNoInchild, awb);
    }

    @Override
    public void saveImage(ImageModel imageModel) {
        mDbHelper.saveImage(imageModel);
    }


    @Override
    public Observable<Boolean> saveCommitPacket(PushApi pushApi) {
        return mDbHelper.saveCommitPacket(pushApi);
    }

    @Override
    public Observable<Boolean> pushPopData(PopData popData) {
        return mDbHelper.pushPopData(popData);
    }



    @Override
    public Observable<List<PushApi>> getUnSyncCommitManifest(long manifest_no, int shipmentStatus) {
        return mDbHelper.getUnSyncCommitManifest(manifest_no, shipmentStatus);
    }

    @Override
    public Observable<List<PopData>> getPopData() {
        return mDbHelper.getPopData();
    }


    @Override
    public void updateRtoShipmentStatus(long manifestNo, long awb, String shipment_status) {
        mDbHelper.updateRtoShipmentStatus(manifestNo, awb, shipment_status);
    }


    @Override
    public void updateBPID(long manifestNo, long awb, String BP_ID) {
        mDbHelper.updateBPID(manifestNo, awb, BP_ID);
    }




    @Override
    public void updateSharedManifestStatus(List<Long> manifestNo, String mobileNoType, Boolean shipment_status) {
        mDbHelper.updateSharedManifestStatus(manifestNo, mobileNoType, shipment_status);
    }


    @Override
    public void insertShipment(List<Shipment_Detail> listOfShipment) {
        mDbHelper.insertShipment(listOfShipment);
    }

    @Override
    public Observable<Boolean> insertShipmentUsingRx(List<Shipment_Detail> listOfShipment) {
        return mDbHelper.insertShipmentUsingRx(listOfShipment);
    }

    @Override
    public Observable<Boolean> isAWBRtoLock(long manifestNumber, long awbNumber) {
        return mDbHelper.isAWBRtoLock(manifestNumber, awbNumber);
    }


    @Override
    public void updateCommitStatus(String shipmentStatus, String manifestNO) {
        mDbHelper.updateCommitStatus(shipmentStatus, manifestNO);
    }

    @Override
    public void updateManifestList(String commitStatus, ArrayList<Long> manifestNo) {
        mDbHelper.updateManifestList(commitStatus, manifestNo);

    }

    @Override
    public void updatePopStatus(String commitStatus, ArrayList<Long> manifestNo) {
        mDbHelper.updatePopStatus(commitStatus, manifestNo);

    }


    @Override
    public void updateManifestSelfList(String commitStatus, ArrayList<Long> manifestNo) {
        mDbHelper.updateManifestSelfList(commitStatus, manifestNo);
    }

    @Override
    public void updateFileUrl(String fileUrl, String mani) {
        mDbHelper.updateFileUrl(fileUrl, mani);
    }

    @Override
    public void updateRecciList(String setShipmentStatus, String compositeKey) {
        mDbHelper.updateRecciList(setShipmentStatus, compositeKey);
    }

    @Override
    public void updateInscanStatus(long manifestNumber) {
        mDbHelper.updateInscanStatus(manifestNumber);
    }

    @Override
    public void insertFirstScanData(FirstInscan firstInscan) {
        mDbHelper.insertFirstScanData(firstInscan);
    }

    @Override
    public void updateInscanToFirstScanTable(long manifestNumber) {
        mDbHelper.updateInscanStatus(manifestNumber);
    }

    @Override
    public LiveData<List<ImageModel>> getImageStatus(String manifestNo) {
        return mDbHelper.getImageStatus(manifestNo);
    }

    @Override
    public Observable<Boolean> updatechildCommitShipment(Long awb, String shipment_status, String vehicle, long manifest_no, boolean isChild) {
        return mDbHelper.updatechildCommitShipment(awb, shipment_status, vehicle, manifest_no, isChild);
    }

    @Override
    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        mDbHelper.failedManifestQuery(commit_status, commit_failed, manifestno);
    }

    @Override
    public LiveData<CountManifest> checkAllManifestStatus() {
        return mDbHelper.checkAllManifestStatus();
    }

    @Override
    public void deleteData() {
        mDbHelper.deleteData();
    }

    @Override
    public void deleteDataAtLogout() {
        mDbHelper.deleteDataAtLogout();
    }

    @Override
    public void deleteForcefully() {
        mDbHelper.deleteForcefully();
    }

    @Override
    public JsonObject getURL() {
        return mPreferencesHelper.getURL();
    }

    @Override
    public void setURL(String urllist) {
        mPreferencesHelper.setURL(urllist);
    }

    @Override
    public void setDeparted(boolean depart) {
        mPreferencesHelper.setDeparted(depart);
    }

    @Override
    public boolean isDepart() {
        return mPreferencesHelper.isDepart();
    }

    @Override
    public boolean isLogout() {
        return mPreferencesHelper.isLogout();
    }

    @Override
    public void setLogout(boolean logout) {
        mPreferencesHelper.setLogout(logout);

    }

    @Override
    public void set_cutOffAlertTime(String config_value) {
        mPreferencesHelper.set_cutOffAlertTime(config_value);
    }

    @Override
    public void set_cutOffActionValue(String config_value) {
        mPreferencesHelper.set_cutOffActionValue(config_value);
    }

    @Override
    public void set_live_tracking(String config_value) {
        mPreferencesHelper.set_live_tracking(config_value);
    }

    @Override
    public String get_live_Tracking() {
        return mPreferencesHelper.get_live_Tracking();
    }

    @Override
    public void set_erm_sync(String config_value) {
        mPreferencesHelper.set_erm_sync(config_value);
    }

    @Override
    public String get_erm_sync() {
        return mPreferencesHelper.get_erm_sync();
    }

    @Override
    public void set_enable_calling(String config_value) {
        mPreferencesHelper.set_enable_calling(config_value);
    }

    @Override
    public String get_enable_calling() {
        return mPreferencesHelper.get_enable_calling();
    }

    @Override
    public String get_cutOffAlertTime() {
        return mPreferencesHelper.get_cutOffAlertTime();
    }

    @Override
    public String get_cutOffActionValue() {
        return mPreferencesHelper.get_cutOffActionValue();
    }

    @Override
    public void setLiveTrackingCalculatedDistance(float distance) {
        mPreferencesHelper.setLiveTrackingCalculatedDistance(distance);
    }

    @Override
    public float getLiveTrackingCalculatedDistance() {
        return mPreferencesHelper.getLiveTrackingCalculatedDistance();
    }

    @Override
    public void setLiveTrackingCount(int count) {

    }

    @Override
    public long getDistance() {
        return 0;
    }

    @Override
    public void setDistance(long distance) {

    }

    @Override
    public void setLiveTrackingCalculatedDistanceWithSpeed(float trackingCalculatedDistanceWithSpeed) {

    }

    @Override
    public int getLiveTrackingMINSpeed() {
        return 0;
    }

    @Override
    public void setLiveTrackingMINSpeed(int minSpeed) {

    }

    @Override
    public int getLiveTrackingSpeed() {
        return 0;
    }

    @Override
    public void setLiveTrackingSpeed(int maxSpeed) {

    }

    @Override
    public float getLiveTrackingCalculatedDistanceWithSpeed() {
        return 0;
    }

    @Override
    public void asParentOrChild(boolean child) {
        mPreferencesHelper.asParentOrChild(child);
    }

    @Override
    public boolean getAsParentChild() {
        return mPreferencesHelper.getAsParentChild();
    }

    @Override
    public LiveData<List<Manifest_List>> getUnPickedManifestList() {
        return mDbHelper.getUnPickedManifestList();
    }


    @Override
    public Observable<Long> getManifestIdFromAwb(long awb) {
        return mDbHelper.getManifestIdFromAwb(awb);
    }

    @Override
    public Observable<Long> getAdvanceCount(boolean b, long manifest_no) {
        return mDbHelper.getAdvanceCount(b, manifest_no);
    }

    @Override
    public Observable<Boolean> insertRemark(Remark remark) {
        return mDbHelper.insertRemark(remark);
    }

    @Override
    public LiveData<Integer> getUnSyncManifestList() {
        return mDbHelper.getUnSyncManifestList();
    }

    @Override
    public Observable<String> getShipmentStatus(long manifestNumber, long awbNumber) {
        return mDbHelper.getShipmentStatus(manifestNumber, awbNumber);
    }

    @Override
    public Observable<Boolean> getShipmentExist(long manifestNumber, long awbNumber) {
        return mDbHelper.getShipmentExist(manifestNumber, awbNumber);
    }

    @Override
    public Observable<Boolean> isAlreadyScanned(Long manifestNo, Long awbNumber) {
        return mDbHelper.isAlreadyScanned(manifestNo, awbNumber);
    }

    @Override
    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        mDbHelper.failedCommitPacket(commit_status, commit_failed, manifestno);
    }

    @Override
    public void inScanCommitPacket(int commit_status, long manifestno) {
        mDbHelper.inScanCommitPacket(commit_status, manifestno);
    }

    @Override
    public void updateCommit_DataTable(long manifestno) {
        mDbHelper.updateCommit_DataTable(manifestno);
    }

    @Override
    public void inScanCommitRecciPacket(int scan_status, String comkey) {
        mDbHelper.inScanCommitRecciPacket(scan_status, comkey);
    }

    @Override
    public void updateIsScanStarted(long manifestNumber) {
        mDbHelper.updateIsScanStarted(manifestNumber);
    }

    @Override
    public void assignUpdateIsScanStarted(long manifestNumber) {
        mDbHelper.assignUpdateIsScanStarted(manifestNumber);
    }

    @Override
    public LiveData<Integer> getStartedManifestCount() {
        return mDbHelper.getStartedManifestCount();
    }

    @Override
    public LiveData<List<PushApi>> UnSyncCommitListAtLogin(int status) {
        return mDbHelper.UnSyncCommitListAtLogin(status);
    }

    @Override
    public void deleteCommitedShipment(ArrayList<Long> manifestIds) {
        mDbHelper.deleteCommitedShipment(manifestIds);
    }

    @Override
    public void updateManifestListWithRecci(String valueOf, String s) {
        mDbHelper.updateManifestListWithRecci(valueOf, s);
    }

    @Override
    public void updateCommitStatusWithRecci(String s, String s1) {
        mDbHelper.updateCommitStatusWithRecci(s, s1);
    }

    @Override
    public void updateFirstInScan(int status, long manifestId) {
        mDbHelper.updateFirstInScan(status, manifestId);
    }


    @Override
    public void set_pickup_geofencing_mode(String mode) {
        mPreferencesHelper.set_pickup_geofencing_mode(mode);
    }

    @Override
    public String get_pickup_geofencing_mode() {
        return mPreferencesHelper.get_pickup_geofencing_mode();
    }

    @Override
    public void set_pickup_geofencing_radius(String radius) {
        mPreferencesHelper.set_pickup_geofencing_radius(radius);
    }

    @Override
    public String get_pickup_geofencing_radius() {
        return mPreferencesHelper.get_pickup_geofencing_radius();
    }


    @Override
    public void set_sruti_enable_otp_for_zero_pickup(String status) {
        mPreferencesHelper.set_sruti_enable_otp_for_zero_pickup(status);
    }

    @Override
    public String get_sruti_enable_otp_for_zero_pickup() {
        return mPreferencesHelper.get_sruti_enable_otp_for_zero_pickup();
    }

    public void set_sruti_enable_delink_for_wh(String status) {
        mPreferencesHelper.set_sruti_enable_delink_for_wh(status);
    }

    public String get_sruti_enable_delink_for_wh() {
        return mPreferencesHelper.get_sruti_enable_delink_for_wh();
    }

    @Override
    public void set_sruti_allow_multispace(String status) {
        mPreferencesHelper.set_sruti_allow_multispace(status);
    }

    @Override
    public String get_multiSpace_allow() {
        return mPreferencesHelper.get_multiSpace_allow();
    }

    public void setListOfMultiSpace(List<String> srutiMultiSpaceAppList) {
        mPreferencesHelper.setListOfMultiSpace(srutiMultiSpaceAppList);
    }

    public ArrayList<String> getListOfMultiSpace() {
        return mPreferencesHelper.getListOfMultiSpace();
    }


    @Override
    public Observable<List<Shipment_Detail>> ifBrandPackagingIDexists(long manifest_no, long awb_no, String BP_id) {
        return mDbHelper.ifBrandPackagingIDexists(manifest_no, awb_no, BP_id);
    }


    @Override
    public Observable<List<Shipment_Detail>> ifAWBbrandPackagingIDexists(long awb_no, String BP_id) {
        return mDbHelper.ifAWBbrandPackagingIDexists(awb_no, BP_id);
    }


    @Override
    public void UpdateAWBViaBpReasonID(Long awb, String status, String vehicle, long date_time, int reason_id, String reason_code,boolean reason_code_applied) {
        mDbHelper.UpdateAWBViaBpReasonID(awb, status, vehicle, date_time, reason_id, reason_code, reason_code_applied);
    }

    @Override
    public Observable<String> getSpecificBpId(long awb) {
        return mDbHelper.getSpecificBpId(awb);
    }

    @Override
    public Observable<Boolean> getTempKey(long awb) {
        return mDbHelper.getTempKey(awb);
    }
}
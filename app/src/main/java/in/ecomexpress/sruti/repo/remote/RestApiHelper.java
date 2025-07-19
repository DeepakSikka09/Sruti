package in.ecomexpress.sruti.repo.remote;


import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.ATTENDANCE_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.AUTH_TOKEN;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.CHECK_AND_UPDATE_STATUS_OF_INSCAN_AWB;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.CHILD_STATUS_COMMIT;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.COMMIT_PACKET;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.DEPART_VEHICLES;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.EXTRA_VEHICLE;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.FETCH_SELF_DROP_MANIFEST_STATUS;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.FIRST_INSCAN;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.HANDOVER_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.IMAGESTARTTRIP;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.LOGOUT_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.MANIFEST;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.MASTER_DATA;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.OTP_VERIFICATION_MANIFEST;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.PERFORMANCE_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.RTO_LOCK;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SEND_PICKUP_OTP;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SEND_POP_SMS_TO_VENDOR;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SKIP_OTP_REASON_LIST;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SOS_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.START_TRIP;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.STOP_TRIP;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.TRAINING_UNIFY_DETAILS;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.TRIP_REIMBURSEMENT_URL;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.UPDATE_BP;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.VERIFY_PICKUP_OTP;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import in.ecomexpress.sruti.model.starttrip.Extra_Vehicle;
import in.ecomexpress.sruti.model.starttrip.ImageUplaodResponse;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.starttrip.StartTripResponse;
import in.ecomexpress.sruti.model.stoptrip.StopTrip;
import in.ecomexpress.sruti.model.stoptrip.StopTripData;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpResponse;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingRequest;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingResponse;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RestApiHelper implements IRestApiHelper {
    @Inject
    IPreferenceHelper iPreferenceHelper;
    private RetrofitService retrofitService;

    @Inject
    public RestApiHelper(RetrofitService retrofitService) {
        this.retrofitService = retrofitService;
    }

    // reactivex
    @Override
    public Single<ForgotPasswordResponse> doResetPasswordApiCall(String token, ChangePasswordRequest changePasswordRequest) {
        return retrofitService.doResetPasswordApiCall(token, changePasswordRequest);
    }

    @Override
    public Single<ScanResponse> callScanApi(String authToken, ScanRequest scanRequest) {
        return retrofitService.scanApicall(urlLoc(CHECK_AND_UPDATE_STATUS_OF_INSCAN_AWB), authToken, scanRequest);
    }

    @Override
    public Single<LoginResponse> doLoginApiCall(LoginRequest request) {
        return retrofitService.doLoginApiCall(request);
    }

    @Override
    public Single<ForgotPasswordResponse> doForgetPasswordApiCall(ForgetPasswordUserRequest forgetPasswordUserRequest) {
        return retrofitService.doForgetPasswordApiCall(forgetPasswordUserRequest);
    }


    @Override
    public Single<ForgotPasswordResponse> doOTPVerifyWithPasswordApiCall(OTPVerifyWithPasswordRequest otpVerifyWithPasswordRequest) {
        return retrofitService.doOTPVerifyWithPasswordApiCall(otpVerifyWithPasswordRequest);
    }


    @Override
    public Single<LoginVerifyOtpResponse> doLoginResendOtpApiCall(String authToken, LoginResendOtpRequest loginVerifyOtpRequest) {
        return retrofitService.doLoginResendOtpApiCall(authToken, loginVerifyOtpRequest);
    }

    @Override
    public Single<LoginVerifyOtpResponse> doLoginVerifyOtpApiCall(String authToken, LoginVerifyOtpRequest loginVerifyOtpRequest) {
        return retrofitService.doLoginVerifyOtpApiCall(authToken, loginVerifyOtpRequest);
    }

    @Override
    public Single<PerformanceResponse> doPerformanceApiCall(String authToken, PerformanceRequest request) {
        return retrofitService.doPerformanceApiCall(urlLoc(PERFORMANCE_URL), authToken, request);
    }


    @Override
    public Single<FuelReimbursementResponse> doFuelListApiCall(String token, FuelReimbursementRequest fuelReimbursementRequest) {
        return retrofitService.doFuelListApiCall(urlLoc(TRIP_REIMBURSEMENT_URL), token, fuelReimbursementRequest);

    }

    @Override
    public Single<SOSResponse> doSOSApiCall(String authToken, SOSRequest sosRequest) {
        return retrofitService.doSOSApiCall(urlLoc(SOS_URL), authToken, sosRequest);
    }

    // LiveData
    @Override
    public LiveData<StartTripResponse> doStartTrip(String authToken, StartTripRequest startTripRequest) {
        MutableLiveData<StartTripResponse> starttrip = new MutableLiveData<>();
        retrofitService.doStartTrip(urlLoc(START_TRIP),authToken,startTripRequest).enqueue(new Callback<StartTripResponse>() {
            @Override
            public void onResponse(Call<StartTripResponse> call, Response<StartTripResponse> response) {
                if (response.body() != null) {
                    starttrip.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        StartTripResponse stopTrip = new StartTripResponse();
                        stopTrip.setStatus(false);
                        stopTrip.setDescription(jObjError.getString("description"));
                        starttrip.setValue(stopTrip);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<StartTripResponse> call, Throwable t) {
                StartTripResponse res = new StartTripResponse();
                res.setStatus(false);
                ArrayList<String> err = new ArrayList<>();
                err.add(t.getMessage());
                in.ecomexpress.sruti.model.starttrip.Response ree = new in.ecomexpress.sruti.model.starttrip.Response();
                ree.setErrors(err);
                res.setResponse(ree);
                starttrip.setValue(res);
            }
        });
        return starttrip;
    }

    @Override
    public LiveData<StopTrip> doStopTrip(String authToken, StopTripRequest stopTripRequest) {
        MutableLiveData<StopTrip> mutableLiveData = new MutableLiveData<>();

        retrofitService.doStopTrip(urlLoc(STOP_TRIP), authToken, stopTripRequest).enqueue(new Callback<StopTrip>() {
            @Override
            public void onResponse(Call<StopTrip> call, Response<StopTrip> response) {
                if (response.body() != null) {
                    mutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        StopTrip stopTrip = new StopTrip();
                        stopTrip.setStatus(false);
                        stopTrip.setDescription(jObjError.getString("description"));
                        mutableLiveData.setValue(stopTrip);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<StopTrip> call, Throwable t) {
                StopTrip res = new StopTrip();
                res.setStatus(false);
                ArrayList<String> err = new ArrayList<>();
                err.add(t.getMessage());
                StopTripData ree = new StopTripData();
                ree.setErrors(err);
                res.setResponse(ree);
                mutableLiveData.setValue(res);
            }
        });
        return mutableLiveData;
    }

    @Override
    public LiveData<ImageUplaodResponse> uploadStartTripImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        MutableLiveData<ImageUplaodResponse> imageres = new MutableLiveData<>();
        retrofitService.uploadStartTripImage(urlLoc(IMAGESTARTTRIP), imageFile, requestBody).enqueue(new Callback<ImageUplaodResponse>() {
            @Override
            public void onResponse(Call<ImageUplaodResponse> call, Response<ImageUplaodResponse> response) {
                if (response.body() != null)
                    imageres.setValue(response.body());
                else {
                    ImageUplaodResponse imgR = new ImageUplaodResponse();
                    imgR.setStatus(false);
                    imgR.setDescription("Server not response properly");
                    imageres.setValue(imgR);
                }
            }

            @Override
            public void onFailure(Call<ImageUplaodResponse> call, Throwable t) {
                ImageUplaodResponse imgR = new ImageUplaodResponse();
                imgR.setStatus(false);
                imgR.setDescription(t.getMessage());
                imageres.setValue(imgR);
            }
        });
        return imageres;
    }


    @Override
    public LiveData<Master_Data_Response> doToDoList(String authToken, User_Data username) {
        MutableLiveData<Master_Data_Response> master_data_responseMutableLiveData = new MutableLiveData<>();
        retrofitService.doToDoList(urlLoc(MASTER_DATA)/*"http://test.ecomexpress.in:7007/sruti/services/master-data"*/, authToken, username).enqueue(new Callback<Master_Data_Response>() {
            @Override
            public void onResponse(Call<Master_Data_Response> call, Response<Master_Data_Response> response) {
                try {
                    master_data_responseMutableLiveData.setValue(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Master_Data_Response> call, Throwable t) {
                Master_Data_Response ob = new Master_Data_Response();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                master_data_responseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return master_data_responseMutableLiveData;//retrofitService.doToDoList(master_data, authToken, username);
    }

    @Override
    public LiveData<AttendanceResponse> doAttendanceData(String authToken, AttendanceRequest attendanceRequest) {
        MutableLiveData<AttendanceResponse> mutableLiveData = new MutableLiveData<>();
        retrofitService.doAttendanceData(urlLoc(ATTENDANCE_URL), authToken, attendanceRequest).enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                if (response.body() != null) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                System.out.println(t.getMessage());
                AttendanceResponse at = new AttendanceResponse();
                at.setDescription(t.getMessage());

                mutableLiveData.setValue(at);
            }
        });
        return mutableLiveData;
    }

    @Override
    public LiveData<HandOverResponse> getHandOverData(String authToken, HandOverRequest handOverRequest) {
        MutableLiveData<HandOverResponse> handOverResponse = new MutableLiveData<>();
        retrofitService.getHandOverData(urlLoc(HANDOVER_URL), authToken, handOverRequest).enqueue(new Callback<HandOverResponse>() {
            @Override
            public void onResponse(Call<HandOverResponse> call, Response<HandOverResponse> response) {
                if (response.body() != null) {
                    handOverResponse.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<HandOverResponse> call, Throwable t) {
                System.out.println(t.getMessage());
                HandOverResponse handOverResponse1 = new HandOverResponse();
                handOverResponse1.setDescription(t.getMessage());
                handOverResponse.setValue(handOverResponse1);
            }
        });

        return handOverResponse;
    }

    @Override
    public LiveData<LogoutResponse> doLogout(String authToken, LogoutRequest logoutRequest) {
        MutableLiveData<LogoutResponse> mutableLiveData = new MutableLiveData<>();
        retrofitService.doLogoutRequest(urlLoc(LOGOUT_URL), authToken, logoutRequest).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.body() != null) {
                    mutableLiveData.setValue(response.body());
                } else if (!response.isSuccessful()) {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<LogoutResponse>() {
                        }.getType();
                        LogoutResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        System.out.println("sdsadsad");
                        if (errorResponse != null)
                            mutableLiveData.setValue(errorResponse);
//                            System.out.println("ERRR "+new JSONObject(response.errorBody().string()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        return mutableLiveData;
    }


    @Override
    public LiveData<Menifest_Data_Master> getManifestDetail(String authToken, int pickupRouteId) {
        MutableLiveData<Menifest_Data_Master> masterMutableLiveData = new MutableLiveData<>();
        retrofitService.getManifestDetail(urlLoc(MANIFEST) + pickupRouteId, authToken, true).enqueue(new Callback<Menifest_Data_Master>() {
            @Override
            public void onResponse(Call<Menifest_Data_Master> call, Response<Menifest_Data_Master> response) {
                if (response.code() == 200) {
                    masterMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        Menifest_Data_Master menifest_data_master = new Menifest_Data_Master();
                        menifest_data_master.setStatus(false);
                        menifest_data_master.setDescription(jObjError.getString("description"));
                        masterMutableLiveData.setValue(menifest_data_master);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Menifest_Data_Master> call, Throwable t) {
                Menifest_Data_Master menifest_data_master = new Menifest_Data_Master();
                menifest_data_master.setStatus(false);
                menifest_data_master.setDescription(t.getMessage());
                masterMutableLiveData.setValue(menifest_data_master);
            }
        });

        return masterMutableLiveData;
    }

    @Override
    public LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest manifest_no) {
        MutableLiveData<RtoLockResponse> rtoLockResponseMutableLiveData = new MutableLiveData<>();
        retrofitService.getAllRTOShipmentList(urlLoc(RTO_LOCK), authToken, manifest_no).enqueue(new Callback<RtoLockResponse>() {
            @Override
            public void onResponse(Call<RtoLockResponse> call, Response<RtoLockResponse> response) {
                if (response.code() == 200) {
                    rtoLockResponseMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        RtoLockResponse rtoLockResponse = new RtoLockResponse();
                        rtoLockResponse.setStatus(false);
                        rtoLockResponse.setDescription(jObjError.getString("description"));
                        rtoLockResponseMutableLiveData.setValue(rtoLockResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RtoLockResponse> call, Throwable t) {
                RtoLockResponse ob = new RtoLockResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                rtoLockResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return rtoLockResponseMutableLiveData;
    }

    @Override
    public LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest) {
        MutableLiveData<UpdatedBPResponse> updatedBpAwbrtoResponseMutableLiveData = new MutableLiveData<>();
        retrofitService.updatedBp(urlLoc(UPDATE_BP), authToken, updatedBPRequest).enqueue(new Callback<UpdatedBPResponse>() {
            @Override
            public void onResponse(Call<UpdatedBPResponse> call, Response<UpdatedBPResponse> response) {
                if (response.code() == 200) {
                    updatedBpAwbrtoResponseMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        UpdatedBPResponse updatedBPResponse = new UpdatedBPResponse();
                        updatedBPResponse.setStatus(false);
                        updatedBPResponse.setDescription(jObjError.getString("description"));
                        updatedBpAwbrtoResponseMutableLiveData.setValue(updatedBPResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatedBPResponse> call, Throwable t) {
                UpdatedBPResponse ob = new UpdatedBPResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                updatedBpAwbrtoResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return updatedBpAwbrtoResponseMutableLiveData;
    }

    @Override
    public LiveData<CommitResponse> doCommitApiCall(String authToken, CommitPacketData commit) {
        MutableLiveData<CommitResponse> commitResponse = new MutableLiveData<>();

        retrofitService.doCommitApiCall(urlLoc(COMMIT_PACKET), authToken, commit).enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(Call<CommitResponse> call, Response<CommitResponse> response) {
                commitResponse.setValue(response.body());
            }

            @Override
            public void onFailure(Call<CommitResponse> call, Throwable t) {
                CommitResponse ob = new CommitResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                commitResponse.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return commitResponse;
    }


    @Override
    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanRequest) {
        System.out.println("firstScanRequest " + firstScanRequest.toString());
        MutableLiveData<FirstScanResponse> firstScanResponseMutableLiveData = new MutableLiveData<>();

        retrofitService.callFirstScanApi(urlLoc(FIRST_INSCAN), authToken, firstScanRequest).enqueue(new Callback<FirstScanResponse>() {
            @Override
            public void onResponse(Call<FirstScanResponse> call, Response<FirstScanResponse> response) {
                firstScanResponseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<FirstScanResponse> call, Throwable t) {
                FirstScanResponse ob = new FirstScanResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                firstScanResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return firstScanResponseMutableLiveData;
    }

    @Override
    public LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest) {
        MutableLiveData<ChildCommitResponse> childCommitResponseMutableLiveData = new MutableLiveData<>();
        retrofitService.checkChildStatusApi(urlLoc(CHILD_STATUS_COMMIT), authToken, childCommitRequest).enqueue(new Callback<ChildCommitResponse>() {
            @Override
            public void onResponse(Call<ChildCommitResponse> call, Response<ChildCommitResponse> response) {
                childCommitResponseMutableLiveData.setValue(response.body());

                if (response.code() == 200) {
                    childCommitResponseMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        ChildCommitResponse childCommitResponse = new ChildCommitResponse();
                        childCommitResponse.setStatus(false);
                        childCommitResponse.setDescription(jObjError.getString("description"));
                        childCommitResponseMutableLiveData.setValue(childCommitResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChildCommitResponse> call, Throwable t) {
                ChildCommitResponse ob = new ChildCommitResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                childCommitResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });

        return childCommitResponseMutableLiveData;

    }

    @Override
    public LiveData<Departure_Response> departuredTimeUpdate(String authToken, Depature_Request depature_request) {
        MutableLiveData<Departure_Response> depature_resp = new MutableLiveData<>();
        retrofitService.departuredTimeUpdate(urlLoc(DEPART_VEHICLES), authToken, depature_request).enqueue(new Callback<Departure_Response>() {
            @Override
            public void onResponse(Call<Departure_Response> call, Response<Departure_Response> response) {
                depature_resp.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Departure_Response> call, Throwable t) {
                Departure_Response departure_response = new Departure_Response();
                departure_response.setStatus(false);
                departure_response.setDescription(t.getMessage());
                depature_resp.setValue(departure_response);
            }
        });
        return depature_resp;
    }

    @Override
    public LiveData<LogoutResponse> callAuthToken(String authToken) {
        MutableLiveData<LogoutResponse> depature_resp = new MutableLiveData<>();
        retrofitService.callAuthToken(urlLoc(AUTH_TOKEN), authToken).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                depature_resp.setValue(response.body());
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                LogoutResponse departure_response = new LogoutResponse();
                departure_response.setStatus(false);
                departure_response.setDescription(t.getMessage());
                depature_resp.setValue(departure_response);
            }
        });
        return depature_resp;
    }

    @Override
    public LiveData<List<LoginResponse.StartRouteDetails>> fetchStartedVehicle(String authToken, int route_id, String empcode) {
        MutableLiveData<List<LoginResponse.StartRouteDetails>> depature_resp = new MutableLiveData<>();
        retrofitService.fetchtartedVehicle(urlLoc(EXTRA_VEHICLE) + "/" + route_id + "/" + empcode, authToken, route_id, empcode).enqueue(new Callback<Extra_Vehicle>() {
            @Override
            public void onResponse(Call<Extra_Vehicle> call, Response<Extra_Vehicle> response) {
                if (response.body() != null && response.body().getStatus())
                    depature_resp.setValue(response.body().getResponse());
            }

            @Override
            public void onFailure(Call<Extra_Vehicle> call, Throwable t) {
            }
        });
        return depature_resp;
    }

   /* @Override
    public LiveData<SkipOtpMainResponse> sendReasonList(String authToken) {
        MutableLiveData<SkipOtpMainResponse> depature_resp = new MutableLiveData<>();
        retrofitService.sendReasonList("https://j5s4r8kkp5.execute-api.ap-south-1.amazonaws.com/sruti/services/fetch-skip-otp-reason-codes", authToken).enqueue(new Callback<SkipOtpMainResponse>() {
            @Override
            public void onResponse(Call<SkipOtpMainResponse> call, Response<SkipOtpMainResponse> response) {
                if (response.body() != null && response.body().getStatus())
                    depature_resp.setValue(response.body());
            }

            @Override
            public void onFailure(Call<SkipOtpMainResponse> call, Throwable t) {
            }
        });
        return depature_resp;
    }
*/


    @Override
    public LiveData<SkipOtpMainResponse> sendReasonList(String authToken) {
        MutableLiveData<SkipOtpMainResponse> masterMutableLiveData = new MutableLiveData<>();
        retrofitService.sendReasonList(urlLoc(SKIP_OTP_REASON_LIST), authToken).enqueue(new Callback<SkipOtpMainResponse>() {
            @Override
            public void onResponse(Call<SkipOtpMainResponse> call, Response<SkipOtpMainResponse> response) {
                if (response.code() == 200) {
                    masterMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        SkipOtpMainResponse menifest_data_master = new SkipOtpMainResponse();
                        menifest_data_master.setStatus(false);
                        menifest_data_master.setDescription(jObjError.getString("description"));
                        masterMutableLiveData.setValue(menifest_data_master);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<SkipOtpMainResponse> call, Throwable t) {
                SkipOtpMainResponse menifest_data_master = new SkipOtpMainResponse();
                menifest_data_master.setStatus(false);
                menifest_data_master.setDescription(t.getMessage());
                masterMutableLiveData.setValue(menifest_data_master);
            }
        });

        return masterMutableLiveData;
    }


    @Override
    public LiveData<SelfDropResponse> selfDrop(String authToken, SelfDropRequest selfDropRequest) {
        MutableLiveData<SelfDropResponse> selfDropResponse = new MutableLiveData<>();
        retrofitService.fetchSelfDropManifest(urlLoc(FETCH_SELF_DROP_MANIFEST_STATUS), authToken, selfDropRequest).enqueue(new Callback<SelfDropResponse>() {
            @Override
            public void onResponse(Call<SelfDropResponse> call, Response<SelfDropResponse> response) {
                if (response.body() != null && response.body().isStatus())
                    selfDropResponse.setValue(response.body());
            }

            @Override
            public void onFailure(Call<SelfDropResponse> call, Throwable t) {

            }
        });
        return selfDropResponse;
    }

    @Override
    public LiveData<SendPickUpOtpResponse> sendPickupOtp(String authToken, SendPickUpOtpRequest sendPickUpOtpRequest) {
        MutableLiveData<SendPickUpOtpResponse> sendPickUpOtpResponse = new MutableLiveData<>();

        retrofitService.sendPickupOtp(urlLoc(SEND_PICKUP_OTP), authToken, sendPickUpOtpRequest).enqueue(new Callback<SendPickUpOtpResponse>() {
            @Override
            public void onResponse(Call<SendPickUpOtpResponse> call, Response<SendPickUpOtpResponse> response) {
                // sendPickUpOtpResponse.setValue(response.body());
                if (response.code() == 200) {
                    sendPickUpOtpResponse.setValue(response.body());
                } else {
                    //sendPickUpOtpResponse.setValue(response.body());
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        SendPickUpOtpResponse sendPickUpOtpResponse1 = new SendPickUpOtpResponse();
                        sendPickUpOtpResponse1.setStatus(false);
                        sendPickUpOtpResponse1.setDescription(jObjError.getString("description"));
                        sendPickUpOtpResponse.setValue(sendPickUpOtpResponse1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SendPickUpOtpResponse> call, Throwable t) {
                SendPickUpOtpResponse ob = new SendPickUpOtpResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                sendPickUpOtpResponse.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return sendPickUpOtpResponse;
    }

    @Override
    public LiveData<VerifyPickUpOtpResponse> verifyPickupOtp(String authToken, VerifyPickUpOtpRequest verifyPickUpOtpRequest) {
        MutableLiveData<VerifyPickUpOtpResponse> verifyPickUpOtpRequestMutableLiveData = new MutableLiveData<>();

        retrofitService.verifyPickupOtp(urlLoc(VERIFY_PICKUP_OTP), authToken, verifyPickUpOtpRequest).enqueue(new Callback<VerifyPickUpOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyPickUpOtpResponse> call, Response<VerifyPickUpOtpResponse> response) {
              /*  if (response.body() != null && response.body().isStatus())
                    verifyPickUpOtpRequestMutableLiveData.setValue(response.body());*/

                if (response.code() == 200) {
                    verifyPickUpOtpRequestMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        VerifyPickUpOtpResponse verifyPickUpOtpResponse = new VerifyPickUpOtpResponse();
                        verifyPickUpOtpResponse.setStatus(false);
                        verifyPickUpOtpResponse.setDescription(jObjError.getString("description"));
                        verifyPickUpOtpRequestMutableLiveData.setValue(verifyPickUpOtpResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<VerifyPickUpOtpResponse> call, Throwable t) {
                VerifyPickUpOtpResponse ob = new VerifyPickUpOtpResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                verifyPickUpOtpRequestMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return verifyPickUpOtpRequestMutableLiveData;
    }


    @Override
    public LiveData<OtpVerificationManifestResponse> otpVerificationManifest(String authToken, OtpVerificationManifestRequest otpVerificationManifestRequest) {
        MutableLiveData<OtpVerificationManifestResponse> otpVerificationManifestResponseMutableLiveData = new MutableLiveData<>();

        retrofitService.otpVerificationManifest(urlLoc(OTP_VERIFICATION_MANIFEST), authToken, otpVerificationManifestRequest).enqueue(new Callback<OtpVerificationManifestResponse>() {
            @Override
            public void onResponse(Call<OtpVerificationManifestResponse> call, Response<OtpVerificationManifestResponse> response) {
                if (response.code() == 200) {
                    otpVerificationManifestResponseMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        OtpVerificationManifestResponse otpVerificationManifestResponse = new OtpVerificationManifestResponse();
                        otpVerificationManifestResponse.setStatus(false);
                        otpVerificationManifestResponse.setDescription(jObjError.getString("description"));
                        otpVerificationManifestResponseMutableLiveData.setValue(otpVerificationManifestResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpVerificationManifestResponse> call, Throwable t) {
                OtpVerificationManifestResponse ob = new OtpVerificationManifestResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                otpVerificationManifestResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return otpVerificationManifestResponseMutableLiveData;
    }

    @Override
    public LiveData<ProofOfPickupResponse> SendProofOfPickup(String authToken, ProofOfPickupRequest proofOfPickupRequest) {
        MutableLiveData<ProofOfPickupResponse> proofOfPickupResponseMutableLiveData = new MutableLiveData<>();

        retrofitService.proofofpickup(urlLoc(SEND_POP_SMS_TO_VENDOR), authToken, proofOfPickupRequest).enqueue(new Callback<ProofOfPickupResponse>() {
            @Override
            public void onResponse(Call<ProofOfPickupResponse> call, Response<ProofOfPickupResponse> response) {
                if (response.code() == 200) {
                    proofOfPickupResponseMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        ProofOfPickupResponse proofOfPickupResponse = new ProofOfPickupResponse();
                        proofOfPickupResponse.setStatus(false);
                        proofOfPickupResponse.setDescription(jObjError.getString("description"));
                        proofOfPickupResponseMutableLiveData.setValue(proofOfPickupResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProofOfPickupResponse> call, Throwable t) {
                ProofOfPickupResponse ob = new ProofOfPickupResponse();
                ob.setDescription(t.getMessage());
                ob.setStatus(false);
                proofOfPickupResponseMutableLiveData.setValue(ob);
                System.out.println("DASHHHHH Live  " + t.getMessage());
            }
        });
        return proofOfPickupResponseMutableLiveData;
    }

    @Override
    public LiveData<TrainingResponse> TrainingVideos(String authToken, TrainingRequest trainingRequest) {
        MutableLiveData<TrainingResponse> trainingVideosMutableLiveData = new MutableLiveData<>();

        retrofitService.training(urlLoc(TRAINING_UNIFY_DETAILS),authToken).enqueue(new Callback<TrainingResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrainingResponse> call, @NonNull Response<TrainingResponse> response) {

                if (response.code() == 200) {
                    trainingVideosMutableLiveData.setValue(response.body());
                } else {
                    JSONObject jObjError;
                    try {
                        jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        TrainingResponse trainingResponse = new TrainingResponse();
                        trainingResponse.setSuccess(false);
                        trainingResponse.setErrorCode(jObjError.getString("description"));
                        trainingVideosMutableLiveData.setValue(trainingResponse);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<TrainingResponse> call, @NonNull Throwable t) {
                TrainingResponse ob = new TrainingResponse();
                ob.setErrorCode(t.getMessage());
                ob.setSuccess(false);
                trainingVideosMutableLiveData.setValue(ob);
            }
        });
        return trainingVideosMutableLiveData;
    }


    @Override
    public LiveData<ImageUplaodResponse> uploadCommitImage(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        return null;
    }

    private String urlLoc(String URL_KEY) {
        return iPreferenceHelper.getURL().get(URL_KEY).getAsString();
    }
}


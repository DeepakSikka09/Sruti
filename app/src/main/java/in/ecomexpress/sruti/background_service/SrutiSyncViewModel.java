package in.ecomexpress.sruti.background_service;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.CommitResponse;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.model.signature.ProofOfPickupRequest;
import in.ecomexpress.sruti.model.signature.ProofOfPickupResponse;
import in.ecomexpress.sruti.model.signature.seller_details_list;
import in.ecomexpress.sruti.model.starttrip.ImageUplaodResponse;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.repo.remote.RetrofitService;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.common_files.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Menifest_Data_Master;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

import static in.ecomexpress.sruti.utils.common_files.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.COMMIT_IMAGE_PACKET;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.COMMIT_PACKET;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.FIRST_INSCAN;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.MANIFEST;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SEND_POP_SMS_TO_VENDOR;
import static in.ecomexpress.sruti.utils.common_files.GlobalConstant.DynamicAppUrl.SRUTI_SEND_LOCATION;
import static in.ecomexpress.sruti.utils.common_files.Helper.updateLocationWithData;

import androidx.lifecycle.LiveData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deepak on 11/11/19.
 */

public class SrutiSyncViewModel {
    private IPreferenceHelper iPreferenceHelper;
    private RetrofitService retrofitService;
    private SrutiDatabase mAppDatabase;


    public SrutiSyncViewModel(IPreferenceHelper iPreferenceHelper, RetrofitService retrofitService, SrutiDatabase mAppDatabase) {
        this.iPreferenceHelper = iPreferenceHelper;
        this.retrofitService = retrofitService;
        this.mAppDatabase = mAppDatabase;
    }

    void getUnSyncImage() {
        try {
            List<ImageModel> obj = ThreadGeneric.runThread(mAppDatabase.imageDAO().getUnSyncImageTEST(Constants.COMMIT_PENDING));
            Image_Package(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Image_Package(List<ImageModel> imageModels) {
        ThreadGeneric.executeCall(() -> {
            for (ImageModel imageModel : imageModels) {
                File file = new File(imageModel.getFilePath());
                Map<String, RequestBody> maprequest = new HashMap<>();
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageModel.getImage_name(), requestFile);
                maprequest.put("image_code", RequestBody.create(MediaType.parse("text/plain"), imageModel.getImage_code()));
                maprequest.put("image_type", RequestBody.create(MediaType.parse("text/plain"), imageModel.getImage_type()));
                maprequest.put("manifest_id", RequestBody.create(MediaType.parse("text/plain"), imageModel.getManifest_id()));
                maprequest.put("image_name", RequestBody.create(MediaType.parse("text/plain"), imageModel.getImage_name()));
                imageUploadRealTime(body, maprequest, imageModel.getImage_name());
            }
        });

    }

    private void imageUploadRealTime(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody, String image_code) {
        try {
            if (!TextUtils.isEmpty(iPreferenceHelper.getCode())) {
                if (TextUtils.isEmpty(urlLoc(COMMIT_IMAGE_PACKET)))
                    return;
                requestBody.put("emp_code", RequestBody.create(MediaType.parse("text/plain"), iPreferenceHelper.getCode()));
                retrofitService.uploadCommitImage(urlLoc(COMMIT_IMAGE_PACKET), imageFile, requestBody).enqueue(new Callback<ImageUplaodResponse>() {
                    @Override
                    public void onResponse(Call<ImageUplaodResponse> call, Response<ImageUplaodResponse> response) {
                        if (response.body() != null) {
                            ThreadGeneric.executeCall(() -> {
                                mAppDatabase.imageDAO().updateImageStatus(1, response.body().getResponse().getImage_id(), response.body().getResponse().getImage_key(), response.body().getResponse().getImage_key());
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUplaodResponse> call, Throwable t) {
                        System.out.println("Commit_Image " + t.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    void getUnSyncedCommitPacket(Context context) {
        try {
            ThreadGeneric.executeCall(() -> {

                List<PushApi> objCommit = ThreadGeneric.runThread(mAppDatabase.pushApiDAO().UnSyncCommitList(Constants.SHIPMENT_STATUS, 10, 0));
                if (objCommit != null) {
                    CommitPacketData commitPacketData = null;

                    for (PushApi pushApi : objCommit) {

                        try {
                            if (pushApi.getFileUrl() != null) {
                                String data = getTextFileData(pushApi.getFileUrl());
                                String fileName = pushApi.getCompositeKey();
                                commitPacketData = new ObjectMapper().readValue(data, CommitPacketData.class);
                            }
                            if (commitPacketData != null) {
                                uploadCommitPacket(commitPacketData, context, pushApi.getCompositeKey());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }

    private void uploadCommitPacket(CommitPacketData commit, Context context, String fileName) {
        if (TextUtils.isEmpty(urlLoc(COMMIT_PACKET)))
            return;
        retrofitService.doCommitApiCall(urlLoc(COMMIT_PACKET), iPreferenceHelper.getAuthToken(), commit).enqueue(new Callback<CommitResponse>() {
            @Override
            public void onResponse(Call<CommitResponse> call, Response<CommitResponse> response) {

                if (response.body() != null && response.body().getStatus()) {
                    ThreadGeneric.executeCall(() -> {
                        System.out.println("commit_process" + commit.getManifest_process().size());
                        if (commit.getManifest_process().size() > 0 && (commit.getManifest_process().get(0).getManifest_type().equals("P") || commit.getManifest_process().get(0).getManifest_type().equals("PR"))) {//commit.getManifest_process().get(0).getPickup_location_id() == null

                            //mAppDatabase.pushApiDAO().updateCommitStatus("1", ""+response.body().getResponse().getManifestIds());
                            mAppDatabase.pushApiDAO().updateCommitStatus("1", commit.getManifest_process().get(0).getManifest_no() + "_" + commit.getManifest_process().get(0).getPickup_location_id());
                            mAppDatabase.callManifestDao().updateManifestList(String.valueOf(Constants.COMMIT_SERVER_SYNC), response.body().getResponse().getManifestIds());

                            //updateLocationWithData(context, String.valueOf(commit.getManifest_process().get(0).getManifest_no()), commit.getManifest_process().get(0).getLocation_latitude() ,commit.getManifest_process().get(0).getLocation_longitude(),"PICKED");

                            if (!iPreferenceHelper.is_Ecom_Vehicle()) {
                                updateLocationWithData(context, String.valueOf(commit.getManifest_process().get(0).getManifest_no()), "PICKED", commit.getManifest_process().get(0).getLocation_latitude(), commit.getManifest_process().get(0).getLocation_longitude());

                            }
                            //     deleteFile(context, fileName);
                            mAppDatabase.callManifestDao().deleteCommitedShipment(response.body().getResponse().getManifestIds());
                        } else {
                            mAppDatabase.pushApiDAO().updateCommitStatusWithRecci("1", "0_" + commit.getManifest_process().get(0).getPickup_location_id());
                            mAppDatabase.callManifestDao().updateManifestListWithRecci(String.valueOf(Constants.COMMIT_SERVER_SYNC), "0_" + commit.getManifest_process().get(0).getPickup_location_id());

                        }

                    });
                }
            }


            @Override
            public void onFailure(Call<CommitResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void getFirstInScanPacket() {
        try {
            List<FirstInscan> objFirstInScan = ThreadGeneric.runThread(mAppDatabase.firstInscanDao().getFirstInScan(0));
            ThreadGeneric.executeCall(() -> {
                for (FirstInscan firstInscan : objFirstInScan) {
                    FirstScanRequest firstInScanRequest = null;
                    try {
                        firstInScanRequest = new ObjectMapper().readValue(firstInscan.getRequestData(), FirstScanRequest.class);
                        // firstInScanRequest.setStart_Time(Calendar.getInstance().getTimeInMillis() + "");
                        if (firstInScanRequest != null)
                            uploadFirstInScanPacket(firstInScanRequest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadFirstInScanPacket(FirstScanRequest firstInScanRequest) {
        Log.d("firstScanRequestSync ", firstInScanRequest.toString());

        if (TextUtils.isEmpty(urlLoc(FIRST_INSCAN)))
            return;
        retrofitService.callFirstScanApi(urlLoc(FIRST_INSCAN), iPreferenceHelper.getAuthToken(), firstInScanRequest).enqueue(new Callback<FirstScanResponse>() {
            @Override
            public void onResponse(Call<FirstScanResponse> call, Response<FirstScanResponse> response) {

                if (response.body() != null && response.body().getStatus()) {
                    ThreadGeneric.executeCall(() -> {
                        mAppDatabase.firstInscanDao().updateFirstInScan(1, firstInScanRequest.getManifest_id());
                        mAppDatabase.callManifestDao().inScanCommitPacket(1, firstInScanRequest.getManifest_id());
                    });
                }

            }

            @Override
            public void onFailure(Call<FirstScanResponse> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    void combineManifestData() {
        retrofitService.getManifestDetail(urlLoc(MANIFEST) + iPreferenceHelper.getRouteID(), iPreferenceHelper.getAuthToken(), true).enqueue(new Callback<Menifest_Data_Master>() {
            @Override
            public void onResponse(Call<Menifest_Data_Master> call, Response<Menifest_Data_Master> response) {
                Menifest_Data_Master menifest_data = response.body();
                if (menifest_data != null) {
                    ThreadGeneric.executeCall(() -> {
                        if (menifest_data.getStatus()) {
                            try {
                                ArrayList<Shipment_Detail> shipment_detailsinsert = new ArrayList<>();
                                if (menifest_data.getResponse() != null && menifest_data.getResponse().getManifest_list_data() != null) {
                                    for (Manifest_List manifest_list : menifest_data.getResponse().getManifest_list_data()) {
                                        ArrayList<Shipment_Detail> shipment_details = manifest_list.getShipment_details();

                                        if (shipment_details != null && shipment_details.size() > 0) {
                                            for (Shipment_Detail shipment_detail : shipment_details) {
                                                shipment_detail.setManifestNoInchild(manifest_list.getManifest_No());
                                                shipment_detail.setLocation_type(manifest_list.getLocation_type());
                                            }
                                            shipment_detailsinsert.addAll(shipment_details);
                                        }
                                    }
                                }
                                try {
                                    iPreferenceHelper.setCutOffTime(menifest_data.getResponse().getManifest_cutoff_time());
                                    iPreferenceHelper.setRouteID(menifest_data.getResponse().getRoute_id());
                                    // iPreferenceHelper.setLastSyncTime(menifest_data.getResponse().getLast_sync_time());
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                                List<Manifest_List> tempList = new ArrayList<>();
                                List<Manifest_List> manifestList = menifest_data.getResponse().getManifest_list_data();
                                for (Manifest_List mList : manifestList) {
                                    mList.setLast_sync_time(menifest_data.getResponse().getLast_sync_time());
                                    tempList.add(mList);
                                }
                                mAppDatabase.callManifestDao().insertShipmentDetail(tempList, shipment_detailsinsert);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<Menifest_Data_Master> call, Throwable t) {
                System.out.println(t.getStackTrace());
            }
        });

    }


    private String urlLoc(String URL_KEY) {
        try {
            if (iPreferenceHelper.getURL() != null)
                return iPreferenceHelper.getURL().get(URL_KEY).getAsString();
            else return "";
        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }


    public String getTextFileData(String fileName) {
        StringBuilder text = new StringBuilder();
        try {
            FileInputStream isr = new FileInputStream(new File(fileName));
            InputStreamReader inputreader = new InputStreamReader(isr);
            BufferedReader br = new BufferedReader(inputreader);
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line + '\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e("Error!", "Error occured while reading text file from Internal Storage!");

        }

        return text.toString();

    }


    public void deleteFile(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        file.delete();
    }


    public void startLiveTracking(Context context) {
        try {
            // accuray from backend
            DashboardActivity.lt.startTrackingWithParameters(context, context.getString(R.string.app_name), Constants.VERSION_NAME, "FirstMile", iPreferenceHelper.getCode(), iPreferenceHelper.getLocationCode(), iPreferenceHelper.getSelfVehicleType(), iPreferenceHelper.getAuthToken(), iPreferenceHelper.getLiveTrackingId(), "start", 5, urlLoc(SRUTI_SEND_LOCATION), 50, 60000, 10, DISTANCE_API_KEY, 200, 100);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public double getGeoFenceRadius() {
        return Double.parseDouble(iPreferenceHelper.get_pickup_geofencing_radius());
    }

    public double getLatitudeFromPreference() {
        return iPreferenceHelper.getCurrentLatitude();
    }

    public double getLongitudeFromPreference() {
        return iPreferenceHelper.getCurrentLongitude();
    }


    void getPopData(Context context) {
        try {
            ThreadGeneric.executeCall(() -> {

                List<PopData> popDataList = ThreadGeneric.runThread(mAppDatabase.popData().getPopList());
                if (popDataList != null) {

                    hitPopAPI(popDataList, context);
                }
            });
        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }


    private void hitPopAPI(List<PopData> popDataList, Context activity) {
        List<Long> popData = new ArrayList<>();
        if (popDataList.size() > 0) {
            for (int i = 0; i < popDataList.size(); i++) {
                popData.add(popDataList.get(i).getManifest_ids());
            }
            ArrayList<seller_details_list> sellerDetailsRequestArrayList = new ArrayList<>();
            seller_details_list sellerDetailsRequest = new seller_details_list();
            sellerDetailsRequest.setSeller_phone_no(popDataList.get(0).getSeller_phone_no());
            sellerDetailsRequest.setManifest_ids(popData);
            sellerDetailsRequestArrayList.add(sellerDetailsRequest);

            ProofOfPickupRequest otpVerificationManifestRequest = new ProofOfPickupRequest();
            otpVerificationManifestRequest.setRoute_id(iPreferenceHelper.getRouteID());
            otpVerificationManifestRequest.setSeller_details_list(sellerDetailsRequestArrayList);
            try {
                if (TextUtils.isEmpty(urlLoc(SEND_POP_SMS_TO_VENDOR)))
                    return;

                retrofitService.proofofpickup(urlLoc(SEND_POP_SMS_TO_VENDOR), iPreferenceHelper.getAuthToken(), otpVerificationManifestRequest).enqueue(new Callback<ProofOfPickupResponse>() {
                    @Override
                    public void onResponse(Call<ProofOfPickupResponse> call, Response<ProofOfPickupResponse> response) {
                        if (response.body() != null && response.body().getStatus()) {
                            Log.d("check_pop_back", " done");
                            ThreadGeneric.executeCall(() -> {

                                mAppDatabase.popData().UpdatePopStatus(String.valueOf(Constants.POP_SUCCESS), popData);

                            });

                        } else {
                            ThreadGeneric.executeCall(() -> {

                                mAppDatabase.popData().UpdatePopStatus(String.valueOf(Constants.POP_PENDING), popData);
                            });
                            Log.d("check_pop_back", "not done");
                        }

                    }

                    @Override
                    public void onFailure(Call<ProofOfPickupResponse> call, Throwable t) {
                        ThreadGeneric.executeCall(() -> {

                            mAppDatabase.popData().UpdatePopStatus(String.valueOf(Constants.POP_PENDING), popData);
                        });
                        // System.out.println(t.getMessage());
                    }
                });
            } catch (Exception e) {
                ThreadGeneric.executeCall(() -> {

                    mAppDatabase.popData().UpdatePopStatus(String.valueOf(Constants.POP_PENDING), popData);
                });
                e.printStackTrace();

            }
        }
    }
}

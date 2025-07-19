package in.ecomexpress.sruti.ui.dashboard.signature;


import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dagger.Module;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestRequest;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestResponse;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.model.signature.ProofOfPickupRequest;
import in.ecomexpress.sruti.model.signature.ProofOfPickupResponse;
import in.ecomexpress.sruti.model.signature.seller_details_list;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


/**
 * Created by Deepak Sikka
 *
 * @company Ecom Express.in
 * on 26/7/19.
 */
@Module
public class SignatureViewModel extends BaseViewModel<ISignatureNavigator> {

  
    private MediatorLiveData<OtpVerificationManifestResponse> otpVerificationManifestResponseMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<ProofOfPickupResponse> pickupResponseMediatorLiveData = new MediatorLiveData<>();




    //TODO BY anubhav
    public LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifest_no) {
        return getDataManager().getSpecificManifestDetail(manifest_no);
    }


    public MediatorLiveData<OtpVerificationManifestResponse> getOtpVerificationManifestResponseMediatorLiveData() {
        return otpVerificationManifestResponseMediatorLiveData;
    }

    public MediatorLiveData<ProofOfPickupResponse> getPickupResponseMediatorLiveData() {
        return pickupResponseMediatorLiveData;
    }

    public SignatureViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public LiveData<List<Shipment_Detail>> getAllParentShipmentlist(long manifest_no) {
        return getDataManager().getAllParentShipmentlist(manifest_no);
    }

    public void onSubmitClick() {

        getNavigator().saveSignature();
    }


    public void uploadLocalImage(ImageModel imageModel) {
        getDataManager().saveImage(imageModel);
    }

    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedManifestQuery(commit_status, commit_failed, manifestno);
    }

    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedCommitPacket(commit_status, commit_failed, manifestno);
    }

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }

    public void createCommitPacketNew(HashMap<Long, List<Shipment_Detail>> shipmentsDetails, ArrayList<Recci> recci, ArrayList<Image_Response> image_response, long pickup_location_id, double wayLatitude, double wayLongitude, ArrayList<String> manifest_type) {
        try {
            Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = shipmentsDetails.entrySet().iterator();
            while (ob.hasNext()) {

                CommitPacketData commitPacketData = new CommitPacketData();
                commitPacketData.setTrip_id(Long.valueOf(getDataManager().getTripID()));
                commitPacketData.setEmp_code(getDataManager().getCode());
                if (getDataManager().getParent()) {
                    commitPacketData.setFe_type("parent");
                } else if (getDataManager().getChild()) {
                    commitPacketData.setFe_type("child");
                }
                Manifest_process manifest_process = new Manifest_process();

                ArrayList<ShipmentDetail> list_ShipmentDetails = new ArrayList<>();
                Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();

                List<Shipment_Detail> shipmentloc = shipmentsDetails.get(pair.getKey());
                if (shipmentloc != null && shipmentloc.size() > 0) {
                    manifest_process.setManifest_no(shipmentloc.get(0).getManifestNoInchild());
                    System.out.println("shipmentloc.get(0).getManifestNoInchild() " + shipmentloc.get(0).getManifestNoInchild());
                } else {
                    manifest_process.setManifest_no(pair.getKey());
                }
                for (int i = 0; i < shipmentloc.size(); i++) {

                    ShipmentDetail shipment_detail = new ShipmentDetail();
                    shipment_detail.setAirway_bill_number(shipmentloc.get(i).getAirwaybill_number());
                    shipment_detail.setStatus_code(shipmentloc.get(i).getStatus());
                    shipment_detail.setDate_time(shipmentloc.get(i).getDateTime());
                    shipment_detail.setVehicle_no(shipmentloc.get(i).getVehicle());
                    shipment_detail.setIs_bp_validated(shipmentloc.get(i).getIs_bp_validated());
                    /**
                     * As Discuss with deepak and sonu
                     shipment_detail.setManifest_shipment_id(shipmentloc.get(i).getManifest_shipment_id());
                     shipment_detail.setReason_id(shipmentloc.get(i).getReason_id());
                     */
                    shipment_detail.setReason_code(shipmentloc.get(i).getReason_code());
                    shipment_detail.setAdvance(shipmentloc.get(i).isAdvance());
                    list_ShipmentDetails.add(shipment_detail);
                }
                if (pickup_location_id != 0)
                    manifest_process.setPickup_location_id(pickup_location_id);

                for (int i = 0; i < manifest_type.size(); i++) {
                    if (manifest_type.get(i).equals(null)) {
                        manifest_process.setManifest_type("P");
                    } else {
                        manifest_process.setManifest_type(manifest_type.get(i));
                    }
                }

                manifest_process.setLocation_longitude(String.valueOf(wayLongitude));
                manifest_process.setLocation_latitude(String.valueOf(wayLatitude));
                manifest_process.setCommit_location_radius("100");
                manifest_process.setCommit_time(Calendar.getInstance().getTimeInMillis() + "");
                manifest_process.setParentmanifestNo("NA");
                manifest_process.setStatus_code("406");
                manifest_process.setShipments(list_ShipmentDetails);
                manifest_process.setRecci(recci);
                manifest_process.setImage_response(image_response);

                ArrayList<Manifest_process> manifest_commit_package = new ArrayList<>();
                manifest_commit_package.add(manifest_process);

                commitPacketData.setManifest_process(manifest_commit_package);
                saveCommit(commitPacketData);


            }


        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void saveCommit(CommitPacketData commitPacketData) {

        try {
            for (Manifest_process manifestProcess : commitPacketData.getManifest_process()) {

                PushApi pushApi = new PushApi();
                pushApi.setCompositeKey(manifestProcess.getManifest_no() + "_" + manifestProcess.getPickup_location_id());
                pushApi.setManifestNo(manifestProcess.getManifest_no());

                pushApi.setAuthtoken(getDataManager().getAuthToken());
                pushApi.setEmpId(getDataManager().getCode());

                // pushApi.setRequestData(new ObjectMapper().writeValueAsString(commitPacketData));
                pushApi.setShipmentStatus(0);
                pushApi.setApiVer(version);
                pushApi.setAppId("1");
                pushApi.setApiVer("mobile");

                getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestProcess.getManifest_no());
                        failedCommitPacket(1, Constants.PICKED, manifestProcess.getManifest_no());

                        if (getDataManager().getParent() || getDataManager().getAsParentChild()) {
                            inScanCommitPacket(manifestProcess.getManifest_no());
                        } else {
                            updateCommit_DataTable(manifestProcess.getManifest_no());
                        }

                        updateManifestList(manifestProcess.getManifest_no());

                    }
                }));
                ThreadGeneric.executeCall(() -> {
                    System.out.println("manifestProcess " + manifestProcess.getManifest_no());
                    try {
                        getNavigator().saveobject(manifestProcess.getManifest_no() + "_" + manifestProcess.getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                });
            }
            getNavigator().nextScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCommit_DataTable(long manifestno) {
        getDataManager().updateCommit_DataTable(manifestno);
    }

    private void updateManifestList(Long manifest_no) {
        ArrayList<Long> ob = new ArrayList<Long>();
        ob.add(manifest_no);
        getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_PICKED), ob);
    }

    void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }


    void hitManifestVerify(long registrd_mobile, ArrayList<Long> manifestNo, Activity activity, String mobile_number_type) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        OtpVerificationManifestRequest otpVerificationManifestRequest = new OtpVerificationManifestRequest();
        otpVerificationManifestRequest.setOtp_verification_source(mobile_number_type);
        otpVerificationManifestRequest.setManifest_id_list(manifestNo);
        otpVerificationManifestRequest.setPickup_route_id(getDataManager().getRouteID());
        otpVerificationManifestRequest.setRegistered_mobile_number(registrd_mobile);
        LiveData<OtpVerificationManifestResponse> obj = getDataManager().otpVerificationManifest(getDataManager().getAuthToken(), otpVerificationManifestRequest);
        otpVerificationManifestResponseMediatorLiveData.addSource(obj, selfDropResponse -> {
            dialog.dismiss();
            otpVerificationManifestResponseMediatorLiveData.removeSource(obj);
            otpVerificationManifestResponseMediatorLiveData.setValue(selfDropResponse);
        });
    }


    void SendProofOfPickup(Activity activity, ArrayList<seller_details_list> sellerDetailsRequest) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        ProofOfPickupRequest otpVerificationManifestRequest = new ProofOfPickupRequest();
        otpVerificationManifestRequest.setRoute_id(getDataManager().getRouteID());
        otpVerificationManifestRequest.setSeller_details_list(sellerDetailsRequest);
        LiveData<ProofOfPickupResponse> obj = getDataManager().SendProofOfPickup(getDataManager().getAuthToken(), otpVerificationManifestRequest);
        pickupResponseMediatorLiveData.addSource(obj, selfDropResponse -> {
            dialog.dismiss();
            pickupResponseMediatorLiveData.removeSource(obj);
            pickupResponseMediatorLiveData.setValue(selfDropResponse);
        });
    }


    public void PushPopData(PopData popData) {


        getCompositeDisposable().add(getDataManager().pushPopData(popData).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d("check_d","ok");
            }
        }));
    }
}



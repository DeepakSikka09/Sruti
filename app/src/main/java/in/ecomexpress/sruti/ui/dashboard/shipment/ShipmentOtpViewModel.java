package in.ecomexpress.sruti.ui.dashboard.shipment;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpRequest;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpResponse;
import in.ecomexpress.sruti.model.OtpRequest.VerifyPickUpOtpRequest;
import in.ecomexpress.sruti.model.OtpRequest.VerifyPickUpOtpResponse;
import in.ecomexpress.sruti.model.masterdata.SkipOtpMainResponse;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

@Module
public class ShipmentOtpViewModel extends BaseViewModel {
    private MediatorLiveData<SendPickUpOtpResponse> sendPickUpOtp = new MediatorLiveData<>();
    private MediatorLiveData<SkipOtpMainResponse> skipOtpMainResponseMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<VerifyPickUpOtpResponse> verifyPickUpOtp = new MediatorLiveData<>();


    public ShipmentOtpViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public MediatorLiveData<SkipOtpMainResponse> getSkipOtpMainResponseMediatorLiveData() {
        return skipOtpMainResponseMediatorLiveData;
    }

    public MediatorLiveData<SendPickUpOtpResponse> getSendPickUpOtp() {
        return sendPickUpOtp;
    }

    public MediatorLiveData<VerifyPickUpOtpResponse> getVerifyPickUpOtp() {
        return verifyPickUpOtp;
    }

    public void updateSharedManifestStatus(List<Long> manifestNo, String mobNoType, Boolean shipment_status) {
        getDataManager().updateSharedManifestStatus(manifestNo, mobNoType, shipment_status);
    }


    void hitOtpApi(long mobileNumber, long pickup_location_id, Activity activity, String mobile_number_type) {


        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        SendPickUpOtpRequest sendPickUpOtpRequest = new SendPickUpOtpRequest();
        sendPickUpOtpRequest.setEmployee_code(getDataManager().getCode());
        sendPickUpOtpRequest.setMobile_number(mobileNumber);
        sendPickUpOtpRequest.setMobile_number_type(mobile_number_type);
        sendPickUpOtpRequest.setPickup_route_id(getDataManager().getRouteID());
        sendPickUpOtpRequest.setPickup_location_id((int) pickup_location_id);

        LiveData<SendPickUpOtpResponse> obj = getDataManager().sendPickupOtp(getDataManager().getAuthToken(), sendPickUpOtpRequest);
        sendPickUpOtp.addSource(obj, selfDropResponse -> {
            dialog.dismiss();
            sendPickUpOtp.removeSource(obj);
            sendPickUpOtp.setValue(selfDropResponse);
        });
    }

    void hitVerifyApi(String otpCollection, long contact_no, Activity activity, ArrayList<Long> manifestNoArray, String reason_code) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        VerifyPickUpOtpRequest verifyPickUpOtpRequest = new VerifyPickUpOtpRequest();
        verifyPickUpOtpRequest.setEmployee_code(getDataManager().getCode());
        verifyPickUpOtpRequest.setOtp(otpCollection.toString());
        verifyPickUpOtpRequest.setVerified_manifest_ids(manifestNoArray);
        verifyPickUpOtpRequest.setPickup_route_id(getDataManager().getRouteID());
        verifyPickUpOtpRequest.setMobile_number(contact_no);
        //-----
        verifyPickUpOtpRequest.setSkip_reason_code(reason_code);
        if (!reason_code.equalsIgnoreCase("")) {
            verifyPickUpOtpRequest.setSkip_pickup_otp(true);
        } else {
            verifyPickUpOtpRequest.setSkip_pickup_otp(false);
        }


        LiveData<VerifyPickUpOtpResponse> obj = getDataManager().verifyPickupOtp(getDataManager().getAuthToken(), verifyPickUpOtpRequest);
        verifyPickUpOtp.addSource(obj, selfDropResponse -> {
            dialog.dismiss();
            verifyPickUpOtp.removeSource(obj);
            verifyPickUpOtp.setValue(selfDropResponse);
        });
    }

    public LiveData<List<Manifest_List>> loadManifest() {
        return getDataManager().getManifestDetail();
    }


    void hitSkipOtpReasonListAPI(Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        LiveData<SkipOtpMainResponse> skipOtpMainResponseLiveData = getDataManager().sendReasonList(getDataManager().getAuthToken());
        skipOtpMainResponseMediatorLiveData.addSource(skipOtpMainResponseLiveData, skipOtpMainResponse -> {
            dialog.dismiss();
            skipOtpMainResponseMediatorLiveData.removeSource(skipOtpMainResponseLiveData);
            skipOtpMainResponseMediatorLiveData.setValue(skipOtpMainResponse);
        });
    }


}

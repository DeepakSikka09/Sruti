package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestRequest;
import in.ecomexpress.sruti.model.OtpRequest.OtpVerificationManifestResponse;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpRequest;
import in.ecomexpress.sruti.model.OtpRequest.SendPickUpOtpResponse;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IManifestReasonCodeNavigation;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function5;

@Module
public class ReasonCodeViewModel extends BaseViewModel<IManifestReasonCodeNavigation> {

    private MediatorLiveData<List<Shipment_Detail>> checkreasontodo = new MediatorLiveData<>();
    public ObservableField<String> vendorAssignedCount = new ObservableField<>("");
    public ObservableField<String> warehouseAssignedcount = new ObservableField<>("");
    public ObservableField<String> recciAssignedcount = new ObservableField<>("");
    public ObservableField<String> recci_venAssignedcount = new ObservableField<>("");
    public ObservableField<String> recci_wrhAssignedcount = new ObservableField<>("");
    public ObservableField<String> totalAssignedCount = new ObservableField<>("");
    private MediatorLiveData<OtpVerificationManifestResponse>otpVerificationManifestResponseMediatorLiveData = new MediatorLiveData<>();


    public MediatorLiveData<OtpVerificationManifestResponse> getOtpVerificationManifestResponseMediatorLiveData() {
        return otpVerificationManifestResponseMediatorLiveData;
    }

    public ReasonCodeViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
    public void getAllShipmentListTODO(long manifest_no) {
        LiveData<List<Shipment_Detail>> ob = getDataManager().getAllShipmentlist(manifest_no);
        checkreasontodo.addSource(ob, shipment_details -> {
            checkreasontodo.removeSource(ob);
            checkreasontodo.setValue(shipment_details);
        });
    }

    public LiveData<List<Shipment_Detail>> getAllShipmentTODO() {
        return checkreasontodo;
    }

    public void markUndelivered(ArrayList<Shipment_Detail> shipment_detail, int reason_id, String reason_code, String vehicleType) {
        List<Shipment_Detail> list = new ArrayList<>();
        for (Shipment_Detail shipmentsDetail1 : shipment_detail) {
            shipmentsDetail1.setStatus(Constants.FAILED);
            shipmentsDetail1.setReason_id(reason_id);
            shipmentsDetail1.setReason_code(reason_code);
            shipmentsDetail1.setVehicle(vehicleType);
            shipmentsDetail1.setChecked(false);
            shipmentsDetail1.setDateTime(Calendar.getInstance().getTimeInMillis());
            list.add(shipmentsDetail1);
        }
        Shipment_Detail[] shipmentsDetails = new Shipment_Detail[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shipmentsDetails[i] = list.get(i);
        }
        try {

            getCompositeDisposable().add(getDataManager()
                    .markUndelivered(shipmentsDetails)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            getAllCategoryAssignedCount();
                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }

    }


    public void createCommitPacketNew(ArrayList<Shipment_Detail> shipmentsDetails, ArrayList<Recci> recci, long pickup_location_id, String manifest_type, double wayLatitude, double wayLongitude) {
        try {
            CommitPacketData commitPacketData = new CommitPacketData();
            commitPacketData.setTrip_id(Long.valueOf(getDataManager().getTripID()));
            commitPacketData.setEmp_code(getDataManager().getCode());
            if (getDataManager().getParent()) {
                commitPacketData.setFe_type("parent");
            } else if (getDataManager().getChild()) {
                commitPacketData.setFe_type("child");
            }

            ArrayList<Manifest_process> manifest_commit_package = new ArrayList<>();
            Manifest_process manifest_process = new Manifest_process();

            if (shipmentsDetails != null && shipmentsDetails.size() > 0) {
                manifest_process.setManifest_no(shipmentsDetails.get(0).getManifestNoInchild());

            }
            if (pickup_location_id != 0)
                manifest_process.setPickup_location_id(pickup_location_id);

            manifest_process.setManifest_type(manifest_type);
            manifest_process.setLocation_longitude(String.valueOf(wayLongitude));
            manifest_process.setLocation_latitude(String.valueOf(wayLatitude));
            manifest_process.setCommit_location_radius("100");
            manifest_process.setCommit_time(Calendar.getInstance().getTimeInMillis() + "");
            manifest_process.setParentmanifestNo("NA");
            manifest_process.setStatus_code("406");

            ArrayList<ShipmentDetail> list_ShipmentDetails = new ArrayList<>();
            for (int i = 0; i < shipmentsDetails.size(); i++) {
                ShipmentDetail shipment_detail = new ShipmentDetail();
                shipment_detail.setAirway_bill_number(shipmentsDetails.get(i).getAirwaybill_number());
                shipment_detail.setStatus_code(shipmentsDetails.get(i).getStatus());
                shipment_detail.setDate_time(shipmentsDetails.get(i).getDateTime());
                shipment_detail.setVehicle_no(shipmentsDetails.get(i).getVehicle());
                shipment_detail.setReason_code(shipmentsDetails.get(i).getReason_code());
                shipment_detail.setAdvance(shipmentsDetails.get(i).isAdvance());
                shipment_detail.setIs_bp_validated(shipmentsDetails.get(i).getIs_bp_validated());
                list_ShipmentDetails.add(shipment_detail);

            }

            manifest_process.setShipments(list_ShipmentDetails);
            manifest_process.setRecci(recci);
            manifest_process.setImage_response(null);
            commitPacketData.setManifest_process(manifest_commit_package);
            manifest_commit_package.add(manifest_process);

            saveCommit(commitPacketData);

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void saveCommit(CommitPacketData commitPacketData) {
        PushApi pushApi = new PushApi();
        System.out.println("XXXXXXXXXXXXX  " + commitPacketData.getManifest_process().get(0).getManifest_no() + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());
        pushApi.setCompositeKey(commitPacketData.getManifest_process().get(0).getManifest_no() + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());
        pushApi.setManifestNo(commitPacketData.getManifest_process().get(0).getManifest_no());
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setShipmentStatus(0);
            pushApi.setEmpId(getDataManager().getCode());
            pushApi.setApiVer(version);
            pushApi.setAppId("1");
            pushApi.setApiVer("mobile");
            pushApi.setFirstInscanStatus(1);

            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    updateManifestList(commitPacketData.getManifest_process().get(0).getManifest_no());
                    getNavigator().saveobject(commitPacketData.getManifest_process().get(0).getManifest_no() + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));
                }
            }));

            getNavigator().nextScreen();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateManifestList(long manifestNo) {
        ArrayList<Long> ob = new ArrayList<Long>();
        ob.add(manifestNo);

        getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_PICKED), ob);
    }
    public void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }

    public void getAllCategoryAssignedCount() {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getVendorStatusCount(Constants.COMMIT_PENDING),
                getDataManager().getWarehouseStatusCount(Constants.COMMIT_PENDING),
                getDataManager().getRecciStatusCount(Constants.COMMIT_PENDING),
                getDataManager().getRecciVenStatusCount(Constants.COMMIT_PENDING),
                getDataManager().getRecciWrhStatusCount(Constants.COMMIT_PENDING),
                new Function5<Long, Long, Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long vendor, Long warehouse, Long recci, Long recci_ven, Long recci_wrh) throws Exception {


                        vendorAssignedCount.set(String.valueOf(vendor));
                        warehouseAssignedcount.set(String.valueOf(warehouse));
                        recciAssignedcount.set(String.valueOf(recci));
                        recci_venAssignedcount.set(String.valueOf(recci_ven));
                        recci_wrhAssignedcount.set(String.valueOf(recci_wrh));


                        Long allAssignedCount = vendor + warehouse + recci + recci_ven + recci_wrh;
                        totalAssignedCount.set(String.valueOf(allAssignedCount));

                        return vendor + warehouse + recci + recci_ven + recci_wrh;


                    }
                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {

            }
        }, throwable -> {
            throwable.printStackTrace();
        }));
    }

    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return getDataManager().callFirstScanApi(authToken, firstScanResponse);
    }
    public void updateInscanStatus(long manifestNumber) {
        getDataManager().updateInscanStatus(manifestNumber);
    }
    public void updateInscanToFirstScanTable(long manifestNumber) {
        getDataManager().updateInscanToFirstScanTable(manifestNumber);
    }
    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }
    public void updateIsScanStarted(long manifestno) {
        getDataManager().updateIsScanStarted(manifestno);
    }
    public void insertFirstScanData(FirstInscan firstInscan) {
        getDataManager().insertFirstScanData(firstInscan);
    }

    public double getGeoFenceRadius() {
        return Double.parseDouble(getDataManager().get_pickup_geofencing_radius());
    }
    public LiveData<List<Manifest_List>> loadManifest() {
        return getDataManager().getManifestDetail();
    }
    public void updateSharedManifestStatus(List<Long> manifestNo, String mobileNoType,Boolean shipment_status) {
        getDataManager().updateSharedManifestStatus(manifestNo, mobileNoType,shipment_status);
    }

    void hitManifestVerify(long mobileNumber, ArrayList<Long> manifestNo , Activity activity, String mobile_number_type) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        OtpVerificationManifestRequest otpVerificationManifestRequest = new OtpVerificationManifestRequest();
        otpVerificationManifestRequest.setOtp_verification_source(mobile_number_type);
        otpVerificationManifestRequest.setManifest_id_list(manifestNo);
        otpVerificationManifestRequest.setPickup_route_id(getDataManager().getRouteID());
        otpVerificationManifestRequest.setRegistered_mobile_number(mobileNumber);

        LiveData<OtpVerificationManifestResponse> obj = getDataManager().otpVerificationManifest(getDataManager().getAuthToken(), otpVerificationManifestRequest);
        otpVerificationManifestResponseMediatorLiveData.addSource(obj, selfDropResponse -> {
            dialog.dismiss();
            otpVerificationManifestResponseMediatorLiveData.removeSource(obj);
            otpVerificationManifestResponseMediatorLiveData.setValue(selfDropResponse);
        });
    }



}

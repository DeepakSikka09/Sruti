package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model;

import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.app.ProgressDialog;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.ecomexpress.sruti.model.RtoLockResponse;
import in.ecomexpress.sruti.model.RtoRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPResponse;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitRequest;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitResponse;
import in.ecomexpress.sruti.model.childCommitStatus.Child_Shipment_details;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.ChildCommitDialog;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces.IWarehouseNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function7;

public class WarehouseViewModel extends BaseViewModel<IWarehouseNavigator> {
    private int fetch_count = 0;
    public ArrayList<Child_Shipment_details> getChildCommitedList;
    public HashMap<Integer, ReasonCodeList> reasonCodeMaster = new HashMap<>();
    private boolean isAllChecked = false;
    boolean isAtleastoneScan=false;
    long manifestNo;
    public final ObservableField<String> sellerName = new ObservableField<>("");
    public final ObservableField<String> totalShipment = new ObservableField<>("");
    public final ObservableField<Boolean> isRtoLockCheck = new ObservableField<>(false);
    public final ObservableField<String> unpickedShipment = new ObservableField<>("");
    public final ObservableField<String> remainingShipment = new ObservableField<>("");
    public final ObservableField<String> advanceShipment = new ObservableField<>("");

    public ObservableField<String> getSellerName() {
        return sellerName;
    }

    public ObservableField<String> getTotalShipment() {
        return totalShipment;
    }

    public ObservableField<String> getPickedShipment() {
        return pickedShipment;
    }

    public ObservableField<String> getRemainingShipment() {
        return remainingShipment;
    }

    public ObservableField<String> getAdvanceShipment() {
        return advanceShipment;
    }

    public final ObservableField<String> pickedShipment = new ObservableField<>("");

    public ObservableField<String> getUnpickedShipment() {
        return unpickedShipment;
    }


    public WarehouseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onNextClick() {
        getNavigator().onNextClick();
    }

    public void onScanClick() {
        getNavigator().onScanClick();
    }

    public void onSynClick() {
        getNavigator().onSynClickEvent();
    }

    public void onCheckStatusClick() {
        getNavigator().onCheckStatusClick();
    }

    public void setManifestDetails(Manifest_List manifestList) {
        sellerName.set(manifestList.getManifest_details().getLocationName());
        getAllCategoryAssignedCount(manifestList.getManifest_No());
        manifestNo = manifestList.getManifest_No();

    }


    public void checkAll(ArrayList<Shipment_Detail> shipment_details) {
        isAllChecked = !isAllChecked;
        for (Shipment_Detail shipmentsDetail : shipment_details) {
            if (shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                shipmentsDetail.setChecked(isAllChecked);
            }
        }
        getNavigator().notifyAdapter();
    }

    public LiveData<Integer> getCount(long manifestNo) {
        return getDataManager().getScannedShipmentCount(Constants.PICKED, manifestNo);
    }

    public void updateShipmentCount(long manifestNo, String shipmentCount) {
        getDataManager().updateShipmentCount(manifestNo, shipmentCount);
    }

    public LiveData<List<Shipment_Detail>> getMpsCondition(long manifestNo) {
        return getDataManager().getMpsCondition(manifestNo);
    }


    public boolean isValidToMoveNext(ArrayList<Shipment_Detail> shipment_detail) {
        boolean isAllNonPending = false;

        try {

            for (Shipment_Detail shipmentsDetail : shipment_detail) {
                if (shipmentsDetail.getStatus() != null) {

                    if (!shipmentsDetail.getStatus().contains(Constants.PENDING)) {

                        isAllNonPending = true;
                     //   Log.d("check_entry", "true");
                    } else {
                        isAllNonPending = false;
                        break;
                    }
                } else {
                    isAllNonPending = false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }
        return isAllNonPending;
    }


    public LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest manifest_no) {
        return getDataManager().getAllRTOShipmentList(authToken, manifest_no);
    }

    public LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest) {
        return getDataManager().getUpdatedBpAwbRto(authToken, updatedBPRequest);
    }

    public void getAllCategoryAssignedCount(long manifest_no) {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getShipmentCount(Constants.PICKED, manifest_no),
                getDataManager().getShipmentCount(Constants.FAILED, manifest_no),
                getDataManager().getShipmentCount(Constants.PENDING, manifest_no),
                getDataManager().getAdvanceCount(true, manifest_no),
                getDataManager().getShipmentCount(Constants.RTO_STATUS_1, manifest_no),
                getDataManager().getShipmentCount(Constants.RTO_STATUS_2, manifest_no),
                getDataManager().getTotalCount(manifest_no),
                new Function7<Long, Long, Long, Long, Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long picked, Long unpicked, Long remaining, Long advance, Long rto1, Long rto2, Long total) throws Exception {

                        pickedShipment.set(String.valueOf(picked));
                        unpickedShipment.set(String.valueOf(unpicked + rto1 + rto2));
                        remainingShipment.set(String.valueOf(remaining));
                        advanceShipment.set(String.valueOf(advance));
                        totalShipment.set(String.valueOf(total));
                        return picked + remaining;

                    }
                }).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {

            }
        }, throwable -> {
            throwable.printStackTrace();
        }));
    }

    public LiveData<List<Shipment_Detail>> getAllShipmentList(long manifest_no) {
        return getDataManager().getAllShipmentlist(manifest_no);
    }
    public LiveData<Manifest_List> getUpdatedTime(ArrayList<Long> manifestNo) {
        return getDataManager().getUpdatedTime(manifestNo);
    }
    public LiveData<List<Shipment_Detail>> getScannedShipmentList(long manifest_no, List<String> status) {
        return getDataManager().getAllScannedShipmentList(manifest_no, status);
    }


    public void updateShipment(Long awb, String status, String vehicle, boolean isAdvance) {
        getDataManager().updateScannedAWBStatus(awb, status, vehicle, Calendar.getInstance().getTimeInMillis(), false, "",0,false);
    }

    public void assignUpdateIsScanStarted(long manifestno) {
        getDataManager().assignUpdateIsScanStarted(manifestno);

    }

    public void getChildApiResponse(ArrayList<Long> manifestNoArray, ProgressDialog dialogOnDataUpdate, ArrayList<Shipment_Detail> shipment_detail, WarehouseActivity globalShipmentListActivity) {
        fetch_count = 0;
        try {
            ChildCommitRequest childCommitRequest = new ChildCommitRequest();
            childCommitRequest.setEmp_code(getDataManager().getCode());
            childCommitRequest.setPickup_route_id(Long.valueOf(getDataManager().getRouteID()));
            childCommitRequest.setManifest_ids(manifestNoArray);
            checkChildStatusApi(getDataManager().getAuthToken(), childCommitRequest).observe(globalShipmentListActivity, shipment -> {
                try {
                    if (shipment != null) {
                        if (shipment.getStatus()) {
                            if (shipment.getResponse().getCommit_status()) {
                                getChildCommitedList = new ArrayList<>();

                                if (shipment.getResponse().getShipment_details() != null) {
                                    getChildCommitedList.addAll(shipment.getResponse().getShipment_details());
                                }
                                if (getChildCommitedList.size() > 0) {
                                    updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(), getChildCommitedList.get(fetch_count).getVehicle_no(), getChildCommitedList.get(fetch_count).getManifest_no(), true, dialogOnDataUpdate);
                                } else {
                                    getNavigator().showDialogBox(shipment.getDescription(), dialogOnDataUpdate);

                                }
                            } else {
                                if (shipment.getResponse().getShipment_details() == null)
                                    dialogOnDataUpdate.dismiss();
                                if (shipment.getDescription().equalsIgnoreCase("Childs not commited yet")) {
                                    ChildCommitDialog ob = new ChildCommitDialog();
                                    ob.ConfirmationDialog(globalShipmentListActivity, shipment.getResponse().getChild_details(), shipment.getDescription());
                                    ob.show(globalShipmentListActivity.getSupportFragmentManager(), "dialog");

                                    getChildCommitedList = new ArrayList<>();
                                    if (shipment.getResponse().getShipment_details() != null) {
                                        getChildCommitedList.addAll(shipment.getResponse().getShipment_details());
                                    }
                                    if (getChildCommitedList.size() > 0) {
                                        updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(), getChildCommitedList.get(fetch_count).getVehicle_no(), getChildCommitedList.get(fetch_count).getManifest_no(), true, dialogOnDataUpdate);
                                    }
                                } else {
                                    getNavigator().showDialogBox(shipment.getDescription(), dialogOnDataUpdate);
                                }
                            }
                        } else {
                            getNavigator().showDialogBox(shipment.getDescription(), dialogOnDataUpdate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertOrUpdateDataStatus(ProgressDialog dialogOnDataUpdate) {
        fetch_count = fetch_count + 1;
        if (fetch_count >= getChildCommitedList.size()) {//fetch count is starting from 0 that why here size - 1 and = used
            dialogOnDataUpdate.dismiss();
            getChildCommitedList.clear();
            fetch_count = 0;
        } else {
            updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(), getChildCommitedList.get(fetch_count).getVehicle_no(), getChildCommitedList.get(fetch_count).getManifest_no(), true, dialogOnDataUpdate);

        }
    }


    public void updatechildCommitShipment(long awb, String shipment_status, String vehicle, long manifest_no, boolean isChild, ProgressDialog dialogOnDataUpdate) {
        try {
            Log.d("update_count", "" + awb);
            getCompositeDisposable().add(getDataManager()
                    .updatechildCommitShipment(awb, shipment_status, vehicle, manifest_no, isChild).subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (aBoolean) {
                                getNavigator().notifyAdapter();
                                insertOrUpdateDataStatus(dialogOnDataUpdate);
                            }
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }
    }


    public LiveData<Boolean> isDataloading() {
        return isloadingcomplet;
    }

    MutableLiveData<Boolean> isloadingcomplet = new MutableLiveData<>();


    public LiveData<List<ReasonCodeList>> getPickupListToView(String reasonCode) {
        return getDataManager().getPickupList(reasonCode);
    }

    public void markUndelivered(ArrayList<Shipment_Detail> shipment_detail, int reason_id, String reason_code, String vehicleType) {
        List<Shipment_Detail> list = new ArrayList<>();
        boolean isAnyChecked = false;
        for (Shipment_Detail shipmentsDetail1 : shipment_detail) {
            if (shipmentsDetail1.isChecked()) {
                isAnyChecked = true;
                shipmentsDetail1.setStatus(Constants.FAILED);
                shipmentsDetail1.setReason_id(reason_id);
                shipmentsDetail1.setReason_code(reason_code);
                shipmentsDetail1.setVehicle(vehicleType);
                shipmentsDetail1.setChecked(false);
                shipmentsDetail1.setDateTime(Calendar.getInstance().getTimeInMillis());
                list.add(shipmentsDetail1);
            }

        }
        if (!isAnyChecked) {
            getNavigator().onErrorMessage("No Shipments Selected");
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
                            getAllCategoryAssignedCount(manifestNo);

                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }
        getNavigator().notifyAdapter();

    }


    public void setallReasonCode(List<ReasonCodeList> reasonCodeListList) {
        for (ReasonCodeList reason : reasonCodeListList) {
            reasonCodeMaster.put(reason.getReason_id(), reason);
        }
    }

    public boolean checkIfAtLeastOneScan(ArrayList<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (!shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                return true;
            }
        }
        return false;
    }



    public boolean checkIfAtLeastScanSuccessfully(ArrayList<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (shipmentsDetail.getStatus().contains(Constants.PICKED)) {
                return true;
            }
        }
        return false;
    }


    public boolean checkIfAllScan(ArrayList<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                return false;
            }
        }
        return true;
    }


    public void onDeleteScanItem(Shipment_Detail shipment_detail) {
        try {

            getCompositeDisposable().add(getDataManager()
                    .deleteAdvanceShipment(shipment_detail.getManifestNoInchild(), shipment_detail.getAirWayBillNumber())
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            getAllCategoryAssignedCount(manifestNo);
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }
    }


    public void updateRtoShipment(long manifestNo, long awb, String shipment_status) {
        getDataManager().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
    }
    public void insertShipment(ArrayList<Shipment_Detail> listOfShipment) {
        getDataManager().insertShipment(listOfShipment);
    }

/*
    public void insertShipment(ArrayList<Shipment_Detail> shipment_detail) {
        try {
            getCompositeDisposable().add(getDataManager()
                    .insertShipmentUsingRx(shipment_detail).subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (aBoolean) {
                                getNavigator().notifyAdapter();
                               // insertOrUpdateDataStatus(dialogOnDataUpdate);
                            }
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }


    }
*/


    public LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest) {
        return getDataManager().checkChildStatusApi(authToken, childCommitRequest);
    }

    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedManifestQuery(commit_status, commit_failed, manifestno);
    }

    public void updateCommit_DataTable(long manifestno) {
        getDataManager().updateCommit_DataTable(manifestno);
    }

    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedCommitPacket(commit_status, commit_failed, manifestno);
    }

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
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

            if (shipmentsDetails != null && shipmentsDetails.size() > 0)
                manifest_process.setManifest_no(shipmentsDetails.get(0).getManifestNoInchild());
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
        pushApi.setCompositeKey(commitPacketData.getManifest_process().get(0).getManifest_no() + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());
        pushApi.setManifestNo(commitPacketData.getManifest_process().get(0).getManifest_no());
        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            pushApi.setShipmentStatus(0);
            pushApi.setEmpId(getDataManager().getCode());
            pushApi.setApiVer(version);
            pushApi.setAppId("1");
            pushApi.setApiVer("mobile");

            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    updateManifestList(commitPacketData.getManifest_process().get(0).getManifest_no());
                    getNavigator().nextScreen(commitPacketData.getManifest_process().get(0).getManifest_no() + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));

                }
            }));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateManifestList(long manifestNo) {
        ArrayList<Long> ob = new ArrayList<Long>();
        ob.add(manifestNo);

        getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_PICKED), ob);
    }

    void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }

    public double getGeoFenceRadius() {
        return Double.parseDouble(getDataManager().get_pickup_geofencing_radius());
    }

    public MutableLiveData<Boolean> isFeAtLocation= new MutableLiveData<>();
    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return getDataManager().callFirstScanApi(authToken, firstScanResponse);
    }
    public void updateIsScanStarted(long manifestno) {
        getDataManager().updateIsScanStarted(manifestno);
    }

    public void insertFirstScanData(FirstInscan firstInscan) {
        getDataManager().insertFirstScanData(firstInscan);
    }

    public void updateInscanStatus(long manifestNumber) {
        getDataManager().updateInscanStatus(manifestNumber);

    }
    public void updateInscanToFirstScanTable(long manifestNumber) {
        getDataManager().updateInscanToFirstScanTable(manifestNumber);
    }
    public double getLastLatitudeFromPref(){
        return getDataManager().getCurrentLatitude();
    }

    public double getLastLongitudeFromPref(){
        return getDataManager().getCurrentLongitude();
    }
    public LiveData<List<Manifest_List>> loadManifest() {
        return getDataManager().getManifestDetail();
    }
    public void updateSharedManifestStatus(List<Long> manifestNo,String mobNoTYpe, Boolean shipment_status) {
        getDataManager().updateSharedManifestStatus(manifestNo, mobNoTYpe, shipment_status);
    }

    public void updateBPID(long manifestNo, long awb, String BP_ID) {
        getDataManager().updateBPID(manifestNo, awb, BP_ID);
    }

}

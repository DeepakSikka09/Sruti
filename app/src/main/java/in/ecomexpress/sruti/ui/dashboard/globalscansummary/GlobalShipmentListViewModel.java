package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.app.ProgressDialog;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dagger.Module;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitRequest;
import in.ecomexpress.sruti.model.childCommitStatus.ChildCommitResponse;
import in.ecomexpress.sruti.model.childCommitStatus.Child_Shipment_details;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.ChildCommitDialog;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.IShipmentNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;


/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd
 */
@Module
public class GlobalShipmentListViewModel extends BaseViewModel<IShipmentNavigator> {
    private boolean isAllChecked = false;
    public HashMap<Integer, ReasonCodeList> reasonCodeMaster = new HashMap<>();
    MutableLiveData<List<Manifest_List>> maListLiveData = new MutableLiveData<>();
    long countValue;
    long remainingcountValue, unPicked, total, rto1, rto2, unPicked_total;
    HashSet<Manifest_List> manifest_lists = new HashSet<>();
    private int fetch_count = 0;
    ArrayList<Child_Shipment_details> getChildCommitedList;
    ProgressDialog dialogOnDataUpdates;

    public GlobalShipmentListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onCheckStatusClick() {
        getNavigator().onCheckStatusClick();
    }

    public LiveData<List<Shipment_Detail>> setManifestListLiveData(List<Long> manifestNumber) {
        return getDataManager().fetchManifestData(manifestNumber);
    }

    public void updateShipmentCount(long manifestNo, String shipmentCount) {
        getDataManager().updateShipmentCount(manifestNo, shipmentCount);
    }

    public LiveData<List<ReasonCodeList>> getPickupListToView(String reasonCode) {
        return getDataManager().getPickupList(reasonCode);
    }

    public void updateShipment(Long awb, String status, String vehicle, boolean isAdvance) {
        getDataManager().updateScannedAWBStatus(awb, status, vehicle, Calendar.getInstance().getTimeInMillis(), isAdvance, "",0,false);
    }

    public void assignUpdateIsScanStarted(long manifestno) {
        getDataManager().assignUpdateIsScanStarted(manifestno);
    }

    public LiveData<List<Shipment_Detail>> getAllShipmentlist(long manifest_no) {
        return getDataManager().getAllShipmentlist(manifest_no);
    }

    public LiveData<List<Shipment_Detail>> getMpsCondition(ArrayList<Long> manifestNo) {
        return getDataManager().getMpsConditionUsingGlobalScan(manifestNo);
    }


    public void setAllReasonCode(List<ReasonCodeList> reasonCodeListList) {
        for (ReasonCodeList reason : reasonCodeListList) {
            reasonCodeMaster.put(reason.getReason_id(), reason);
        }
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

    public void markUndelivered(ArrayList<Shipment_Detail> shipment_detail, int reason_in, String reason_code, String vehicleType) {
        List<Shipment_Detail> list = new ArrayList<>();
        boolean isAnyChecked = false;
        for (Shipment_Detail shipmentsDetail1 : shipment_detail) {
            if (shipmentsDetail1.isChecked()) {
                isAnyChecked = true;
                shipmentsDetail1.setStatus(Constants.FAILED);
                shipmentsDetail1.setReason_id(reason_in);
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
                            getAllCategoryAssignedCount(GlobalShipmentListActivity.manifestNo);

                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }
        getNavigator().notifyAdapter();

    }


    public void getAllCategoryAssignedCount(long manifest_no) {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getShipmentCount(Constants.PICKED, manifest_no),
                getDataManager().getShipmentCount(Constants.FAILED, manifest_no),
                getDataManager().getShipmentCount(Constants.PENDING, manifest_no),
                new Function3<Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long picked, Long unpicked, Long remaining) throws Exception {
                        getNavigator().updateCountAfterMarkingFail(picked, unpicked, remaining);
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

    public boolean isValidToMoveNext(ArrayList<Shipment_Detail> shipment_detail) {
        boolean isAllNonPending = false;
        try {

            for (Shipment_Detail shipmentsDetail : shipment_detail) {
                if (shipmentsDetail.getStatus() != null) {

                    if (!shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                        isAllNonPending = true;
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

    public boolean checkIfAtLeastOneScan(ArrayList<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (!shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                return true;
            }
        }
        return false;
    }

    public void createCommitMuiltiPacketNew(HashMap<Long, List<Shipment_Detail>> shipmentsDetails, ArrayList<Image_Response> image_response/*, long pickup_location_id*/, double wayLatitude, double wayLongitude, ArrayList<String> manifest_type) {
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

                ArrayList<Manifest_process> manifest_commit_package = new ArrayList<>();
                Manifest_process manifest_process = new Manifest_process();
                ArrayList<ShipmentDetail> list_ShipmentDetails = new ArrayList<>();
                Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();

                List<Shipment_Detail> shipmentloc = shipmentsDetails.get(pair.getKey());

                for (int i = 0; i < shipmentloc.size(); i++) {
                    manifest_process.setManifest_no(shipmentloc.get(i).getManifestNoInchild());
                    ShipmentDetail shipment_detail = new ShipmentDetail();
                    shipment_detail.setAirway_bill_number(shipmentloc.get(i).getAirwaybill_number());
                    shipment_detail.setStatus_code(shipmentloc.get(i).getStatus());
                    shipment_detail.setDate_time(shipmentloc.get(i).getDateTime());
                    shipment_detail.setVehicle_no(shipmentloc.get(i).getVehicle());
                    shipment_detail.setReason_code(shipmentloc.get(i).getReason_code());
                    shipment_detail.setIs_bp_validated(shipmentloc.get(i).getIs_bp_validated());
                    shipment_detail.setAdvance(shipmentloc.get(i).isAdvance());
                    list_ShipmentDetails.add(shipment_detail);
                }

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
                manifest_process.setImage_response(image_response);
                manifest_commit_package.add(manifest_process);

                commitPacketData.setManifest_process(manifest_commit_package);
                saveMultiCommit(commitPacketData);
            }


        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void saveMultiCommit(CommitPacketData commitPacketData) {
        try {
            int i = 0;
            for (Manifest_process manifestProcess : commitPacketData.getManifest_process()) {
                PushApi pushApi = new PushApi();
                pushApi.setCompositeKey(commitPacketData.getManifest_process().get(i).getManifest_no() + "_" + commitPacketData.getManifest_process().get(i).getPickup_location_id());
                i++;
                pushApi.setManifestNo(manifestProcess.getManifest_no());

                pushApi.setAuthtoken(getDataManager().getAuthToken());
                pushApi.setEmpId(getDataManager().getCode());
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
                        getNavigator().nextScreen(manifestProcess.getManifest_no() + "_" + manifestProcess.getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));

                    }
                }));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateManifestList(long manifestNo) {
        ArrayList<Long> ob = new ArrayList<Long>();
        ob.add(manifestNo);
        getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_PICKED), ob);
    }

    public void updateCommit_DataTable(long manifestno) {
        getDataManager().updateCommit_DataTable(manifestno);
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

    public LiveData<ChildCommitResponse> checkChildStatusApi(String authToken, ChildCommitRequest childCommitRequest) {
        return getDataManager().checkChildStatusApi(authToken, childCommitRequest);
    }


    public void getChildApiResponse(ArrayList<Long> manifestNoArray, ProgressDialog dialogOnDataUpdate, ArrayList<Shipment_Detail> shipment_detail,GlobalShipmentListActivity globalShipmentListActivity) {
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
                                    updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(),getChildCommitedList.get(fetch_count).getVehicle_no(),getChildCommitedList.get(fetch_count).getManifest_no(),true, dialogOnDataUpdate);
                                } else {
                                    getNavigator().showDialogBox(shipment.getDescription(),dialogOnDataUpdate);

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
                                        updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(),getChildCommitedList.get(fetch_count).getVehicle_no(),getChildCommitedList.get(fetch_count).getManifest_no(),true, dialogOnDataUpdate);
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
            updatechildCommitShipment(getChildCommitedList.get(fetch_count).getAwb_number(), getChildCommitedList.get(fetch_count).getShipment_status(),getChildCommitedList.get(fetch_count).getVehicle_no(),getChildCommitedList.get(fetch_count).getManifest_no(),true, dialogOnDataUpdate);

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
    public MutableLiveData<List<Manifest_List>> getMaListLiveData() {
        return maListLiveData;
    }


    public void setMaListLiveData(MutableLiveData<List<Manifest_List>> maListLiveData) {
        this.maListLiveData = maListLiveData;
    }

    void getScannedManifestListAllData() {
        getCompositeDisposable().add(getDataManager().getLastManifestdata(Constants.PICKED)
                .subscribeOn(getSchedulerProvider().io())
                .subscribe(manifest_lists -> {
                    maListLiveData.postValue(manifest_lists);
                }));
    }

    public void getPickedCount(List<Manifest_List> manifest_lists) {
        Observable.fromCallable(() -> {
            getData(manifest_lists);
            return false;
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe((result) -> {

                });

    }

    private void getData(List<Manifest_List> manifestLists) {
        try {
            manifest_lists.addAll(manifestLists);
            for (Manifest_List manifestLists1 : manifestLists) {
                countValue = getDataManager().getShipmentCount(Constants.PICKED, manifestLists1.getManifest_No()).blockingSingle();
                remainingcountValue = getDataManager().getShipmentCount(Constants.PENDING, manifestLists1.getManifest_No()).blockingSingle();
                rto1 = getDataManager().getShipmentCount(Constants.RTO_STATUS_1, manifestLists1.getManifest_No()).blockingSingle();
                rto2 = getDataManager().getShipmentCount(Constants.RTO_STATUS_2, manifestLists1.getManifest_No()).blockingSingle();
                unPicked = getDataManager().getShipmentCount(Constants.FAILED, manifestLists1.getManifest_No()).blockingSingle();
                unPicked_total = rto1 + rto2 + unPicked;
                total = countValue + remainingcountValue + unPicked_total;
                manifestLists1.setPicked_count(countValue);
                manifestLists1.setRemaining_count(remainingcountValue);
                manifestLists1.setUnpicked_count(unPicked_total);
                manifestLists1.setTotalShipmentCount(total);
                manifest_lists.remove(manifestLists1);
                manifest_lists.add(manifestLists1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getNavigator().setUpdatedCount(manifest_lists);
    }

    void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }


    public LiveData<List<Shipment_Detail>> getScannedShipmentList(long manifest_no, List<String> status) {
        return getDataManager().getAllScannedShipmentList(manifest_no, status);
    }

    public LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifest_no) {
        return getDataManager().getSpecificManifestDetail(manifest_no);
    }
}

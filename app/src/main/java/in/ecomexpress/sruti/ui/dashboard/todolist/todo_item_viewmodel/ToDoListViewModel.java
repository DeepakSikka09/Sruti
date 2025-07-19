package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;

import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.CommitResponse;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Menifest_Data_Master;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IToDoListNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Function6;

public class ToDoListViewModel extends BaseViewModel<IToDoListNavigator> {
    private final ObservableField<Boolean> indicator = new ObservableField<>();
    String selectedManifestPosition="";
    //recci
    public ObservableField<String> vendorAssignedCount = new ObservableField<>("");
    public ObservableField<String> vendorPickedCount = new ObservableField<>("");
    public ObservableField<String> vendorUnPickedCount = new ObservableField<>("");

    public ObservableField<String> vendorContactNumber = new ObservableField<>("");// Making observable to get the phone number and set it.
    //warehouse
    public ObservableField<String> warehouseAssignedcount = new ObservableField<>("");
    public ObservableField<String> warehousePickedCount = new ObservableField<>("");
    public ObservableField<String> warehouseUnPickedCount = new ObservableField<>("");


    //recci
    public ObservableField<String> recciAssignedcount = new ObservableField<>("");
    public ObservableField<String> recciPickedCount = new ObservableField<>("");
    public ObservableField<String> recciUnPickedCount = new ObservableField<>("");


    //recci and ven
    public ObservableField<String> recci_venAssignedcount = new ObservableField<>("");
    public ObservableField<String> recci_venPickedCount = new ObservableField<>("");
    public ObservableField<String> recci_venUnPickedCount = new ObservableField<>("");


    //recci and warehouse
    public ObservableField<String> recci_wrhAssignedcount = new ObservableField<>("");
    public ObservableField<String> recci_wrhPickedCount = new ObservableField<>("");
    public ObservableField<String> recci_wrhUnPickedCount = new ObservableField<>("");

    //Total
    public ObservableField<String> totalAssignedCount = new ObservableField<>("");
    public ObservableField<String> totalPickedCount = new ObservableField<>("");
    public ObservableField<String> totalUnPickedCount = new ObservableField<>("");

    //Total
    public ObservableField<Long> recceTotalCount = new ObservableField<>();
    public ObservableField<Long> sellerTotalCount = new ObservableField<>();
    public ObservableField<Long> warehouseTotalCount = new ObservableField<>();
    public ObservableField<Long> recceSellerTotalCount = new ObservableField<>();
    public ObservableField<Long> recceWarehouseTotalCount = new ObservableField<>();
    public HashMap<Integer, ReasonCodeList> reasonCodeMaster = new HashMap<>();
    private MediatorLiveData<Menifest_Data_Master> liveMenifestData = new MediatorLiveData<>();
    private MutableLiveData<String> isloadingcomplet = new MutableLiveData<>();
    private MediatorLiveData<List<Shipment_Detail>> checkreason = new MediatorLiveData<>();
    private MediatorLiveData<List<Shipment_Detail>> checkreasontodo = new MediatorLiveData<>();
    private MediatorLiveData<Manifest_List> mSectionLive = new MediatorLiveData<>();

    public ToDoListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public double getlat() {
        return getDataManager().getCurrentLatitude();
    }

    public double getlng() {
        return getDataManager().getCurrentLongitude();
    }

    public LiveData<List<Manifest_List>> loadManifest() {
        return getDataManager().getManifestDetail();
    }

    public LiveData<Integer> getCount(long manifestNo) {
        return getDataManager().getScannedShipmentCount(Constants.PICKED, manifestNo);
    }


    public LiveData<Menifest_Data_Master> getMenifest() {
        return liveMenifestData;
    }

    public LiveData<Manifest_List> getUpdatedTimeResponse() {
        return mSectionLive;
    }

    public LiveData<String> isDataloading() {
        return isloadingcomplet;
    }

    public ObservableField<Boolean> getIndicator() {
        return indicator;
    }

    public void setImage(Boolean image) {
        this.indicator.set(image);
    }

    public void onClearFilterClick() {
        getNavigator().onClearFilterClick();
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

    public void getAllCategoryPickedCount() {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getVendorStatusPickedSyncedCount(Constants.COMMIT_PICKED, Constants.COMMIT_SERVER_SYNC),
                getDataManager().getWarehouseStatusPickedSyncedCount(Constants.COMMIT_PICKED, Constants.COMMIT_SERVER_SYNC),
                getDataManager().getRecciStatusPickedSyncedCount(Constants.COMMIT_PICKED, Constants.COMMIT_SERVER_SYNC),
                getDataManager().getRecciVenStatusPickedSyncedCount(Constants.COMMIT_PICKED, Constants.COMMIT_SERVER_SYNC),
                getDataManager().getRecciWrhStatusPickedSyncedCount(Constants.COMMIT_PICKED, Constants.COMMIT_SERVER_SYNC),
                new Function5<Long, Long, Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long vendor, Long warehouse, Long recci, Long recci_ven, Long recci_wrh) throws Exception {
                        Long allPickedCount = vendor + warehouse + recci + recci_ven + recci_wrh;

                        vendorPickedCount.set(String.valueOf(vendor));
                        warehousePickedCount.set(String.valueOf(warehouse));
                        recciPickedCount.set(String.valueOf(recci));
                        recci_venPickedCount.set(String.valueOf(recci_ven));
                        recci_wrhPickedCount.set(String.valueOf(recci_wrh));

                        totalPickedCount.set(String.valueOf(allPickedCount));
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

    public void getAllCategoryUnPickedCount() {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getVendorStatusCount(Constants.COMMIT_FAILED),
                getDataManager().getWarehouseStatusCount(Constants.COMMIT_FAILED),
                getDataManager().getRecciStatusCount(Constants.COMMIT_FAILED),
                getDataManager().getRecciVenStatusCount(Constants.COMMIT_FAILED),
                getDataManager().getRecciWrhStatusCount(Constants.COMMIT_FAILED),
                new Function5<Long, Long, Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long vendor, Long warehouse, Long recci, Long recci_ven, Long recci_wrh) throws Exception {
                        Long allUnPickedCount = vendor + warehouse + recci + recci_ven + recci_wrh;

                        vendorUnPickedCount.set(String.valueOf(vendor));

                        warehouseUnPickedCount.set(String.valueOf(warehouse));
                        recciUnPickedCount.set(String.valueOf(recci));
                        recci_venUnPickedCount.set(String.valueOf(recci_ven));
                        recci_wrhUnPickedCount.set(String.valueOf(recci_wrh));

                        totalUnPickedCount.set(String.valueOf(allUnPickedCount));
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

    public boolean iscompleteDepart() {
        if (!getDataManager().is_Ecom_Vehicle())
            return getDataManager().getDepartSelfVehicle();
        List<LoginResponse.StartRouteDetails> startRouteDetails = getDataManager().getRouteDetail();
        boolean completedepart = true;
        for (LoginResponse.StartRouteDetails ff : startRouteDetails) {
            if (!ff.isDeparted())
                completedepart = false;

        }
        return completedepart;
    }

    public void getAllShipmentCount(Manifest_List manifest) {
        getCompositeDisposable().add(Observable.zip(
                getDataManager().getShipmentCount(Constants.PICKED, manifest.getManifest_No()),
                getDataManager().getShipmentCount(Constants.FAILED, manifest.getManifest_No()),
                getDataManager().getShipmentCount(Constants.PENDING, manifest.getManifest_No()),
                getDataManager().getShipmentCount(Constants.ADVANCE_STATUS, manifest.getManifest_No()),
                getDataManager().getShipmentCount(Constants.RTO_STATUS_1, manifest.getManifest_No()),
                getDataManager().getShipmentCount(Constants.RTO_STATUS_2, manifest.getManifest_No()),
                new Function6<Long, Long, Long, Long, Long, Long, Long>() {
                    @Override
                    public Long apply(Long picked, Long unpicked, Long remaining, Long advance, Long rto1, Long rto2) throws Exception {

                        long total = picked + remaining + unpicked + advance + rto1 + rto2;

                        manifest.setTotalShipmentCount(total);
                        manifest.setPicked_count(picked);
                        manifest.setUnpicked_count(unpicked + rto1 + rto2);
                        manifest.setRemaining_count(remaining);
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

    public MutableLiveData<Integer> pickedShipmentCount = new MutableLiveData<>();

    public void getPickedShipmentCountTest(Manifest_List manifest) {
        getCompositeDisposable().add(getDataManager().getShipmentCount(Constants.PICKED, manifest.getManifest_No())
                .subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        pickedShipmentCount.postValue(count.intValue());

                    }
                }));
    }


    public void applyFilterOnList(List<Manifest_List> manifestList, boolean[] itemShipmentType, boolean[] itemSelectedDataStatus) {
        List<Manifest_List> filteredManifestTypeList = new ArrayList<>();
        //RECCE
        if (itemShipmentType[0]) {
            for (Manifest_List myManifest_list : manifestList) {
                if (myManifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_RECCE)) {
                    if (itemSelectedDataStatus[0] && myManifest_list.getCommit_status() == Constants.COMMIT_PENDING) {
                        filteredManifestTypeList.add(myManifest_list);
                    }
                    if (itemSelectedDataStatus[1] && ((myManifest_list.getCommit_status() == Constants.COMMIT_PICKED) || (myManifest_list.getCommit_status() == Constants.COMMIT_SERVER_SYNC))) {
                        filteredManifestTypeList.add(myManifest_list);
                    }
                    if (itemSelectedDataStatus[2] && myManifest_list.getCommit_status() == Constants.COMMIT_FAILED) {
                        filteredManifestTypeList.add(myManifest_list);
                    } /*if (itemSelectedDataStatus[0] || itemSelectedDataStatus[1] || itemSelectedDataStatus[2])
                        filteredManifestTypeList.add(myManifest_list);
                }*/
                }
            }
        }
        //PICKUP -Seller
        if (itemShipmentType[1]) {
            for (Manifest_List myManifest_list : manifestList) {
                if (myManifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_PICKUP)) {
                    if (myManifest_list.getLocation_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_SELLER)) {
                        if (itemSelectedDataStatus[0] && myManifest_list.getCommit_status() == Constants.COMMIT_PENDING) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[1] && ((myManifest_list.getCommit_status() == Constants.COMMIT_PICKED) || (myManifest_list.getCommit_status() == Constants.COMMIT_SERVER_SYNC))) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[2] && myManifest_list.getCommit_status() == Constants.COMMIT_FAILED) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                       /* if (itemSelectedDataStatus[0] || itemSelectedDataStatus[1] || itemSelectedDataStatus[2])
                            filteredManifestTypeList.add(myManifest_list);
                    }*/
                    }
                }
            }
        }

        //PICKUP -Warehouse
        if (itemShipmentType[2]) {
            for (Manifest_List myManifest_list : manifestList) {
                if (myManifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_PICKUP)) {
                    if (myManifest_list.getLocation_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_WAREHOUSE)) {
                        if (itemSelectedDataStatus[0] && myManifest_list.getCommit_status() == Constants.COMMIT_PENDING) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[1] && ((myManifest_list.getCommit_status() == Constants.COMMIT_PICKED) || (myManifest_list.getCommit_status() == Constants.COMMIT_SERVER_SYNC))) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[2] && myManifest_list.getCommit_status() == Constants.COMMIT_FAILED) {
                            filteredManifestTypeList.add(myManifest_list);
                        } /*if (itemSelectedDataStatus[0] || itemSelectedDataStatus[1] || itemSelectedDataStatus[2])
                            filteredManifestTypeList.add(myManifest_list);
                    }*/
                    }
                }
            }
        }

        //RECCEPICKUP -Seller
        if (itemShipmentType[3]) {
            for (Manifest_List myManifest_list : manifestList) {
                if (myManifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_PICKUP_RECCE)) {
                    if (myManifest_list.getLocation_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_SELLER)) {
                        if (itemSelectedDataStatus[0] && myManifest_list.getCommit_status() == Constants.COMMIT_PENDING) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[1] && ((myManifest_list.getCommit_status() == Constants.COMMIT_PICKED) || (myManifest_list.getCommit_status() == Constants.COMMIT_SERVER_SYNC))) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[2] && myManifest_list.getCommit_status() == Constants.COMMIT_FAILED) {
                            filteredManifestTypeList.add(myManifest_list);
                        } /*if (itemSelectedDataStatus[0] || itemSelectedDataStatus[1] || itemSelectedDataStatus[2])
                            filteredManifestTypeList.add(myManifest_list);
                    }*/
                    }
                }
            }
        }

        //RECCEPICKUP -Warehouse
        if (itemShipmentType[4]) {
            for (Manifest_List myManifest_list : manifestList) {
                if (myManifest_list.getManifest_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_PICKUP_RECCE)) {
                    if (myManifest_list.getLocation_type().equalsIgnoreCase(Constants.MANIFEST_TYPE_WAREHOUSE)) {
                       /* if (itemSelectedDataStatus[0] || itemSelectedDataStatus[1] || itemSelectedDataStatus[2])
                            filteredManifestTypeList.add(myManifest_list);
                    }*/
                        if (itemSelectedDataStatus[0] && myManifest_list.getCommit_status() == Constants.COMMIT_PENDING) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[1] && ((myManifest_list.getCommit_status() == Constants.COMMIT_PICKED) || (myManifest_list.getCommit_status() == Constants.COMMIT_SERVER_SYNC))) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                        if (itemSelectedDataStatus[2] && myManifest_list.getCommit_status() == Constants.COMMIT_FAILED) {
                            filteredManifestTypeList.add(myManifest_list);
                        }
                    }
                }
            }
        }

        getNavigator().setFilteredAdapter(filteredManifestTypeList);

    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void onIndicatorClick() {
        getNavigator().onIndicatorClick();
    }

    public void onApplyClick() {
        getNavigator().onApplyClick();
    }

    public void onFilterClick() {
        getNavigator().onFilterClick();
    }


    public void onScanClick() {
        getNavigator().onScanClick();
    }

    public void onSynClick() {
        getNavigator().onSynClickEvent();
    }

    public String getEmployeeCode() {
        return getDataManager().getCode();

    }


    public void addRemarks(Remark remark) {
        try {

            getCompositeDisposable().add(getDataManager().insertRemark(remark)
                    .observeOn(getSchedulerProvider().io())
                    .subscribeOn(getSchedulerProvider().io())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
//                            Logger.e(TAG, aBoolean ? "successfully update remarks" : "remarks not inserted or updated in database.");
//                            getRemarksCount();
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
    }

    public void updateShipment(String status, String vehicle, String date_time, boolean isAdvance, boolean checked, long manifestNo) {
        getDataManager().updateOtpShipment(status, vehicle, date_time, isAdvance, checked, manifestNo);
    }

    public LiveData<List<ReasonCodeList>> getPickupListToView(String reasonCode) {
        return getDataManager().getPickupList(reasonCode);
    }

    public void setallReasonCode(List<ReasonCodeList> reasonCodeListList) {
        for (ReasonCodeList reason : reasonCodeListList) {
            reasonCodeMaster.put(reason.getReason_id(), reason);
        }
    }
    public boolean checkIfAtLeastScanSuccessfully(List<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (shipmentsDetail.getStatus().contains(Constants.PICKED)) {
                return true;
            }
        }
        return false;
    }


    public void getAllShipmentList(long manifest_no) {
        LiveData<List<Shipment_Detail>> ob = getDataManager().getAllShipmentlist(manifest_no);
        checkreason.addSource(ob, shipment_details -> {
            checkreason.removeSource(ob);
            System.out.println("KKKKKKK");
//            if (shipment_details !=null){
            checkreason.setValue(shipment_details);
//            }
        });
    }

    public LiveData<List<Shipment_Detail>> getAllShipment() {
        return checkreason;
    }

    public void getAllShipmentListTODO(long manifest_no, int position1) {

        LiveData<List<Shipment_Detail>> ob = getDataManager().getAllShipmentlist(manifest_no);
        checkreasontodo.addSource(ob, shipment_details -> {
            checkreasontodo.removeSource(ob);
            checkreasontodo.setValue(shipment_details);
        });
    }

    public LiveData<List<Shipment_Detail>> getAllShipmentTODO() {
        return checkreasontodo;
    }


    private boolean isAllChecked = false;

    public void checkAll(ArrayList<Shipment_Detail> shipment_details) {
        isAllChecked = !isAllChecked;
        for (Shipment_Detail shipmentsDetail : shipment_details) {
            if (shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                shipmentsDetail.setChecked(isAllChecked);
            }
        }
    }


    public void markUndelivered(ArrayList<Shipment_Detail> shipment_detail, int reason_id, String reason_code, String vehicleType) {
        List<Shipment_Detail> list = new ArrayList<>();
        //  boolean isAnyChecked = false;
        for (Shipment_Detail shipmentsDetail1 : shipment_detail) {
            //if (shipmentsDetail1.isChecked()) {
            //        isAnyChecked = true;
            shipmentsDetail1.setStatus(Constants.FAILED);
            shipmentsDetail1.setReason_id(reason_id);
            shipmentsDetail1.setReason_code(reason_code);
            shipmentsDetail1.setVehicle(vehicleType);
            shipmentsDetail1.setChecked(false);
            shipmentsDetail1.setDateTime(Calendar.getInstance().getTimeInMillis());
            list.add(shipmentsDetail1);
            // }

        }
/*
        if (!isAnyChecked) {
            getNavigator().onErrorMessage("No Shipments Selected");
        }
*/
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
        //Deepak
        // getNavigator().notifyAdapter();

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


    public void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
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

    public String getCutOffTime() {
        return getDataManager().getCutOffTime();
    }

    public String getCutOffAlertTime() {
        return getDataManager().get_cutOffAlertTime();
    }

    public String get_cutOffActionValue() {
        return getDataManager().get_cutOffActionValue();
    }

    public void updateProgressBar() {
        getNavigator().updateProgressBar();
    }

    public boolean checkIfAtLeastOneScan(List<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {
            if (!shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                return true;
            }
        }
        return false;
    }

    public void commitToserver(Context context, long manifest_no) {
        int shipmentStatus = 0;
        getCompositeDisposable().add(getDataManager().getUnSyncCommitManifest(manifest_no, shipmentStatus)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<PushApi>>() {
                    @Override
                    public void accept(List<PushApi> pushApis) throws Exception {
                        if (pushApis != null) {
                            CommitPacketData commitPacketData = null;

                            for (PushApi pushApi : pushApis) {

                                try {
                                    if (pushApi.getFileUrl() != null) {
                                        String data = getTextFileData(pushApi.getFileUrl());
                                        String fileName = pushApi.getCompositeKey();
                                        commitPacketData = new ObjectMapper().readValue(data, CommitPacketData.class);
                                    }
                                    if (commitPacketData != null) {

                                        getNavigator().uploadCommitPacket(commitPacketData, pushApi.getCompositeKey());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }));

    }


    public void deleteCommitedShipment(ArrayList<Long> manifestIds) {
        getDataManager().deleteCommitedShipment(manifestIds);

    }


    public LiveData<CommitResponse> uploadCommitPacket(String authToken, CommitPacketData commit) {
        return getDataManager().doCommitApiCall(authToken, commit);
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

    public void updateManifestListWithRecci(String valueOf, String s) {
        getDataManager().updateManifestListWithRecci(valueOf, s);
    }

    public void updateCommitStatusWithRecci(String s, String s1) {
        getDataManager().updateCommitStatusWithRecci(s, s1);
    }


    public void getUpdatedTime(ArrayList<Long> manifestNo) {
        LiveData<Manifest_List> sections = getDataManager().getUpdatedTime(manifestNo);
        mSectionLive.addSource(sections, manifest_list -> {
            mSectionLive.removeSource(sections);
            mSectionLive.setValue(manifest_list);
        });
    }


    public double getGeoFenceRadius() {
        return Double.parseDouble(getDataManager().get_pickup_geofencing_radius());
    }

    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return getDataManager().callFirstScanApi(authToken, firstScanResponse);
    }

    public void updateFirstInscanStatus(long manifestId) {
        getDataManager().updateFirstInScan(1, manifestId);
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

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }

    public double getLastLatitudeFromPref(){
        return getDataManager().getCurrentLatitude();
    }

    public double getLastLongitudeFromPref(){
        return getDataManager().getCurrentLongitude();
    }


}

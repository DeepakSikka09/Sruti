package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;

import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import androidx.lifecycle.LiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IQrNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by deepak on 9/12/19.
 */
@Module
public class QrcReaderViewModel extends BaseViewModel<IQrNavigator> {
    private boolean isAllChecked = false;

    public QrcReaderViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public LiveData<List<ReasonCodeList>> getPickupListToView() {
        return getDataManager().getPickupList(Constants.QR_WISE_REASON_CODE);
    }


    public LiveData<List<Shipment_Detail>> getAllShipmentList(long manifest_no) {
        return getDataManager().getAllShipmentlist(manifest_no);
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
           // getNavigator().onErrorMessage("No Shipments Selected");
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
//                                            updateStatistics();
                            //                           getAllCategoryAssignedCount(manifestNo);

                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }

    }

    public void checkAll(ArrayList<Shipment_Detail> shipment_details) {
        isAllChecked = !isAllChecked;
        for (Shipment_Detail shipmentsDetail : shipment_details) {
            if (shipmentsDetail.getStatus().contains(Constants.PENDING)) {
                shipmentsDetail.setChecked(isAllChecked);
            }
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
                shipment_detail.setIs_bp_validated(shipmentsDetails.get(i).getIs_bp_validated());
                /**
                 * As Discuss with dinesh
                 shipment_detail.setManifest_shipment_id(shipmentsDetails.get(i).getManifest_shipment_id());
                 shipment_detail.setReason_id(shipmentsDetails.get(i).getReason_id());*/
                shipment_detail.setAdvance(shipmentsDetails.get(i).isAdvance());
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
            // pushApi.setRequestData(new ObjectMapper().writeValueAsString(commitPacketData));
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


    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedManifestQuery(commit_status, commit_failed, manifestno);
    }
    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedCommitPacket(commit_status, commit_failed, manifestno);
    }
    public void inScanCommitPacket(long manifestno){
        getDataManager().inScanCommitPacket(1,manifestno);
    }
    public void updateCommit_DataTable(long manifestno) {
        getDataManager().updateCommit_DataTable(manifestno);
    }

    public boolean checkIfAtLeastOneReasonCodeSelected(ArrayList<Shipment_Detail> shipment_detail) {
        for (Shipment_Detail shipmentsDetail : shipment_detail) {

            if (shipmentsDetail.getReason_code() == null) {
                return true;
            }
        }
        return false;
    }


    public void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl,mani);
    }

}

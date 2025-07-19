package in.ecomexpress.sruti.repo.local.db;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.Departure.CountManifest;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Flags;
import in.ecomexpress.sruti.model.menifestdata.ManifestAndShipment;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import io.reactivex.Flowable;
import io.reactivex.Observable;


public interface IDBHelper {
    Observable<Boolean> deleteAllTables();

    void insertToDoList(List<ReasonCodeList> reasonCodeLists, List<in.ecomexpress.sruti.model.masterdata.Post_option> post_options);

    void updateShipmentCount(long manifestNo, String shipmentCount);

    LiveData<List<ReasonCodeList>> getPickupList(String differenceState);

    Observable<List<Post_option>> getCbPstnOptions();

    LiveData<List<Shipment_Detail>> getAllShipmentlist(long Status);

    LiveData<List<Shipment_Detail>> getMpsCondition(long manifestNo);

    LiveData<List<Shipment_Detail>> getMpsConditionUsingGlobalScan(ArrayList<Long> manifestCollection);

    LiveData<List<Shipment_Detail>> getAllParentShipmentlist(long manifest_no);

    LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifestNo);

    LiveData<List<Shipment_Detail>> getAllScannedShipmentList(long manifest_No, List<String> status);

    LiveData<List<Shipment_Detail>> getCommitAPICall(String vehcileId);

    LiveData<List<Shipment_Detail>> getScannedShipmentList(String picked);

    LiveData<List<Shipment_Detail>> getTotalShipmentList(List<Long> manifest_no);

    Observable<List<Shipment_Detail>> ifAWBexists(long manifestNumber, long awbNo, String status);

    LiveData<Shipment_Detail> ifAWBPresent(long manifestNumber, long awbNo, String status);

    Observable<Boolean> ifAWBPresentNew(long manifestNumber, long awbNo, String status);

    Observable<Long> isFirstScan(long manifestNumber);

    Observable<Manifest_List> getSingleManifestDetail(long manifestNumber);

    void updateScannedAWBStatus(Long awb, String status, String vehicle, long currenttime, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied);


    void updateScannedAWBStatusTemp(Long awb, String status, String vehicle, long currenttime, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied,boolean tempKey);

    void updateOtpShipment(String status, String vehicle, String date_time, boolean isAdvance, boolean checked, long manifestNo);

    Observable<Long> getVendorStatusCount(int status);

    Observable<Long> getVendorStatusPickedSyncedCount(int picked, int synced);

    Observable<Long> getWarehouseStatusPickedSyncedCount(int picked, int synced);

    Observable<Long> getRecciStatusPickedSyncedCount(int picked, int synced);

    Observable<Long> getRecciVenStatusPickedSyncedCount(int picked, int synced);

    Observable<Long> getRecciWrhStatusPickedSyncedCount(int picked, int synced);

    Observable<Long> getWarehouseStatusCount(int status);

    Observable<Long> getRecciStatusCount(int status);

    Observable<Long> getRecciVenStatusCount(int status);

    Observable<Long> getRecciWrhStatusCount(int status);

    void insertManifestShipmentDetail(List<Manifest_List> manifest_lists, List<Shipment_Detail> shipment_details);

    LiveData<List<Shipment_Detail>> getShipmentDetail();

    LiveData<List<Manifest_List>> getManifestDetail();


    LiveData<Manifest_List> getUpdatedTime(ArrayList<Long> manifestNo);

    LiveData<List<Manifest_List>> getManifestDetailStatus();

    LiveData<List<Shipment_Detail>> getScannedManifestList(String status);

    LiveData<List<Manifest_List>> getAllManifestShipments(long manifestids);

    Flowable<List<Manifest_List>> getLastManifestdata(String status);

    Observable<Long> getShipmentCount(String status, long manifestNo);

    LiveData<Integer> getScannedShipmentCount(String status, long manifestNo);

    Observable<Long> getGlobalCount(String status);

    Observable<Long> getTotalCount(long manifestNo);

    LiveData<List<Shipment_Detail>> fetchToDoFilterData(String searchdata);

    LiveData<List<Shipment_Detail>> fetchManifestData(List<Long> manifestNumber);

    LiveData<List<ManifestAndShipment>> getManifestAndShipment(String searchdata);

    LiveData<List<HandOverShipmentList>> getAllAwbData();

    void insertAwbNumberList(HandOverShipmentList handOverShipmentList);

    LiveData<Integer> deleteAwbData(int awbNumber);

    Observable<Boolean> saveCommitPacket(PushApi pushApi);
    Observable<Boolean> pushPopData(PopData pushApi);

    Observable<List<PushApi>> getUnSyncCommitManifest(long manifest_no, int shipmentStatus);
    Observable<List<PopData>> getPopData();
    LiveData<HandOverShipmentList> isValidateAwb(long awbNumber);

    LiveData<Shipment_Detail> isScannedAWBValid(long awb);

    LiveData<List<Shipment_Detail>> getAllScanAWBlist(String Status, Long manifest_No);
    LiveData<List<Shipment_Detail>> getAllScanAWBlistTemp(List<String> Status, Long manifest_No);
    LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlist(String Status, int commit_status);
    LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlistTemp(List<String> Status, int commit_status,long tempManifestNo);
    LiveData<List<Shipment_Detail>> getTempGlobalScanAWBlistTemp(String Status, int commit_status,long tempManifestNo);


    LiveData<List<Shipment_Detail>> getScannedVendorStatus(long manifestNumber, long awbNumber);

    LiveData<Flags> getFlagDetail();

    void setRecciQuestion(List<General_Question> recciQuestion);

    LiveData<List<General_Question>> getRecciQuestion(List<Integer> recciQuestion);

    Observable<Boolean> markUndelivered(Shipment_Detail... shipmentDetails);

    Observable<Boolean> insertAdvanceShipmentNew(Shipment_Detail shipment_detail);

    void saveImage(ImageModel imageModel);

    Observable<Boolean> deleteAdvanceShipment(long manifestNoInchild, long awb);

    void updateRtoShipmentStatus(long manifestNo, long awb, String shipment_status);
    void updateBPID(long manifestNo, long awb, String BP_ID);
    void updateSharedManifestStatus(List<Long>manifestNo,  String mobileNoType,Boolean shipment_status);

    void insertShipment(List<Shipment_Detail> listOfShipment);

    Observable<Boolean> insertShipmentUsingRx(List<Shipment_Detail> listOfShipment);

    Observable<Boolean> isAWBRtoLock(long manifestNumber, long awbNumber);

    void updateCommitStatus(String shipmentStatus, String manifestNo);

    void updateManifestList(String commitStatus, ArrayList<Long> manifestNo);
    void updatePopStatus(String commitStatus, ArrayList<Long> manifestNo);

    void updateManifestSelfList(String commitStatus, ArrayList<Long> manifestNo);

    void updateFileUrl(String fileUrl, String mani);

    void updateRecciList(String setShipmentStatus, String compositeKey);

    void updateInscanStatus(long manifestNumber);

    void insertFirstScanData(FirstInscan firstInscan);

    void updateInscanToFirstScanTable(long manifestNumber);

    LiveData<List<ImageModel>> getImageStatus(String manifestNo);

    Observable<Boolean> updatechildCommitShipment(Long awb, String shipment_status, String vehicle, long manifest_no, boolean isChild);

    void failedManifestQuery(int commit_status, String commit_failed, long manifestno);

    LiveData<CountManifest> checkAllManifestStatus();

    void deleteData();

    void deleteDataAtLogout();

    void deleteForcefully();

    LiveData<List<Manifest_List>> getUnPickedManifestList();


    Observable<Long> getManifestIdFromAwb(long awb);

    Observable<Long> getAdvanceCount(boolean b, long manifest_no);

    Observable<Boolean> insertRemark(Remark remark);

    LiveData<Integer> getUnSyncManifestList();

    Observable<String> getShipmentStatus(long manifestNumber, long awbNumber);

    Observable<Boolean> getShipmentExist(long manifestNumber, long awbNumber);

    Observable<Boolean> isAlreadyScanned(Long manifestNo, Long awbNumber);


    void failedCommitPacket(int commit_status, String commit_failed, long manifestno);

    void inScanCommitPacket(int commit_status, long manifestno);

    void updateCommit_DataTable(long manifestno);

    void inScanCommitRecciPacket(int scan_status, String comkey);

    void updateIsScanStarted(long manifestNumber);

    void assignUpdateIsScanStarted(long manifestNumber);

    LiveData<Integer> getStartedManifestCount();

    LiveData<List<PushApi>> UnSyncCommitListAtLogin(int status);

    void deleteCommitedShipment(ArrayList<Long> manifestIds);

    void updateManifestListWithRecci(String valueOf, String s);

    void updateCommitStatusWithRecci(String s, String s1);

    void updateFirstInScan(int status, long manifestId);


    Observable<List<Shipment_Detail>> ifBrandPackagingIDexists(long manifest_no,long awb_no,String BP_id);
    Observable<List<Shipment_Detail>> ifAWBbrandPackagingIDexists(long awb_no,String BP_id);

    void UpdateAWBViaBpReasonID(Long awb, String status, String vehicle, long currenttime, int reason_id, String reason_code,boolean reason_code_applied);

    Observable<String> getSpecificBpId(long awb);
    Observable<Boolean> getTempKey(long awb);
}
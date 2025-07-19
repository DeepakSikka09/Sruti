package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.Departure.CountManifest;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;

/**
 * Created by 63091 on 01-07-2019.
 */
@Dao
public abstract class ManifestDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertShipmentDetail(List<Manifest_List> manifest_lists, List<Shipment_Detail> shipment_details);

    @Query("select * from manifest_list")
    public abstract LiveData<List<Manifest_List>> getManifestDetailList();

    @Query("Select * from manifest_list where manifest_No in (:manifestNo) order by last_sync_time Desc")
    public abstract LiveData<Manifest_List> getUpdatedTime(List<Long> manifestNo);

    @Query("select * from manifest_list where commit_status!=:commit_status")
    public abstract LiveData<List<Manifest_List>> getManifestDetail(int commit_status);

    @Query("select * from manifest_list WHERE manifest_No =:manifestNo")
    public abstract Manifest_List getSingleManifestDetail(long manifestNo);

    @Query("select * from manifest_shipment_detail")
    public abstract LiveData<List<Shipment_Detail>> getShipmentDetail();

    @Query("select * from manifest_shipment_detail WHERE status =:status AND manifestNoInchild = :manifest_No ORDER by datetime DESC")
    public abstract LiveData<List<Shipment_Detail>> getAllAWBList(String status, long manifest_No);


    @Query("select * from manifest_shipment_detail WHERE status in (:status) AND manifestNoInchild = :manifest_No ORDER by datetime DESC")
    public abstract LiveData<List<Shipment_Detail>> getAllAWBListTemp(List<String> status, long manifest_No);

    @Query("select * from manifest_shipment_detail as a left join manifest_list as b on  a.manifestNoInchild = b.manifest_No  WHERE a.status =:status AND b.commit_status=:commit_status ORDER by datetime DESC")
    public abstract LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlist(String status, int commit_status);


    @Query("select * from manifest_shipment_detail as a left join manifest_list as b on  a.manifestNoInchild = b.manifest_No  WHERE a.status in (:status) and airwaybill_number= :tempManifestNo AND b.commit_status=:commit_status ORDER by datetime DESC")
    public abstract LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlistTemp( List<String>status,int commit_status,long tempManifestNo);

    @Query("select * from manifest_shipment_detail as a left join manifest_list as b on  a.manifestNoInchild = b.manifest_No  WHERE a.status = :status and airwaybill_number= :tempManifestNo AND b.commit_status=:commit_status ORDER by datetime DESC")
    public abstract LiveData<List<Shipment_Detail>> getTempGlobalScanAWBlistTemp( String status,int commit_status,long tempManifestNo);


    @Query("select * from manifest_shipment_detail WHERE manifestNoInchild = :manifest_No ORDER by master_airwaybill_number DESC")
    public abstract LiveData<List<Shipment_Detail>> getAllShipmentList(long manifest_No);
    /*
select manifestnoinchild,status,master_airwaybill_number,CASE status  WHEN 'EXS_20_00' THEN 'Scan' ELSE 'Unscan' END StatusGroup from manifest_shipment_detail where master_airwaybill_number !=0 and manifestnoinchild=120035 order by master_airwaybill_number;
 */

    @Query("select * from manifest_shipment_detail where master_airwaybill_number !=0 AND manifestnoinchild=:manifestNo AND is_mps=:is_mps ORDER by master_airwaybill_number")
    public abstract LiveData<List<Shipment_Detail>> getMpsCondition(long manifestNo, boolean is_mps);

    @Query("select * from manifest_shipment_detail where master_airwaybill_number !=0 AND manifestnoinchild in(:manifestCollection) AND is_mps=:is_mps ORDER by master_airwaybill_number")
    public abstract LiveData<List<Shipment_Detail>> getMpsConditionUsingGlobalScan(List<Long> manifestCollection, boolean is_mps);

    @Query("select * from manifest_shipment_detail WHERE manifestNoInchild = :manifest_No AND isChild=0")
    public abstract LiveData<List<Shipment_Detail>> getAllParentShipmentlist(long manifest_No);

    @Query("select * from manifest_list WHERE manifest_No in (:manifestNo)")
    public abstract LiveData<List<Manifest_List>> getSpecificManifestDetail(List<Long> manifestNo);

  /*  @Query("Select * from manifest_shipment_detail WHERE manifestNoInchild= :manifest_No AND status= :status")
    public abstract LiveData<List<Shipment_Detail>> getAllScannedShipmentList(long manifest_No, String status);
*/

      @Query("Select * from manifest_shipment_detail WHERE manifestNoInchild= :manifest_No AND status in (:status)")
   public abstract LiveData<List<Shipment_Detail>> getAllScannedShipmentList(long manifest_No, List<String> status);




      @Query("select * from manifest_shipment_detail WHERE vehicle=:vehicleId")
    public abstract LiveData<List<Shipment_Detail>> getCommitAPICall(String vehicleId);

    @Query("select * from manifest_shipment_detail WHERE status =:status")
    public abstract LiveData<List<Shipment_Detail>> getScannedShipmentList(String status);

    @Query("select * from manifest_shipment_detail WHERE manifestNoInchild in (:manifest_No)")
    public abstract LiveData<List<Shipment_Detail>> getTotalShipmentList(List<Long> manifest_No);

    @Query("UPDATE manifest_shipment_detail SET status= :status ,advance=:isAdvance, vehicle= :vehicle , dateTime =:date_time, reason_code=:reason_code,is_bp_validated=:is_bp_validated,bp_Reason_Code_Applied=:reason_code_applied WHERE airwaybill_number = :awbNo")
    public abstract void updateScannedAWBStatus(Long awbNo, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied);

 @Query("UPDATE manifest_shipment_detail SET status= :status ,advance=:isAdvance, vehicle= :vehicle , dateTime =:date_time, reason_code=:reason_code,is_bp_validated=:is_bp_validated,bp_Reason_Code_Applied=:reason_code_applied ,temp_key =:tempKey WHERE airwaybill_number = :awbNo")
 public abstract void updateScannedAWBStatusTemp(Long awbNo, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied,boolean tempKey);


    @Query("UPDATE manifest_shipment_detail SET status= :status ,advance=:isAdvance, vehicle= :vehicle , dateTime =:date_time,checked =:checked WHERE manifestNoInchild =:manifestNo")
    public abstract void updateOtpShipment(String status, String vehicle, String date_time, boolean isAdvance, boolean checked, long manifestNo);

    @Query("select distinct(manifestNoInchild) from manifest_shipment_detail WHERE status = :status")
    public abstract List<Long> getScannedManifestList(String status);

    @Query("select * from manifest_list WHERE manifest_No in (:manifestNo) AND commit_status = '0'")
    public abstract List<Manifest_List> getManifestList(List<Long> manifestNo);
    // New updated table
    @Query("select * from manifest_list")
    public abstract Manifest_List getManifestList();

    @Update
     public  abstract void Update_table(Manifest_List manifestList) ;


    @Transaction
    public List<Manifest_List> getLastManifestdata(String status) {
        List<Long> data = getScannedManifestList(status);
//        List<Manifest_List> manifest_lists = new ArrayList<>();
       /* Set<Long> datast = new HashSet<>();
        if (data !=null  && data.size() > 0) {
            for (ShipmentDetail s : data) {
                datast.add(s.getManifestNoInchild());
            }
        }*/
        return getManifestList(data);

    }

    //getting seller count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:status AND location_type='SELLER' AND manifest_type='P'")
    public abstract long getVendorStatusCount(int status);

    //getting seller count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:picked AND location_type='SELLER' AND manifest_type='P' OR commit_status =:synced AND location_type='SELLER' AND manifest_type='P'")
    public abstract long getVendorStatusPickedSyncedCount(int picked, int synced);

    //getting warehouse count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:status AND location_type='WAREHOUSE' AND manifest_type='P'")
    public abstract long getWarehouseStatusCount(int status);

    //getting warehouse count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:picked AND location_type='WAREHOUSE' AND manifest_type='P' OR commit_status =:synced AND location_type='WAREHOUSE' AND manifest_type='P'")
    public abstract long getWarehouseStatusPickedSyncedCount(int picked, int synced);

    //getting recce count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:status AND manifest_type='R'")
    public abstract long getRecciStatusCount(int status);

    //getting recce count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:picked AND manifest_type='R' OR commit_status =:synced AND manifest_type='R'")
    public abstract long getRecciStatusPickedSyncedCount(int picked, int synced);

    //getting recce + seller count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:status AND manifest_type='PR' AND location_type='SELLER'")
    public abstract long getRecciVenStatusCount(int status);

    //getting recce + seller count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:picked AND manifest_type='PR' AND location_type='SELLER' OR commit_status =:synced AND manifest_type='PR' AND location_type='SELLER'")
    public abstract long getRecciVenStatusPickedSyncedCount(int picked, int synced);

    //getting recce + warehouse count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:status AND manifest_type='PR' AND location_type='WAREHOUSE'")
    public abstract long getRecciWrhStatusCount(int status);

    //getting recce + warehouse count
    @Query("SELECT COUNT(*) FROM manifest_list   WHERE  commit_status =:picked AND manifest_type='PR' AND location_type='WAREHOUSE' OR  commit_status =:synced AND manifest_type='PR' AND location_type='WAREHOUSE'")
    public abstract long getRecciWrhStatusPickedSyncedCount(int picked, int synced);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE status =:status AND manifestNoInchild =:manifestNo")
    public abstract long getShipmentCount(String status, long manifestNo);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE status=:status And manifestNoInchild=:manifestNo")
    public abstract LiveData<Integer> getScannedShipmentCount(String status, long manifestNo);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE status=:status And manifestNoInchild=:manifestNo")
    public abstract int getScannedShipmentCountTest(String status, long manifestNo);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE status =:status")
    public abstract long getGlobalCount(String status);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE manifestNoInchild =:manifestNo")
    public abstract long getTotalCount(long manifestNo);

    @Query("SELECT COUNT(*) FROM manifest_shipment_detail WHERE advance =:status AND manifestNoInchild =:manifestNo")
    public abstract long getAdvanceCount(boolean status, long manifestNo);

    @Query("SELECT * FROM manifest_shipment_detail WHERE manifestnoinchild =:manifestNo AND airwaybill_number =:airWayBillNumber")
    public abstract LiveData<List<Shipment_Detail>> getScannedVendorStatus(long manifestNo, long airWayBillNumber);


    @Update
    public abstract void insertAllShipment(Shipment_Detail... shipmentsDetail);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAdvanceShipment(Shipment_Detail shipment_detail);


    @Query("DELETE FROM manifest_shipment_detail WHERE manifestnoinchild =:manifestNoInchild AND airwaybill_number = :awbNo")
    public abstract void deleteAdvanceShipment(long manifestNoInchild, long awbNo);


    @Query("UPDATE manifest_shipment_detail SET status=:shipment_status WHERE manifestNoInchild= :manifestNo AND airwaybill_number=:awb AND status='EXS_10_10' ")
    public abstract void updateRtoShipmentStatus(long manifestNo, long awb, String shipment_status);


    @Query("UPDATE manifest_shipment_detail SET brand_package_id=:BP_ID WHERE manifestNoInchild= :manifestNo AND airwaybill_number=:awb AND status='EXS_10_10'")
    public abstract void updateBPID(long manifestNo, long awb, String BP_ID);


    @Query("UPDATE manifest_list SET isSharedManifest=:isSharedManifestStatus ,mobileNoType=:mobileNoType WHERE manifest_No IN (:manifestNo)")
    public abstract void updateSharedManifestStatus(List<Long> manifestNo, String mobileNoType, Boolean isSharedManifestStatus);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertShipment(Shipment_Detail shipment_detail);

   // @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertShipmentUsingRx(List<Shipment_Detail> shipment_detail);

    //status=:shipment_status , vehicle=:vehicle,manifestNoInchild= :manifestNo AND
    // @Query("UPDATE manifest_shipment_detail SET isChild=:isChild  where airwaybill_number=:awb")
    //public abstract void updateShipmentStatus(long awb, boolean isChild);

    @Query("UPDATE manifest_shipment_detail SET status=:shipment_status , vehicle=:vehicle, isChild=:isChild WHERE manifestNoInchild= :manifestNo AND airwaybill_number=:awb")
    public abstract void updateShipmentStatus(Long manifestNo, Long awb, String vehicle, String shipment_status, boolean isChild);

    @Query("UPDATE manifest_list SET shipment_count=:shipmentCount Where manifest_No=:manifestNo")
    public abstract void updateShipmentCount(long manifestNo, String shipmentCount);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number = :airwaybill_number AND manifestNoInchild = :manifest_num AND status= :status or status='EXS_20_00'")
    public abstract List<Shipment_Detail> isValidAWB(long manifest_num, long airwaybill_number, String status);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number =:airwaybill_number AND manifestNoInchild = :manifest_num")
    public abstract LiveData<Shipment_Detail> isAWBPresent(long manifest_num, long airwaybill_number);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number =:airwaybill_number AND manifestNoInchild = :manifest_num")
    public abstract boolean ifAWBPresentNew(long manifest_num, long airwaybill_number);

    @Query("select inScan from manifest_list WHERE manifest_No = :manifest_num")
    public abstract long isFirstScan(long manifest_num);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number = :airwaybill_number AND manifestNoInchild = :manifest_num AND status='EXS_110_00' OR airwaybill_number = :airwaybill_number AND manifestNoInchild = :manifest_num AND status='EXS_110_10'")
    public abstract boolean isAWBRtoLock(long manifest_num, long airwaybill_number);


    @Query("select manifestNoInchild from manifest_shipment_detail WHERE airwaybill_number = :airwaybill_number")
    public abstract long getManifestIdFromAwb(long airwaybill_number);

    @Query("UPDATE manifest_list SET inScan='1' WHERE manifest_No= :manifestNumber")
    public abstract void updateInscanStatus(long manifestNumber);

    @Query("UPDATE manifest_list SET isscanstarted='2',commit_status=:commitStatus WHERE manifest_No IN (:manifestNo)")
    public abstract void updateManifestList(String commitStatus, List<Long> manifestNo);

    @Query("UPDATE manifest_list SET commit_status=:commitStatus WHERE manifest_No IN (:manifestNo)")
    public abstract void updateManifestSelfList(String commitStatus, List<Long> manifestNo);

    @Query("UPDATE manifest_list SET commit_status=:commitStatus WHERE composite_Key= :compositeKey")
    public abstract void updateManifestListWithRecci(String commitStatus, String compositeKey);

    @Query("UPDATE manifest_list SET commit_status=:commit_status WHERE composite_Key=:compositeKey")
    public abstract void updateRecciList(String commit_status, String compositeKey);

    @Query("UPDATE manifest_list SET manifest_failed=:commit_status where (Select count(status) from manifest_shipment_detail where status=:commit_failed and manifestNoInchild=:manifestno)==0 and manifest_No=:manifestno")
    public abstract void failedManifestQuery(int commit_status, String commit_failed, long manifestno);

    //        @Query("select count(*) FROM manifest_list")
    @Query("select count(1) AS total, count(case when commit_status=:status THEN 1 end) AS pendingtotal from manifest_list")
//    @Query("select count(*)  from manifest_list where commit_status=:status")
    public abstract LiveData<CountManifest> checkAllManifestStatus(int status);

    @Query("update  manifest_list set commit_status=:status")
    public abstract void checkAllManifestStatusUpdate(int status);

    @Query("select * from manifest_list where commit_status=:commit_status and manifest_No > 0")
    public abstract LiveData<List<Manifest_List>> getUnPickedManifestList(int commit_status);

    @Query("select count(1) from manifest_list where commit_status=:commit_status and manifest_No > 0")
    public abstract LiveData<Integer> getUnSyncManifestList(int commit_status);

    @Query("Select Exists(select status from manifest_shipment_detail where manifestNoInchild=:manifestNumber and airwaybill_number =:awbNumber)")
    public abstract boolean getShipmentExist(long manifestNumber, long awbNumber);

    @Query("select status from manifest_shipment_detail where manifestNoInchild=:manifestNumber and airwaybill_number =:awbNumber")
    public abstract String getShipmentStatus(long manifestNumber, long awbNumber);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number = :awbNumber")
    public abstract boolean isAlreadyScanned(Long awbNumber);

    @Query("UPDATE commit_data SET firstInscanStatus=:firstscanstatus where (Select count(status) from manifest_shipment_detail where status=:commit_failed and manifestNoInchild=:manifestno)==0 and manifestNo=:manifestno")
    public abstract void failedCommitPacket(int firstscanstatus, String commit_failed, long manifestno);

    @Query("UPDATE commit_data SET firstInscanStatus=:scan_status where  manifestNo=:manifestno")
    public abstract void inScanCommitPacket(int scan_status, long manifestno);

    @Query("update commit_data set firstInscanStatus=1 where (select status from FirstInscan where manifetsId=:manifestno)==1 and manifestNo =:manifestno")
    public abstract void updateCommit_DataTable(long manifestno);

    @Query("Select * from manifest_shipment_detail  where manifestNoInchild in (:manifestNost) ORDER by master_airwaybill_number DESC")
    public abstract LiveData<List<Shipment_Detail>> fetchManifestData(List<Long> manifestNost);

    @Query("UPDATE commit_data SET firstInscanStatus=:scan_status where  CompositeKey=:comkey")
    public abstract void inScanCommitRecciPacket(int scan_status, String comkey);

    @Query("DELETE FROM manifest_shipment_detail WHERE  manifestnoinchild in (:manifestNoInchild)")
    //((select commit_status from manifest_list where manifest_No=:manifestNoInchild )==3) AND
    public abstract void deleteCommitedShipment(List<Long> manifestNoInchild);

    @Query("UPDATE manifest_list SET isscanstarted='1' WHERE manifest_No= :manifestNumber")
    public abstract void updateIsScanStarted(long manifestNumber);

    @Query("UPDATE manifest_list SET isscanstarted='0' WHERE manifest_No= :manifestNumber")
    public abstract void assignUpdateIsScanStarted(long manifestNumber);

    @Query("SELECT COUNT(*) FROM manifest_list WHERE isscanstarted='1' and manifest_No > 0 ")
    public abstract LiveData<Integer> getStartedManifestCount();

    @Query("SELECT * FROM manifest_shipment_detail WHERE manifestNoInchild = :manifestNoInchild AND airwaybill_number = :airwaybill_number AND brand_package_id  = :BrandPackaging_id")
    public abstract List<Shipment_Detail> BrandPackagingIDvalid(long manifestNoInchild, long airwaybill_number, String BrandPackaging_id);


 /*   @Query("SELECT * FROM manifest_shipment_detail WHERE  airwaybill_number = :airwaybill_number AND brand_package_id  = :BrandPackaging_id")
    public abstract List<Shipment_Detail> BrandPackagingIDvalidForAWB( long airwaybill_number, String BrandPackaging_id);

    */
 /*   @Query("SELECT * FROM manifest_shipment_detail WHERE manifestNoInchild = :manifestNoInchild AND airwaybill_number = :airwaybill_number AND brand_package_id  = :BrandPackaging_id")
    public abstract boolean BrandPackagingIDvalid(long manifestNoInchild, long airwaybill_number, long BrandPackaging_id);
*/

   /* @Query("select * from manifest_shipment_detail WHERE airwaybill_number =:airwaybill_number AND manifestNoInchild = :manifest_num")
    public abstract boolean ifAWBPresentNew(long manifest_num, long airwaybill_number);*/

    @Query("UPDATE manifest_shipment_detail SET status= :status ,reason_id=:reason_id, vehicle= :vehicle ,bp_Reason_Code_Applied=:reason_code_applied, dateTime =:date_time, reason_code=:reason_code WHERE airwaybill_number = :awbNo")
    public abstract void UpdateAWBViaBpReasonID(Long awbNo, String status, String vehicle, long date_time, int reason_id, String reason_code ,boolean reason_code_applied);


    @Query("select brand_package_id from manifest_shipment_detail WHERE airwaybill_number = :awb")
    public abstract String getSpecificBpId(long awb);

    @Query("select temp_key from manifest_shipment_detail WHERE airwaybill_number = :awb")
    public abstract boolean getTempKey(long awb);



}
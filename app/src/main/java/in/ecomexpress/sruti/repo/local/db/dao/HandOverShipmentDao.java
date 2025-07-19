package in.ecomexpress.sruti.repo.local.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Observable;

import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

;


/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 6/7/19.
 */
@Dao
public interface HandOverShipmentDao {
    // handover screen
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAwbNumber(HandOverShipmentList handOverShipmentList);

    @Query("select * from handOver_shipment_list")
    LiveData<List<HandOverShipmentList>> getAllAwbData();

    @Query("Delete from handOver_shipment_list where airWayBillNumber =:awbNumber")
    int delete(long awbNumber);

    @Query("SELECT * FROM handOver_shipment_list WHERE airwaybillnumber =:awbdata")
    LiveData<HandOverShipmentList> validateHandOverNumber(long awbdata);

    @Query("select * from manifest_shipment_detail WHERE airwaybill_number =:awbdata")
    LiveData<Shipment_Detail> validateAwbNumber(long awbdata);

    /*  @Query("select * from manifest_shipment_detail WHERE airwaybill_number =:airwaybill_number AND brand_package_id  = :BrandPackaging_id")
      Observable<List<Shipment_Detail>> BrandPackagingIDvalidForAWB(long airwaybill_number, String BrandPackaging_id);
     */
    @Query("SELECT * FROM manifest_shipment_detail WHERE  airwaybill_number = :airwaybill_number AND brand_package_id  = :BrandPackaging_id")
    public abstract List<Shipment_Detail> BrandPackagingIDvalidForAWB(long airwaybill_number, String BrandPackaging_id);

     /*   @Query("SELECT * FROM manifest_shipment_detail WHERE  airwaybill_number = :airwaybill_number AND brand_package_id  = :BrandPackaging_id")
    public abstract List<Shipment_Detail> BrandPackagingIDvalidForAWB( long airwaybill_number, String BrandPackaging_id);

    */

}

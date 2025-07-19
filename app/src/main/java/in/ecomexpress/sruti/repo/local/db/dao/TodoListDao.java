package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

/**
 * Created by 63091 on 28-06-2019.
 */

@Dao
public interface TodoListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTodoList(@NonNull List<ReasonCodeList> data, @NonNull List<Post_option> post_options);

    @Query("Select * from reason_code_list where Shipment=:value OR Manifest=:value")
    LiveData<List<ReasonCodeList>> getPickupList(boolean value);

    @Query("Select * from reason_code_list where Shipment=:value")
    LiveData<List<ReasonCodeList>> getPickupListShipment(boolean value);

    @Query("Select * from reason_code_list where Manifest =:value")
    LiveData<List<ReasonCodeList>> getPickupListManifest(boolean value);

    @Query("Select * from reason_code_list where Otp =:value")
    LiveData<List<ReasonCodeList>> getPickupListForOtp(boolean value);

    @Query("Select * from reason_code_list where Qr =:value")
    LiveData<List<ReasonCodeList>> getPickupListForQr(boolean value);

    @Query("Select * from reason_code_list where Branded_packaging=:value")
    LiveData<List<ReasonCodeList>> getBpReasonCodeList(boolean value);

    @Query("Select * from manifest_shipment_detail a Left JOIN manifest_list b ON a.composite_Key_child = b.composite_Key where b.composite_Key LIKE :searchdata")
    LiveData<List<Shipment_Detail>> fetchToDoFilterData(String searchdata);

    //Select * from manifest_shipment_detail a LEFT JOIN manifest_list b ON a.manifestNoInchild = b.manifestNo where b.manifestNo LIKE :searchdata
//    @Query("Select * from manifest_list where manifestNo  =:search")
//    LiveData<List<ManifestAndShipment>> getManifestAndShipment(String search);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecciQuestion(List<General_Question> recciquetion);

    @Query("Select * from recci_question_list where id in (:recciquetion)")
    LiveData<List<General_Question>> fetchQuestionList(List<Integer> recciquetion);

    @Query("DELETE FROM manifest_list where commit_status in (0,3)")
    void deleteManifestTable();

    @Query("DELETE FROM ApiUrlData")
    void deleteApiUrlDataTable();

    @Query("DELETE FROM reason_code_list")
    void deletereason_code_listTable();

    @Query("DELETE FROM global_scan_awb")
    void deleteglobal_scan_awbTable();

    @Query("DELETE FROM callbridge_pstns")
    void deletecallbridge_pstnsTable();

    @Query("DELETE FROM manifest_shipment_detail")
    void deletemanifest_shipment_detailTable();

    @Query("DELETE FROM handOver_shipment_list")
    void deletehandOver_shipment_listTable();

    @Query("DELETE FROM recci_question_list")
    void deleterecci_question_listTable();

    @Query("DELETE FROM FirstInscan")
    void deleteFirstInscanTable();

    @Query("DELETE FROM commit_data")
    void deletePushTable();

    @Query("DELETE FROM manifest_list")
    void deleteManifestTableForcefully();


    @Query("DELETE FROM pop_manifest")
    void deletePopData();
}

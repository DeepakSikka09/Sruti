package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.popData.PopData;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 6/11/19.
 */

@Dao
public interface PushApiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PushApi pushApi);


    @Query("UPDATE commit_data SET shipmentStatus= :shipmentStatus Where CompositeKey= :compositeKey")
    void updateCommitStatus(String shipmentStatus, String compositeKey);//List<Long> manifestNo

    @Query("UPDATE commit_data set fileUrl=:fileUrl where CompositeKey=:manifest")
    void updateFileUrl(String fileUrl, String manifest);

    @Query("UPDATE commit_data SET shipmentStatus= :shipmentStatus Where CompositeKey= :compositeKey")
    void updateCommitStatusWithRecci(String shipmentStatus, String compositeKey);

    @Query("SELECT * FROM commit_data WHERE  shipmentStatus =:shipmentStatus And firstInscanStatus=1 LIMIT :limit OFFSET :offset")
    List<PushApi> UnSyncCommitList(int shipmentStatus, int limit, int offset);

    @Query("SELECT * FROM commit_data WHERE  shipmentStatus =:shipmentStatus And manifestNo=:manifestNo")
    List<PushApi> getUnSyncCommitManifest( long manifestNo,int shipmentStatus);

    @Query("SELECT * FROM commit_data WHERE  shipmentStatus =:shipmentStatus And firstInscanStatus=1")
    LiveData<List<PushApi>> UnSyncCommitListAtLogin(int shipmentStatus);

}

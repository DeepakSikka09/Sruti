package in.ecomexpress.sruti.repo.local.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.popData.PopData;
@Dao
public interface PopApiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PopData popData);

/*    @Query("SELECT * FROM pop_manifest where status='PENDING'")
    public abstract LiveData<List<PopData> > getPopList();*/


    @Query("SELECT * FROM pop_manifest WHERE status='PENDING'")
   public  List<PopData> getPopList();


    @Query("UPDATE pop_manifest SET status= 'SUCCESS'")
    void updateCommitStatus();//List<Long> manifestNo

    @Query("UPDATE pop_manifest SET status=:commitStatus WHERE manifest_ids IN (:manifestNo)")
    public abstract void UpdatePopStatus(String commitStatus, List<Long> manifestNo);


   /* @Query("SELECT * FROM pop_manifest INNER JOIN pop_manifest_list ON pop_manifest.route_id = pop_manifest_list.route_id WHERE pop_manifest.status = 'pending'")
    List<Long> getPendingManifestIds();
}*/
}
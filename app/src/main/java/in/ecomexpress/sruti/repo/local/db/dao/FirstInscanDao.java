package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sruti.model.inScanData.FirstInscan;

/**
 * Created by shivangi on 7/11/19.
 */

@Dao
public interface FirstInscanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFirstScanShipment(FirstInscan firstInscan);

    @Query("UPDATE FirstInscan SET status='1' WHERE manifetsId =:manifestNumber")
    void updateInscanToFirstScanTable(long manifestNumber);

    @Query("Select * from firstinscan  WHERE status= :status")
    List<FirstInscan> getFirstInScan(int status);

    @Query("UPDATE firstinscan SET status= :status WHERE manifetsId =:manifestNo")
    void updateFirstInScan(int status,long manifestNo);


}

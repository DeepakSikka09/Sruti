package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import in.ecomexpress.sruti.model.masterdata.Post_option;

@Dao
public interface CallBridgePSTNDao {
    @Query("SELECT * FROM callbridge_pstns")
    List<Post_option> getAllCbPstnoptions();


}

package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import in.ecomexpress.sruti.repo.local.db.model.Remark;

@Dao
public interface DAORemarks {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Remark remarks);

}


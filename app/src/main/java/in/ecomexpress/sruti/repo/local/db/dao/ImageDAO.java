package in.ecomexpress.sruti.repo.local.db.dao;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.commitdata.ImageModel;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 21/10/19.
 */
@Dao
public abstract class ImageDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertIMG(ImageModel imageModel);

    @Query("UPDATE Image SET status= :status ,image_id= :image_id ,image_key= :image_key WHERE image_code =:image_name")
    public abstract void updateImageStatus(int status, String image_id, String image_key, String image_name);

    @Query("Select * from Image  WHERE status= :status AND manifest_id =:manifestNo")
    public abstract List<ImageModel> checkImageStatus(String manifestNo, int status);

    @Query("Select count(*) from Image  WHERE  manifest_id =:manifestNo")
    public abstract int checkImageStatusCount(String manifestNo);

  //  @Transaction
    public LiveData<List<ImageModel>> getImageStatus(String manifestNo) {
        MutableLiveData<List<ImageModel>> listMutableLiveData = new MutableLiveData<>();
        List<ImageModel> data = checkImageStatus(manifestNo, 1);
        int count = checkImageStatusCount(manifestNo);
        if (data != null && data.size() == count) {
            listMutableLiveData.postValue(data);
            return listMutableLiveData;
        } else {
            int i = 0;
            List<ImageModel> loc = new ArrayList<>();
            while (i < count) {
                ImageModel ob = new ImageModel();
                ob.setImage_id("-1");
                loc.add(ob);
                i++;
            }
            listMutableLiveData.postValue(loc);
            return listMutableLiveData;
        }
    }

    @Query("Select * from Image  WHERE status= :status")
    public abstract List<ImageModel> getUnSyncImageTEST(int status);


}

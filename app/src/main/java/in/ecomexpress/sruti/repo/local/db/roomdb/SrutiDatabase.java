package in.ecomexpress.sruti.repo.local.db.roomdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.repo.local.db.dao.CallBridgePSTNDao;
import in.ecomexpress.sruti.repo.local.db.dao.DAORemarks;
import in.ecomexpress.sruti.repo.local.db.dao.FirstInscanDao;
import in.ecomexpress.sruti.repo.local.db.dao.HandOverShipmentDao;
import in.ecomexpress.sruti.repo.local.db.dao.ImageDAO;
import in.ecomexpress.sruti.repo.local.db.dao.ManifestDao;
import in.ecomexpress.sruti.repo.local.db.dao.PopApiDao;
import in.ecomexpress.sruti.repo.local.db.dao.PushApiDao;
import in.ecomexpress.sruti.repo.local.db.dao.TodoListDao;
import in.ecomexpress.sruti.repo.local.db.model.ApiUrlData;
import in.ecomexpress.sruti.repo.local.db.model.GlobalScan;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.utils.common_files.Constants;

@Database(version = Constants.DB_VERSION, entities = {ApiUrlData.class, ReasonCodeList.class, GlobalScan.class, Post_option.class, Manifest_List.class, Shipment_Detail.class, HandOverShipmentList.class, ImageModel.class, General_Question.class, PushApi.class, PopData.class, Remark.class, FirstInscan.class}, exportSchema = false)
@TypeConverters(DataTypeConverterObjectToGson.class)
public abstract class SrutiDatabase extends RoomDatabase {
    public abstract TodoListDao doTOdoList();

    public abstract PushApiDao pushApiDAO();

    public abstract CallBridgePSTNDao callBridgePSTNDao();

    public abstract ManifestDao callManifestDao();

    public abstract HandOverShipmentDao awbNumberDao();

    public abstract ImageDAO imageDAO();

    public abstract FirstInscanDao firstInscanDao();

    public abstract DAORemarks daoRemarks();
   // public abstract PopApiDao popData();
    public abstract PopApiDao popData();

}
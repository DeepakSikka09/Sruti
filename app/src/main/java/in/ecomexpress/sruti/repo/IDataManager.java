package in.ecomexpress.sruti.repo;


import in.ecomexpress.sruti.repo.local.db.IDBHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPopPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.local.storage.IStorageHelper;
import in.ecomexpress.sruti.repo.remote.IRestApiHelper;

public interface IDataManager extends IDBHelper, IStorageHelper, IRestApiHelper, IPreferenceHelper, IPopPreferenceHelper {
    void setUserAsLoggedOut();

    void updateUserInfo(
            LoggedInMode loggedInMode,
            String authToken,
            String serviceCenter,
            String ServerTime,
            String name,
            String designation,
            String mobile,
            boolean is_ecom_vehicle,
            String code);

    void setVodaOrderNo(String orderNo);

    enum LoggedInMode {

        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_SERVER(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_GOOGLE(3);

        private final int mType;

        LoggedInMode(int type) {
            mType = type;
        }

        public int getType() {
            return mType;
        }
    }

}

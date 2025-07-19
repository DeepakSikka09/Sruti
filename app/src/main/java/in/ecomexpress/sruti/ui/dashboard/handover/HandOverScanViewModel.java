package in.ecomexpress.sruti.ui.dashboard.handover;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 5/7/19.
 */

@Module
public class HandOverScanViewModel extends BaseViewModel<IHandOverNavigator> {
    private static final String TAG = HandOverScanViewModel.class.getSimpleName();
    private final MediatorLiveData<HandOverShipmentList> mSectionLive = new MediatorLiveData<>();
    private final MediatorLiveData<HandOverShipmentList> mInsertLive = new MediatorLiveData<>();
    private final MediatorLiveData<List<HandOverShipmentList>> total_data = new MediatorLiveData<>();
    private static final String DeleteScannedStatus = "001";

    public HandOverScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public MutableLiveData<HandOverShipmentList> getShiListLiveData() {
        return mSectionLive;
    }

    public MutableLiveData<HandOverShipmentList> getInsertAwb() {
        return mInsertLive;
    }

    public MutableLiveData<List<HandOverShipmentList>> getAwbDetailLiveData() {
        return total_data;
    }

    public void onNext() {
        getNavigator().onNext();
    }

    public void insertAwbNumberList(HandOverShipmentList handOverShipmentList) {
        getDataManager().insertAwbNumberList(handOverShipmentList);
    }

    LiveData<Integer> deleteAwbNumber(int awbNumber) {
        return getDataManager().deleteAwbData(awbNumber);
    }

    public void isValidateAwbNumber(long awbNumber) {
        LiveData<HandOverShipmentList> dd = getDataManager().isValidateAwb(awbNumber);
        mSectionLive.addSource(dd, new Observer<HandOverShipmentList>() {
            @Override
            public void onChanged(HandOverShipmentList handOverShipmentLists) {
                if (handOverShipmentLists == null) {
                    // Fetch data from API
                    mSectionLive.setValue(handOverShipmentLists);
                } else {
                    mSectionLive.removeSource(dd);
                    mSectionLive.setValue(handOverShipmentLists);
                }
            }
        });

    }

    public LiveData<List<HandOverShipmentList>> getAllAwbData() {
        return getDataManager().getAllAwbData();
    }
}

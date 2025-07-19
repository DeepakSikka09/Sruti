package in.ecomexpress.sruti.ui.dashboard.handover;


import android.view.View;
import android.widget.AdapterView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import dagger.Module;
import in.ecomexpress.sruti.model.handoverdata.HandOverRequest;
import in.ecomexpress.sruti.model.handoverdata.HandOverResponse;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 17/9/19.
 */

@Module
public class HandOverListViewModel extends BaseViewModel<IHandOverListNavigator> {
    private final MediatorLiveData<HandOverResponse> mSectionLive = new MediatorLiveData<>();

    public HandOverListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public MutableLiveData<HandOverResponse> getHandOverLiveData() {
        return mSectionLive;
    }


    public void onVehicleType(AdapterView<?> parent, View view, int pos, long id) {

        getNavigator().VehicleType(parent.getSelectedItem().toString());
    }

    public void handOverListData(HandOverRequest handOverRequest) {
        try {
            handOverRequest.setEmployee_code(getDataManager().getCode());
            LiveData<HandOverResponse> att = getDataManager().getHandOverData(getDataManager().getAuthToken(), handOverRequest);
            mSectionLive.addSource(att, handOverResponse -> {
                mSectionLive.removeSource(att);
                mSectionLive.setValue(handOverResponse);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertHandOverNumberList(HandOverShipmentList handOverShipmentList) {
        getDataManager().insertAwbNumberList(handOverShipmentList);
    }

}

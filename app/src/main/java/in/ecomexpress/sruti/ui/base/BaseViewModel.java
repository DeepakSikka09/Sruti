package in.ecomexpress.sruti.ui.base;


import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;


public abstract class BaseViewModel<N> extends ViewModel {

    private WeakReference<N> mNavigator;
    private final IDataManager mDataManager;
    private final ISchedulerProvider mSchedulerProvider;
    private final ObservableBoolean mIsLoading = new ObservableBoolean(false);

    private CompositeDisposable mCompositeDisposable;

    public IDataManager getDataManager() {
        return mDataManager;
    }


    public BaseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    public N getNavigator() {
        return mNavigator.get();
    }


    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

    public ISchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public void writeErrors(long timeStamp, String e) {
        StringWriter sw = new StringWriter();
        String exceptionAsString = sw.toString();
    }

    public  MutableLiveData<Boolean> isGPSEnabled= new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();

        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    private double getGeoFenceRadius(){
        return Double.parseDouble(mDataManager.get_pickup_geofencing_radius());
    }


    public boolean loginAfterServerTime() {
        long serverTime= Long.parseLong(getDataManager().getServerTime());
        Date date = new Date();

        long currentTimeMilli = date.getTime();

        return currentTimeMilli <= serverTime;

    }





}

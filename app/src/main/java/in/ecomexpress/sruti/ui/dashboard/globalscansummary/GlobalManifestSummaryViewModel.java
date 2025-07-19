package in.ecomexpress.sruti.ui.dashboard.globalscansummary;


import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import java.util.HashSet;
import java.util.List;

import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.Observable;


public class GlobalManifestSummaryViewModel extends BaseViewModel<IScanSummaryNavigator> {
    public ObservableField<String> totalManifestCount = new ObservableField<>("");
    public ObservableField<String> totalShipmentCount = new ObservableField<>("");
    public ObservableField<String> pickedShipmentCount = new ObservableField<>("");
    public ObservableField<String> pendingShipmentCount = new ObservableField<>("");
    public ObservableField<String> unpickedShipmentCount = new ObservableField<>("");

    private static final String TAG = GlobalManifestSummaryViewModel.class.getSimpleName();
    long pickedValue;
    long pendingValue, unPicked, total,  unPicked_total ,rto1,rto2;
    HashSet<Manifest_List> manifest_lists = new HashSet<>();

    MutableLiveData<List<Manifest_List>> maListLiveData = new MutableLiveData<>();

    public ObservableField<String> getTotalManifestCount() {
        return totalManifestCount;
    }

    public void setTotalManifestCount(ObservableField<String> totalManifestCount) {
        this.totalManifestCount = totalManifestCount;
    }

    public ObservableField<String> getTotalShipmentCount() {
        return totalShipmentCount;
    }

    public void setTotalShipmentCount(ObservableField<String> totalShipmentCount) {
        this.totalShipmentCount = totalShipmentCount;
    }

    public ObservableField<String> getPickedShipmentCount() {
        return pickedShipmentCount;
    }

    public void setPickedShipmentCount(ObservableField<String> pickedShipmentCount) {
        this.pickedShipmentCount = pickedShipmentCount;
    }

    public ObservableField<String> getPendingShipmentCount() {
        return pendingShipmentCount;
    }

    public void setPendingShipmentCount(ObservableField<String> pendingShipmentCount) {
        this.pendingShipmentCount = pendingShipmentCount;
    }

    public ObservableField<String> getUnpickedShipmentCount() {
        return unpickedShipmentCount;
    }

    public void setUnpickedShipmentCount(ObservableField<String> unpickedShipmentCount) {
        this.unpickedShipmentCount = unpickedShipmentCount;
    }


    public MutableLiveData<List<Manifest_List>> getMaListLiveData() {
        return maListLiveData;
    }

    public void setMaListLiveData(MutableLiveData<List<Manifest_List>> maListLiveData) {
        this.maListLiveData = maListLiveData;
    }


    public GlobalManifestSummaryViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    void getScannedManifestListAllData() {
        getCompositeDisposable().add(getDataManager().getLastManifestdata(Constants.PICKED)
                .subscribeOn(getSchedulerProvider().io())
                .subscribe(manifest_lists -> {
                    maListLiveData.postValue(manifest_lists);

                }));
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void onNext() {
        getNavigator().onNext();
    }


    public void getPickedCount(List<Manifest_List> manifest_lists) {

        Observable.fromCallable(() -> {
            getData(manifest_lists);
            return false;
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().io())
                .subscribe((result) -> {

                });

    }


    private void getData(List<Manifest_List> manifestLists) {

        try {

            manifest_lists.addAll(manifestLists);
            for (Manifest_List manifestLists1 : manifestLists) {
                pickedValue = getDataManager().getShipmentCount(Constants.PICKED, manifestLists1.getManifest_No()).blockingSingle();
                pendingValue = getDataManager().getShipmentCount(Constants.PENDING, manifestLists1.getManifest_No()).blockingSingle();
                rto1 = getDataManager().getShipmentCount(Constants.RTO_STATUS_1, manifestLists1.getManifest_No()).blockingSingle();
                rto2 = getDataManager().getShipmentCount(Constants.RTO_STATUS_2, manifestLists1.getManifest_No()).blockingSingle();
                unPicked = getDataManager().getShipmentCount(Constants.FAILED, manifestLists1.getManifest_No()).blockingSingle();
                unPicked_total = rto1 + rto2 + unPicked;
                total = pickedValue + unPicked_total + pendingValue;
                manifestLists1.setPicked_count(pickedValue);
                manifestLists1.setUnpicked_count(unPicked_total);
                manifestLists1.setRemaining_count(pendingValue);
                manifestLists1.setTotalShipmentCount(total);
                manifest_lists.remove(manifestLists1);
                manifest_lists.add(manifestLists1);

            }
            Log.d(TAG, "getData: " + unPicked_total);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getNavigator().setAdapter(manifest_lists);
    }

    public void passImageView(ImageView cam, int position, long manifest_list) {
        Log.e("passImageView", "clicked");
        getNavigator().captureImage(cam, position, manifest_list);
    }

    public void uploadLocalImage(ImageModel imageModel) {
        getDataManager().saveImage(imageModel);
    }


}


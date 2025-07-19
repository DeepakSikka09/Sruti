package in.ecomexpress.sruti.ui.dashboard.signature;


import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by deepak on 8/11/19.
 */

@Module
public class SuccessFailViewModel extends BaseViewModel {

    public SuccessFailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifest_no) {
        return getDataManager().getSpecificManifestDetail(manifest_no);
    }


}

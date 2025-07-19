package in.ecomexpress.sruti.ui.dashboard.training;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import dagger.Module;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.OtpRequest.VerifyPickUpOtpRequest;
import in.ecomexpress.sruti.model.starttrip.StartTripResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.IDashboardNavigator;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
@Module
public class TrainingViewModel extends BaseViewModel<ITrainingNavigator> {

    private MediatorLiveData<TrainingResponse> trainingResponseMediatorLiveData = new MediatorLiveData<TrainingResponse>();

    MutableLiveData<TrainingResponse> getTrainingResponse() {
        return trainingResponseMediatorLiveData;
    }
    public TrainingViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider){
        super(dataManager, schedulerProvider);
    }



    public void onBackClick(){
        getNavigator().onBackClick();
    }


    public void getTrainingUrlAPI(Activity activity) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching DATA...");
        dialog.setIndeterminate(true);
        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setEmployeeCode(getDataManager().getCode());
        LiveData<TrainingResponse> ob = getDataManager().TrainingVideos(getDataManager().getAuthToken(), trainingRequest);
        dialog.dismiss();
        trainingResponseMediatorLiveData.addSource(ob, trainingResponse -> {
            trainingResponseMediatorLiveData.removeSource(ob);
            trainingResponseMediatorLiveData.setValue(trainingResponse);

        });

    }

}
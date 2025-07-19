package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import android.app.ProgressDialog;
import android.util.Log;


import java.util.List;

import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

import static android.content.ContentValues.TAG;


public class SwitchNumberViewModel extends BaseViewModel<SwitchNumberCallBack> {
    ProgressDialog dialog;
    List<Post_option> mycbPstnOptions;
    Post_option postoption;

    public SwitchNumberViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    public void onSubmitNumber() {
        getNavigator().onSubmitNumber();
    }


    public void onCancelClick() {
        getNavigator().dismissDialog();
    }

    public void getCbPstnOptions() {
        try {
            getCompositeDisposable().add(getDataManager().
                    getCbPstnOptions().observeOn(getSchedulerProvider().ui()).
                    subscribeOn(getSchedulerProvider().io()).
                    subscribe(new Consumer<List<Post_option>>() {
                        @Override
                        public void accept(List<Post_option> cbPstnOptions) {
                            Log.d(TAG, "getCbPstnOptions: " + cbPstnOptions.toString());
                            mycbPstnOptions = cbPstnOptions;
                            getNavigator().OnSetFuelAdapter(cbPstnOptions);
                        }
                    }, throwable ->

                    {
                        setIsLoading(false);
                        String error;
                        try {
//                            writeErrors(timeStamp, new Exception(throwable));
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            getNavigator().showErrorMessage(error.contains("HTTP 500 "));
                        } catch (Exception e) {
                            getNavigator().showException(e);
                            e.printStackTrace();

                        }
                    }));
        } catch (Exception e) {
            getNavigator().showException(e);
//            writeErrors(timeStamp, e);
//            Log.e(TAG, e.getCause().getStackTrace().toString());
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }

        }
    }

}

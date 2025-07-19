package in.ecomexpress.sruti.ui.dashboard.fuel;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.databinding.ObservableField;

import java.util.List;

import in.ecomexpress.sruti.model.fuel.FuelReimbursementRequest;
import in.ecomexpress.sruti.model.fuel.response.FuelReimbursementResponse;
import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

public class FuelReimburseViewModel extends BaseViewModel<IFuelReimburseNavigator> {
    private static final String TAG = FuelReimburseViewModel.class.getSimpleName();
    ProgressDialog dialog;
    ObservableField<Boolean> listEmpty = new ObservableField<>();
    List<Reports> getReports;

    public FuelReimburseViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    /*public FuelReimburseViewModel(FuelReimbursementResponse fuelReimbursementResponse, WarehouseShipmentAdapter.MyViewHolder myViewHolder) {
        super(fuelReimbursementResponse,myViewHolder);
    }
*/


    public void onBackClick() {
        getNavigator().onBackClick();
    }


    public void getAllFuelList(Context context) {
        try {
            dialog = new ProgressDialog(context);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setMessage("Fetching Data...");
            dialog.setIndeterminate(true);
//        FuelReimburseViewModel.this.setIsLoading(true);
            final long timeStamp = System.currentTimeMillis();
            FuelReimbursementRequest request = new FuelReimbursementRequest(getDataManager().getCode(), "", "");

            getCompositeDisposable()
                    .add(getDataManager()
                            .doFuelListApiCall(getDataManager().getAuthToken(), request)
                            .doOnSuccess(new Consumer<FuelReimbursementResponse>() {
                                @Override
                                public void accept(FuelReimbursementResponse fuelReimbursementResponse) {
//                                dialog.dismiss();
                                    Log.d(TAG, "accept: " + fuelReimbursementResponse);
                                }

                            })
                            .subscribeOn(getSchedulerProvider().io())
                            .observeOn(getSchedulerProvider().ui())
                            .subscribe(new Consumer<FuelReimbursementResponse>() {
                                @Override
                                public void accept(FuelReimbursementResponse fuelReimbursementResponse) {
                                    dialog.dismiss();
                                    try {

                                        try {
//                                    FuelReimburseViewModel.this.setIsLoading(false);
                                            if (fuelReimbursementResponse != null) {
                                                Log.d("fuelReimbursement", fuelReimbursementResponse.toString());
                                                if (fuelReimbursementResponse.getStatus() == true) {
                                                    getReports = fuelReimbursementResponse.getResponse().getReports();
                                                    if (getReports.size() > 0) {
                                                        getNavigator().OnSetFuelAdapter(fuelReimbursementResponse.getResponse().getReports());

                                                    } else {
                                                        getNavigator().onshowDescription("No data found");
                                                    }
//                            setListEmpty(false);
                                                } else {
                                                    getNavigator().onshowDescription(fuelReimbursementResponse.getResponse().getDescription());

                                                }
                                            } else {
                                                getNavigator().showNoResultMessage();

//                        setListEmpty(true);
                                            }

                                        } catch (Exception e) {
                                            getNavigator().showException(e);
                                            dialog.dismiss();
                                            String error;
                                            error = new RestApiErrorHandler(e).getErrorDetails().getEResponse().getDescription();
                                            if (error.contains("HTTP 500 ")) {
                                                getNavigator().showErrorMessage(true);
                                            }


//                                    dialog.dismiss();
//                                    FuelReimburseViewModel.this.setIsLoading(false);
                                            Log.d(TAG, "fuelReimbursementResponse: " + fuelReimbursementResponse.toString());
                                            if ((fuelReimbursementResponse.getResponse() != null) && (fuelReimbursementResponse.getResponse().getCode().equalsIgnoreCase("107"))) {
                                                getNavigator().onshowLogout(fuelReimbursementResponse.getResponse().getDescription());
                                            } else {
                                                getNavigator().showException(e);
                                                e.printStackTrace();
                                                //getNavigator().onHandleError(error);
                                            }
                                        }


                                    } catch (Exception e) {
                                        dialog.dismiss();
                                        getNavigator().showException(e);
                                        e.printStackTrace();
                                    }

                                }
                            }, throwable -> {
//                            FuelReimburseViewModel.this.setIsLoading(false);
                                String error;
                                dialog.dismiss();
                                try {
                                    error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                                    if (error.contains("HTTP 500 ")) {
                                        getNavigator().showErrorMessage(true);

                                    } else {
                                        getNavigator().showErrorMessage(false);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    getNavigator().showException(e);
                                    dialog.dismiss();
                                }
                            }));

        } catch (Exception e) {
            if (dialog.isShowing())
                dialog.dismiss();
            getNavigator().showException(e);
            e.printStackTrace();
        }
    }

    public void logoutLocal() {
        //getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
    /*    getCompositeDisposable().add(getDataManager()
                .deleteAllTables().subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        try {
                            getDataManager().clearPrefrence();
                            getDataManager().setUserAsLoggedOut();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        getNavigator().clearStack();

                    }
                }));*/
    }

    public Boolean getListEmpty() {
        return listEmpty.get();
    }

    private void setListEmpty(boolean empty) {
        listEmpty.set(empty);
    }
}

package in.ecomexpress.sruti.ui.dashboard.performance;


import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.model.performance.PerformanceRequest;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

public class PerformanceViewModel extends BaseViewModel<IPerformanceNavigator> {


    public PerformanceViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    public void onBackClick() {
        getNavigator().backClick();
    }

    public String getAuthToken() {
        return getDataManager().getAuthToken();
    }

    public void callApi(PerformanceActivity context) {
        try {
            PerformanceRequest request = new PerformanceRequest(getDataManager().getCode());
            final long timeStamp = System.currentTimeMillis();
            getCompositeDisposable().add(getDataManager()
                    .doPerformanceApiCall(getDataManager().getAuthToken(), request)
                    .observeOn(getSchedulerProvider().ui())
                    .subscribeOn(getSchedulerProvider().io()).
                            subscribe(performanceResponse -> {
                                try {
                                    context.dialog.dismiss();
                                    getNavigator().startPerformanceWebView(performanceResponse.getPerformance_response());
                                } catch (Exception e) {
                                    getNavigator().showException(e);
                                    e.printStackTrace();
                                    writeErrors(System.currentTimeMillis(), e.toString());
                                    if (!performanceResponse.status) {
                                        if (performanceResponse.response.getCode().equalsIgnoreCase("107")) {
                                            getNavigator().doLogout(performanceResponse.response.getDescription());
                                        } else if (performanceResponse.response.getDescription().contains("Invalid")
                                                && performanceResponse.response.getDescription().contains("Token")) {
                                        }
                                    }
                                }
                            }, throwable -> {
                                try {
//                                    writeErrors(System.currentTimeMillis(), String.valueOf(new Exception(throwable)));
                                    context.dialog.dismiss();
                                    if (throwable.getMessage().contains("HTTP 500 "))
                                        getNavigator().showHandleError(true);
                                    else
                                        getNavigator().showHandleError(false);

                                } catch (Exception e) {
                                    getNavigator().showException(e);
                                    e.printStackTrace();
                                }
                            }));
        } catch (Exception e) {
            getNavigator().showException(e);
            e.printStackTrace();
            writeErrors(System.currentTimeMillis(), e.toString());
            context.dialog.dismiss();
            getNavigator().errorHandler("Unable to Load...");
        }


    }
    public void logoutLocal() {
       // getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
    }


}

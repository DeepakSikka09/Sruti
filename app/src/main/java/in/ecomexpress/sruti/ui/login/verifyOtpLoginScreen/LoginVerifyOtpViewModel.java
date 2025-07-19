package in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen;

import android.util.Log;

import in.ecomexpress.sruti.model.resendOtp.LoginResendOtpRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpRequest;
import in.ecomexpress.sruti.model.verifyOtp.LoginVerifyOtpResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static android.content.ContentValues.TAG;

/**
 * Created by dhananjayk on 29-01-2019.
 */

public class LoginVerifyOtpViewModel extends BaseViewModel<ILoginVerifyOtpNavigator> {

    public LoginVerifyOtpViewModel(IDataManager dataManager,
                                   ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onResendOtpClick() {
        getNavigator().onResendOtp();
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public String getmobile() {
        return "We have sent you SMS with a code to " + getDataManager().getMobile();
    }

    public void updateUserLoggedInState() {
        getDataManager().updateUserLoggedInState(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
    }

    public void onOtpVerify() {
        getNavigator().onverify();
    }

    public void veryfyOtp(String otpDelimiter) {
        setIsLoading(true);
        try {
            String empCode = getDataManager().getCode();
            LoginVerifyOtpRequest request = new LoginVerifyOtpRequest(empCode, otpDelimiter);
            final long timeStamp = System.currentTimeMillis();
//            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doLoginVerifyOtpApiCall(getDataManager().getAuthToken(), request)
                    .doOnSuccess(new Consumer<LoginVerifyOtpResponse>() {
                        @Override
                        public void accept(LoginVerifyOtpResponse loginVerifyOtpResponse) {
                            Log.d(TAG, loginVerifyOtpResponse.toString());
//                            writeRestAPIResponse(timeStamp, loginVerifyOtpResponse);
                            if (loginVerifyOtpResponse.isStatus()) {

                            }
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<LoginVerifyOtpResponse>() {
                        @Override
                        public void accept(LoginVerifyOtpResponse response) {
                            Log.d(TAG, "login: " + response.toString());
                            LoginVerifyOtpViewModel.this.setIsLoading(false);

                            if (response.isStatus()) {

                                getNavigator().onNext(otpDelimiter);
//                                getMasterData(response, otpDelimiter);

                            } else {
                                //Error message comes in All Caps and this not good at all.
                                if (response.getsResponse().getCode() == 107) {
                                    LocalLogout();
                                } else
                                    LoginVerifyOtpViewModel.this.getNavigator().onHandleError(response.getsResponse().getDescription());
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
                        try {
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showErrorMessage(true);
                            } else {
                                getNavigator().showErrorMessage(false);
                            }
                        } catch (Exception e) {
                            getNavigator().showException(e);
                            e.printStackTrace();
                        }
                    }));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            getNavigator().showException(e);
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

    private void LocalLogout() {
       // getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(getDataManager()
                .deleteAllTables().subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        try {
                            getDataManager().clearPrefrence();
                            getDataManager().clearPopPrefrence();
                            getDataManager().setUserAsLoggedOut();
                        } catch (Exception e) {
                            getNavigator().showException(e);
                            e.printStackTrace();
                        }
                        clearStack();

                    }
                }));
    }

    private void clearStack() {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
            stopSelf();
        } else {
            SyncServices.stop(getApplicationContext());
        }*/
        Constants.SERVICERUNNING = false;
        getNavigator().logout();

    }

    public void resendOtp(String otpDelimiter) {
        setIsLoading(true);
        try {
            String empCode = getDataManager().getCode();
            LoginResendOtpRequest request = new LoginResendOtpRequest(empCode);
            final long timeStamp = System.currentTimeMillis();
//            writeRestAPIRequst(timeStamp, request);
            getCompositeDisposable().add(getDataManager()
                    .doLoginResendOtpApiCall(getDataManager().getAuthToken(), request)
                    .doOnSuccess(new Consumer<LoginVerifyOtpResponse>() {
                        @Override
                        public void accept(LoginVerifyOtpResponse loginVerifyOtpResponse) {
//                            writeRestAPIResponse(timeStamp, loginVerifyOtpResponse);
                            Log.d(TAG, loginVerifyOtpResponse.toString());

                            if (loginVerifyOtpResponse.isStatus()) {


                            }
                        }
                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<LoginVerifyOtpResponse>() {
                        @Override
                        public void accept(LoginVerifyOtpResponse response) {
                            Log.d(TAG, "login: " + response.toString());
                            LoginVerifyOtpViewModel.this.setIsLoading(false);

                            if (response.isStatus()) {

                                getNavigator().onNext(otpDelimiter);
                            } else {
                                if (response.getsResponse().getCode() == 107) {
                                    LocalLogout();
                                }
                                //Error message comes in All Caps and this not good at all.
                                LoginVerifyOtpViewModel.this.getNavigator().onHandleError(response.getsResponse().getDescription());
                            }
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        String error;
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

                        }
                    }));
        } catch (Exception e) {
            getNavigator().showException(e);
            Log.e(TAG, e.getMessage());
            setIsLoading(false);
            if (e instanceof Throwable) {
                getNavigator().onHandleError(new RestApiErrorHandler(e.fillInStackTrace()).getErrorDetails().getEResponse().getDescription());
            }
        }
    }

}

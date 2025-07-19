package in.ecomexpress.sruti.ui.login.forget;

import android.util.Log;

import in.ecomexpress.sruti.model.login.ForgetPasswordUserRequest;
import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;
import in.ecomexpress.sruti.model.login.OTPVerifyWithPasswordRequest;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

import io.reactivex.functions.Consumer;

import static android.content.ContentValues.TAG;

/**
 * Created by dhananjayk on 24-05-2018.
 */

public class ForgetViewModel extends BaseViewModel<IForgetNavigator> {
    public ForgetViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onServerLoginClick() {
        getNavigator().onServerLogin();
    }

    public void onBackClick() {
        getNavigator().onBackClicked();
    }

    public void onForgetPasswordClick() {
        getNavigator().ViewFlag();
    }

    public void forgetPassword(String email) {

        setIsLoading(true);
        ForgetPasswordUserRequest request = new ForgetPasswordUserRequest(email);
        final long timeStamp=System.currentTimeMillis();
//        writeRestAPIRequst(timeStamp,request);
        getCompositeDisposable().add(getDataManager()
                .doForgetPasswordApiCall(request)
                .doOnSuccess(new Consumer<ForgotPasswordResponse>() {
                    @Override
                    public void accept(ForgotPasswordResponse response) {
                        Log.d(TAG, response.toString());
//                        writeRestAPIResponse(timeStamp,response);
                    }
                })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe((ForgotPasswordResponse response) -> {
                    setIsLoading(false);
                    if (response.isStatus()) {
                        getNavigator().ViewFlag();
                    } else {
                        getNavigator().onHandleError(response.getResponse().getDescription());

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        setIsLoading(false);
                        Log.e("error", throwable.getMessage());
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
                        //getNavigator().onHandleError(new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription());
                        // getNavigator().ViewFlag();
                    }
                }));
    }

    public void otpVerifyWithPassword(String userName, String otp, String newPassword) {
        setIsLoading(true);
        OTPVerifyWithPasswordRequest request = new OTPVerifyWithPasswordRequest(userName, otp, newPassword);
        final long timeStamp=System.currentTimeMillis();
//        writeRestAPIRequst(timeStamp,request);
        getCompositeDisposable().add(getDataManager()
                .doOTPVerifyWithPasswordApiCall(request)
                .doOnSuccess(new Consumer<ForgotPasswordResponse>() {
                    @Override
                    public void accept(ForgotPasswordResponse response) {
                        Log.d(TAG, "accept: " + response.toString());
//                        writeRestAPIResponse(timeStamp,response);

                    }
                })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe((ForgotPasswordResponse response) -> {
                    setIsLoading(false);
                    if (response.isStatus()) {
                        getNavigator().onSuccessFullyChangePassword(response.getResponse().getDescription());
                    } else {
                        getNavigator().onHandleError(response.getResponse().getDescription());

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
                    //  getNavigator().onHandleError(new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription());

                }));
    }

    public boolean isUserIdValid(String userId) {
        //validate email and password
        return userId != null && !userId.isEmpty();
    }
}


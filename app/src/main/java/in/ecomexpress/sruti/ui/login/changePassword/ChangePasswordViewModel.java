package in.ecomexpress.sruti.ui.login.changePassword;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableField;

import in.ecomexpress.sruti.model.login.ChangePasswordRequest;
import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


public class ChangePasswordViewModel extends BaseViewModel<ChangePasswordNavigator> {
    private static final String TAG = ChangePasswordViewModel.class.getSimpleName();
    private final ObservableField<String> empCode = new ObservableField<>();
    String username="";
    public ObservableField<String> getCode() {
        return empCode;
    }

    public ChangePasswordViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    public void onChangePasswordClick() {
        getNavigator().onChangePassword();
    }


    public void changePasswordRequest(String usename, String oldPassword, String newPassword) {
        setIsLoading(true);
        ChangePasswordRequest request = new ChangePasswordRequest(usename, oldPassword, newPassword);
        final long timeStamp = System.currentTimeMillis();
//        writeRestAPIRequst(timeStamp, request);
        String auth = getDataManager().getAuthToken();
        String token="";
        if (auth!=null){
            token.equals(auth);
        }
        getCompositeDisposable().add(getDataManager()
                .doResetPasswordApiCall(token, request)
                .doOnSuccess(new Consumer<ForgotPasswordResponse>() {
                    @Override
                    public void accept(ForgotPasswordResponse response) {
                        Log.d(TAG, "accept: " + response.toString());
//                        writeRestAPIResponse(timeStamp, response);

                    }
                })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe((ForgotPasswordResponse response) -> {
                    setIsLoading(false);
                    try {

                        if (response.isStatus()) {
                            getNavigator().onSuccess(response);
                        } else if (!response.isStatus()) {
                            if (response.getResponse().getStatusCode() == 3009) {
                                getNavigator().onHandleError(response.getResponse().getDescription());
                            } else if (response.getResponse().getDescription().contains("Invalid")
                                    && response.getResponse().getDescription().contains("Token")) {
                                getNavigator().onHandleError(response.getResponse().getDescription());
                            } else {
                                getNavigator().onHandleError(response.getResponse().getDescription());
                            }
                        }
                    } catch (Exception e) {
                        getNavigator().showException(e);
//                        writeErrors(System.currentTimeMillis(), e);
                        e.printStackTrace();
                    }
                }, (Throwable throwable) -> {
                    setIsLoading(false);
                    String error;
                    try {
//                        writeErrors(System.currentTimeMillis(), new Exception(throwable));
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
                }));
    }

    public boolean isStringMatch(String newstring, String oldstring) {
        //validate email and password
        if (newstring == null || newstring.isEmpty()) {
            return false;
        }
        if (oldstring == null || oldstring.isEmpty()) {
            return false;
        }
        return newstring.equals(oldstring);

    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void setUserId() {
        final String code = getDataManager().getCode();
        if (!TextUtils.isEmpty(code)) {
            empCode.set(code);
        }
    }

    public void updateUserLoggedInState() {
        getDataManager().updateUserLoggedInState(IDataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
    }

    public void logoutLocal() {
        //getDataManager().setTripId("");
        getDataManager().setCurrentUserLoggedInMode(IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
        clearAppData();
    }

    private void clearAppData() {
        getCompositeDisposable().add(getDataManager()
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
                        getNavigator().clearStack();

                    }
                }));
    }


}
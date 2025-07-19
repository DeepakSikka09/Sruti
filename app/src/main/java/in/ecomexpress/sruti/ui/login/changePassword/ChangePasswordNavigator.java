package in.ecomexpress.sruti.ui.login.changePassword;


//import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;

import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;

public interface ChangePasswordNavigator {
    void onServerLogin();

    void onChangePassword();

    void onHandleError(String errorresponse);

    void onSuccess(ForgotPasswordResponse forgotPasswordResponse);

    void onBackClick();

    void doLogout(String description);

    void clearStack();

    void showErrorMessage(boolean status);

    void showException(Exception e);
}

package in.ecomexpress.sruti.ui.login.forget;

/**
 * Created by dhananjayk on 24-05-2018.
 */

public interface IForgetNavigator {
    void onForgetPassword();
    void onServerLogin();
    void ViewFlag();
    void onHandleError(String errorDetails);
    void onSuccessFullyChangePassword(String statusMessage);
    void onBackClicked();
    void showErrorMessage(boolean status);

    void showException(Exception e);
}
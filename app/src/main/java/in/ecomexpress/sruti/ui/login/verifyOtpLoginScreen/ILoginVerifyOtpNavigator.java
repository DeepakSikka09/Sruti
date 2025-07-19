package in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen;

/**
 * Created by dhananjayk on 29-01-2019.
 */

public interface ILoginVerifyOtpNavigator {
    void onResendOtp();

    void onHandleError(String description);

    void onNext(String otpDelimiter);

    void onverify();

    void logout();

    void showErrorMessage(boolean status);

    void onBackClick();

    void showException(Exception e);
}

package in.ecomexpress.sruti.ui.dashboard.performance;

public interface IPerformanceNavigator {
    void backClick();

    void startPerformanceWebView(String s);

    void errorHandler(String s);

    void showHandleError(boolean status);

    void doLogout(String message);

    void clearStack();

    void showException(Exception e);
}

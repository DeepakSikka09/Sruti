package in.ecomexpress.sruti.ui.dashboard.sos;

public interface SOSCallBack {

    void dismissDialog();

    void showDescription(String description);

    void logout();

    void showOptionMenu();


    void showException(Exception e);
}

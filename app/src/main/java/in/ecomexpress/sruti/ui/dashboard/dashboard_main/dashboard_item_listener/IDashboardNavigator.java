package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.repo.remote.ErrorResponse;
import in.ecomexpress.sruti.model.DashboardBanner;

public interface IDashboardNavigator {
    void openChangePasswordActivity();

    void openProfileActivity();

    void openstatisticsActivity();

    void openTodo();

    void onStartStopTrip();

    void openStopTrip();

    void noToDo();

    void onHandleError(ErrorResponse errorDetails);

   // void doLogout(String message);

    void openFuelReimburse();

    void onAttendanceClick();

    void onSosClick();

    void onLogoutClick();

    void onTrainingClick();

    void showError(String error);

    Context getActivityContext();

    void dashboardBannerList(List<DashboardBanner> mydashboardBannerList);

    void onHandoverClick();

    void nextScreen(String manifest_no, String sFileBody);
    void checkMultiSpace(ArrayList<String> multipspaceList, String multi_space_allow);
}


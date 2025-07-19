package in.ecomexpress.sruti.ui.dashboard.fuel;

import java.util.List;

import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.repo.remote.ErrorResponse;


public interface IFuelReimburseNavigator {

    void onBackClick();

    void onHandleError(ErrorResponse errorDetails);

    void onshowDescription(String error);

    void OnSetFuelAdapter(List<Reports> reports);

    void showNoResultMessage();

    void showError(String error);

    void clearStack();

    void onshowLogout(String description);

    void showErrorMessage(boolean status);

    void showException(Exception e);
}

package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import java.util.List;

import in.ecomexpress.sruti.model.masterdata.Post_option;


/**
 * Created by  on 4/12/2018.
 */

public interface SwitchNumberCallBack {
    void dismissDialog();

    void cancel();

    void OnSetFuelAdapter(List<Post_option> postoptionList);
    void onSubmitNumber();

    void showException(Exception e);

    void onHandleError(String description);

    void showErrorMessage(boolean b);
}

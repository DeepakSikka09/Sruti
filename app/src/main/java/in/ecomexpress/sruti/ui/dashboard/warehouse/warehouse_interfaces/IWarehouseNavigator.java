package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces;

import android.app.ProgressDialog;

import in.ecomexpress.sruti.model.childCommitStatus.Child_Shipment_details;

/**
 * Created by shivangi on 14/8/19.
 */

public interface IWarehouseNavigator {

    void onSynClickEvent();

    void onScanClick();

    void onErrorMessage(String message);

    void notifyAdapter();

    void onNextClick();

    void onCheckStatusClick();

    //void isAwbValid(Boolean aBoolean, Child_Shipment_details child_shipment_details);

    void nextScreen(String manifest_no, String sFileBody);

    void showDialogBox(String response, ProgressDialog dialogOnDataUpdate);
}

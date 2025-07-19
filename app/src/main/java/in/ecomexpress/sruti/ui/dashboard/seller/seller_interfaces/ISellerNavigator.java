package in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces;


import android.app.ProgressDialog;

import in.ecomexpress.sruti.model.childCommitStatus.Child_Shipment_details;

public interface ISellerNavigator {

    void onScanClick();

    void onSynClickEvent();

    void onErrorMessage(String message);

    void notifyAdapter();

    void onNextClick();

    void onCheckStatusClick();

    void nextScreen(String manifest_no, String sFileBody);

    void showDialogBox(String response, ProgressDialog dialogOnDataUpdate);

   // void isAwbValid(Boolean aBoolean, Child_Shipment_details child_shipment_details);
}

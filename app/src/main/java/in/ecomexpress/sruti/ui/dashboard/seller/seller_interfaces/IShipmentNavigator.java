package in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces;

import android.app.ProgressDialog;

import java.util.HashSet;

import in.ecomexpress.sruti.model.childCommitStatus.Child_Shipment_details;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

/**
 * Created by deepak on 28/8/19.
 */

public interface IShipmentNavigator {
    void onErrorMessage(String message);

    void notifyAdapter();

    void update(Shipment_Detail shipmentsDetail);

    void onCheckStatusClick();

    void updateCountAfterMarkingFail(Long picked, Long unpicked, Long remaining);

    void setUpdatedCount(HashSet<Manifest_List> hashMap);

    void notifyUpdatedCount();

  //  void isAwbValid(Boolean aBoolean, Child_Shipment_details child_shipment_details);

    void showDialogBox(String response, ProgressDialog dialogOnDataUpdate);

    void nextScreen(String manifest_no, String sFileBody);

}

package in.ecomexpress.sruti.ui.dashboard.handover;


import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 5/7/19.
 */


public interface IHandOverNavigator {
    void onNext();

    void showValidationForNull();

    void showErrorMessage(String b);

    void showException(Exception e);

    void isAwbValid(List<Shipment_Detail> shipment_details, long awbNumber);

    void realTimeCheckInvalid(String awb);

    void notifyAdapter();

    void isAWBRtoLock(Boolean aBoolean, long awbNumber);

    void checkFirstScan(Long aBoolean);


    void getShipmentStatus(String aBoolean, long awbNumber);

    void isAlreadyScanned(Boolean aBoolean, Long awbNumber);

    void RtoLocked();

    void ifAWBPresent(Boolean aBoolean, long awb);

    void getShipmentExist(Boolean manifest_list, long awbNumber);
    void isBrandPackagingIDisValid(List<Shipment_Detail> shipment_details, String bp_id);

    void getBP_ID(String bp_id, long awb_no);

    void getTempKey(String bpId,long awbNo,Boolean tempKey);

}
package in.ecomexpress.sruti.ui.dashboard.globalscan;


import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

public interface IScanScreenNavigator {


    void onNext();

    void checkFirstScan(Long isFirst, Long manifest, Manifest_List scannedManifest, String came_from, long awb, boolean bp_exist);


    void isBrandPackagingIDisValid(List<Shipment_Detail> shipment_details, String bp_id);

    void getBP_ID(String bp_id, long awb_no);

    void getTempKey( long awbNo, Boolean tempValue);
}

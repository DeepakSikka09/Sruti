package in.ecomexpress.sruti.ui.dashboard.globalscan;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;


public class GlobalScanItemViewModel {

    public IGlobalAdapterInterface iGlobalAdapterInterface;
    public Shipment_Detail globalScan;

    public GlobalScanItemViewModel(Shipment_Detail globalScan, IGlobalAdapterInterface iGlobalAdapterInterface) {
        this.globalScan = globalScan;
        this.iGlobalAdapterInterface = iGlobalAdapterInterface;
    }
    public String Airwaybill() {
        return String.valueOf(globalScan.getAirWayBillNumber());
    }



}

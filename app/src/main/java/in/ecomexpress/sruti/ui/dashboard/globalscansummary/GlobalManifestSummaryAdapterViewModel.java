package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import in.ecomexpress.sruti.model.menifestdata.Manifest_List;


public class GlobalManifestSummaryAdapterViewModel {

    public IScanSummaryItemAdapterInterface iScanSummaryItemAdapterInterface;
    public Manifest_List manifest_list;
    private int position;

    public GlobalManifestSummaryAdapterViewModel(Manifest_List manifest_list, IScanSummaryItemAdapterInterface iScanSummaryItemAdapterInterface, int position) {
        this.manifest_list = manifest_list;
        this.iScanSummaryItemAdapterInterface = iScanSummaryItemAdapterInterface;
        this.position = position;
    }

    public String shipperName() {
        return manifest_list.getCust_name();
    }

    public String manifestID() {
        return "Manifest No. - " + manifest_list.getManifest_No();
    }

    public String totalItem() {
        return String.valueOf(manifest_list.getTotalShipmentCount());
    }

    public String totalpicked() {
        return String.valueOf(manifest_list.getPicked_count());
    }

    public String totalcancelled() {
        return String.valueOf(manifest_list.getRemaining_count());
    }

    public String totalunpicked() {
        return String.valueOf(manifest_list.getUnpicked_count());
    }

    public void onCameraClick() {
        iScanSummaryItemAdapterInterface.onCameraClick(manifest_list, position);
    }
}

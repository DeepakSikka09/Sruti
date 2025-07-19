package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;


import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

public class SellerShipmentItemViewModel {

    public Shipment_Detail shipment_detail;


    public SellerShipmentItemViewModel(Shipment_Detail reports) {
        this.shipment_detail = reports;
    }


    public String Airwaybill() {
        return String.valueOf(shipment_detail.getAirwaybill_number());
    }

    public String OrderNo() {
        return shipment_detail.getOrderNo();
    }

    public String MasterNo() {
        return String.valueOf(shipment_detail.getMaster_airwaybill_number());
    }


}

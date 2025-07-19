package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces;


import android.util.Log;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

public class WarehouseShipmentItemViewModel {

    public Shipment_Detail shipment_detail;


    public WarehouseShipmentItemViewModel(Shipment_Detail reports) {
        this.shipment_detail = reports;

    }


    public String Airwaybill() {
        return String.valueOf(shipment_detail.getAirWayBillNumber());

    }


    public String OrderNo() {
        return shipment_detail.getOrderNo();
    }

    public String MasterNo() {
        return String.valueOf(shipment_detail.getMaster_airwaybill_number());
    }

}

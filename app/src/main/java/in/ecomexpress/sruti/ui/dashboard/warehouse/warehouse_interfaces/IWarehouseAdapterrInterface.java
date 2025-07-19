package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;

/**
 * Created by shivangi on 22/8/19.
 */

public interface IWarehouseAdapterrInterface {

    void update(Shipment_Detail shipmentsDetail);
    void delete(Shipment_Detail shipmentsDetail);
}

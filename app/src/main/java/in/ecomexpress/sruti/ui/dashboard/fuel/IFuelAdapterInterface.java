package in.ecomexpress.sruti.ui.dashboard.fuel;


import in.ecomexpress.sruti.model.fuel.response.Reports;

public interface IFuelAdapterInterface {

    void onItemClick(Reports reports);

    String onVehicleType(String vehicle);

}

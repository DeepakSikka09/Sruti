package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Manifest_List;

public interface DepartCallback {
    void checkForDepart(ArrayList<String> vehicleid,String vehicle_owner);
    void startStopDialog(boolean isStartTrip);
    void commitMenifest(List<Manifest_List> vehicle);
    void syncManifestData();
}

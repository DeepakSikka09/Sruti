package in.ecomexpress.sruti.ui.dashboard.fuel;


import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.utils.CommonUtils;

public class FuelReimburseItemViewModel {

    public Reports reports;
    public IFuelAdapterInterface iFuelAdapterInterface;


    public FuelReimburseItemViewModel(Reports reports, IFuelAdapterInterface iFuelAdapterInterface) {
        this.reports = reports;
        this.iFuelAdapterInterface = iFuelAdapterInterface;
    }

    public void onItemClick() {
        iFuelAdapterInterface.onItemClick(reports);
    }

    public String TripName() {
        return reports.getTrip_name();
    }

    public String TripDate() {
        return reports.getTrip_date();
    }

    public String TripTime() {
        String millis = reports.getTrip_time();
        long seconds = Long.parseLong(millis) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time =/* days + ":" + */hours % 24 + " hours " + minutes % 60 + " min " /*+ seconds % 60*/;
        return time;

    }

    public String TripDistance() {
        return reports.getTrip_distance();
    }

    public String Undelivered() {
        return reports.getShipment_details().getUndelivered();
    }

    public String Delivered() {
        return reports.getShipment_details().getSuccess();
    }

    public String Pending() {
        return reports.getShipment_details().getUnattempted();
    }

    public String Claimed() {
        return reports.getReimbursement_status().getClaimed();
    }

    public String Approved() {
        return reports.getReimbursement_status().getApproved();
    }

    public String Status() {
        return CommonUtils.toTitleCase(reports.getReimbursement_status().getStatus());
    }


}

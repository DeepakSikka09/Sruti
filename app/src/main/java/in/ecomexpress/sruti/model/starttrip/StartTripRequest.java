package in.ecomexpress.sruti.model.starttrip;


import java.util.ArrayList;

/**
 * Created by 63091 on 12-06-2019.
 */

public class StartTripRequest {
    private String app_version;
    private String employee_code;
    private String imei_number;
    private int pickup_route_id;
    private String role;
    private double start_lattitude;
    private double start_longitude;
    private String vehicle_owner_type;
    private boolean otc_enabled;
    private boolean erm_enabled;

    public boolean isOtc_enabled() {
        return otc_enabled;
    }

    public void setOtc_enabled(boolean otc_enabled) {
        this.otc_enabled = otc_enabled;
    }

    public boolean isErm_enabled() {
        return erm_enabled;
    }

    public void setErm_enabled(boolean erm_enabled) {
        this.erm_enabled = erm_enabled;
    }
    @Override
    public String toString() {
        return "StartTripRequest{" +
                "app_version='" + app_version + '\'' +
                ", employee_code='" + employee_code + '\'' +
                ", imei_number='" + imei_number + '\'' +
                ", pickup_route_id=" + pickup_route_id +
                ", role='" + role + '\'' +
                ", start_lattitude=" + start_lattitude +
                ", start_longitude=" + start_longitude +
                ", vehicle_owner_type='" + vehicle_owner_type + '\'' +
                ", otc_enabled=" + otc_enabled +
                ", erm_enabled=" + erm_enabled +
                ", start_trip_multi_vehicle_data=" + start_trip_multi_vehicle_data +
                '}';
    }

    public ArrayList<Start_Trip_Multi_Vehicle> getStart_trip_multi_vehicle_data() {
        return start_trip_multi_vehicle_data;
    }

    public void setStart_trip_multi_vehicle_data(ArrayList<Start_Trip_Multi_Vehicle> start_trip_multi_vehicle_data) {
        this.start_trip_multi_vehicle_data = start_trip_multi_vehicle_data;
    }

    private ArrayList<Start_Trip_Multi_Vehicle> start_trip_multi_vehicle_data;

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public double getStart_lattitude() {
        return start_lattitude;
    }

    public void setStart_lattitude(double start_lattitude) {
        this.start_lattitude = start_lattitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public String getVehicle_owner_type() {
        return vehicle_owner_type;
    }

    public void setVehicle_owner_type(String vehicle_owner_type) {
        this.vehicle_owner_type = vehicle_owner_type;
    }

    public int getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(int pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getImei_number() {
        return imei_number;
    }

    public void setImei_number(String imei_number) {
        this.imei_number = imei_number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

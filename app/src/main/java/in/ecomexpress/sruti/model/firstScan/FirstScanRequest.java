package in.ecomexpress.sruti.model.firstScan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstScanRequest {
    private int trip_Id;
    private String emp_Code;
    private String role;
    private String start_Time;
    private long manifest_id;
    private double first_scan_lat;
    private double first_scan_lng;
    private int inscan_within_geofence;
    private double verified_lat;
    private double verified_lng;
    private double distance_from_pickup_location;

    public double getDistance_from_pickup_location() {
        return distance_from_pickup_location;
    }

    public void setDistance_from_pickup_location(double distance_from_pickup_location) {
        this.distance_from_pickup_location = distance_from_pickup_location;
    }

    public int getInscan_within_geofence() {
        return inscan_within_geofence;
    }

    public void setInscan_within_geofence(int inscan_within_geofence) {
        this.inscan_within_geofence = inscan_within_geofence;
    }

    public double getVerified_lat() {
        return verified_lat;
    }

    public void setVerified_lat(double verified_lat) {
        this.verified_lat = verified_lat;
    }

    public double getVerified_lng() {
        return verified_lng;
    }

    public void setVerified_lng(double verified_lng) {
        this.verified_lng = verified_lng;
    }

    public double getFirst_scan_lat() {
        return first_scan_lat;
    }

    public void setFirst_scan_lat(double first_scan_lat) {
        this.first_scan_lat = first_scan_lat;
    }

    public double getFirst_scan_lng() {
        return first_scan_lng;
    }

    public void setFirst_scan_lng(double first_scan_lng) {
        this.first_scan_lng = first_scan_lng;
    }


    public int getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(int trip_Id) {
        this.trip_Id = trip_Id;
    }

    public String getEmp_Code() {
        return emp_Code;
    }

    public void setEmp_Code(String emp_Code) {
        this.emp_Code = emp_Code;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStart_Time() {
        return start_Time;
    }

    public void setStart_Time(String start_Time) {
        this.start_Time = start_Time;
    }

    public long getManifest_id() {
        return manifest_id;
    }

    public void setManifest_id(long manifest_id) {
        this.manifest_id = manifest_id;
    }

    @Override
    public String toString()
    {
        return "FirstScanRequest [trip_Id = "+trip_Id+", emp_Code = "+emp_Code+", role = "+role+", start_Time = "+start_Time+", manifest_id = "+manifest_id+", first_scan_lat="+first_scan_lat+",first_scan_lng="+first_scan_lng+"]";
    }
}

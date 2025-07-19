package in.ecomexpress.sruti.model.Departure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Depature_Request {
    private String emp_code;

    public String getVehicle_owner() {
        return vehicle_owner;
    }

    public void setVehicle_owner(String vehicle_owner) {
        this.vehicle_owner = vehicle_owner;
    }

    private String vehicle_owner;

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public int getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(int pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public ArrayList<DepartVehicles> getDepart_vehicles() {
        return depart_vehicles;
    }

    public void setDepart_vehicles(ArrayList<DepartVehicles> depart_vehicles) {
        this.depart_vehicles = depart_vehicles;
    }

    private int pickup_route_id;
    private ArrayList<DepartVehicles> depart_vehicles;



}

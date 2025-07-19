package in.ecomexpress.sruti.model.handoverdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 18/9/19.
 */
public class HandOverRequest {
    private String employee_code;
    private String vehicle_number;


    public HandOverRequest(String employee_code, String vehicle_number) {
        this.employee_code = employee_code;
        this.vehicle_number = vehicle_number;
    }


    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }
    @Override
    public String toString() {
        return "HandOverRequest{" +
                "employee_id=" + employee_code +
                ", vehicle_number='" + vehicle_number + '\'' +
                '}';
    }

}

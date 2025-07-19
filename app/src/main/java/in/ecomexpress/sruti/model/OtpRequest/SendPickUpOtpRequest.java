package in.ecomexpress.sruti.model.OtpRequest;

import java.util.ArrayList;

public class SendPickUpOtpRequest {
    private long mobile_number;
    private long pickup_route_id;
    private int pickup_location_id;
    private String mobile_number_type;
    private String employee_code;

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public String getMobile_number_type() {
        return mobile_number_type;
    }

    public void setMobile_number_type(String mobile_number_type) {
        this.mobile_number_type = mobile_number_type;
    }

    public long getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(long mobile_number) {
        this.mobile_number = mobile_number;
    }

    public long getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(long pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public int getPickup_location_id() {
        return pickup_location_id;
    }

    public void setPickup_location_id(int pickup_location_id) {
        this.pickup_location_id = pickup_location_id;
    }


}

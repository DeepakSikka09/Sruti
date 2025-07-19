package in.ecomexpress.sruti.model.OtpRequest;

import java.util.ArrayList;

public class VerifyPickUpOtpRequest {
    private String otp;
    private long mobile_number;
    private long pickup_route_id;
    private String employee_code;
    private boolean skip_pickup_otp;
    private String skip_reason_code;

    public String getSkip_reason_code() {
        return skip_reason_code;
    }

    public void setSkip_reason_code(String skip_reason_code) {
        this.skip_reason_code = skip_reason_code;
    }

    public boolean isSkip_pickup_otp() {
        return skip_pickup_otp;
    }

    public void setSkip_pickup_otp(boolean skip_pickup_otp) {
        this.skip_pickup_otp = skip_pickup_otp;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    private ArrayList<Long> verified_manifest_ids;

    public ArrayList<Long> getVerified_manifest_ids() {
        return verified_manifest_ids;
    }

    public void setVerified_manifest_ids(ArrayList<Long> verified_manifest_ids) {
        this.verified_manifest_ids = verified_manifest_ids;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
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
}

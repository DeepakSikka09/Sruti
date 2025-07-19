package in.ecomexpress.sruti.model.OtpRequest;

import java.util.ArrayList;

public class OtpVerificationManifestRequest {
    private ArrayList<Long> manifest_id_list;
    private String otp_verification_source;
    private long pickup_route_id;
    private long registered_mobile_number;

    public ArrayList<Long> getManifest_id_list() {
        return manifest_id_list;
    }

    public void setManifest_id_list(ArrayList<Long> manifest_id_list) {
        this.manifest_id_list = manifest_id_list;
    }

    public String getOtp_verification_source() {
        return otp_verification_source;
    }

    public void setOtp_verification_source(String otp_verification_source) {
        this.otp_verification_source = otp_verification_source;
    }

    public long getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(long pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public long getRegistered_mobile_number() {
        return registered_mobile_number;
    }

    public void setRegistered_mobile_number(long registered_mobile_number) {
        this.registered_mobile_number = registered_mobile_number;
    }
}


package in.ecomexpress.sruti.model.childCommitStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Child_Shipment_details {
    private long awb_number;
    private String vehicle_no;
    private String shipment_status;

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }


     public long getManifest_no() {
         return manifest_no;
     }

     public void setManifest_no(long manifest_no) {
         this.manifest_no = manifest_no;
     }

     private long manifest_no;
    public long getAwb_number() {
        return awb_number;
    }

    public void setAwb_number(long awb_number) {
        this.awb_number = awb_number;
    }

    public String getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    @Override
    public String toString() {
        return "Child_Shipment_details{" +
                "awb_number=" + awb_number +
                ", vehicle_no='" + vehicle_no + '\'' +
                ", shipment_status='" + shipment_status + '\'' +
                '}';
    }

}

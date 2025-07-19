package in.ecomexpress.sruti.model.commitdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Deepak.Sikka on 1/11/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentDetail {
    private Long airway_bill_number;// Primary


    private String status_code;
    private String vehicle_no;
    private Long date_time;

    /**
     * As Discuss with dinesh
     * private Integer reason_id;
     *  private Long manifest_shipment_id;
     */
    private String reason_code;
    @JsonProperty("isAdvance")
    private boolean advance;

    public int getIs_bp_validated() {
        return is_bp_validated;
    }

    public void setIs_bp_validated(int is_bp_validated) {
        this.is_bp_validated = is_bp_validated;
    }

    private int is_bp_validated;

    public boolean isAdvance() {
        return advance;
    }

    public void setAdvance(boolean advance) {
        this.advance = advance;
    }

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public Long getAirway_bill_number() {
        return airway_bill_number;
    }

    public void setAirway_bill_number(Long airWay_bill_number) {
        this.airway_bill_number = airWay_bill_number;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public Long getDate_time() {
        return date_time;
    }

    public void setDate_time(Long date_time) {
        this.date_time = date_time;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }



    /* public Long getManifest_shipment_id() {
            return manifest_shipment_id;
        }

        public void setManifest_shipment_id(Long manifest_shipment_id) {
            this.manifest_shipment_id = manifest_shipment_id;
        }*/
    @Override
    public String toString() {
        return "ShipmentDetail{" +
                "airway_bill_number=" + airway_bill_number +
                ", status_code='" + status_code + '\'' +
                ", vehicle_no='" + vehicle_no + '\'' +
                ", date_time=" + date_time +
                /*", manifest_shipment_id=" + manifest_shipment_id +*/
                ", reason_code='" + reason_code + '\'' +
                ", advance=" + advance +
                '}';
    }


}

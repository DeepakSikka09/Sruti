package in.ecomexpress.sruti.model.UpdatedBP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatedBPResponse implements Serializable {

    private boolean status;
    private int code;
    private String description;
    @SerializedName("response")

    private List<ResponseData> response;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public List<ResponseData> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseData> response) {
        this.response = response;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData implements Serializable {
    /*    @SerializedName("updated_bp_details")
        public List<UpdatedBpDetails> updatedBpDetailsList;

        public List<UpdatedBpDetails> getUpdatedBpDetailsList() {
            return updatedBpDetailsList;
        }*/

        @SerializedName("manifest_id")

        public Integer manifest_id;
        @SerializedName("bp_shipment_details")

        public List<bp_shipment_details> bp_shipment_details;




        public Integer getManifest_id() {
            return manifest_id;
        }

        public void setManifest_id(Integer manifest_id) {
            this.manifest_id = manifest_id;
        }

        public List<bp_shipment_details> getBpShipmentDetails() {
            return bp_shipment_details;
        }

        public void setBpShipmentDetails(List<bp_shipment_details> bpShipmentDetails) {
            this.bp_shipment_details = bpShipmentDetails;
        }
    }

    /*    public static class BpShipmentDetail implements Serializable{
            @SerializedName("manifest_id")
            public int manifest_id;

            @SerializedName("bp_shipment_details")
            public List<BpItem> bp_shipment_details;

            public int getManifestId() {
                return manifest_id;
            }


            public List<BpItem> getBp_shipment_details() {
                return bp_shipment_details;
            }

            public void setBp_shipment_details(List<BpItem> bp_shipment_details) {
                this.bp_shipment_details = bp_shipment_details;
            }
        }*/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class bp_shipment_details implements Serializable {
        @SerializedName("airwaybill_number")
        public long airwaybill_number;

        @SerializedName("brand_package_id")
        public String brand_package_id;

        public long getAirwaybill_number() {
            return airwaybill_number;
        }

        public void setAirwaybill_number(long airwaybill_number) {
            this.airwaybill_number = airwaybill_number;
        }

        public String getBrand_package_id() {
            return brand_package_id;
        }

        public void setBrand_package_id(String brand_package_id) {
            this.brand_package_id = brand_package_id;
        }
    }
}

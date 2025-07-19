package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;


/**
 * Created by Deepak.Sikka on 1/11/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtoResponse {
    @JsonProperty("manifest_status_details")
    private List<Manifest_status_details> manifest_status_details;
    @JsonProperty("last_sync_time")
    private long last_sync_time;
    private String description;


    public long getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(long last_sync_time) {
        this.last_sync_time = last_sync_time;
    }


    public List<Manifest_status_details> getManifest_status_details() {
        return manifest_status_details;
    }

    public void setManifest_status_details(List<Manifest_status_details> manifest_status_details) {
        this.manifest_status_details = manifest_status_details;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RtoResponse{" +
                "manifest_status_details=" + manifest_status_details +
                ", last_sync_time=" + last_sync_time +
                ", description='" + description + '\'' +
                '}';
    }

}
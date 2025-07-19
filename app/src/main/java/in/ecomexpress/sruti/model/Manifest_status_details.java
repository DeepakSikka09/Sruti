package in.ecomexpress.sruti.model;


/**
 * Created by Deepak.sikka on 1/11/19.
 * deepak.sikka@ecomexpress.in
 * +91-9910154059
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manifest_status_details {
    @JsonProperty("listOfAwbs")
    private List<ListOfAwbs> listOfAwbs;
    @JsonProperty("shipment_details")
    private List<Shipment_Detail> shipment_details;
    private long manifest_id;

    public long getManifest_id() {
        return manifest_id;
    }

    public void setManifest_id(long manifest_id) {
        this.manifest_id = manifest_id;
    }


    public List<ListOfAwbs> getListOfAwbs() {
        return listOfAwbs;
    }

    public void setListOfAwbs(List<ListOfAwbs> listOfAwbs) {
        this.listOfAwbs = listOfAwbs;
    }

    public List<Shipment_Detail> getShipment_details() {
        return shipment_details;
    }

    public void setShipment_details(List<Shipment_Detail> shipment_details) {
        this.shipment_details = shipment_details;
    }


    @Override
    public String toString() {
        return "Manifest_status_details{" +
                "listOfAwbs=" + listOfAwbs +
                ", shipment_details=" + shipment_details +
                ", manifest_id=" + manifest_id +
                '}';
    }


}



package in.ecomexpress.sruti.model.commitdata;


import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;
import in.ecomexpress.sruti.model.starttrip.Image_Response;

/**
 * Created by deepak on 10/10/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manifest_process {
    @TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<ShipmentDetail> shipments;

    @TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<Recci> recci;

    @TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<Image_Response> image_response;

    public long getManifest_no() {
        return manifest_no;
    }

    public void setManifest_no(long manifest_no) {
        this.manifest_no = manifest_no;
    }

    @PrimaryKey
    private long manifest_no;
    private String manifest_type;
    private String location_longitude;
    private String location_latitude;
    private String commit_location_radius;
    private String commit_time;
    private String parentmanifestNo;
    private String status_code;

    private long pickup_location_id;

    public long getPickup_location_id() {
        return pickup_location_id;
    }

    public void setPickup_location_id(long pickup_location_id) {
        this.pickup_location_id = pickup_location_id;
    }

    public String getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(String location_longitude) {
        this.location_longitude = location_longitude;
    }

    public String getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(String location_latitude) {
        this.location_latitude = location_latitude;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }


    public ArrayList<Image_Response> getImage_response() {
        return image_response;
    }

    public void setImage_response(ArrayList<Image_Response> image_respose) {
        this.image_response = image_respose;
    }

    public String getManifest_type() {
        return manifest_type;
    }

    public void setManifest_type(String manifest_type) {
        this.manifest_type = manifest_type;
    }

    public ArrayList<Recci> getRecci() {
        return recci;
    }

    public void setRecci(ArrayList<Recci> recci) {
        this.recci = recci;
    }


    public String getCommit_location_radius() {
        return commit_location_radius;
    }

    public void setCommit_location_radius(String commit_location_radius) {
        this.commit_location_radius = commit_location_radius;
    }

    public String getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(String commit_time) {
        this.commit_time = commit_time;
    }

    public String getParentmanifestNo() {
        return parentmanifestNo;
    }

    public void setParentmanifestNo(String parentmanifestNo) {
        this.parentmanifestNo = parentmanifestNo;
    }

    public ArrayList<ShipmentDetail> getShipments() {
        return shipments;
    }

    public void setShipments(ArrayList<ShipmentDetail> shipments) {
        this.shipments = shipments;
    }


    @Override
    public String toString() {
        return "ClassPojo [image_respose = " + image_response + ", manifest_type = " + manifest_type + ", recci = " + recci + ", location_longitude = " + location_longitude + ", manifestNo = " + manifest_no + ", location_latitude = " + location_latitude + ", commit_location_radius = " + commit_location_radius + ", commit_time = " + commit_time + ", parentmanifestNo = " + parentmanifestNo + ", shipments = " + shipments + ", status_code = " + status_code + "]";
    }
}


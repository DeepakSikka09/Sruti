package in.ecomexpress.sruti.model.menifestdata;


import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by 63091 on 01-07-2019.
 */

@Entity(tableName = "manifest_shipment_detail", foreignKeys = @ForeignKey(entity = Manifest_List.class, parentColumns = "composite_Key", childColumns = "composite_Key_child", onDelete = CASCADE), indices = {@Index("composite_Key_child")})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipment_Detail implements Comparable<Shipment_Detail>  {
    public long manifestNoInchild;
    public String location_type;
    @PrimaryKey
    public long airwaybill_number;
    private long master_airwaybill_number;
    private String orderNo;
    private String status;
    private long dateTime;
    private String composite_Key_child;
    private String vehicle;
    /**
     * As discuss with dinesh
     * private long manifest_shipment_id;
     */
    private boolean isChild;
    private String reason_code;
    private int reason_id;
    private boolean checked = false;
    @JsonProperty("isAdvance")
    private boolean advance;
    private boolean is_mps;
    private String statusGroup="";
    public String brand_package_id="";
    public int is_bp_validated=0;
    public boolean bp_Reason_Code_Applied=false;
    public  boolean temp_key=false;

    public boolean isTemp_key() {
        return temp_key;
    }

    public void setTemp_key(boolean temp_key) {
        this.temp_key = temp_key;
    }

    public boolean isBp_Reason_Code_Applied() {
        return bp_Reason_Code_Applied;
    }

    public void setBp_Reason_Code_Applied(boolean bp_Reason_Code_Applied) {
        this.bp_Reason_Code_Applied = bp_Reason_Code_Applied;
    }


    public int getIs_bp_validated() {
        return is_bp_validated;
    }

    public void setIs_bp_validated(int is_bp_validated) {
        this.is_bp_validated = is_bp_validated;
    }

    public String getBrand_package_id() {
        return brand_package_id;
    }

    public void setBrand_package_id(String brand_package_id) {
        this.brand_package_id = brand_package_id;
    }

    public String getStatusGroup() {
        return statusGroup;
    }

    public void setStatusGroup(String statusGroup) {
        statusGroup = statusGroup;
    }

    public boolean isAdvance() {
        return advance;
    }

    public void setAdvance(boolean advance) {
        this.advance = advance;
    }


    public boolean isIs_mps() {
        return is_mps;
    }

    public void setIs_mps(boolean is_mps) {
        this.is_mps = is_mps;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        this.isChild = child;
    }

    public String getComposite_Key_child() {
        return composite_Key_child;
    }

    public void setComposite_Key_child(String composite_Key_child) {
        this.composite_Key_child = composite_Key_child;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    @NonNull
    public long getAirwaybill_number() {
        return airwaybill_number;
    }

    public void setAirwaybill_number(@NonNull long airwaybill_number) {
        this.airwaybill_number = airwaybill_number;
    }

    public long getAirWayBillNumber() {
        return airwaybill_number;
    }

    public void setAirWayBillNumber(long airWayBillNumber) {
        this.airwaybill_number = airWayBillNumber;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }


    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public int getReason_id() {
        return reason_id;
    }

    public void setReason_id(int reason_id) {
        this.reason_id = reason_id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getManifestNoInchild() {
        return manifestNoInchild;
    }

    public void setManifestNoInchild(long manifestNoInchild) {
        this.manifestNoInchild = manifestNoInchild;
    }

    public long getMaster_airwaybill_number() {
        return master_airwaybill_number;
    }

    public void setMaster_airwaybill_number(long master_airwaybill_number) {
        this.master_airwaybill_number = master_airwaybill_number;
    }


    @Override
    public int compareTo(@NonNull Shipment_Detail shipment_detail) {
        return this.airwaybill_number < shipment_detail.getAirWayBillNumber() ? 1 : 0;
    }


    public String getLocation_type() {
        return location_type;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }
}

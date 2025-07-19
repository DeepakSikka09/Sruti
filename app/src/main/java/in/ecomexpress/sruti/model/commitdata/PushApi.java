package in.ecomexpress.sruti.model.commitdata;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Deepak.sikka on 26/10/2019.
 * deepak.sikka@ecomexpress.in
 * +91-9910154059
 */

@Entity(tableName = "commit_data")
public class PushApi {
    private long manifestNo;
    @JsonIgnore
    @PrimaryKey
    @NonNull
    public String CompositeKey;
    private String authtoken;
    private int shipmentStatus;
    private String appId;
    private String apiVer;
    private String empId;
    private String requestData;
    private String fileUrl;

    @JsonIgnore
    private int firstInscanStatus;

    public int getFirstInscanStatus() {
        return firstInscanStatus;
    }

    public void setFirstInscanStatus(int firstInscanStatus) {
        this.firstInscanStatus = firstInscanStatus;
    }

    public long getManifestNo() {
        return manifestNo;
    }

    public void setManifestNo(long awbNo) {
        this.manifestNo = awbNo;
    }

    public int getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(int shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getCompositeKey() {
        return CompositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.CompositeKey = compositeKey;//CompositeKey= manifestNo+"_"+compositeKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiVer() {
        return apiVer;
    }

    public void setApiVer(String apiVer) {
        this.apiVer = apiVer;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }


    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }


    @Override
    public String toString() {
        return "PushApi{" +
                "manifestNo=" + manifestNo +
                ", CompositeKey=" + CompositeKey +
                ", shipmentStatus=" + shipmentStatus +
                ", appId='" + appId + '\'' +
                ", apiVer='" + apiVer + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", empId='" + empId + '\'' +
                ", authtoken='" + authtoken + '\'' +
                ", requestData='" + requestData + '\'' +
                '}';
    }
}
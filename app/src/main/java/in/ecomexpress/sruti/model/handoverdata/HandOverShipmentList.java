package in.ecomexpress.sruti.model.handoverdata;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "handOver_shipment_list")
public class HandOverShipmentList {
    @PrimaryKey
    private long airWayBillNumber;
    private String status;
    private long manifestId;
    private String order_number;


    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    

    public HandOverShipmentList() {
    }

    public HandOverShipmentList(long awbNumber) {
        this.airWayBillNumber = awbNumber;
    }

    public long getAirWayBillNumber() {
        return airWayBillNumber;
    }

    public void setAirWayBillNumber(long airWayBillNumber) {
        this.airWayBillNumber = airWayBillNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getManifestId() {
        return manifestId;
    }

    public void setManifestId(long manifestId) {
        this.manifestId = manifestId;
    }
}
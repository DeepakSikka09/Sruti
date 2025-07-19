package in.ecomexpress.sruti.model.inScanData;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FirstInscan")
public class FirstInscan {
    @PrimaryKey
    private long manifetsId;

    private int status;

    private String requestData;

    public long getManifetsId() {
        return manifetsId;
    }

    public void setManifetsId(long manifetsId) {
        this.manifetsId = manifetsId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }


}

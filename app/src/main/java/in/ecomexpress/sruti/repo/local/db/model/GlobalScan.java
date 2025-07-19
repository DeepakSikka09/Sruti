package in.ecomexpress.sruti.repo.local.db.model;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "global_scan_awb")
public class GlobalScan{
    @PrimaryKey
    @NonNull
    @JsonProperty("awb")
    public long awb;

    @JsonProperty("status")
    public int status;

    @JsonProperty("date_time")
    public long date_time;

    public GlobalScan(Parcel in) {
        awb = in.readLong();
        status = in.readInt();
        date_time = in.readLong();
    }


    public long getAwb() {
        return awb;
    }

    public void setAwb(long awb) {
        this.awb = awb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }
    public GlobalScan() {

    }





}

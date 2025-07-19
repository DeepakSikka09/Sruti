package in.ecomexpress.sruti.repo.local.db.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Remark {
    @PrimaryKey
    public long manifest_no;
    @ColumnInfo
    public String empCode;
    @ColumnInfo
    public long date;
    @ColumnInfo
    public int sync_status;
    @ColumnInfo
    public String remark;


    @Override
    public String toString() {
        return "manifest_no: " + manifest_no + "empCode: " + empCode + "date: " + date + "sync_status: " + "remark: " + remark;
    }


}

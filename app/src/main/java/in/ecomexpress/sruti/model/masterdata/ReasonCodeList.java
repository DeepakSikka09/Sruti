package in.ecomexpress.sruti.model.masterdata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Deepak.sikka on 02/04/2020.
 * deepak.sikka@ecomexpress.in
 * +91-9910154059
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "reason_code_list")

public class ReasonCodeList implements Parcelable {
    private String reason_code;
    private String reason_msg;
    private String sub_group;
    @PrimaryKey
    private int reason_id;
    @Embedded
    private MasterDataAttributeResponse masterDataAttributeResponse = new MasterDataAttributeResponse();

    protected ReasonCodeList(Parcel in) {
        reason_code = in.readString();
        reason_msg = in.readString();
        sub_group = in.readString();
        reason_id = in.readInt();
        masterDataAttributeResponse = in.readParcelable(MasterDataAttributeResponse.class.getClassLoader());
    }

    public static final Creator<ReasonCodeList> CREATOR = new Creator<ReasonCodeList>() {
        @Override
        public ReasonCodeList createFromParcel(Parcel in) {
            return new ReasonCodeList(in);
        }

        @Override
        public ReasonCodeList[] newArray(int size) {
            return new ReasonCodeList[size];
        }
    };

    public int getReason_id() {
        return reason_id;
    }

    public void setReason_id(int reason_id) {
        this.reason_id = reason_id;
    }

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public String getReason_msg() {
        return reason_msg;
    }

    public void setReason_msg(String reason_msg) {
        this.reason_msg = reason_msg;
    }

    public String getSub_group() {
        return sub_group;
    }

    public void setSub_group(String sub_group) {
        this.sub_group = sub_group;
    }

    @JsonProperty("attribute")
    public MasterDataAttributeResponse getMasterDataAttributeResponse() {
        return masterDataAttributeResponse;
    }

    public void setMasterDataAttributeResponse(MasterDataAttributeResponse masterDataAttributeResponse) {
        this.masterDataAttributeResponse = masterDataAttributeResponse;
    }


    public ReasonCodeList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reason_code);
        parcel.writeString(reason_msg);
        parcel.writeString(sub_group);
        parcel.writeInt(reason_id);
        parcel.writeParcelable(masterDataAttributeResponse, i);
    }


}

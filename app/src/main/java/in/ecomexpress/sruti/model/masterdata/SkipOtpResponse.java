package in.ecomexpress.sruti.model.masterdata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkipOtpResponse {
    private String description;
    public ArrayList<ReasonList> reason_list;
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ReasonList> getReasonList() {
        return reason_list;
    }

    public void setReasonList(ArrayList<ReasonList> reasonList) {
        this.reason_list = reasonList;
    }
//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("SkipOtpResponse{");
//        sb.append("status=").append(status);
//        sb.append(", description='").append(description).append('\'');
//        sb.append(", reason_list=").append(reason_list);
//        sb.append('}');
//        return sb.toString();
//    }


 /* protected ReasonList(Parcel in) {
        reason_code = in.readString();
        reason = in.readString();
        }*/
   /* public static final Creator<ReasonList> CREATOR = new Creator<ReasonList>() {
        @Override
        public ReasonList createFromParcel(Parcel in) {
            return new ReasonList(in);
        }

        @Override
        public ReasonList[] newArray(int size) {
            return new ReasonList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(reason_code);
        parcel.writeString(reason);
    }*/
}

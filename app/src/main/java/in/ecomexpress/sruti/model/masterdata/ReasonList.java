package in.ecomexpress.sruti.model.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReasonList  {

    private String reason_code;
    private String reason;

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("ReasonList{");
//        sb.append("reason_code='").append(reason_code).append('\'');
//        sb.append(", reason='").append(reason).append('\'');
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

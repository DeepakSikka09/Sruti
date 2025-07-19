package in.ecomexpress.sruti.model.menifestdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by 63091 on 01-07-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Menifest_Data_Master implements Parcelable {
    private boolean status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int code;
    private String description;
    private Menifest_Response response;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Menifest_Response getResponse() {
        return response;
    }

    public void setResponse(Menifest_Response response) {
        this.response = response;
    }


    public Menifest_Data_Master() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeInt(this.code);
        dest.writeString(this.description);
        dest.writeParcelable(this.response, flags);
    }

    protected Menifest_Data_Master(Parcel in) {
        this.status = in.readByte() != 0;
        this.code = in.readInt();
        this.description = in.readString();
        this.response = in.readParcelable(Menifest_Response.class.getClassLoader());
    }

    public static final Creator<Menifest_Data_Master> CREATOR = new Creator<Menifest_Data_Master>() {
        @Override
        public Menifest_Data_Master createFromParcel(Parcel source) {
            return new Menifest_Data_Master(source);
        }

        @Override
        public Menifest_Data_Master[] newArray(int size) {
            return new Menifest_Data_Master[size];
        }
    };
}

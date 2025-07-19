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
public class Flags implements Parcelable {
    private boolean otp_required;
    private boolean oda_allowed;
    private boolean is_location_verified;

    public boolean isOtp_required() {
        return otp_required;
    }

    public void setOtp_required(boolean otp_required) {
        this.otp_required = otp_required;
    }

    public boolean isOda_allowed() {
        return oda_allowed;
    }

    public void setOda_allowed(boolean oda_allowed) {
        this.oda_allowed = oda_allowed;
    }

    public boolean isIs_location_verified() {
        return is_location_verified;
    }

    public void setIs_location_verified(boolean is_location_verified) {
        this.is_location_verified = is_location_verified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.otp_required ? (byte) 1 : (byte) 0);
        dest.writeByte(this.oda_allowed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_location_verified ? (byte) 1 : (byte) 0);
    }

    public Flags() {
    }

    protected Flags(Parcel in) {
        this.otp_required = in.readByte() != 0;
        this.oda_allowed = in.readByte() != 0;
        this.is_location_verified = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Flags> CREATOR = new Parcelable.Creator<Flags>() {
        @Override
        public Flags createFromParcel(Parcel source) {
            return new Flags(source);
        }

        @Override
        public Flags[] newArray(int size) {
            return new Flags[size];
        }
    };
}

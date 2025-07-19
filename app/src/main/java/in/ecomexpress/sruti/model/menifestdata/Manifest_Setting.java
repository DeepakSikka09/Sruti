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
public class Manifest_Setting implements Parcelable {
    private int advance_pickup_check;
    private int geo_code_check;

    public String getIs_partial_allow() {
        return is_partial_allow;
    }

    public void setIs_partial_allow(String is_partial_allow) {
        this.is_partial_allow = is_partial_allow;
    }

    public int getAdvance_pickup_check() {
        return advance_pickup_check;
    }

    public void setAdvance_pickup_check(int advance_pickup_check) {
        this.advance_pickup_check = advance_pickup_check;
    }

    public int getGeo_code_check() {
        return geo_code_check;
    }

    public void setGeo_code_check(int geo_code_check) {
        this.geo_code_check = geo_code_check;
    }

    public double getGeo_code_latitude() {
        return geo_code_latitude;
    }

    public void setGeo_code_latitude(double geo_code_latitude) {
        this.geo_code_latitude = geo_code_latitude;
    }

    public double getGeo_code_longitude() {
        return geo_code_longitude;
    }

    public void setGeo_code_longitude(double geo_code_longitude) {
        this.geo_code_longitude = geo_code_longitude;
    }

    public int getQr_code_check() {
        return qr_code_check;
    }

    public void setQr_code_check(int qr_code_check) {
        this.qr_code_check = qr_code_check;
    }

    public String getReal_time_soft_data_check() {
        return real_time_soft_data_check;
    }

    public void setReal_time_soft_data_check(String real_time_soft_data_check) {
        this.real_time_soft_data_check = real_time_soft_data_check;
    }

    public String getGeo_code_radius() {
        return geo_code_radius;
    }

    public void setGeo_code_radius(String geo_code_radius) {
        this.geo_code_radius = geo_code_radius;
    }

    public String getIsPartialAllow() {
        return is_partial_allow;
    }

    public void setIsPartialAllow(String isPartialAllow) {
        is_partial_allow = isPartialAllow;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    private double geo_code_latitude;
    private double geo_code_longitude;
    private int qr_code_check;
    private String real_time_soft_data_check;
    private String geo_code_radius;
    private String is_partial_allow;
    private String qr_code;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.advance_pickup_check);
        dest.writeInt(this.geo_code_check);
        dest.writeDouble(this.geo_code_latitude);
        dest.writeDouble(this.geo_code_longitude);
        dest.writeInt(this.qr_code_check);
        dest.writeString(this.real_time_soft_data_check);
        dest.writeString(this.geo_code_radius);
        dest.writeString(this.is_partial_allow);
        dest.writeString(this.qr_code);
    }

    public Manifest_Setting() {
    }

    protected Manifest_Setting(Parcel in) {
        this.advance_pickup_check = in.readInt();
        this.geo_code_check = in.readInt();
        this.geo_code_latitude = in.readDouble();
        this.geo_code_longitude = in.readDouble();
        this.qr_code_check = in.readInt();
        this.real_time_soft_data_check = in.readString();
        this.geo_code_radius = in.readString();
        this.is_partial_allow = in.readString();
        this.qr_code = in.readString();
    }

    public static final Parcelable.Creator<Manifest_Setting> CREATOR = new Parcelable.Creator<Manifest_Setting>() {
        @Override
        public Manifest_Setting createFromParcel(Parcel source) {
            return new Manifest_Setting(source);
        }

        @Override
        public Manifest_Setting[] newArray(int size) {
            return new Manifest_Setting[size];
        }
    };
}

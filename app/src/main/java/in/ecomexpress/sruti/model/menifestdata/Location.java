package in.ecomexpress.sruti.model.menifestdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by 63091 on 02-07-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location implements Parcelable {
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;
    private String validate_pickup_slot;
    private String pickup_slot_time_in;
    private String pickup_slot_time_out;
    private boolean proof_of_pickup_enable;
    private long secondary_contact_no;
    private boolean is_valid_secondary_contact_no;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(validate_pickup_slot);
        dest.writeString(pickup_slot_time_in);
        dest.writeString(pickup_slot_time_out);
        dest.writeByte(this.proof_of_pickup_enable ? (byte) 1 : (byte) 0);
        dest.writeLong(secondary_contact_no);
        dest.writeByte(this.is_valid_secondary_contact_no ? (byte) 1 : (byte) 0);

    }

    public boolean isProof_of_pickup_enable() {
        return proof_of_pickup_enable;
    }

    public void setProof_of_pickup_enable(boolean proof_of_pickup_enable) {
        this.proof_of_pickup_enable = proof_of_pickup_enable;
    }

    public String getValidate_pickup_slot() {
        return validate_pickup_slot;
    }

    public void setValidate_pickup_slot(String validate_pickup_slot) {
        this.validate_pickup_slot = validate_pickup_slot;
    }

    public String getPickup_slot_time_in() {
        return pickup_slot_time_in;
    }

    public void setPickup_slot_time_in(String pickup_slot_time_in) {
        this.pickup_slot_time_in = pickup_slot_time_in;
    }

    public String getPickup_slot_time_out() {
        return pickup_slot_time_out;
    }

    public void setPickup_slot_time_out(String pickup_slot_time_out) {
        this.pickup_slot_time_out = pickup_slot_time_out;
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        validate_pickup_slot = in.readString();
        pickup_slot_time_in = in.readString();
        pickup_slot_time_out = in.readString();
        proof_of_pickup_enable = in.readByte() != 0;
        secondary_contact_no = in.readLong();
        is_valid_secondary_contact_no = in.readByte() != 0;
    }


    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public long getSecondary_contact_no() {
        return secondary_contact_no;
    }

    public void setSecondary_contact_no(long secondary_contact_no) {
        this.secondary_contact_no = secondary_contact_no;
    }

    public boolean isIs_valid_secondary_contact_no() {
        return is_valid_secondary_contact_no;
    }

    public void setIs_valid_secondary_contact_no(boolean is_valid_secondary_contact_no) {
        this.is_valid_secondary_contact_no = is_valid_secondary_contact_no;
    }
}

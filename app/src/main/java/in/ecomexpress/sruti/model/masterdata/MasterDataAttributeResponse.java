package in.ecomexpress.sruti.model.masterdata;


import android.os.Parcel;
import android.os.Parcelable;

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
public class MasterDataAttributeResponse implements Parcelable {
    @JsonProperty("SHIPMENT")
    public boolean Shipment = false;
    @JsonProperty("MANIFEST")
    public boolean Manifest = false;
    @JsonProperty("QR")
    public boolean Qr = false;
    @JsonProperty("OTP")
    public boolean Otp = false;

    @JsonProperty("BRANDED_PACKAGE_ID_MISMATCH_INCORRECT")
    public boolean Branded_packaging = false;

    public boolean isBranded_packaging() {
        return Branded_packaging;
    }

    public void setBranded_packaging(boolean branded_packaging) {
        Branded_packaging = branded_packaging;
    }

    public boolean isShipment() {
        return Shipment;
    }

    public void setShipment(boolean shipment) {
        Shipment = shipment;
    }

    public boolean isManifest() {
        return Manifest;
    }

    public void setManifest(boolean manifest) {
        Manifest = manifest;
    }

    public boolean isQr() {
        return Qr;
    }

    public void setQr(boolean qr) {
        Qr = qr;
    }

    public boolean isOtp() {
        return Otp;
    }

    public void setOtp(boolean otp) {
        Otp = otp;
    }

    public MasterDataAttributeResponse() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.Shipment ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Manifest ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Qr ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Otp ? (byte) 1 : (byte) 0);
    }

    protected MasterDataAttributeResponse(Parcel in) {
        this.Shipment = in.readByte() != 0;
        this.Manifest = in.readByte() != 0;
        this.Qr = in.readByte() != 0;
        this.Otp = in.readByte() != 0;
    }

    public static final Creator<MasterDataAttributeResponse> CREATOR = new Creator<MasterDataAttributeResponse>() {
        @Override
        public MasterDataAttributeResponse createFromParcel(Parcel source) {
            return new MasterDataAttributeResponse(source);
        }

        @Override
        public MasterDataAttributeResponse[] newArray(int size) {
            return new MasterDataAttributeResponse[size];
        }
    };
}


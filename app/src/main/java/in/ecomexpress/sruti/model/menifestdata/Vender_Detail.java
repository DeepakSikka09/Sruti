package in.ecomexpress.sruti.model.menifestdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by 63091 on 01-07-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vender_Detail implements Parcelable {
    @Embedded
    private Address address;
    private String locationCode;
    private String ecomVendordayoff;
    private String location_name;
    private String productType;
    private String pickup_slot;
    private String sub_product_Type;
    private String product_type;
    private boolean is_valid_contact_no;
    @Embedded
    private Location location;
    private long location_contact_no;


    private boolean verify_geocode;
    private boolean signature_pad_visible;

    private String product_sub_type_code;


    protected Vender_Detail(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        locationCode = in.readString();
        ecomVendordayoff = in.readString();
        location_name = in.readString();
        productType = in.readString();
        pickup_slot = in.readString();
        sub_product_Type = in.readString();
        product_sub_type_code = in.readString();
        product_type = in.readString();
        is_valid_contact_no = in.readByte() != 0;
        location = in.readParcelable(Location.class.getClassLoader());
        location_contact_no = in.readLong();
        verify_geocode = in.readByte() != 0;
        signature_pad_visible = in.readByte() != 0;
        concernedPersonName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
        dest.writeString(locationCode);
        dest.writeString(ecomVendordayoff);
        dest.writeString(location_name);
        dest.writeString(productType);
        dest.writeString(pickup_slot);
        dest.writeString(sub_product_Type);
        dest.writeString(product_sub_type_code);
        dest.writeString(product_type);
        dest.writeByte((byte) (is_valid_contact_no ? 1 : 0));
        dest.writeParcelable(location, flags);
        dest.writeLong(location_contact_no);
        dest.writeByte((byte) (verify_geocode ? 1 : 0));
        dest.writeByte((byte) (signature_pad_visible ? 1 : 0));

        dest.writeString(concernedPersonName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vender_Detail> CREATOR = new Creator<Vender_Detail>() {
        @Override
        public Vender_Detail createFromParcel(Parcel in) {
            return new Vender_Detail(in);
        }

        @Override
        public Vender_Detail[] newArray(int size) {
            return new Vender_Detail[size];
        }
    };

    public boolean isVerify_geocode() {
        return verify_geocode;
    }

    public void setVerify_geocode(boolean verify_geocode) {
        this.verify_geocode = verify_geocode;
    }

    public boolean isSignature_pad_visible() {
        return signature_pad_visible;
    }

    public void setSignature_pad_visible(boolean signature_pad_visible) {
        this.signature_pad_visible = signature_pad_visible;
    }



    public boolean isIs_valid_contact_no() {
        return is_valid_contact_no;
    }

    public void setIs_valid_contact_no(boolean is_valid_contact_no) {
        this.is_valid_contact_no = is_valid_contact_no;
    }


    public String getConcerned_person_name() {
        return concernedPersonName;
    }

    public void setConcerned_person_name(String concerned_person_name) {
        this.concernedPersonName = concerned_person_name;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public long getLocation_contact_no() {
        return location_contact_no;
    }

    public void setLocation_contact_no(long location_contact_no) {
        this.location_contact_no = location_contact_no;
    }

    private String concernedPersonName;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getConcernedPersonName() {
        return concernedPersonName;
    }

    public void setConcernedPersonName(String concernedPersonName) {
        this.concernedPersonName = concernedPersonName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getEcomVendordayoff() {
        return ecomVendordayoff;
    }

    public void setEcomVendordayoff(String ecomVendordayoff) {
        this.ecomVendordayoff = ecomVendordayoff;
    }

    public String getLocationName() {
        return location_name;
    }

    public void setLocationName(String locationName) {
        this.location_name = locationName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }


    public String getPickup_slot() {
        return pickup_slot;
    }

    public void setPickup_slot(String pickup_slot) {
        this.pickup_slot = pickup_slot;
    }

    public String getSub_product_Type() {
        return sub_product_Type;
    }

    public void setSub_product_Type(String sub_product_Type) {
        this.sub_product_Type = sub_product_Type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getLocationContactNo() {
        return location_contact_no;
    }

    public void setLocationContactNo(long locationContactNo) {
        this.location_contact_no = locationContactNo;
    }

    public String getProduct_sub_type_code() {
        return product_sub_type_code;
    }

    public void setProduct_sub_type_code(String product_sub_type_code) {
        this.product_sub_type_code = product_sub_type_code;
    }


    public Vender_Detail() {
    }



}

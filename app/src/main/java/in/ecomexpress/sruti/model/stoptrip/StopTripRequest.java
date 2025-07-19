package in.ecomexpress.sruti.model.stoptrip;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.starttrip.Start_Trip_Multi_Vehicle;

public class StopTripRequest implements Parcelable {
    private String employee_code;
    private int pickup_route_id;
    private double stop_lattitude;
    private double stop_longitude;
    private String vehicle_owner_type;
    private int vehicle_trip_id;
    private String role;
    private String live_tracking_id;
    private float mobile_km_otc;
    private String imei;
    private boolean otc_enabled;
    private boolean erm_enabled;

    @Override
    public String toString() {
        return "StopTripRequest{" +
                "employee_code='" + employee_code + '\'' +
                ", pickup_route_id=" + pickup_route_id +
                ", stop_lattitude=" + stop_lattitude +
                ", stop_longitude=" + stop_longitude +
                ", vehicle_owner_type='" + vehicle_owner_type + '\'' +
                ", vehicle_trip_id=" + vehicle_trip_id +
                ", role='" + role + '\'' +
                ", live_tracking_id='" + live_tracking_id + '\'' +
                ", mobile_km_otc='" + mobile_km_otc + '\'' +
                ", imei='" + imei + '\'' +
                ", otc_enabled=" + otc_enabled +
                ", erm_enabled=" + erm_enabled +
                ", stop_trip_multi_vehicle_data=" + stop_trip_multi_vehicle_data +
                '}';
    }

    public boolean isOtc_enabled() {
        return otc_enabled;
    }

    public void setOtc_enabled(boolean otc_enabled) {
        this.otc_enabled = otc_enabled;
    }

    public boolean isErm_enabled() {
        return erm_enabled;
    }

    public void setErm_enabled(boolean erm_enabled) {
        this.erm_enabled = erm_enabled;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLive_tracking_id() {
        return live_tracking_id;
    }

    public void setLive_tracking_id(String live_tracking_id) {
        this.live_tracking_id = live_tracking_id;
    }

    public float getMobile_km_otc() {
        return mobile_km_otc;
    }

    public void setMobile_km_otc(float mobile_km_otc) {
        this.mobile_km_otc = mobile_km_otc;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    private ArrayList<Start_Trip_Multi_Vehicle> stop_trip_multi_vehicle_data;

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public int getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(int pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public double getStop_lattitude() {
        return stop_lattitude;
    }

    public void setStop_lattitude(double stop_lattitude) {
        this.stop_lattitude = stop_lattitude;
    }

    public double getStop_longitude() {
        return stop_longitude;
    }

    public void setStop_longitude(double stop_longitude) {
        this.stop_longitude = stop_longitude;
    }

    public String getVehicle_owner_type() {
        return vehicle_owner_type;
    }

    public void setVehicle_owner_type(String vehicle_owner_type) {
        this.vehicle_owner_type = vehicle_owner_type;
    }

    public int getVehicle_trip_id() {
        return vehicle_trip_id;
    }

    public void setVehicle_trip_id(int vehicle_trip_id) {
        this.vehicle_trip_id = vehicle_trip_id;
    }

    public ArrayList<Start_Trip_Multi_Vehicle> getStop_trip_multi_vehicle_data() {
        return stop_trip_multi_vehicle_data;
    }

    public void setStop_trip_multi_vehicle_data(ArrayList<Start_Trip_Multi_Vehicle> stop_trip_multi_vehicle_data) {
        this.stop_trip_multi_vehicle_data = stop_trip_multi_vehicle_data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.employee_code);
        dest.writeString(this.live_tracking_id);
        dest.writeFloat(this.mobile_km_otc);
        dest.writeString(this.imei);
        dest.writeInt(this.pickup_route_id);
        dest.writeDouble(this.stop_lattitude);
        dest.writeDouble(this.stop_longitude);
        dest.writeString(this.vehicle_owner_type);
        dest.writeInt(this.vehicle_trip_id);
        dest.writeList(this.stop_trip_multi_vehicle_data);
    }

    public StopTripRequest() {
    }

    protected StopTripRequest(Parcel in) {
        this.employee_code = in.readString();
        this.live_tracking_id = in.readString();
        this.mobile_km_otc = in.readFloat();
        this.imei = in.readString();
        this.pickup_route_id = in.readInt();
        this.stop_lattitude = in.readDouble();
        this.stop_longitude = in.readDouble();
        this.vehicle_owner_type = in.readString();
        this.vehicle_trip_id = in.readInt();
        this.stop_trip_multi_vehicle_data = new ArrayList<Start_Trip_Multi_Vehicle>();
        in.readList(this.stop_trip_multi_vehicle_data, Start_Trip_Multi_Vehicle.class.getClassLoader());
    }

    public static final Parcelable.Creator<StopTripRequest> CREATOR = new Parcelable.Creator<StopTripRequest>() {
        @Override
        public StopTripRequest createFromParcel(Parcel source) {
            return new StopTripRequest(source);
        }

        @Override
        public StopTripRequest[] newArray(int size) {
            return new StopTripRequest[size];
        }
    };


}

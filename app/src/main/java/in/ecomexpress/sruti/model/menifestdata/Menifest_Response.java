package in.ecomexpress.sruti.model.menifestdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by 63091 on 01-07-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Menifest_Response implements Parcelable {
    private ArrayList<Manifest_List> manifest_list_data;
    private String facility_in_time;
    private int route_id;
    private String route_end_time;
    private int code;
    private String description;
    private String manifest_cutoff_time;
    private long last_sync_time;


    public long getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(long last_sync_time) {
        this.last_sync_time = last_sync_time;
    }

    public String getManifest_cutoff_time() {
        return manifest_cutoff_time;
    }

    public void setManifest_cutoff_time(String manifest_cutoff_time) {
        this.manifest_cutoff_time = manifest_cutoff_time;
    }


    public ArrayList<Manifest_List> getManifest_list_data() {
        return manifest_list_data;
    }

    public void setManifest_list_data(ArrayList<Manifest_List> manifest_list_data) {
        this.manifest_list_data = manifest_list_data;
    }

    public String getFacility_in_time() {
        return facility_in_time;
    }

    public void setFacility_in_time(String facility_in_time) {
        this.facility_in_time = facility_in_time;
    }

    public String getRoute_end_time() {
        return route_end_time;
    }

    public void setRoute_end_time(String route_end_time) {
        this.route_end_time = route_end_time;
    }


    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.manifest_list_data);
    }

    public Menifest_Response() {
    }

    protected Menifest_Response(Parcel in) {
        this.manifest_list_data = new ArrayList<Manifest_List>();
        in.readList(this.manifest_list_data, Manifest_List.class.getClassLoader());
    }

    public static final Parcelable.Creator<Menifest_Response> CREATOR = new Parcelable.Creator<Menifest_Response>() {
        @Override
        public Menifest_Response createFromParcel(Parcel source) {
            return new Menifest_Response(source);
        }

        @Override
        public Menifest_Response[] newArray(int size) {
            return new Menifest_Response[size];
        }
    };
}

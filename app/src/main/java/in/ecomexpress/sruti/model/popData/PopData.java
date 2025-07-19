package in.ecomexpress.sruti.model.popData;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;

import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "pop_manifest")
public class PopData {
    public Integer route_id;

    @PrimaryKey
    private long  manifest_ids;
    private String seller_phone_no;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeller_phone_no() {
        return seller_phone_no;
    }

    public void setSeller_phone_no(String seller_phone_no) {
        this.seller_phone_no = seller_phone_no;
    }


    public Integer getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Integer route_id) {
        this.route_id = route_id;
    }

/*    public ArrayList<PopManifestList> getPopManifestLists() {
        return popManifestLists;
    }

    public void setPopManifestLists(ArrayList<PopManifestList> popManifestLists) {
        this.popManifestLists = popManifestLists;
    }*/

    public long getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(long manifest_ids) {
        this.manifest_ids = manifest_ids;
    }
}

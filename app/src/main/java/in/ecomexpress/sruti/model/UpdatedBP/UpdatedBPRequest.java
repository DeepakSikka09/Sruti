package in.ecomexpress.sruti.model.UpdatedBP;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatedBPRequest implements Serializable {

    @SerializedName("manifest_ids")
    private ArrayList<Long> manifest_ids;
    private long last_sync_time;
    @SerializedName("is_with_mps")
    private boolean is_with_mps;

    public boolean isIs_with_mps() {
        return is_with_mps;
    }

    public void setIs_with_mps(boolean is_with_mps) {
        this.is_with_mps = is_with_mps;
    }

    public ArrayList<Long> getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(ArrayList<Long> manifest_ids) {
        this.manifest_ids = manifest_ids;
    }

    public long getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(long last_sync_time) {
        this.last_sync_time = last_sync_time;
    }
}

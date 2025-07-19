package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Deepak Sikka on 5/11/19.
 */

public class RtoRequest {
    @JsonProperty("manifest_ids")
    private ArrayList<Long> manifestIds;
    private long last_sync_time;
    @JsonProperty("is_with_mps")
    private boolean is_mps;

    public boolean isIs_mps() {
        return is_mps;
    }

    public void setIs_mps(boolean is_mps) {
        this.is_mps = is_mps;
    }
    public ArrayList<Long> getManifestIds() {
        return manifestIds;
    }

    public void setManifestIds(ArrayList<Long> manifestIds) {
        this.manifestIds = manifestIds;
    }


    public long getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(long last_sync_time) {
        this.last_sync_time = last_sync_time;
    }


}

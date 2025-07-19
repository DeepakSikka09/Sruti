package in.ecomexpress.sruti.model.popData;


import androidx.room.Entity;


public class PopManifestList {
    private long  manifest_ids;
    private String  manifest_status;

    public PopManifestList(long manifest_ids, String manifest_status) {
        this.manifest_ids = manifest_ids;
        this.manifest_status = manifest_status;
    }

    public long getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(long manifest_ids) {
        this.manifest_ids = manifest_ids;
    }

    public String getManifest_status() {
        return manifest_status;
    }

    public void setManifest_status(String manifest_status) {
        this.manifest_status = manifest_status;
    }
}

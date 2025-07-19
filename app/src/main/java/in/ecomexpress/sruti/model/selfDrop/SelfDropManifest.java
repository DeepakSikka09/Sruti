package in.ecomexpress.sruti.model.selfDrop;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SelfDropManifest {
    @JsonProperty("is_self_drop_off")
    private Boolean isSelfDropOff;
    @JsonProperty("manifest_id")
    private long manifestId;

    public Boolean getIsSelfDropOff() {
        return isSelfDropOff;
    }

    public void setIsSelfDropOff(Boolean isSelfDropOff) {
        this.isSelfDropOff = isSelfDropOff;
    }

    public long getManifestId() {
        return manifestId;
    }

    public void setManifestId(long manifestId) {
        this.manifestId = manifestId;
    }
}

package in.ecomexpress.sruti.model.selfDrop;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    @JsonProperty("self_drop_manifests")
    private List<SelfDropManifest> selfDropManifests;

    public List<SelfDropManifest> getSelfDropManifests() {
        return selfDropManifests;
    }

    public void setSelfDropManifests(List<SelfDropManifest> selfDropManifests) {
        this.selfDropManifests = selfDropManifests;
    }
}

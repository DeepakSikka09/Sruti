package in.ecomexpress.sruti.model.signature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class seller_details_list {

    public List<Long> manifest_ids;

    private String seller_phone_no;

    public List<Long> getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(List<Long> manifest_ids) {
        this.manifest_ids = manifest_ids;
    }

    public String getSeller_phone_no() {
        return seller_phone_no;
    }

    public void setSeller_phone_no(String seller_phone_no) {
        this.seller_phone_no = seller_phone_no;
    }
}

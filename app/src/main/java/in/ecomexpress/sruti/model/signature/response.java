package in.ecomexpress.sruti.model.signature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class response {

    private Boolean status;

    private String description;

    private String seller_phone_no;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeller_phone_no() {
        return seller_phone_no;
    }

    public void setSeller_phone_no(String seller_phone_no) {
        this.seller_phone_no = seller_phone_no;
    }
}

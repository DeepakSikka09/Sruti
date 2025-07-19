package in.ecomexpress.sruti.model.signature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProofOfPickupRequest {
    private Integer route_id;
    public List<seller_details_list> seller_details_list;

    public Integer getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Integer route_id) {
        this.route_id = route_id;
    }

    public List<in.ecomexpress.sruti.model.signature.seller_details_list> getSeller_details_list() {
        return seller_details_list;
    }

    public void setSeller_details_list(List<in.ecomexpress.sruti.model.signature.seller_details_list> seller_details_list) {
        this.seller_details_list = seller_details_list;
    }


}

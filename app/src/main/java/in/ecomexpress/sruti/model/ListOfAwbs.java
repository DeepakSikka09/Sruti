package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Deepak.Sikka on 1/11/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListOfAwbs {
    private long awb;
    private long manifest_id;
    private String status_code;
    private String order_number;

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }


    public long getAwb() {
        return awb;
    }

    public void setAwb(long awb) {
        this.awb = awb;
    }

    public long getManifest_id() {
        return manifest_id;
    }

    public void setManifest_id(long manifest_id) {
        this.manifest_id = manifest_id;
    }

    @Override
    public String toString() {
        return "ListOfAwbs{" +
                "awb=" + awb +
                ", manifest_id=" + manifest_id +
                ", status_code='" + status_code + '\'' +
                ", order_number='" + order_number + '\'' +
                '}';
    }

}

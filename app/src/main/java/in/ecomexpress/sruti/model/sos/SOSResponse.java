package in.ecomexpress.sruti.model.sos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by parikshittomar on 28-02-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SOSResponse {

    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private boolean status;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SOSResponse{" +
                "response=" + response +
                ", status='" + status + '\'' +
                '}';
    }
}

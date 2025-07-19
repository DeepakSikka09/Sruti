package in.ecomexpress.sruti.model.signature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProofOfPickupResponse {
    private Boolean status;

    private Integer code;

    private String description;
    public List<in.ecomexpress.sruti.model.signature.response> response;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<in.ecomexpress.sruti.model.signature.response> getResponse() {
        return response;
    }

    public void setResponse(List<in.ecomexpress.sruti.model.signature.response> response) {
        this.response = response;
    }
}

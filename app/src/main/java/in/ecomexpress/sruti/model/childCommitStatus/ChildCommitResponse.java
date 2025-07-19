package in.ecomexpress.sruti.model.childCommitStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCommitResponse {
    private String code;
    private String description;
    @JsonProperty("status")
    private boolean status;
    private ChildResponse response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ChildResponse getResponse() {
        return response;
    }

    public void setResponse(ChildResponse response) {
        this.response = response;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("status")
    public boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChildCommitResponse [code = " + code + ", response = " + response + ", description = " + description + ", status = " + status + "]";
    }
}

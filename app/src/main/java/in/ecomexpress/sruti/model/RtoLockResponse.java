package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Deepak.Sikka on 1/11/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RtoLockResponse {
    private String code;
    private String description;
    private boolean status;
    private RtoResponse response;

    public RtoResponse getResponse() {
        return response;
    }

    public void setResponse(RtoResponse response) {
        this.response = response;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RtoLockResponse{" +
                "code='" + code + '\'' +
                ", response=" + response +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
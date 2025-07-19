package in.ecomexpress.sruti.model.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import in.ecomexpress.sruti.model.Response;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkipOtpMainResponse{
    private Boolean status;
    private Integer code;
    private String description;
    public SkipOtpResponse response;

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

    public SkipOtpResponse getResponse() {
        return response;
    }

    public void setResponse(SkipOtpResponse response) {
        this.response = response;
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("SkipOtpMainResponse{");
//        sb.append("status=").append(status);
//        sb.append(", code=").append(code);
//        sb.append(", description='").append(description).append('\'');
//        sb.append(", response=").append(response);
//        sb.append('}');
//        return sb.toString();
//    }

}

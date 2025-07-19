package in.ecomexpress.sruti.model.OtpRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response  implements Serializable {
    public boolean status;
    public String description;
    public String verification_source;
    public Boolean max_reach;
    public String message;
    public ArrayList<String> errors;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVerification_source() {
        return verification_source;
    }

    public void setVerification_source(String verification_source) {
        this.verification_source = verification_source;
    }

    public Boolean getMax_reach() {
        return max_reach;
    }

    public void setMax_reach(Boolean max_reach) {
        this.max_reach = max_reach;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}

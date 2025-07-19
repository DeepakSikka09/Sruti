package in.ecomexpress.sruti.model.resendOtp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 29-01-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResendOtpRequest {

    @JsonProperty("username")
    private String username;


    public LoginResendOtpRequest(String employeeCode) {
        this.username = employeeCode;

    }

    public String getEmployeeCode() {
        return username;
    }

    public void setEmployeeCode(String employeeCode) {
        this.username = employeeCode;
    }


}

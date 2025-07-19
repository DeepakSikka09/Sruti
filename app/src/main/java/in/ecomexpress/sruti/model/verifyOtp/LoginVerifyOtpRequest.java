package in.ecomexpress.sruti.model.verifyOtp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dhananjayk on 29-01-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginVerifyOtpRequest {

    @JsonProperty("employee_code")
    private String employeeCode;
    @JsonProperty("otp")
    private String otp;

    public LoginVerifyOtpRequest(String employeeCode, String otp) {
        this.employeeCode = employeeCode;
        this.otp = otp;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}

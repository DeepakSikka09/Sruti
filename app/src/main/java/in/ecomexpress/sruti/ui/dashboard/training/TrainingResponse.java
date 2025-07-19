package in.ecomexpress.sruti.ui.dashboard.training;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingResponse  implements Serializable {
    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("redirectURL")
    private String redirectURL;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("success")
    private boolean success;

    // Constructors
    public TrainingResponse() {
    }

    public TrainingResponse(String errorCode, String redirectURL, int statusCode, boolean success) {
        this.errorCode = errorCode;
        this.redirectURL = redirectURL;
        this.statusCode = statusCode;
        this.success = success;
    }

    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    @Override
    public String toString() {
        return "TrainingResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", redirectURL='" + redirectURL + '\'' +
                ", statusCode=" + statusCode +
                ", success=" + success +
                '}';
    }
}

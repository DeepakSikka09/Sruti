package in.ecomexpress.sruti.model.login;


import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogoutResponse {
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("code")
    private int code;
    @JsonProperty("description")
    private String description;
    @JsonProperty("response")
    public EResponse response;


    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public EResponse getResponse() {
        return response;
    }

    @JsonProperty("response")
    public void setResponse(EResponse response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status=" + status +
                ", code=" + code +
                ", description='" + description + '\'' +
                ", response=" + response +
                '}';
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EResponse {
        @Ignore
        @JsonProperty("description")
        private String description;

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        private  boolean token_is_verified;

        public boolean isToken_is_verified() {
            return token_is_verified;
        }

        public void setToken_is_verified(boolean token_is_verified) {
            this.token_is_verified = token_is_verified;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }
    }
}


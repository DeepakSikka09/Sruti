package in.ecomexpress.sruti.model.inScanData;


import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by deepak on 28/10/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstInscanResponse {
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("code")
    private int code;
    @JsonProperty("description")
    private String description;
    @JsonProperty("response")
    public EResponse response;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public EResponse getResponse() {
        return response;
    }

    public void setResponse(EResponse response) {
        this.response = response;
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

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }
    }


}
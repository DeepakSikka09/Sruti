package in.ecomexpress.sruti.model.commitdata;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by deepak on 1/11/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitResponse {
    private float code;
    private String description;
    Response responseObject;
    private boolean status;

    public float getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Response getResponse() {
        return responseObject;
    }

    public boolean getStatus() {
        return status;
    }

    public void setCode(float code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResponse(Response responseObject) {
        this.responseObject = responseObject;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public class Response {
        private String description;
        private ArrayList<String> errors;
        ArrayList<Long> manifestIds = new ArrayList<Long>();

        public ArrayList<String> getErrors() {
            return errors;
        }

        public void setErrors(ArrayList<String> errors) {
            this.errors = errors;
        }

        public ArrayList<Long> getManifestIds() {
            return manifestIds;
        }

        public void setManifestIds(ArrayList<Long> manifestIds) {
            this.manifestIds = manifestIds;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}



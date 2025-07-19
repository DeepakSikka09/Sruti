package in.ecomexpress.sruti.model.starttrip;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.login.LoginResponse;

public class Extra_Vehicle {
    private boolean status;
    private float code;
    private String description;

    public ArrayList<LoginResponse.StartRouteDetails> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<LoginResponse.StartRouteDetails> response) {
        this.response = response;
    }

    ArrayList < LoginResponse.StartRouteDetails > response = new ArrayList< LoginResponse.StartRouteDetails >();


    // Getter Methods

    public boolean getStatus() {
        return status;
    }

    public float getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Setter Methods

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setCode(float code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

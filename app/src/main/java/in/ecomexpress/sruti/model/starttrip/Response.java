package in.ecomexpress.sruti.model.starttrip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by 63091 on 12-06-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private int trip_id;
    private String live_tracking_id;
    private ArrayList<String> errors;

    public String getLive_tracking_id() {
        return live_tracking_id;
    }

    public void setLive_tracking_id(String live_tracking_id) {
        this.live_tracking_id = live_tracking_id;
    }
    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }


}

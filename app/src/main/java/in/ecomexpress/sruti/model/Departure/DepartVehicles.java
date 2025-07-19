package in.ecomexpress.sruti.model.Departure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartVehicles {
    private long dep_latitude;
    private long dep_longitude;
    private long dep_timestamp;
    private String vehicle_number;
    public long getDep_latitude() {
        return dep_latitude;
    }

    public void setDep_latitude(long dep_latitude) {
        this.dep_latitude = dep_latitude;
    }

    public long getDep_longitude() {
        return dep_longitude;
    }

    public void setDep_longitude(long dep_longitude) {
        this.dep_longitude = dep_longitude;
    }

    public long getDep_timestamp() {
        return dep_timestamp;
    }

    public void setDep_timestamp(long dep_timestamp) {
        this.dep_timestamp = dep_timestamp;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }


}

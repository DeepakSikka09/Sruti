package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;


/**
 * Created by Deepak.sikka on 1/11/19.
 * deepak.sikka@ecomexpress.in
 * +91-9910154059
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private ArrayList<String> errors;

    public String getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    private String shipment_status;

    public ArrayList<String> getErrors ()
    {
        return errors;
    }

    public void setErrors (ArrayList<String> errors)
    {
        this.errors = errors;
    }


    @Override
    public String toString()
    {
        return "ScanResponse [errors = "+errors+", status = "+shipment_status+"]";
    }
}

package in.ecomexpress.sruti.model.childCommitStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildResponse {
    private boolean commit_status;
    private ArrayList<Child_Shipment_details> shipment_details;
    private ArrayList<Child_details> child_details;

    public boolean getCommit_status ()
    {
        return commit_status;
    }

    public void setCommit_status (boolean commit_status)
    {
        this.commit_status = commit_status;
    }

    public ArrayList<Child_Shipment_details> getShipment_details ()
    {
        return shipment_details;
    }

    public void setShipment_details (ArrayList<Child_Shipment_details> shipment_details)
    {
        this.shipment_details = shipment_details;
    }

    public ArrayList<Child_details> getChild_details ()
    {
        return child_details;
    }

    public void setChild_details (ArrayList<Child_details> child_details)
    {
        this.child_details = child_details;
    }

    @Override
    public String toString()
    {
        return "ChildResponse [commit_status = "+commit_status+", shipment_details = "+shipment_details+", child_details = "+child_details+"]";
    }
}

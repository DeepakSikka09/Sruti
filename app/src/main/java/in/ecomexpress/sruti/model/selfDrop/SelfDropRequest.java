package in.ecomexpress.sruti.model.selfDrop;

import java.util.ArrayList;

public class SelfDropRequest {
    private String emp_code;
    private long pickup_route_id;
    private ArrayList<Long> manifest_ids;

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public long getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(long pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public ArrayList<Long> getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(ArrayList<Long> manifest_ids) {
        this.manifest_ids = manifest_ids;
    }


}

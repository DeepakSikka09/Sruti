package in.ecomexpress.sruti.model.childCommitStatus;

import java.util.ArrayList;

/**
 * Created by shivangi on 14/11/19.
 */

public class ChildCommitRequest {
    private String emp_code;
    private Long pickup_route_id;

    private ArrayList<Long> manifest_ids;

    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public Long getPickup_route_id() {
        return pickup_route_id;
    }

    public void setPickup_route_id(Long pickup_route_id) {
        this.pickup_route_id = pickup_route_id;
    }

    public ArrayList<Long> getManifest_ids() {
        return manifest_ids;
    }

    public void setManifest_ids(ArrayList<Long> manifest_ids) {
        this.manifest_ids = manifest_ids;
    }

    @Override
    public String toString() {
        return "ChildCommitRequest{" +
                "emp_code='" + emp_code + '\'' +
                ", pickup_route_id=" + pickup_route_id +
                ", manifest_ids=" + manifest_ids +
                '}';
    }

}

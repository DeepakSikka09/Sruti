package in.ecomexpress.sruti.model.childCommitStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Child_details {
    private String employee_code;
    private String employee_name;
    private String description;
    private boolean status;
    private long manifest_number;

    public long getManifest_number() {
        return manifest_number;
    }

    public void setManifest_number(long manifest_number) {
        this.manifest_number = manifest_number;
    }



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }


    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }


    @Override
    public String toString() {
        return "Child_details{" +
                "employee_code='" + employee_code + '\'' +
                ", employee_name='" + employee_name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", manifest_number=" + manifest_number +
                '}';
    }


}

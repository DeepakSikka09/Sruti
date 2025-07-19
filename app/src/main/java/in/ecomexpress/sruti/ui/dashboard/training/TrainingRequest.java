package in.ecomexpress.sruti.ui.dashboard.training;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingRequest {
    @JsonProperty("emp_code")
    private String employeeCode;



    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
}

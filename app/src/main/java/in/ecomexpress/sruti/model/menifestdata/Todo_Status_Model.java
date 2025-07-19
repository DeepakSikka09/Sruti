package in.ecomexpress.sruti.model.menifestdata;

/**
 * Created by 63091 on 03-07-2019.
 */

public class Todo_Status_Model {
    private String tasktype;

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }

    public Long getSuccess() {
        return success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }

    public Long getUnsuccess() {
        return unsuccess;
    }

    public void setUnsuccess(Long unsuccess) {
        this.unsuccess = unsuccess;
    }

    private Long pending;
    private Long success;
    private Long unsuccess;
}

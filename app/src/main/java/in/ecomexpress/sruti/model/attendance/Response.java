package in.ecomexpress.sruti.model.attendance;


import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response
{


    @JsonProperty("avg_working_hours")
    private String avg_working_hours;

    public String getAvg_working_hours() {
        return avg_working_hours;
    }

    public void setAvg_working_hours(String avg_working_hours) {
        this.avg_working_hours = avg_working_hours;
    }

    public List<AttendanceResponseList> getAttendanceResponseList() {
        return attendanceResponseList;
    }

    public void setAttendanceResponseList(List<AttendanceResponseList> attendanceResponseList) {
        this.attendanceResponseList = attendanceResponseList;
    }

    public String getStart_day_of_month() {
        return start_day_of_month;
    }

    public void setStart_day_of_month(String start_day_of_month) {
        this.start_day_of_month = start_day_of_month;
    }

    @Embedded
    @JsonProperty("attendance_response_list")
    private List<AttendanceResponseList> attendanceResponseList;

    @JsonProperty("start_day_of_month")
    private String start_day_of_month;


}

package in.ecomexpress.sruti.model.attendance;


import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceResponse
{
    @Embedded
    @JsonProperty("response")
    private Response response;

    @JsonProperty("status")
    private boolean status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("code")
    private int code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("response")
    public Response getResponse ()
    {
        return response;
    }

    @JsonProperty("response")
    public void setResponse (Response response)
    {
        this.response = response;
    }

    @JsonProperty("status")
    public boolean getStatus ()
    {
        return status;
    }

    @JsonProperty("status")
    public void setStatus (boolean status)
    {
        this.status = status;
    }

}


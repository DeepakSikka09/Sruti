package in.ecomexpress.sruti.model.firstScan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstScanResponse {
    private String code;

    private FirstResponse response;

    private String description;

    private boolean status;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public FirstResponse getResponse ()
    {
        return response;
    }

    public void setResponse (FirstResponse response)
    {
        this.response = response;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public boolean getStatus ()
    {
        return status;
    }

    public void setStatus (boolean status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "FirstScanResponse [code = "+code+", response = "+response+", description = "+description+", status = "+status+"]";
    }

}

package in.ecomexpress.sruti.model.firstScan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirstResponse {

    private String description;

    private ArrayList<String> errors;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

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
        return "FirstResponse [description = "+description+", errors = "+errors+"]";
    }
}

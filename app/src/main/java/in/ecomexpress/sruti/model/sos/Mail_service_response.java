package in.ecomexpress.sruti.model.sos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangi on 6/8/19.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mail_service_response {

    @JsonProperty("description")
    private String description;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("description")
    public String getDescription ()
    {
        return description;
    }

    @JsonProperty("description")
    public void setDescription (String description)
    {
        this.description = description;
    }

    @JsonProperty("from")
    public String getFrom ()
    {
        return from;
    }

    @JsonProperty("from")
    public void setFrom (String from)
    {
        this.from = from;
    }

    @JsonProperty("to")
    public String getTo ()
    {
        return to;
    }

    @JsonProperty("to")
    public void setTo (String to)
    {
        this.to = to;
    }

    @Override
    public String toString()
    {
        return "Mail_service_response [description = "+description+", from = "+from+", to = "+to+"]";
    }
}

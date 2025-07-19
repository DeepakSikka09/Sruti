package in.ecomexpress.sruti.model.handoverdata;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd on 18/9/19.
 */

public class HandOverResponse
{
    private String code;

    private Response response;

    private String description;

    private String status;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public Response getResponse ()
    {
        return response;
    }

    public void setResponse (Response response)
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

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [code = "+code+", response = "+response+", description = "+description+", status = "+status+"]";
    }
}

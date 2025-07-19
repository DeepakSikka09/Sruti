package in.ecomexpress.sruti.model.handoverdata;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd on 18/9/19.
 */



public class ListOfAwbs
{
    private String status_code;

    private String order_number;

    private String awb;

    private String manifest_id;

    public String getStatus_code ()
    {
        return status_code;
    }

    public void setStatus_code (String status_code)
    {
        this.status_code = status_code;
    }

    public String getOrder_number ()
    {
        return order_number;
    }

    public void setOrder_number (String order_number)
    {
        this.order_number = order_number;
    }

    public String getAwb ()
    {
        return awb;
    }

    public void setAwb (String awb)
    {
        this.awb = awb;
    }

    public String getManifest_id ()
    {
        return manifest_id;
    }

    public void setManifest_id (String manifest_id)
    {
        this.manifest_id = manifest_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status_code = "+status_code+", order_number = "+order_number+", awb = "+awb+", manifest_id = "+manifest_id+"]";
    }
}

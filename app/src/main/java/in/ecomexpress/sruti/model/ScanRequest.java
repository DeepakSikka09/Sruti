package in.ecomexpress.sruti.model;

/**
 * Created by shivangi on 4/11/19.
 */

public class ScanRequest {

    private long awb;

    private long manifest_id;




    public long getAwb ()
    {
        return awb;
    }

    public void setAwb (long awb)
    {
        this.awb = awb;
    }

    public long getManifest_id ()
    {
        return manifest_id;
    }

    public void setManifest_id (long manifest_id)
    {
        this.manifest_id = manifest_id;
    }

    @Override
    public String toString()
    {
        return "ScanRequest [code = +, awb = "+awb+", manifest_id = "+manifest_id+"]";
    }
}

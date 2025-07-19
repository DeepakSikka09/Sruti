package in.ecomexpress.sruti.model.masterdata;

import java.util.ArrayList;

/**
 * Created by 63091 on 28-06-2019.
 */

public class Callbridge_Configuration {
    public String getCb_calling_type() {
        return cb_calling_type;
    }

    public void setCb_calling_type(String cb_calling_type) {
        this.cb_calling_type = cb_calling_type;
    }



    private String cb_calling_type;

    public String getCb_calling_api() {
        return cb_calling_api;
    }

    public void setCb_calling_api(String cb_calling_api) {
        this.cb_calling_api = cb_calling_api;
    }

    private String cb_calling_api;

    public ArrayList<Post_option> getCb_pstn_options() {
        return cb_pstn_options;
    }

    public void setCb_pstn_options(ArrayList<Post_option> cb_pstn_options) {
        this.cb_pstn_options = cb_pstn_options;
    }

    private ArrayList<Post_option> cb_pstn_options=new ArrayList<>();

}

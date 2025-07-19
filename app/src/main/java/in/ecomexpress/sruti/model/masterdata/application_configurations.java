package in.ecomexpress.sruti.model.masterdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class application_configurations{

    /*@SerializedName("SRUTI_MULTI_SPACE_APP_LIST")
    @Expose*/
    public ArrayList<String> SRUTI_MULTI_SPACE_APP_LIST;

    public ArrayList<String> getSrutiMultiSpaceAppList() {
        return SRUTI_MULTI_SPACE_APP_LIST;
    }

    public void setSrutiMultiSpaceAppList(ArrayList<String> srutiMultiSpaceAppList) {
        this.SRUTI_MULTI_SPACE_APP_LIST = srutiMultiSpaceAppList;
    }
}

package in.ecomexpress.sruti.model.masterdata;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by 63091 on 29-06-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "callbridge_pstns")
public class Post_option {

    private String pstn_format;

    public String getPstn_format() {
        return pstn_format;
    }

    public void setPstn_format(String pstn_format) {
        this.pstn_format = pstn_format;
    }

    public String getPstn_provider() {
        return pstn_provider;
    }

    public void setPstn_provider(String pstn_provider) {
        this.pstn_provider = pstn_provider;
    }

    @PrimaryKey
    @NonNull
    private String pstn_provider;


    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    public String getAdditional_text() {
        return additional_text;
    }

    public void setAdditional_text(String additional_text) {
        this.additional_text = additional_text;
    }

    public String isLocal;

    public String additional_text;
}

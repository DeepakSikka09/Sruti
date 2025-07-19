package in.ecomexpress.sruti.model.masterdata;

/**
 * Created by 63091 on 29-06-2019.
 */

public class Global_Config_Item {
    private String config_group;

    public String getConfig_group() {
        return config_group;
    }

    public void setConfig_group(String config_group) {
        this.config_group = config_group;
    }

    public String getConfig_value() {
        return config_value;
    }

    public void setConfig_value(String config_value) {
        this.config_value = config_value;
    }

    private String config_value="False";
}

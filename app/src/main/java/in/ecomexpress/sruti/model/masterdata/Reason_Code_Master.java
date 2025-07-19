package in.ecomexpress.sruti.model.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by 63091 on 28-06-2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reason_Code_Master {
    public ArrayList<Global_Config_Item> getGlobal_configuration() {
        return global_configuration;
    }

    public void setGlobal_configuration(ArrayList<Global_Config_Item> global_configuration) {
        this.global_configuration = global_configuration;
    }

    public Banner_Configuration getBanner_configuration() {
        return banner_configuration;
    }

    public void setBanner_configuration(Banner_Configuration banner_configuration) {
        this.banner_configuration = banner_configuration;
    }

    public Callbridge_Configuration getCallbridge_configuration() {
        return callbridge_configuration;
    }

    public void setCallbridge_configuration(Callbridge_Configuration callbridge_configuration) {
        this.callbridge_configuration = callbridge_configuration;
    }

    public ArrayList<ReasonCodeList> getReason_code_list() {
        return reason_code_list;
    }

    public void setReason_code_list(ArrayList<ReasonCodeList> reason_code_list) {
        this.reason_code_list = reason_code_list;
    }

    public ArrayList<General_Question> getGeneral_question() {
        return general_question;
    }

    public void setGeneral_question(ArrayList<General_Question> general_question) {
        this.general_question = general_question;
    }

    private ArrayList<General_Question> general_question;
    private ArrayList<ReasonCodeList> reason_code_list = new ArrayList<>();
    private ArrayList<Global_Config_Item> global_configuration;
    private Banner_Configuration banner_configuration;
    private Callbridge_Configuration callbridge_configuration = new Callbridge_Configuration();
    public application_configurations application_configurations;

    public application_configurations getApplication_configurations() {
        return application_configurations;
    }

    public void setApplication_configurations(application_configurations application_configurations) {
        this.application_configurations = application_configurations;
    }
}

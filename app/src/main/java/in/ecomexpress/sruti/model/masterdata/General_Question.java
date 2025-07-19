package in.ecomexpress.sruti.model.masterdata;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;

/**
 * Created by 63091 on 14-08-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "recci_question_list")
public class General_Question {
    private String ans_type;
    private String option;
    private String qtag;
    private String type;

    public String getAdditional_control() {
        return additional_control;
    }

    public void setAdditional_control(String additional_control) {
        this.additional_control = additional_control;
    }

    private String additional_control;


    public ArrayList<String> getValue_array() {
        return value_array;
    }

    public void setValue_array(ArrayList<String> value_array) {
        this.value_array = value_array;
    }
@TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<String> value_array;
    @PrimaryKey
    private int id;

    public String getAns_type() {
        return ans_type;
    }

    public void setAns_type(String ans_type) {
        this.ans_type = ans_type;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getQtag() {
        return qtag;
    }

    public void setQtag(String qtag) {
        this.qtag = qtag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

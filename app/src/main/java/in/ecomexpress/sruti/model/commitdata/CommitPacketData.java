package in.ecomexpress.sruti.model.commitdata;


import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.menifestdata.DataTypeConverterObjectToGson;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 18/9/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitPacketData {
    private String emp_code;
    private Long trip_id;
    private String fe_type;

    @TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<Manifest_process> manifest_process;


    public String getEmp_code() {
        return emp_code;
    }

    public void setEmp_code(String emp_code) {
        this.emp_code = emp_code;
    }

    public Long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(Long trip_id) {
        this.trip_id = trip_id;
    }

    public ArrayList<Manifest_process> getManifest_process() {
        return manifest_process;
    }

    public void setManifest_process(ArrayList<Manifest_process> manifest_process) {
        this.manifest_process = manifest_process;
    }


    public String getFe_type() {
        return fe_type;
    }

    public void setFe_type(String fe_type) {
        this.fe_type = fe_type;
    }

    @Override
    public String toString() {
        return "ClassPojo [emp_code = " + emp_code + ", trip_id = " + trip_id + ", manifest_process = " + manifest_process + ", fe_type = " + fe_type + "]";
    }

}

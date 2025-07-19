package in.ecomexpress.sruti.model.menifestdata;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.utils.TimeUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by 63091 on 01-07-2019.
 */
@Entity(tableName = "manifest_list", indices = {@Index(value = {"composite_Key"}, unique = true)})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Manifest_List implements Parcelable {
    private long manifest_No;
    private int sequence_no;
    private int manifest_failed;
    private int inScan = 0;
    private Long picked_count;
    private Long unpicked_count;
    private int total_shipment_count;
    private Long remaining_count;
    private String shipment_status;
    private int commit_status = Constants.COMMIT_PENDING;
    private long totalShipmentCount;
    private String shipment_count;
    private long assign_date;
    private String cust_code;
    private String cust_name="";
    private String location_type;
    private String location_otp;
    private int pickup_location_id;
    @PrimaryKey
    @NonNull
    private String composite_Key;
    private String manifest_type;
    @TypeConverters(DataTypeConverterObjectToGson.class)
    private Manifest_Setting setting;
    @Ignore
    private ArrayList<Shipment_Detail> shipment_details;
    @TypeConverters(DataTypeConverterObjectToGson.class)
    private Flags flags;
    @TypeConverters(DataTypeConverterObjectToGson.class)
    private Vender_Detail manifest_details;
    @TypeConverters(DataTypeConverterObjectToGson.class)
    private ArrayList<Integer> question_id;
    @Ignore
    private int synkcount;
    private boolean is_mps_manifest;
    private int isscanstarted;
    @Ignore
    private Remark temporary_remark;
    private long last_sync_time;
    private boolean is_express_seller;



    private boolean isSharedManifest=false;
    private String mobileNoType="";
    private int otp_pickup_location_id;

    public int getOtp_pickup_location_id() {
        return otp_pickup_location_id;
    }

    public void setOtp_pickup_location_id(int otp_pickup_location_id) {
        this.otp_pickup_location_id = otp_pickup_location_id;
    }

    public boolean isSharedManifest() {
        return isSharedManifest;
    }

    public void setSharedManifest(boolean sharedManifest) {
        isSharedManifest = sharedManifest;
    }
    public String getMobileNoType() {
        return mobileNoType;
    }

    public void setMobileNoType(String mobileNoType) {
        this.mobileNoType = mobileNoType;
    }

    public boolean is_express_seller() {
        return is_express_seller;
    }

    public void setIs_express_seller(boolean is_express_seller) {
        this.is_express_seller = is_express_seller;
    }

    public long getLast_sync_time() {
        return last_sync_time;
    }

    public void setLast_sync_time(long last_sync_time) {
        this.last_sync_time = last_sync_time;
    }

    public int getIsscanstarted() {
        return isscanstarted;
    }

    public void setIsscanstarted(int isscanstarted) {
        this.isscanstarted = isscanstarted;
    }

    public int getSynkcount() {
        return synkcount;
    }

    public void setSynkcount(int synkcount) {
        this.synkcount = synkcount;
    }

    public Remark getTemporary_remark() {
        return temporary_remark;
    }

    public void setTemporary_remark(Remark temporary_remark) {
        this.temporary_remark = temporary_remark;
    }

    public String getShipment_count() {
        return shipment_count;
    }

    public void setShipment_count(String shipment_count) {
        this.shipment_count = shipment_count;
    }

    public Remark set_list_Temporary_remark(String remarkStr, String EmpCode) {
        if (remarkStr == null) {
            new NullPointerException("Remark can not be null");
        }
        if (temporary_remark == null) {
            temporary_remark = new Remark();
            temporary_remark.manifest_no = manifest_No;
            temporary_remark.empCode = EmpCode + "";
            temporary_remark.sync_status = Constants.COMMIT_PENDING;
            temporary_remark.date = TimeUtils.getDateYearMonthMillies();
        }
        temporary_remark.remark = remarkStr;

        return temporary_remark;
    }

    public int getManifest_failed() {
        return manifest_failed;
    }

    public void setManifest_failed(int manifest_failed) {
        this.manifest_failed = manifest_failed;
    }

    public int getInScan() {
        return inScan;
    }

    public boolean getIs_mps_manifest() {
        return is_mps_manifest;
    }

    public void setIs_mps_manifest(boolean is_mps_manifest) {
        this.is_mps_manifest = is_mps_manifest;
    }

    public void setInScan(int inScan) {
        this.inScan = inScan;
    }

    public Long getUnpicked_count() {
        return unpicked_count;
    }

    public void setUnpicked_count(Long unpicked_count) {
        this.unpicked_count = unpicked_count;
    }

    public int getCommit_status() {
        return commit_status;
    }

    public void setCommit_status(int commit_status) {
        this.commit_status = commit_status;
    }

    public int getPickup_location_id() {
        return pickup_location_id;
    }

    public void setPickup_location_id(int pickup_location_id) {
        this.pickup_location_id = pickup_location_id;
    }

    public String getComposite_Key() {
        return manifest_No + "_" + pickup_location_id;
    }

    public void setComposite_Key(String composite_Key) {
        this.composite_Key = manifest_No + "_" + pickup_location_id;
    }

    public String getManifest_type() {
        return manifest_type;
    }

    public void setManifest_type(String manifest_type) {
        this.manifest_type = manifest_type;
    }

    public ArrayList<Integer> getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(ArrayList<Integer> question_id) {
        this.question_id = question_id;
    }

    public Long getPicked_count() {
        return picked_count;
    }

    public void setPicked_count(Long picked_count) {
        this.picked_count = picked_count;
    }

    public Long getRemaining_count() {
        return remaining_count;
    }

    public void setRemaining_count(Long remaining_count) {
        this.remaining_count = remaining_count;
    }


    public String getLocation_type() {
        return location_type;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }


    public String getCust_code() {
        return cust_code;
    }

    public void setCust_code(String cust_code) {
        this.cust_code = cust_code;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public Vender_Detail getManifest_details() {
        return manifest_details;
    }

    public void setManifest_details(Vender_Detail manifest_details) {
        this.manifest_details = manifest_details;
    }

    public long getAssign_date() {
        return assign_date;
    }

    public void setAssign_date(long assign_date) {
        this.assign_date = assign_date;
    }

    public long getManifest_No() {
        return manifest_No;
    }

    public void setManifest_No(long manifest_No) {
        this.manifest_No = manifest_No;
    }

    public int getTotal_shipment_count() {
        return total_shipment_count;
    }

    public void setTotal_shipment_count(int total_shipment_count) {
        this.total_shipment_count = total_shipment_count;
    }

    public String getShipment_status() {
        return shipment_status;
    }

    public void setShipment_status(String shipment_status) {
        this.shipment_status = shipment_status;
    }

    public String getLocation_otp() {
        return location_otp;
    }

    public void setLocation_otp(String location_otp) {
        this.location_otp = location_otp;
    }

    public long getAssignDate() {
        return assign_date;
    }

    public void setAssignDate(long assignDate) {
        this.assign_date = assignDate;
    }

    public int getSequence_no() {
        return sequence_no;
    }

    public void setSequence_no(int sequence_no) {
        this.sequence_no = sequence_no;
    }

    public String getStatus() {
        return shipment_status;
    }

    public void setStatus(String status) {
        this.shipment_status = status;
    }

    public long getTotalShipmentCount() {
        return totalShipmentCount;
    }

    public void setTotalShipmentCount(long totalShipmentCount) {
        this.totalShipmentCount = totalShipmentCount;
    }

    public Manifest_Setting getSetting() {
        return setting;
    }

    public void setSetting(Manifest_Setting setting) {
        this.setting = setting;
    }

    public ArrayList<Shipment_Detail> getShipment_details() {
        return shipment_details;
    }

    public void setShipment_details(ArrayList<Shipment_Detail> shipment_details) {
        this.shipment_details = shipment_details;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }


    public Manifest_List() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.manifest_No);
        dest.writeInt(this.sequence_no);
        dest.writeInt(this.inScan);
        dest.writeValue(this.picked_count);
        dest.writeValue(this.unpicked_count);
        dest.writeValue(this.remaining_count);
        dest.writeString(this.shipment_status);
        dest.writeInt(this.commit_status);
        dest.writeLong(this.totalShipmentCount);
        dest.writeLong(this.assign_date);
        dest.writeString(this.cust_code);
        dest.writeString(this.cust_name);
        dest.writeString(this.location_type);
        dest.writeString(this.location_otp);
        dest.writeInt(this.pickup_location_id);
        dest.writeInt(this.otp_pickup_location_id);
        dest.writeString(this.composite_Key);
        dest.writeString(this.manifest_type);
        dest.writeParcelable(this.setting, flags);
        dest.writeList(this.shipment_details);
        dest.writeParcelable(this.flags, flags);
        dest.writeParcelable(this.manifest_details, flags);
        dest.writeByte((byte) (is_express_seller ? 1 : 0));
        dest.writeByte((byte) (isSharedManifest ? 1 : 0));
        dest.writeString(this.mobileNoType);
        dest.writeList(this.question_id);
      //  dest.writeLong(this.last_sync_time);
    }

    protected Manifest_List(Parcel in) {
        this.manifest_No = in.readLong();
        this.sequence_no = in.readInt();
        this.inScan = in.readInt();
        this.picked_count = (Long) in.readValue(Long.class.getClassLoader());
        this.unpicked_count = (Long) in.readValue(Long.class.getClassLoader());
        this.remaining_count = (Long) in.readValue(Long.class.getClassLoader());
        this.shipment_status = in.readString();
        this.commit_status = in.readInt();
        this.totalShipmentCount = in.readLong();
        this.assign_date = in.readLong();
      //  this.last_sync_time = in.readLong();
        this.cust_code = in.readString();
        this.cust_name = in.readString();
        this.location_otp = in.readString();
        this.location_type = in.readString();

        this.pickup_location_id = in.readInt();
        this.otp_pickup_location_id = in.readInt();
        this.composite_Key = in.readString();
        this.manifest_type = in.readString();
        this.setting = in.readParcelable(Manifest_Setting.class.getClassLoader());
        this.shipment_details = new ArrayList<Shipment_Detail>();
        in.readList(this.shipment_details, Shipment_Detail.class.getClassLoader());
        this.flags = in.readParcelable(Flags.class.getClassLoader());
        this.manifest_details = in.readParcelable(Vender_Detail.class.getClassLoader());
        this.question_id = new ArrayList<Integer>();
        this.is_express_seller = in.readByte() != 0;
        this.isSharedManifest = in.readByte() != 0;
        this.mobileNoType=in.readString();
        in.readList(this.question_id, Integer.class.getClassLoader());
    }

    public static final Creator<Manifest_List> CREATOR = new Creator<Manifest_List>() {
        @Override
        public Manifest_List createFromParcel(Parcel source) {
            return new Manifest_List(source);
        }

        @Override
        public Manifest_List[] newArray(int size) {
            return new Manifest_List[size];
        }
    };
}

package in.ecomexpress.sruti.repo.local.db.prefs;

import static in.ecomexpress.sruti.repo.IDataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT;
import static in.ecomexpress.sruti.utils.common_files.Constants.MULTISPACE_APPS;
import static in.ecomexpress.sruti.utils.common_files.Constants.PICKUP_LOCATION_GEOFENCING_MODE;
import static in.ecomexpress.sruti.utils.common_files.Constants.PICKUP_LOCATION_GEOFENCING_RADIUS;
import static in.ecomexpress.sruti.utils.common_files.Constants.SRUTI_ALLOW_MULTI_SPACE_APPS;
import static in.ecomexpress.sruti.utils.common_files.Constants.SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH;
import static in.ecomexpress.sruti.utils.common_files.Constants.SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sruti.di.appmodule.AppModule;
import in.ecomexpress.sruti.model.DashboardBanner;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.MultiSpaceCheck.MultiSpaceActivity;

@Singleton
public class PreferenceHelper implements IPreferenceHelper {
    private static final String SELECTED_DRS_VIEW = "selected_drs_view";
    private static final String BOTTOM_TEXT = "bottom_text";
    private static final String SELECTED_SORTING = "selected_sorting";
    private static final String SOS_NUMBERS = "sos_numbers";
    private static final String SOS_TEMPLATE = "sos_template";
    private static final String ROUTE_CUTOFFTIME = "route_time";
    private static final String FE_REACH_DC = "fe_reached_dc";
    private static final String DC_LINE1 = "dc_line1";
    private static final String DC_LINE2 = "dc_line2";
    private static final String DC_LINE3 = "dc_line3";
    private static final String DC_LINE4 = "dc_line4";
    private static final String DC_CITY = "dc_city";
    private static final String DC_STATE = "dc_state";
    private static final String DC_PINCODE = "dc_pincode";


    private static String APK_DOWNLOAD_IS_IN_PROCESS = "apk_download_is_in_process";
    private final String PREF_TYPEOFVEHICLE = "type_of_vehicle";
    private final String PREF_VEHICLETYPE = "vehicle_type";
    private final String CALL_IT_EXECUTIVE = "call_it_executive";
    private final String PREF_ROUTENAME = "route_name";
    private final String PREF_KEY_AUTH_TOKEN = "auth_token";
    private final String PREF_KEY_SERVICE_CENTER = "service_center";
    private final String PREF_KEY_NAME = "name";
    private final String PREF_KEY_PSTN = "pstn_format";
    private final String PREF_KEY_DESIGNATION = "designation";
    private final String PREF_KEY_MOBILE = "mobile";
    private final String PREF_SERVER_TIME = "server_time";
    private final String CUTOFF_ACTION_VALUE = "value";
    private final String CALCULATED_DISTANCE = "calculated";
    private final String CUTOFF_ALERT_TIME = "alert_time";
    private final String PREF_KEY_VODA_ORDER = "order_no";
    private final String PREF_KEY_LOCTAION_TYPE = "location_type";
    private final String PREF_KEY_LOCATION_CODE = "location_code";
    private final String PREF_KEY_CODE = "code";
    private final String PREF_KEY_IS_USER_VALIDATED = "is_user_validated";
    private final String PREF_KEY_IS_CHILD = "is_child";
    private final String PREF_KEY_IS_CHILD_AVIALABLE = "is_child_available";

    private final String PREF_KEY_IS_PARENT = "is_parent";
    private final String PREF_KEY_PHOTO_URL = "photo_url";
    private final String PREF_KEY_USER_LOGGED_IN_MODE = "logged_in_mode";
    private final String PREF_KEY_USER_PINCODE = "user_pincode";
    private final String PREF_KEY_TRIP_ID = "trip_id";
    private final String METER_READING_START_TRIP = "start_trip_meter_reading";
    private final String METER_READING_STOP_TRIP = "stop_trip_meter_reading";
    private final String CURRENT_LATITUDE = "current_latitude";
    private final String CURRENT_LONGITUDE = "current_longitude";
    private final String VEHICLE_NO = "vehicle_no";
    private final String DC_LATITUDE = "dc_latitude";
    private final String DC_LONGITUDE = "dc_longitude";
    private final String DASHBOARD_BANNER = "dhashboard_banner";
    private final String LIVE_TRACKING = "live_Tracking";
    private final String AWB_COUNT = "awb_count";
    private final String VEHICLE_DETAIL = "vehicle_detail";
    private final String RECCI_QUESTION = "recci_question";
    private final String TRIPID = "tripid";
    private final String ROUTEID = "routeid";
    private final String LASTSYNCTIME = "last_sync_time";
    private final String IS_ECOM = "isecom";
    private SharedPreferences mPrefs;
 /*   private SharedPreferences mPref;*/
    private final String api_url = "api_url";
    private final String departure = "departure";
    private final String isLogout = "isLogout";
    private final String selfvehicledepart = "selfvehicledepart";
    private final String asparentorchild = "asparentorchild";
    private final String selfvehicletype = "selfvehicletype";
    private final String LIVETRACKING = "liveTracking";
    private final String ERMSYNC = "ermSync";
    private final String ENABLECALLING = "enableCalling";


    @Inject
    public PreferenceHelper(Context context, @AppModule.Pref1 String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public PreferenceHelper() {
        super();
    }


    @Override
    public String getAuthToken() {
        return mPrefs.getString(PREF_KEY_AUTH_TOKEN, null);
    }

    @Override
    public void setAuthToken(String authToken) {
        mPrefs.edit().putString(PREF_KEY_AUTH_TOKEN, authToken).apply();

    }

    @Override
    public void isChild(boolean is_child) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_CHILD, is_child).apply();

    }



    @Override
    public boolean getChild() {
        return mPrefs.getBoolean(PREF_KEY_IS_CHILD, false);
    }
    @Override
    public void isParent(boolean isParent) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_PARENT, isParent).apply();
    }









    @Override
    public boolean getParent() {
        return mPrefs.getBoolean(PREF_KEY_IS_PARENT, false);
    }

    @Override
    public String getServiceCenter() {
        return mPrefs.getString(PREF_KEY_SERVICE_CENTER, null);
    }

    @Override
    public void setServiceCenter(String serviceCenter) {
        mPrefs.edit().putString(PREF_KEY_SERVICE_CENTER, serviceCenter).apply();

    }

    @Override
    public String getLocationCode() {
        return mPrefs.getString(PREF_KEY_LOCATION_CODE, null);
    }

    @Override
    public void setLocationCode(String locationCode) {
        mPrefs.edit().putString(PREF_KEY_LOCATION_CODE, locationCode).apply();

    }

    @Override
    public String getDesignation() {
        return mPrefs.getString(PREF_KEY_DESIGNATION, null);
    }

    @Override
    public void setDesignation(String designation) {
        mPrefs.edit().putString(PREF_KEY_DESIGNATION, designation).apply();

    }

    @Override
    public double getCurrentLatitude() {
        return Double.parseDouble(mPrefs.getString(CURRENT_LATITUDE, "0.0"));
    }

    @Override
    public double getCurrentLongitude() {
        return Double.parseDouble(mPrefs.getString(CURRENT_LONGITUDE, "0.0"));
    }

    @Override
    public void setCurrentLatitude(String latitude) {
        mPrefs.edit().putString(CURRENT_LATITUDE, latitude).apply();
    }

    @Override
    public void setCurrentLongitude(String longitude) {
        mPrefs.edit().putString(CURRENT_LONGITUDE, longitude).apply();
    }

    @Override
    public String getPstnFormat() {
        return mPrefs.getString(PREF_KEY_PSTN, null);
    }

    @Override
    public void setPstnFormat(String format) {
        mPrefs.edit().putString(PREF_KEY_PSTN, format).apply();
    }

    @Override
    public List<DashboardBanner> getDashBoardBanner() {
        Type listType = new TypeToken<List<DashboardBanner>>() {
        }.getType();
        return new Gson().fromJson(mPrefs.getString(DASHBOARD_BANNER, ""), listType);
    }

    @Override
    public void setDashBoardBanner(String dashBoardBanner) {
        mPrefs.edit().putString(DASHBOARD_BANNER, dashBoardBanner).apply();
    }

    @Override
    public int getTripID() {
        return mPrefs.getInt(TRIPID, -1);
    }

    @Override
    public void setTripID(int tripID) {
        mPrefs.edit().putInt(TRIPID, tripID).apply();
    }

    @Override
    public void setLiveTrackingId(String live_tracking_id) {
        mPrefs.edit().putString(LIVE_TRACKING, live_tracking_id).apply();
    }

    @Override
    public String getLiveTrackingId() {
        return mPrefs.getString(LIVE_TRACKING, "");
    }

    @Override
    public int getRouteID() {
        return mPrefs.getInt(ROUTEID, -1);
    }

    @Override
    public void setRouteID(int routeID) {
        mPrefs.edit().putInt(ROUTEID, routeID).apply();
    }

    @Override
    public void setCutOffTime(String time) {
        mPrefs.edit().putString(ROUTE_CUTOFFTIME, time).apply();
    }

    @Override
    public String getCutOffTime() {
        return mPrefs.getString(ROUTE_CUTOFFTIME, "");
    }

    @Override
    public long getLastSyncTime() {
        return mPrefs.getLong(LASTSYNCTIME, -1);
    }

    @Override
    public void setLastSyncTime(long lastSyncTime) {
        mPrefs.edit().putLong(LASTSYNCTIME, lastSyncTime).apply();

    }

    @Override
    public boolean is_Ecom_Vehicle() {
        return mPrefs.getBoolean(IS_ECOM, false);
    }

    @Override
    public void setecom_vehicle(boolean is_com) {
        mPrefs.edit().putBoolean(IS_ECOM, is_com).apply();
    }

    @Override
    public String getSOSNumbers() {
        return mPrefs.getString(SOS_NUMBERS, "");
    }

    @Override
    public String getSOSSMSTemplate() {
        return mPrefs.getString(SOS_TEMPLATE, "");
    }

    @Override
    public String getName() {
        return mPrefs.getString(PREF_KEY_NAME, null);
    }

    @Override
    public void setName(String name) {
        mPrefs.edit().putString(PREF_KEY_NAME, name).apply();

    }

    @Override
    public String getCode() {
        return mPrefs.getString(PREF_KEY_CODE, null);
    }

    @Override
    public void setCode(String code) {
        mPrefs.edit().putString(PREF_KEY_CODE, code).apply();

    }

    /**
     * @return Point to note:
     * 1. Never reset IT executive number its comes at the time of login and will never clean
     */
    @Override
    public boolean clearPrefrence() {
        mPrefs.edit().clear().commit();
        return true;
    }

    @Override
    public void setLocationType(Long locationType) {
        mPrefs.edit().putLong(PREF_KEY_LOCTAION_TYPE, locationType).apply();

    }

    @Override
    public void setIsUserValided(Boolean isUserValided) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_USER_VALIDATED, isUserValided).apply();

    }

    @Override
    public void setPhotoUrl(String photoUrl) {
        mPrefs.edit().putString(PREF_KEY_PHOTO_URL, photoUrl).apply();

    }

    @Override
    public void setAuthPinCode(String authPinCode) {
        mPrefs.edit().putString(PREF_KEY_USER_PINCODE, authPinCode).apply();

    }

    @Override
    public List<LoginResponse.StartRouteDetails> getRouteDetail() {
        Type listType = new TypeToken<List<LoginResponse.StartRouteDetails>>() {
        }.getType();
        return new Gson().fromJson(mPrefs.getString(VEHICLE_DETAIL, ""), listType);
    }

    @Override
    public void setRouteDetail(String routeDetails) {
        mPrefs.edit().putString(VEHICLE_DETAIL, routeDetails).apply();
    }

    /* @Override
     public long getStartTripMeterReading() {
         return mPrefs.getLong(METER_READING_START_TRIP, 0L);
     }
 */
    @Override
    public void setStopTripMeterReading(long reading) {
        mPrefs.edit().putLong(METER_READING_STOP_TRIP, reading).apply();
    }

    @Override
    public void setVodaOrderNo(String orderNo) {
        mPrefs.edit().putString(PREF_KEY_VODA_ORDER, orderNo).apply();

    }

    @Override
    public void setRouteName(String routeName) {
        mPrefs.edit().putString(PREF_ROUTENAME, routeName).apply();

    }

    @Override
    public String getVehicleNo() {
        return mPrefs.getString(VEHICLE_NO, "");
    }

    @Override
    public void setVehicleNo(String vehicleNo) {
        System.out.println("vehicleNo " + vehicleNo);
        mPrefs.edit().putString(VEHICLE_NO, vehicleNo).apply();
    }

    @Override
    public void setSelfVehicleType(String type) {
        mPrefs.edit().putString(selfvehicletype, type).apply();
    }

    @Override
    public String getSelfVehicleType() {
        return mPrefs.getString(selfvehicletype, "");
    }

    @Override
    public void setDepartSelfVehicle(boolean departSelfVehicle) {
        mPrefs.edit().putBoolean(selfvehicledepart, departSelfVehicle).apply();
    }

    @Override
    public boolean getDepartSelfVehicle() {
        return mPrefs.getBoolean(selfvehicledepart, false);
    }

    @Override
    public String getAwbCount() {
        return mPrefs.getString(AWB_COUNT, "");
    }

    @Override
    public void setAwbCount(String awbCount) {
        mPrefs.edit().putString(AWB_COUNT, awbCount).apply();
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE, LOGGED_IN_MODE_LOGGED_OUT.getType());
    }

    @Override
    public void setCurrentUserLoggedInMode(IDataManager.LoggedInMode currentUserLoggedInMode) {

    }

    @Override
    public JsonObject getURL() {
        return new JsonParser().parse(mPrefs.getString(api_url, "")).getAsJsonObject();
    }

    @Override
    public void setURL(String urllist) {
        mPrefs.edit().putString(api_url, urllist).apply();
    }

    @Override
    public void setDeparted(boolean depart) {
        mPrefs.edit().putBoolean(departure, depart).apply();
    }

    @Override
    public boolean isDepart() {
        return mPrefs.getBoolean(departure, false);
    }

    @Override
    public boolean isLogout() {
        return mPrefs.getBoolean(isLogout, false);
    }

    @Override
    public void setLogout(boolean logout) {
        mPrefs.edit().putBoolean(isLogout, logout).apply();
    }

    @Override
    public void set_cutOffAlertTime(String alertTime) {
        mPrefs.edit().putString(CUTOFF_ALERT_TIME, alertTime).apply();
    }

    @Override
    public void set_cutOffActionValue(String actionValue) {
        mPrefs.edit().putString(CUTOFF_ACTION_VALUE, actionValue).apply();
    }

    @Override
    public void set_live_tracking(String config_value) {
        mPrefs.edit().putString(LIVETRACKING, config_value).apply();
    }

    @Override
    public String get_live_Tracking() {
        return mPrefs.getString(LIVETRACKING, null);
    }

    @Override
    public void set_erm_sync(String config_value) {
        mPrefs.edit().putString(ERMSYNC, config_value).apply();
    }

    @Override
    public String get_erm_sync() {
        return mPrefs.getString(ERMSYNC, null);
    }

    @Override
    public void set_enable_calling(String config_value) {
        mPrefs.edit().putString(ENABLECALLING, config_value).apply();
    }

    @Override
    public String get_enable_calling() {
        return mPrefs.getString(ENABLECALLING, null);
    }

    @Override
    public String get_cutOffAlertTime() {
        return mPrefs.getString(CUTOFF_ALERT_TIME, null);
    }

    @Override
    public String get_cutOffActionValue() {
        return mPrefs.getString(CUTOFF_ACTION_VALUE, null);
    }

    @Override
    public void setLiveTrackingCalculatedDistance(float distance) {
        mPrefs.edit().putFloat(CALCULATED_DISTANCE, distance).apply();
    }

    @Override
    public float getLiveTrackingCalculatedDistance() {
        return mPrefs.getFloat(CALCULATED_DISTANCE, 0);
    }

    @Override
    public void setLiveTrackingCount(int count) {

    }

    @Override
    public long getDistance() {
        return 0;
    }

    @Override
    public void setDistance(long distance) {

    }

    @Override
    public void setLiveTrackingCalculatedDistanceWithSpeed(float trackingCalculatedDistanceWithSpeed) {

    }

    @Override
    public int getLiveTrackingMINSpeed() {
        return 0;
    }

    @Override
    public void setLiveTrackingMINSpeed(int minSpeed) {

    }

    @Override
    public int getLiveTrackingSpeed() {
        return 0;
    }

    @Override
    public void setLiveTrackingSpeed(int maxSpeed) {

    }

    @Override
    public float getLiveTrackingCalculatedDistanceWithSpeed() {
        return 0;
    }

    @Override
    public void asParentOrChild(boolean child) {
        mPrefs.edit().putBoolean(asparentorchild, child).apply();
    }

    @Override
    public boolean getAsParentChild() {
        return mPrefs.getBoolean(asparentorchild, false);
    }

    @Override
    public String getMobile() {
        return mPrefs.getString(PREF_KEY_MOBILE, null);
    }

    @Override
    public void setMobile(String mobile) {
        mPrefs.edit().putString(PREF_KEY_MOBILE, mobile).apply();

    }

    @Override
    public void setServerTime(String serverTime) {
        mPrefs.edit().putString(PREF_SERVER_TIME, serverTime).apply();
    }

    @Override
    public String getServerTime() {
        return mPrefs.getString(PREF_SERVER_TIME, null);
    }

    @Override
    public void updateUserLoggedInState(IDataManager.LoggedInMode loggedInMode) {
        mPrefs.edit().putInt(PREF_KEY_USER_LOGGED_IN_MODE, loggedInMode.getType()).apply();

    }

    @Override
    public String getBottomText() {
        return mPrefs.getString(BOTTOM_TEXT, null);
    }

    @Override
    public String getCallITExecutiveNo() {
        return mPrefs.getString(CALL_IT_EXECUTIVE, "02262820481");
    }

    @Override
    public void setCallITExecutiveNo(String number) {
        mPrefs.edit().putString(CALL_IT_EXECUTIVE, number).apply();

    }

    @Override
    public void setDownloadAPKIsInProcess(long b) {
        mPrefs.edit().putLong(APK_DOWNLOAD_IS_IN_PROCESS, b).apply();
        return;
    }

    @Override
    public long isDownloadAPKIsInProcess() {
        return mPrefs.getLong(APK_DOWNLOAD_IS_IN_PROCESS, -1);

    }

    @Override
    public void updateDCDetails(LoginResponse.scGeoLocation scGeoLocation) {
        mPrefs.edit().putString(DC_LATITUDE, String.valueOf(scGeoLocation.getLocationLat())).apply();
        mPrefs.edit().putString(DC_LONGITUDE, String.valueOf(scGeoLocation.getLocationLong())).apply();

    }


    @Override
    public void set_pickup_geofencing_mode(String mode) {
        mPrefs.edit().putString(PICKUP_LOCATION_GEOFENCING_MODE, mode).apply();
    }

    @Override
    public String get_pickup_geofencing_mode() {
        return mPrefs.getString(PICKUP_LOCATION_GEOFENCING_MODE, "");
    }


    @Override
    public void set_pickup_geofencing_radius(String radius) {
        mPrefs.edit().putString(PICKUP_LOCATION_GEOFENCING_RADIUS, radius).apply();

    }

    @Override
    public String get_pickup_geofencing_radius() {
        return mPrefs.getString(PICKUP_LOCATION_GEOFENCING_RADIUS, "100");
    }

    @Override
    public void set_sruti_enable_otp_for_zero_pickup(String status) {
        mPrefs.edit().putString(SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP, status).apply();

    }

    @Override
    public String get_sruti_enable_otp_for_zero_pickup() {
        return mPrefs.getString(SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP, "false");
    }


    public void set_sruti_enable_delink_for_wh(String status) {
        mPrefs.edit().putString(SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH, status).apply();
    }

    public String get_sruti_enable_delink_for_wh() {
        return mPrefs.getString(SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH, "false");
    }

    public void set_sruti_allow_multispace(String status) {
        mPrefs.edit().putString(SRUTI_ALLOW_MULTI_SPACE_APPS, status).apply();
    }

    public String get_multiSpace_allow() {
        return mPrefs.getString(SRUTI_ALLOW_MULTI_SPACE_APPS, "false");
    }


    @Override
    public ArrayList<String> getListOfMultiSpace() {



      /*  // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<CourseModal>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        courseModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (courseModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            courseModalArrayList = new ArrayList<>();
        }
    }*/

        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(mPrefs.getString(MULTISPACE_APPS, ""), listType);
    }

    @Override
    public void setListOfMultiSpace(List<String> srutiMultiSpaceAppList) {
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(srutiMultiSpaceAppList);

        mPrefs.edit().putString(MULTISPACE_APPS, json).apply();
    }

    public void setMultiSpaceApps(String configValue) {
        mPrefs.edit().putString(MULTISPACE_APPS, configValue).apply();
    }

    public String getMultiSpaceApps() {
        return mPrefs.getString(MULTISPACE_APPS, "");
    }


}

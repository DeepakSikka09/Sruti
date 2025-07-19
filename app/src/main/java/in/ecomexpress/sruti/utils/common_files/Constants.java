package in.ecomexpress.sruti.utils.common_files;

import android.Manifest;
import android.content.Intent;

import in.ecomexpress.sruti.BuildConfig;

public class Constants {
    public static final String ENC_DEC_KEY = ".EcomExpress";
    public static final String ZEBRA = "ZEBRA TECHNOLOGIES:MC36";
    //Shipment level
//    public static final String REMAINING ="EXS_00_00" ;
    public static final String PENDING = "EXS_10_10";
    public static final String PICKED = "EXS_20_00";
    public static final String FAILED = "EXS_10_20";
    public static final String ADVANCE_STATUS = "004";
    public static final String RTO_STATUS_1 = "EXS_110_00";
    public static final String RTO_STATUS_2 = "EXS_110_10";
    public static final int ADVANCE_SETTING = 1;
    //Manifest level
    public static final int COMMIT_PENDING = 0;
    public static final int COMMIT_PICKED = 1;
    public static final int COMMIT_FAILED = 2;
    public static final int COMMIT_SERVER_SYNC = 3;
    public static final int SELF_DROP = 4;
    public static final int SHIPMENT_STATUS = 0;
    public static final int SHIPMENT_SYNCED_STATUS = 1;
    public static final String SRUTI_ALLOW_MULTI_SPACE_APPS = "SRUTI_ALLOW_MULTI_SPACE_APPS";
    public static final String BP_ID = "BP_ID";
    public static final String ACTION_START_WORK ="START_WORK" ;
    public static final String ACTION_STOP_WORK ="STOP_WORK" ;
    public static String EcomExpress = "EcomExpress.nomedia";
    public static final String VEHICLE_REGEX = "([A-Z]{2}\\d{2}[A-Z]{3}\\d{4})|([A-Z]{2}\\d{2}[A-Z]{2}\\d{4})|([A-Z]{2}\\d{2}[A-Z]{1}\\d{4})|([A-Z]{2}\\d{6})|([A-Z]{3}\\d{4})|([A-Z]{2}\\d{3}[A-Z]{1}\\d{4})|([A-Z]{2}\\d{1}[A-Z]{2}\\d{4})|[A-Z]{2}\\d{1}[A-Z]{3}\\d{4}";
    public static final String REGEX = "^[0-9]{9,12}$";
    public static final String pstn_pin = "@@PIN@@";
    public static final String pstn_awb = "@@AWB@@";
    public static String OTP_DELIMITER = "OTP";
    public static final String DB_NAME = "sruti_database.db";
    public static final int DB_VERSION = 25; //updated on 35 version
    public static final String ERROR_404 = "Server Down. Please try again later.";
    public static final String PREF_NAME = "sruti_preference";
    public static final String POP_PREF_NAME = "sruti_pop_preference";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final Integer VERSION_CODE = BuildConfig.VERSION_CODE;
    public static final String SERVER_URL = BuildConfig.BASE_URL;
    public static boolean SERVICERUNNING = false;
    public static final String _95W_IDATA = "ALPS:ANDROID";
    public static final String _FREEDOM = "ALPS:FREEDOM ABB-100-NIR";
    public static final String _SEUIC_CH = "AUTOID:PDT-900";
    public static final String _SEUIC2_CH = "AUTOID:PDT-6LP";
    public static final String _ZEBRA = "ZEBRA TECHNOLOGIES:MC36";
    public static final String _LUNATE_IDATA = "ALPS:GIONEELY72_CWET_KK";
    public static final String _NEWLAND = "NEWLAND:MT65";
    public static final String _NEW_NEWLAND = "DROI:NLS-MT90";
    public static final String _NEWLAND_T90 = "NEWLAND:NLS-MT90";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    public static final String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE};

    public static final String MANIFEST_RECCE = "Recce";
    public static final String MANIFEST_SELLER = "Seller";
    public static final String MANIFEST_WAREHOUSE = "Warehouse";
    public static final String MANIFEST_RECCE_SELLER = "Recce Seller";
    public static final String MANIFEST_RECCE_WAREHOUSE = "Recce Warehouse";
    public static final String COMMITPENDING = "Pending";
    public static final String COMMITPICKED = "Picked";
    public static final String COMMITFAILED = "Failed";
    public static final String MANIFEST_TYPE = "Manifest Type";
    public static final String MANIFEST_STATUS = "Manifest Status";
    public static final String MANIFEST_TYPE_RECCE = "R";
    public static final String MANIFEST_TYPE_PICKUP_RECCE = "PR";
    public static final String MANIFEST_TYPE_PICKUP = "P";
    public static final String MANIFEST_TYPE_SELLER = "Seller";
    public static final String MANIFEST_TYPE_WAREHOUSE = "Warehouse";
    public static final String FIELD_RETUN_ALERT_TIME = "FIELD_RETUN_ALERT_TIME";
    public static final String SCAN_AFTER_FIELD_RETURN_CUTOFF = "SCAN_AFTER_FIELD_RETURN_CUTOFF";
    public static final String SRUTI_ENABLE_LIVE_TRACKING = "SRUTI_ENABLE_LIVE_TRACKING";
    public static final String SRUTI_ENABLE_ERM_SYNC = "SRUTI_ENABLE_ERM_SYNC";
    public static final String SRUTI_ENABLE_CALLING="SRUTI_ENABLE_CALLING";
    public static final String CUTT_OFF_VALUE = "cut_off_value";
    public static String IS_ALERT_COMPELETED = "no";
    public static String DISABLE_SCAN = "false";
    public static final String SHIPMENT_WISE_REASON_CODE = "SHIPMENT";
    public static final String MANIFEST_WISE_REASON_CODE = "MANIFEST";
    public static final String QR_WISE_REASON_CODE = "QR";
    public static final String OTP_WISE_REASON_CODE = "OTP";
    public static final String ALL_REASON_CODE = "ALL";
    public static final String BRANDED_PACKAGE_ID_MISMATCH_INCORRECT = "BRANDED_PACKAGE_ID_MISMATCH_INCORRECT";
    public static final String SELF = "self";
    public static int LIVE_TRACKING_ACCURACY = 60;
    public static long LIVE_TRACKING_INTERVAL = 5000;
    public static float LIVE_TRACKING_DISPLACEMENT = 20.0f;
    public static float LIVE_TRACKING_DISPLACEMEN_COUNT = 20;
    public static String PICKUP_LOCATION_GEOFENCING_MODE="SRUTI_PICKUP_LOCATION_GEOFENCING_MODE";
    public static String PICKUP_LOCATION_GEOFENCING_RADIUS="SRUTI_PICKUP_LOCATION_GEOFENCING_RADIUS";
    public static String SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP="SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP";
    public static String CURRENT_LATITUDE = "0.0";
    public static String CURRENT_LONGITUDE = "0.0";

    public static String SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH="SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH";
    public static  String MULTISPACE_APPS = "MULTISPACE_APPS";
    public static final String DISTANCE_API_KEY = "AIzaSyBDXuU3HgiGUZg8CQt5HIT-TtbEcfxytlQ";

    public  static  String apiKey = DISTANCE_API_KEY;

    public static String LIVE_TRACKING_URL = "https://otc.ecomexpress.in/location/sruti_send_location/";  // prod configurations
    // public static String LIVE_TRACKING_URL = "https://test.ecomexpress.in:8030/location/sruti_send_location/";   // stagging configurations

    public static  String ESPER_TOKEN="UgOF8lq0PgkHHrGPy2oM7N3SObPAUZ";
    public static  String ESPER_ENTERPRISE_ID="95beaedc-10f0-43fc-b0e7-d5115682482c";

    public static final String POP_PENDING = "PENDING";
    public static final String POP_SUCCESS= "SUCCESS";
    public static final String POP_INPROCESS= "InProcess";

    public  static  String shield_Id = BuildConfig.SHIELD_ID;
    public  static  String Shield_Key = BuildConfig.SHIELD_KEY;
}

package in.ecomexpress.sruti.model.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    private SResponse sResponse;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public SResponse getSResponse() {
        return sResponse;
    }

    @JsonProperty("response")
    public void setResponse(SResponse response) {
        this.sResponse = response;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SResponse {
        @JsonProperty("emp_code")
        private String code;

        @JsonProperty("status-code")
        private long statusCode;

        @JsonProperty("api_urls")
        private APIUrls apiUrls;

        @JsonProperty("sc_geo_location")
        private scGeoLocation scGeoLocation;

        @JsonProperty("emp_name")
        private String name;

        @JsonProperty("mobile")
        private String mobile;

        @JsonProperty("flags")
        private LoginFlags flags;

        @JsonProperty("description")
        private String description;

        @JsonProperty("start_route_details")
        private ArrayList<StartRouteDetails> start_route_details;

        @JsonProperty("service_center_code")
        private String serviceCenter;

        @JsonProperty("server_time")
        private String serverTime;

        @JsonProperty("apk_update_response")
        private APKUpdateResponse apkUpdateResponse = new APKUpdateResponse();

        @JsonProperty("designation")
        private String designation;

        @JsonProperty("auth_token")
        private String authToken;


        public int getRoute_id() {
            return route_id;
        }

        public void setRoute_id(int route_id) {
            this.route_id = route_id;
        }

        private int route_id;


        @JsonProperty("sc_geo_location")
        public scGeoLocation getScGeoLocation() {
            return scGeoLocation;
        }

        @JsonProperty("sc_geo_location")
        public void setScGeoLocation(scGeoLocation scGeoLocation) {
            this.scGeoLocation = scGeoLocation;
        }

        @JsonProperty("api_urls")
        public APIUrls getApiUrls() {
            return apiUrls;
        }

        @JsonProperty("api_urls")
        public void setApiUrls(APIUrls apiUrls) {
            this.apiUrls = apiUrls;
        }

        @JsonProperty("status-code")
        public long getStatusCode() {
            return statusCode;
        }

        @JsonProperty("status-code")
        public void setStatusCode(long statusCode) {
            this.statusCode = statusCode;
        }

        @JsonProperty("flags")
        public LoginFlags getFlags() {
            return flags;
        }

        @JsonProperty("flags")
        public void setFlags(LoginFlags flags) {
            this.flags = flags;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("auth_token")
        public String getAuthToken() {
            return authToken;
        }

        @JsonProperty("auth_token")
        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        @JsonProperty("service_center_code")
        public String getServiceCenter() {
            return serviceCenter;
        }

        @JsonProperty("service_center_code")
        public void setServiceCenter(String serviceCenter) {
            this.serviceCenter = serviceCenter;
        }

        @JsonProperty("server_time")
        public String getServerTime() {
            return serverTime;
        }

        @JsonProperty("server_time")
        public void setServerTime(String serverTime) {
            this.serverTime = serverTime;
        }

        @JsonProperty("emp_name")
        public String getName() {
            return name;
        }

        @JsonProperty("emp_name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("designation")
        public String getDesignation() {
            return designation;
        }

        @JsonProperty("designation")
        public void setDesignation(String designation) {
            this.designation = designation;
        }

        @JsonProperty("mobile")
        public String getMobile() {
            return mobile;
        }

        @JsonProperty("mobile")
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        @JsonProperty("emp_code")
        public String getCode() {
            return code;
        }

        @JsonProperty("emp_code")
        public void setCode(String code) {
            this.code = code;
        }


        @JsonProperty("start_route_details")
        public ArrayList<StartRouteDetails> getStart_route_details() {
            return start_route_details;
        }

        @JsonProperty("start_route_details")
        public void setStart_route_details(ArrayList<StartRouteDetails> start_route_details) {
            this.start_route_details = start_route_details;
        }

        @JsonProperty("apk_update_response")
        public APKUpdateResponse getApkUpdateResponse() {
            return apkUpdateResponse;
        }

        @JsonProperty("apk_update_response")
        public void setApkUpdateResponse(APKUpdateResponse apkUpdateResponse) {
            this.apkUpdateResponse = apkUpdateResponse;
        }


    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status=" + status +
                ", sResponse=" + sResponse +
                '}';
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class APKUpdateResponse {

        @JsonProperty("version_status")
        private int version_status;

        @JsonProperty("apk_version")
        private String apk_version;

        @JsonProperty("apk_url")
        private String apk_url;

        @JsonProperty("apk_version_message")
        private String apk_version_message;

        @JsonProperty("version_status")
        public int getVersion_status() {
            return version_status;
        }

        @JsonProperty("version_status")
        public void setVersion_status(int version_status) {
            this.version_status = version_status;
        }

        @JsonProperty("apk_version")
        public String getApk_version() {
            return apk_version;
        }

        @JsonProperty("apk_version")
        public void setApk_version(String apk_version) {
            this.apk_version = apk_version;
        }

        @JsonProperty("apk_url")
        public String getApk_url() {
            return apk_url;
        }

        @JsonProperty("apk_url")
        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
        }

        @JsonProperty("apk_version_message")
        public String getApk_version_message() {
            return apk_version_message;
        }

        @JsonProperty("apk_version_message")
        public void setApk_version_message(String apk_version_message) {
            this.apk_version_message = apk_version_message;
        }

        @Override
        public String toString() {
            return "version_status: " + version_status + ", apk_version: " + apk_version + ", apk_url: " + apk_url + ", apk_version_message: " + apk_version_message;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class APIUrls {

        @JsonProperty("base_url")
        private String baseUrl;

        @JsonProperty("base_url")
        public String getBaseUrl() {
            return baseUrl;
        }

        @JsonProperty("base_url")
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @JsonProperty("live_api_urls")
        public HashMap<String, String> liveAPKUrls;

        @JsonProperty("live_api_urls")
        public HashMap<String, String> getLive_api_url() {
            return liveAPKUrls;
        }

        @JsonProperty("live_api_urls")
        public void setLiveAPKUrls(HashMap<String, String> liveAPKUrls) {
            this.liveAPKUrls = liveAPKUrls;
        }


    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveAPKUrls {

        @JsonProperty("trip-reimbursement")
        private String trip_reimbursement;


        @JsonProperty("image-postMultipleImage")
        private String image_postMultipleImage;


        @JsonProperty("change-password")
        private String change_password;


        @JsonProperty("start-trip")
        private String start_trip;


        @JsonProperty("login")
        private String login;


        @JsonProperty("forgot-password")
        private String forgot_password;


        @JsonProperty("image-postImage")
        private String image_postImage;


        @JsonProperty("reverse_drs_commit")
        private String reverse_drs_commit;


        @JsonProperty("drs_list-otp-verify")
        private String drs_list_otp_verify;

        @JsonProperty("forgot-password-with-otp")
        private String forgot_password_with_otp;


        @JsonProperty("forward_drs_commit")
        private String forward_drs_commit;

        @JsonProperty("call-bridge")
        private String call_bridge;

        @JsonProperty("stop-trip")
        private String stop_trip;

        @JsonProperty("drs_list_processing")
        private String drs_list_processing;

        @JsonProperty("master_data_configuration")
        private String master_data_configuration;

        @JsonProperty("drs_list-otp-resend")
        private String drs_list_otp_resend;

        @JsonProperty("drs_list-otp-send")
        private String drs_list_otp_send;


        @JsonProperty("weblink_url")
        private String performanceUrl;

        @JsonProperty("weblink_url")
        public String getPerformanceUrl() {
            return performanceUrl;
        }

        @JsonProperty("weblink_url")
        public void setPerformanceUrl(String performanceUrl) {
            this.performanceUrl = performanceUrl;
        }

        @JsonProperty("trip-reimbursement")
        public String getTrip_reimbursement() {
            return trip_reimbursement;
        }

        @JsonProperty("trip-reimbursement")
        public void setTrip_reimbursement(String trip_reimbursement) {
            this.trip_reimbursement = trip_reimbursement;
        }

        @JsonProperty("image-postMultipleImage")
        public String getImage_postMultipleImage() {
            return image_postMultipleImage;
        }

        @JsonProperty("image-postMultipleImage")
        public void setImage_postMultipleImage(String image_postMultipleImage) {
            this.image_postMultipleImage = image_postMultipleImage;
        }

        @JsonProperty("change-password")
        public String getChange_password() {
            return change_password;
        }

        @JsonProperty("change-password")
        public void setChange_password(String change_password) {
            this.change_password = change_password;
        }

        @JsonProperty("start-trip")
        public String getStart_trip() {
            return start_trip;
        }

        @JsonProperty("start-trip")
        public void setStart_trip(String start_trip) {
            this.start_trip = start_trip;
        }

        @JsonProperty("login")
        public String getLogin() {
            return login;
        }

        @JsonProperty("login")
        public void setLogin(String login) {
            this.login = login;
        }

        @JsonProperty("forgot-password")
        public String getForgot_password() {
            return forgot_password;
        }

        @JsonProperty("forgot-password")
        public void setForgot_password(String forgot_password) {
            this.forgot_password = forgot_password;
        }

        @JsonProperty("image-postImage")
        public String getImage_postImage() {
            return image_postImage;
        }

        @JsonProperty("image-postImage")
        public void setImage_postImage(String image_postImage) {
            this.image_postImage = image_postImage;
        }

        @JsonProperty("reverse_drs_commit")
        public String getReverse_drs_commit() {
            return reverse_drs_commit;
        }

        @JsonProperty("reverse_drs_commit")
        public void setReverse_drs_commit(String reverse_drs_commit) {
            this.reverse_drs_commit = reverse_drs_commit;
        }

        @JsonProperty("drs_list-otp-verify")
        public String getDrs_list_otp_verify() {
            return drs_list_otp_verify;
        }

        @JsonProperty("drs_list-otp-verify")
        public void setDrs_list_otp_verify(String drs_list_otp_verify) {
            this.drs_list_otp_verify = drs_list_otp_verify;
        }

        @JsonProperty("forgot-password-with-otp")
        public String getForgot_password_with_otp() {
            return forgot_password_with_otp;
        }

        @JsonProperty("forgot-password-with-otp")
        public void setForgot_password_with_otp(String forgot_password_with_otp) {
            this.forgot_password_with_otp = forgot_password_with_otp;
        }

        @JsonProperty("forward_drs_commit")
        public String getForward_drs_commit() {
            return forward_drs_commit;
        }

        @JsonProperty("forward_drs_commit")
        public void setForward_drs_commit(String forward_drs_commit) {
            this.forward_drs_commit = forward_drs_commit;
        }

        @JsonProperty("call-bridge")
        public String getCall_bridge() {
            return call_bridge;
        }

        @JsonProperty("call-bridge")
        public void setCall_bridge(String call_bridge) {
            this.call_bridge = call_bridge;
        }

        @JsonProperty("stop-trip")
        public String getStop_trip() {
            return stop_trip;
        }

        @JsonProperty("stop-trip")
        public void setStop_trip(String stop_trip) {
            this.stop_trip = stop_trip;
        }

        @JsonProperty("drs_list_processing")
        public String getDrs_list_processing() {
            return drs_list_processing;
        }

        @JsonProperty("drs_list_processing")
        public void setDrs_list_processing(String drs_list_processing) {
            this.drs_list_processing = drs_list_processing;
        }

        @JsonProperty("master_data_configuration")
        public String getMaster_data_configuration() {
            return master_data_configuration;
        }

        @JsonProperty("master_data_configuration")
        public void setMaster_data_configuration(String master_data_configuration) {
            this.master_data_configuration = master_data_configuration;
        }

        @JsonProperty("drs_list-otp-resend")
        public String getDrs_list_otp_resend() {
            return drs_list_otp_resend;
        }

        @JsonProperty("drs_list-otp-resend")
        public void setDrs_list_otp_resend(String drs_list_otp_resend) {
            this.drs_list_otp_resend = drs_list_otp_resend;
        }

        @JsonProperty("drs_list-otp-send")
        public String getDrs_list_otp_send() {
            return drs_list_otp_send;
        }

        @JsonProperty("drs_list-otp-send")
        public void setDrs_list_otp_send(String drs_list_otp_send) {
            this.drs_list_otp_send = drs_list_otp_send;
        }


    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class scGeoLocation {

        @JsonProperty("location_lat")
        private double locationLat;
        @JsonProperty("location_long")
        private double locationLong;


        @JsonProperty("location_lat")
        public double getLocationLat() {
            return locationLat;
        }

        @JsonProperty("location_lat")
        public void setLocationLat(double locationLat) {
            this.locationLat = locationLat;
        }

        @JsonProperty("location_long")
        public double getLocationLong() {
            return locationLong;
        }

        @JsonProperty("location_long")
        public void setLocationLong(double locationLong) {
            this.locationLong = locationLong;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginFlags {
        @JsonProperty("reset_password")
        public Login_Reset_password getReset_password() {
            return reset_password;
        }

        @JsonProperty("reset_password")
        public void setReset_password(Login_Reset_password reset_password) {
            this.reset_password = reset_password;
        }

        @JsonProperty("reset_password")
        private Login_Reset_password reset_password;
        @JsonProperty("is_otp_required")
        private boolean is_otp_required;

        @JsonProperty("is_primary")
        public boolean isIs_parent() {
            return is_parent;
        }

        @JsonProperty("is_primary")
        public void setIs_parent(boolean is_parent) {
            this.is_parent = is_parent;
        }

        @JsonProperty("is_child")
        public boolean isIs_child() {
            return is_child;
        }


        @JsonProperty("is_child")
        public void setIs_child(boolean is_child) {
            this.is_child = is_child;
        }

        @JsonProperty("is_primary")
        private boolean is_parent;
        @JsonProperty("is_child")
        private boolean is_child;

        @JsonProperty("is_child_available")
        private boolean is_child_available;

        public boolean isIs_child_available() {
            return is_child_available;
        }

        public void setIs_child_available(boolean is_child_available) {
            this.is_child_available = is_child_available;
        }

        @JsonProperty("is_ecom_vehicle")
        private boolean is_ecom_vehicle;
        @JsonProperty("is_apk_update_required")
        private boolean is_apk_update_required;

        private  boolean is_departure;

        public boolean isIs_departure() {
            return is_departure;
        }

        public void setIs_departure(boolean is_departure) {
            this.is_departure = is_departure;
        }

        @JsonProperty("is_otp_required")
        public boolean getIs_otp_required() {
            return is_otp_required;
        }

        @JsonProperty("is_otp_required")
        public void setIs_otp_required(boolean is_otp_required) {
            this.is_otp_required = is_otp_required;
        }

        @JsonProperty("is_ecom_vehicle")
        public boolean getIs_ecom_vehicle() {
            return is_ecom_vehicle;
        }

        @JsonProperty("is_ecom_vehicle")
        public void setIs_ecom_vehicle(boolean is_ecom_vehicle) {
            this.is_ecom_vehicle = is_ecom_vehicle;
        }

        @JsonProperty("is_apk_update_required")
        public boolean getIs_apk_update_required() {
            return is_apk_update_required;
        }

        @JsonProperty("is_apk_update_required")
        public void setIs_apk_update_required(boolean is_apk_update_required) {
            this.is_apk_update_required = is_apk_update_required;
        }

        @Override
        public String toString() {
            return "LoginFlags [is_otp_required = " + is_otp_required + ", is_ecom_vehicle = " + is_ecom_vehicle + ", is_apk_update_required = " + is_apk_update_required + "]";
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Login_Reset_password {
            @JsonProperty("notify_password_reset_info")
            private String notify_password_reset_info;
            @JsonProperty("is_password_reset_required")
            private boolean is_password_reset_required;
            @JsonProperty("is_notify_password_reset_required")
            private boolean is_notify_password_reset_required;

            @JsonProperty("notify_password_reset_info")
            public String getNotify_password_reset_info() {
                return notify_password_reset_info;
            }

            @JsonProperty("notify_password_reset_info")
            public void setNotify_password_reset_info(String notify_password_reset_info) {
                this.notify_password_reset_info = notify_password_reset_info;
            }

            @JsonProperty("is_password_reset_required")
            public boolean getIs_password_reset_required() {
                return is_password_reset_required;
            }

            @JsonProperty("is_password_reset_required")
            public void setIs_password_reset_required(boolean is_password_reset_required) {
                this.is_password_reset_required = is_password_reset_required;
            }

            @JsonProperty("is_notify_password_reset_required")
            public boolean getIs_notify_password_reset_required() {
                return is_notify_password_reset_required;
            }

            @JsonProperty("is_notify_password_reset_required")
            public void setIs_notify_password_reset_required(boolean is_notify_password_reset_required) {
                this.is_notify_password_reset_required = is_notify_password_reset_required;
            }

            @Override
            public String toString() {
                return "Login_Reset_password [notify_password_reset_info = " + notify_password_reset_info + ", is_password_reset_required = " + is_password_reset_required + ", is_notify_password_reset_required = " + is_notify_password_reset_required + "]";
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StartRouteDetails {
        @JsonProperty("start_meter_reading")
        private long start_meter_reading;
        @JsonProperty("start_vehicle_type")
        private String start_vehicle_type;
        @JsonProperty("start_route_id")
        private int start_route_id;
        @JsonProperty("start_vehicle_number")
        private String start_vehicle_number;
        @JsonProperty("start_vehicle_owner")
        private String start_vehicle_owner;
        @JsonProperty("live_tracking_id")
        private String live_tracking_id;
        private int start_trip_id;
        private boolean departed;

        public String getLive_tracking_id() {
            return live_tracking_id;
        }

        public void setLive_tracking_id(String live_tracking_id) {
            this.live_tracking_id = live_tracking_id;
        }
        public int getStart_trip_id() {
            return start_trip_id;
        }

        public void setStart_trip_id(int start_trip_id) {
            this.start_trip_id = start_trip_id;
        }

        public boolean isDeparted() {
            return departed;
        }

        public void setDeparted(boolean departed) {
            this.departed = departed;
        }


        @JsonProperty("start_meter_reading")
        public long getStart_meter_reading() {
            return start_meter_reading;
        }

        @JsonProperty("start_meter_reading")
        public void setStart_meter_reading(long start_meter_reading) {
            this.start_meter_reading = start_meter_reading;
        }

        @JsonProperty("start_vehicle_type")
        public String getStart_vehicle_type() {
            return start_vehicle_type;
        }

        @JsonProperty("start_vehicle_type")
        public void setStart_vehicle_type(String start_vehicle_type) {
            this.start_vehicle_type = start_vehicle_type;
        }

        @JsonProperty("start_route_id")
        public int getStart_route_id() {
            return start_route_id;
        }

        @JsonProperty("start_route_id")
        public void setStart_route_id(int start_route_id) {
            this.start_route_id = start_route_id;
        }

        @JsonProperty("start_vehicle_number")
        public String getStart_vehicle_number() {
            return start_vehicle_number;
        }

        @JsonProperty("start_vehicle_number")
        public void setStart_vehicle_number(String start_vehicle_number) {
            this.start_vehicle_number = start_vehicle_number;
        }

        @JsonProperty("start_vehicle_owner")
        public String getStart_vehicle_owner() {
            return start_vehicle_owner;
        }

        @JsonProperty("start_vehicle_owner")
        public void setStart_vehicle_owner(String start_vehicle_owner) {
            this.start_vehicle_owner = start_vehicle_owner;
        }

        @Override
        public String toString() {
            return "StartRouteDetails [start_meter_reading = " + start_meter_reading + ", start_vehicle_type = " + start_vehicle_type + ", start_route_id = " + start_route_id + ", start_vehicle_number = " + start_vehicle_number + ", start_vehicle_owner = " + start_vehicle_owner + "]";
        }
    }
}


package in.ecomexpress.sruti.utils.common_files;

public interface GlobalConstant {
    interface DynamicAppUrl {
        String HANDOVER_URL = "handover";
        String LOGOUT_URL = "logout";
        String ATTENDANCE_URL = "attendance";
        String SOS_URL = "sos";
        String TRIP_REIMBURSEMENT_URL = "trip_reimbursement";
        String PERFORMANCE_URL = "performance";
        String LOGIN_RESEND_OTP = "resend-otp";
        String LOGIN_VERIFY_OTP_URL = "loginVerifyOTP";
        String CHANGE_PASSWORD = "change-password";
        String FORGOT_PASSWORD = "forgot-password";
        String FORGET_PASSWORD_WITH_OTP = "forgot-password-with-otp";
        String MASTER_DATA = "master-data";
        String MANIFEST = "manifest";
        String START_TRIP = "startTrip";
        String STOP_TRIP = "stopTrip";
        String IMAGESTARTTRIP = "imageStartTrip";
        String CHECK_AND_UPDATE_STATUS_OF_INSCAN_AWB = "check-and-update-status-of-inscan-awb";
        String FIRST_INSCAN = "first-inscan";
        String RTO_LOCK = "manifest-status";
        String UPDATE_BP = "sync-updated-bp-shipments";
        String RETURN_TIME = "update-field-return-time";
        String COMMIT_PACKET = "commit";
        String COMMIT_IMAGE_PACKET = "commit-image-processing";
        String CHILD_STATUS_COMMIT = "manifest-child-commit-status";
        String DEPART_VEHICLES = "depart-vehicles";
        String AUTH_TOKEN = "verify-auth-token";
        String EXTRA_VEHICLE = "fetch-vehicles-on-routes";
        String SKIP_OTP_REASON_LIST = "fetch-skip-otp-reason-codes";
        String FETCH_SELF_DROP_MANIFEST_STATUS = "fetch-self-drop-manifest-status";
        String SRUTI_SEND_LOCATION = "sruti-send-location";
        String SEND_PICKUP_OTP="send-pickup-otp";
        String VERIFY_PICKUP_OTP="verify-pickup-otp";
        String OTP_VERIFICATION_MANIFEST="otp-verification-manifest";
        String SEND_POP_SMS_TO_VENDOR="send-pop-sms-to-vendor";
   //     String TRAINING_UNIFY_DETAILS="trainings/unify/details";
        String TRAINING_UNIFY_DETAILS="trainings-unify-details";

    }

    interface ErrorCodes {
        int HTTP_EXCEPTION = 10101;
    }

    interface RemarksTypeConstants {
        String CONSIGNEE_NOT_PICKING_CALL = "Consignee Not Picking Call";
        String CONSIGNEE_REQUESTED_TO_CALL_LATER = "Consignee Requested to Call Later";
        String SAME_DAY_RESCHEDULE = "Same Day Reschedule";
        String CONSIGNEE_CALLED = "Consignee called";
        String NO_REMARKS = "No Remark";
    }

}

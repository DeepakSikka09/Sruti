package in.ecomexpress.sruti.ui.dashboard.starttrip;

import static in.ecomexpress.sruti.utils.common_files.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sruti.utils.common_files.Constants.apiKey;

import android.content.Context;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import in.ecomexpress.sruti.BuildConfig;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.starttrip.ImageUplaodResponse;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.starttrip.StartTripResponse;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Image;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class StartTripViewModel extends BaseViewModel {
    private MediatorLiveData<StartTripResponse> startTripresponse = new MediatorLiveData<StartTripResponse>();
    private MutableLiveData<List<LoginResponse.StartRouteDetails>> vehicle_detail = new MutableLiveData<>();
    private ArrayList<Start_Trip_Image> start_trip_images = new ArrayList<>();
    private MediatorLiveData<String> error_msg = new MediatorLiveData<>();
    private MediatorLiveData<ImageUplaodResponse> image_upload = new MediatorLiveData<>();

    public StartTripViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    @BindingAdapter("spinnerListner")
    public static void setSpinnerListner(Spinner spinner, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    public void setStart_trip_images(ArrayList<Start_Trip_Image> start_trip_images) {
        this.start_trip_images = start_trip_images;
    }

    public LiveData<ImageUplaodResponse> getImage_Response() {
        return image_upload;
    }

    public String empCode() {
        return getDataManager().getCode();
    }

    public MutableLiveData<String> getError_msg() {
        return error_msg;
    }

    LiveData<List<LoginResponse.StartRouteDetails>> getVehicle_detail() {
        vehicle_detail.setValue(getDataManager().getRouteDetail());
        return vehicle_detail;
    }

    boolean is_Ecom_Vehicle() {
        return getDataManager().is_Ecom_Vehicle();
    }

    MutableLiveData<StartTripResponse> getStartTripResponse() {
        return startTripresponse;
    }

    public boolean isParent() {
        return getDataManager().getParent();
    }

    void imageUploadRealTime(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        requestBody.put("trip_emp_code", RequestBody.create(MediaType.parse("text/plain"), getDataManager().getCode()));

        LiveData<ImageUplaodResponse> ob = getDataManager().uploadStartTripImage(imageFile, requestBody);

        image_upload.addSource(ob, imageUplaodResponse -> {
            image_upload.removeSource(ob);
            if (imageUplaodResponse != null) {
                image_upload.setValue(imageUplaodResponse);
            } else {
                ImageUplaodResponse imageUplaodResponse1 = new ImageUplaodResponse();
                imageUplaodResponse1.setStatus(false);
                image_upload.setValue(imageUplaodResponse1);
            }
        });
    }

    void submitData(StartTripRequest startTripRequest) {
        startTripRequest.setEmployee_code(getDataManager().getCode());
        int route_id = getDataManager().getRouteID();
        startTripRequest.setPickup_route_id(route_id);
        startTripRequest.setApp_version(Constants.VERSION_NAME);


        LiveData<StartTripResponse> ob = getDataManager().doStartTrip(getDataManager().getAuthToken(), startTripRequest);

        startTripresponse.addSource(ob, startResponse -> {
            startTripresponse.removeSource(ob);

            if (startResponse.getStatus()) {
                if (!getDataManager().is_Ecom_Vehicle()) {
                    getDataManager().setVehicleNo(startTripRequest.getStart_trip_multi_vehicle_data().get(0).getVehicle_number());
                    getDataManager().setSelfVehicleType(startTripRequest.getStart_trip_multi_vehicle_data().get(0).getType_of_vehicle());

                    if (startResponse.getResponse().getLive_tracking_id() != (null)) {
                        //&& startResponse.getResponse().getLive_tracking_id() != ("null") &&
                        //  !startResponse.getResponse().getLive_tracking_id().equalsIgnoreCase(""))
                        getDataManager().setLiveTrackingId(startResponse.getResponse().getLive_tracking_id());

                    }

                }
                getDataManager().setTripID(startResponse.getResponse().getTrip_id());
                startTripresponse.setValue(startResponse);
            } else {
                startTripresponse.setValue(startResponse);
            }

           /* if (startResponse != null) {
                if (!getDataManager().is_Ecom_Vehicle()) {
                    getDataManager().setVehicleNo(startTripRequest.getStart_trip_multi_vehicle_data().get(0).getVehicle_number());
                    getDataManager().setSelfVehicleType(startTripRequest.getStart_trip_multi_vehicle_data().get(0).getType_of_vehicle());
                }
                getDataManager().setTripID(startResponse.getResponse().getTrip_id());
                startTripresponse.setValue(startResponse);
            } else {
                StartTripResponse startTripResponse = new StartTripResponse();
                startTripResponse.setStatus(false);
                startTripResponse.setDescription("Response not proper");
                startTripresponse.setValue(startTripResponse);
            }*/
        });
    }

    public void validationStartTrip() {
        int i = 0;
        boolean isOk = true;
        String err_msg = "";
        for (Start_Trip_Image image : start_trip_images) {
            if (TextUtils.isEmpty(image.getVehicleno())) {
                isOk = false;
                err_msg = "Please enter vehicle no";
                break;
            } else if (image.getMeter_reading() == 0) {//TextUtils.isEmpty(Float.toString(
                isOk = false;
                err_msg = "Please enter meter reading";
                break;
            } else if (TextUtils.isEmpty(image.getImage_path())) {
                isOk = false;
                err_msg = "Please capture image of meter reading";
                break;
            }
            i++;
        }
        if (!isOk) {
            error_msg.setValue(err_msg);
        } else {
            error_msg.setValue("success");
        }
    }

    public void validationStartTripSelf() {
        int i = 0;
        boolean isOk = true;
        String err_msg = "";
        for (Start_Trip_Image image : start_trip_images) {
            if (TextUtils.isEmpty(image.getVehicleno())) {
                isOk = false;
                err_msg = "Please enter vehicle no";
                break;
            } else if (!verifyVehicleNumber(image.getVehicleno())) {
                isOk = false;
                err_msg = "Please enter correct vehicle no";
                break;
            } else if (image.getMeter_reading() == 0) {//TextUtils.isEmpty(Float.toString(
                isOk = false;
                err_msg = "Please enter meter reading";
                break;
            } else if (TextUtils.isEmpty(image.getImage_path())) {
                isOk = false;
                err_msg = "Please capture image of meter reading";
                break;
            }
            i++;
        }
        if (!isOk) {
            error_msg.setValue(err_msg);
        } else {
            error_msg.setValue("success");
        }
    }

    private boolean verifyVehicleNumber(String vehicleNumber) {
        boolean flag = false;
        if (vehicleNumber.toUpperCase(Locale.ENGLISH) != null) {
            Pattern pattern = Pattern.compile(Constants.VEHICLE_REGEX);
            flag = pattern.matcher(vehicleNumber.toUpperCase(Locale.ENGLISH)).matches();
        }
        return flag;
    }

    public void startLiveTracking(Context context) {
        try {
            // accuray from backend
            DashboardActivity.lt.startTrackingWithParameters(context, context.getString(R.string.app_name), Constants.VERSION_NAME, "FirstMile", getDataManager().getCode(), getDataManager().getLocationCode(), getDataManager().getSelfVehicleType(), getDataManager().getAuthToken(), getDataManager().getLiveTrackingId(), "start", 5, Constants.LIVE_TRACKING_URL, 100, 10, 99, apiKey, 0, 100);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

}

package in.ecomexpress.sruti.ui.dashboard.stoptrip;

import static in.ecomexpress.sruti.utils.common_files.Constants.DISTANCE_API_KEY;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import in.ecomexpress.geolocations.LocationBeans;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.starttrip.ImageUplaodResponse;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Image;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Multi_Vehicle;
import in.ecomexpress.sruti.model.stoptrip.StopTrip;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class StopTripViewModelMultiVehicle extends BaseViewModel {
    private MediatorLiveData<StopTrip> stopTripresponse = new MediatorLiveData<StopTrip>();
    private MutableLiveData<List<LoginResponse.StartRouteDetails>> vehicle_detail = new MutableLiveData<>();
    private ArrayList<Start_Trip_Image> start_trip_images = new ArrayList<>();
    private MediatorLiveData<String> error_msg = new MediatorLiveData<>();
    private MediatorLiveData<ImageUplaodResponse> image_upload = new MediatorLiveData<>();
    private ProgressDialog progressDialog, progressDialogwithspeed;
    private ArrayList<LocationBeans> arr_all_geoLocation = new ArrayList<>();
    private ArrayList<LocationBeans> arr_all_geoLocation_withspeed;
    Location start_location, end_location;
    float total_distance = 0;
    float distance=0;
    float total_distance_with_speed = 0;

    public StopTripViewModelMultiVehicle(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
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

    public MutableLiveData<String> getError_msg() {
        return error_msg;
    }


    LiveData<List<LoginResponse.StartRouteDetails>> getVehicle_detail() {
        vehicle_detail.setValue(getDataManager().getRouteDetail());
        return vehicle_detail;
    }

    MutableLiveData<StopTrip> getStopTripResponse() {
        return stopTripresponse;
    }

    boolean is_Ecom_Vehicle() {
        return getDataManager().is_Ecom_Vehicle();
    }

    public String empCode() {
        return getDataManager().getCode();
    }

    void imageUploadRealTime(MultipartBody.Part imageFile, Map<String, RequestBody> requestBody) {
        requestBody.put("trip_emp_code", RequestBody.create(MediaType.parse("text/plain"), getDataManager().getCode()));

        LiveData<ImageUplaodResponse> ob = getDataManager().uploadStartTripImage(imageFile, requestBody);

        image_upload.addSource(ob, imageUplaodResponse -> {
            image_upload.removeSource(ob);
            if (imageUplaodResponse != null) {
                image_upload.setValue(imageUplaodResponse);
            }
        });
    }


    void submitData(StopTripRequest stopTripRequest) {
        stopTripRequest.setEmployee_code(getDataManager().getCode());
        stopTripRequest.setVehicle_trip_id(getDataManager().getTripID());
        int route_id = getDataManager().getRouteID();
        stopTripRequest.setPickup_route_id(route_id);
       // System.out.println("stopTripRequest " + stopTripRequest.toString());


        LiveData<StopTrip> ob = getDataManager().doStopTrip(getDataManager().getAuthToken(), stopTripRequest);

        stopTripresponse.addSource(ob, stopResponse -> {
            stopTripresponse.removeSource(ob);
            if (stopResponse.getStatus()) {
                getDataManager().setTripID(-1);
                stopTripresponse.setValue(stopResponse);
            } else {
//               System.out.println("startResponse " + startResponse.getResponse().getErrors().get(0));
                stopTripresponse.setValue(stopResponse);
            }
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
            } else if (image.getMeter_reading() == 0) {
                isOk = false;
                err_msg = "Please enter meter reading";
                break;
            } else if (getDataManager().is_Ecom_Vehicle() && image.getVehicleno().equals(vehicle_detail.getValue().get(i).getStart_vehicle_number()) && image.getMeter_reading() <= vehicle_detail.getValue().get(i).getStart_meter_reading()) {
                isOk = false;
                err_msg = "Meter reading is less than start trip meter reading";
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
            } else if (image.getMeter_reading() == 0) {
                isOk = false;
                err_msg = "Please enter meter reading";
                break;
            } else if (getDataManager().is_Ecom_Vehicle() && image.getVehicleno().equals(vehicle_detail.getValue().get(i).getStart_vehicle_number()) && image.getMeter_reading() <= vehicle_detail.getValue().get(i).getStart_meter_reading()) {
                isOk = false;
                err_msg = "Meter reading is less than start trip meter reading";
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

    private boolean showToast(String msg) {
        error_msg.setValue(msg);
        return false;
    }


    void calculateDistance(Context context, StopTripAdapter start_trip_adapter, double wayLatitude, double wayLongitude, String vehicleOwner) {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("Calculating distance, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                //// main code start from here (table wise)
                try {
                    getDataManager().setLiveTrackingCalculatedDistance(0);

                    arr_all_geoLocation.clear();

                    arr_all_geoLocation.addAll(LocationTracker.getLatLng());

                    ArrayList<LocationBeans> latList = new ArrayList<>();
                     for (int i = 0; i < arr_all_geoLocation.size(); i++) {
                        try {
                            LocationBeans locationBeans = new LocationBeans();
                            locationBeans.setLatitude(arr_all_geoLocation.get(i).getLatitude());
                            locationBeans.setLongitude(arr_all_geoLocation.get(i).getLongitude());
                            locationBeans.setSpeed(arr_all_geoLocation.get(i).getSpeed());
                            latList.add(locationBeans);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("arr_all_geoLocatio", arr_all_geoLocation.size() + "");
                    progressDialog.setMax(latList.size());
                    getDataManager().setLiveTrackingCount(latList.size());
                    distance = 0;
                    total_distance=0;
                    total_distance_with_speed=0;
                    ArrayList<LocationBeans> locationBeans= new ArrayList<>(latList);
                    for (int i = 1; i < locationBeans.size(); i++) {
                        try {
                            start_location = new Location("");
                            start_location.setLatitude(locationBeans.get(i - 1).getLatitude());
                            start_location.setLongitude(locationBeans.get(i - 1).getLongitude());
                            //  start_time = arr_all_geoLocation.get(indexValue).getTimeStamp();
                            //indexValue = indexValue + 1;
                            end_location = new Location("");
                            end_location.setLatitude(locationBeans.get(i).getLatitude());
                            end_location.setLongitude(locationBeans.get(i).getLongitude());
                            distance = start_location.distanceTo(end_location);
                            Log.e("distance_check", distance + "");
                            // total_distance = mStopViewModel.getDataManager().getLiveTrackingCalculatedDistance();
                            // setDistance on DashboardViewModel
                           /* if (distance > 4000) {
                                try {
                                    LatLng lastlatLng = new LatLng(start_location.getLatitude(), start_location.getLongitude());
                                    LatLng firstlatLng = new LatLng(end_location.getLatitude(), end_location.getLongitude());
                                    GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
                                    DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(firstlatLng).optimizeWaypoints(true).destination(lastlatLng).awaitIgnoreError();
                                    String dis = (result.routes[0].legs[0].distance.humanReadable);
                                    //Log.d("direction Distance",dis);
                                    if (dis.endsWith("km")) {
                                        distance = Float.parseFloat(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
                                    } else {
                                        distance = Float.parseFloat(dis.replaceAll("[^\\.0123456789]", ""));
                                    }
                                    total_distance = total_distance + distance;
                                } catch (Exception e) {
                                    distance = start_location.distanceTo(end_location);
                                    total_distance = total_distance + distance;
                                }
                            } else {*/
                                total_distance = total_distance + distance;
                                Log.e("total_distance", total_distance + "");
                           // }

                        } catch (Exception e) {
                            e.getMessage();
                            continue;
                        }
                        publishProgress(i);
                    }
                    getDataManager().setLiveTrackingCalculatedDistance(total_distance);
                    Log.e("total_distance", total_distance + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressDialog.setProgress(values[0]);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("MissingPermission")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                DashboardActivity.lt.startTrackingWithParameters(context, context.getString(R.string.app_name), Constants.VERSION_NAME, "FirstMile", "", "", "", "", "", "stop", 5, Constants.LIVE_TRACKING_URL, 50, 60, 99, DISTANCE_API_KEY, 200, 100);


                ArrayList<Start_Trip_Image> data = start_trip_adapter.getValueDATA();
                StopTripRequest req = new StopTripRequest();
                ArrayList<Start_Trip_Multi_Vehicle> vehicleAll = new ArrayList<>();

                for (Start_Trip_Image dd : data) {
                    Start_Trip_Multi_Vehicle vehicle = new Start_Trip_Multi_Vehicle();
                    vehicle.setType_of_vehicle(dd.getVehicle_type());
                    vehicle.setVehicle_meter_reading(dd.getMeter_reading().toString());
                    vehicle.setVehicle_number(dd.getVehicleno());
                    vehicle.setImage_response(dd.getImage_response());
                    vehicleAll.add(vehicle);
                    System.out.println("getImage_path " + dd.getImage_path());
                }
                req.setRole("parent");
                req.setVehicle_owner_type(vehicleOwner);
                req.setImei(CommonUtils.getImei(context));
                req.setLive_tracking_id(getDataManager().getLiveTrackingId());
                req.setStop_lattitude(wayLatitude);
                req.setStop_longitude(wayLongitude);
                req.setStop_trip_multi_vehicle_data(vehicleAll);
                req.setMobile_km_otc(getDataManager().getLiveTrackingCalculatedDistance() / 1000);
                req.setOtc_enabled(Boolean.parseBoolean(getDataManager().get_live_Tracking()));
                req.setErm_enabled(Boolean.parseBoolean(getDataManager().get_erm_sync()));
                submitData(req);

                //   calculateDistanceWithSpeed(context);

            }
        }.execute();
    }

    private void calculateDistanceWithSpeed(Context context) {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialogwithspeed = new ProgressDialog(context);
                progressDialogwithspeed.setIndeterminate(false);
                // progressDialogwithspeed.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialogwithspeed.setMessage("Calculating distance, please wait...");
                progressDialogwithspeed.setCancelable(false);
                progressDialogwithspeed.show();
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                getDataManager().setLiveTrackingCalculatedDistanceWithSpeed(0);
                arr_all_geoLocation_withspeed.addAll(LocationTracker.getLatLng());
                ArrayList<LocationBeans> latList = new ArrayList<>();
                try {
                    for (int i = 0; i < arr_all_geoLocation_withspeed.size(); i++) {
                        if (arr_all_geoLocation_withspeed.get(i).getGps() != null && !arr_all_geoLocation_withspeed.get(i).getGps().equalsIgnoreCase("0.0")
                                && !arr_all_geoLocation.get(i).getGps().equalsIgnoreCase("null")) {
                            double speed = Double.parseDouble(arr_all_geoLocation_withspeed.get(i).getGps()) * 3.6; // its speed value
                            if (speed > getDataManager().getLiveTrackingMINSpeed() && speed < getDataManager().getLiveTrackingSpeed()) {
                                LocationBeans locationBeans = new LocationBeans();
                                locationBeans.setLatitude(arr_all_geoLocation_withspeed.get(i).getLatitude());
                                locationBeans.setLongitude(arr_all_geoLocation_withspeed.get(i).getLongitude());
                                locationBeans.setSpeed(arr_all_geoLocation_withspeed.get(i).getGps());
                                latList.add(locationBeans);
                            }
                        }
                    }
                    //  progressDialogwithspeed.setMax(latList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  Log.e("tabble data length", arr_all_geoLocation.size() + "");
                float distance = 0;
                for (int i = 1; i < latList.size(); i++) {
                    try {
                        start_location = new Location("");
                        start_location.setLatitude(latList.get(i - 1).getLatitude());
                        start_location.setLongitude(latList.get(i - 1).getLongitude());
                        //  start_time = arr_all_geoLocation.get(indexValue).getTimeStamp();
                        //indexValue = indexValue + 1;
                        end_location = new Location("");
                        end_location.setLatitude(latList.get(i).getLatitude());
                        end_location.setLongitude(latList.get(i).getLongitude());
                        distance = start_location.distanceTo(end_location);
                        // total_distance = mStopViewModel.getDataManager().getLiveTrackingCalculatedDistance();
                        Log.e("gogole distanca", getDataManager().getDistance() + "");
                        if (distance > getDataManager().getDistance()) {
                            LatLng lastlatLng = new LatLng(start_location.getLatitude(), start_location.getLongitude());
                            LatLng firstlatLng = new LatLng(end_location.getLatitude(), end_location.getLongitude());
                            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
                            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(firstlatLng).optimizeWaypoints(true).destination(lastlatLng).awaitIgnoreError();
                            String dis = (result.routes[0].legs[0].distance.humanReadable);
                            //Log.d("direction Distance",dis);
                            if (dis.endsWith("km")) {
                                distance = Float.parseFloat(dis.replaceAll("[^\\.0123456789]", "")) * 1000;
                            } else {
                                distance = Float.parseFloat(dis.replaceAll("[^\\.0123456789]", ""));
                            }
                            Log.e("distance_google", distance + "");
                            total_distance_with_speed = total_distance_with_speed + distance;
                            // Log.d("Direction Distance", "" + i+" : "+ total_distance);
                            // mStopViewModel.getDataManager().setLiveTrackingCalculatedDistance(total_distance);
                        } else {
                            Log.e("distance", distance + "");
                            total_distance_with_speed = total_distance_with_speed + distance;
                        }
                    } catch (Exception e) {
                        e.getMessage();
                        i++;
                        continue;
                    }
                    //publishProgress(i);
                }
                getDataManager().setLiveTrackingCalculatedDistanceWithSpeed(total_distance_with_speed);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                //progressDialogwithspeed.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialogwithspeed.dismiss();
              /*  float diff = getStopMeterReadingXML() - mStopViewModel.getDataManager().getStartTripMeterReading();
                float distanceTracking = LocationTracker.calculateDistanceLibrary(stopTripViewModel.getDataManager().getLiveTrackingCalculatedDistance(), diff);
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                            stopTripViewModel.uploadAWSImage(context, fileName, String.valueOf(timestamp), Long.parseLong(activityStopTripBinding.etMeter.getText().toString()), activityStopTripBinding.etOtherExprense.getText().toString().trim().isEmpty() ? Float.valueOf("0.0") : Float.valueOf(activityStopTripBinding.etOtherExprense.getText().toString().trim()), Double.parseDouble(Constants.CURRENT_LATITUDE), Double.parseDouble(Constants.CURRENT_LONGITUDE), imageCode, distanceTracking);
                        } else {
                            stopTripViewModel.uploadAWSImage(context, fileName, String.valueOf(timestamp), Long.parseLong(activityStopTripBinding.etMeter.getText().toString()), activityStopTripBinding.etOtherExprense.getText().toString().trim().isEmpty() ? Float.valueOf("0.0") : Float.valueOf(activityStopTripBinding.etOtherExprense.getText().toString().trim()), latitude, longitude, imageCode, distanceTracking);
                        }
                    }
                });*/
            }
        }.execute();
    }


}

package in.ecomexpress.sruti.ui.dashboard.starttrip;

import static in.ecomexpress.sruti.utils.AppConstants.CAMERA_REQUEST;
import static in.ecomexpress.sruti.utils.AppConstants.REQUEST_IMAGE_CAPTURE;
import static in.ecomexpress.sruti.utils.CommonUtils.deleteIMG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityStartTripBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Image;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Multi_Vehicle;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class StartTripDialog extends BaseDialog implements View.OnClickListener, Start_Trip_Adapter.Image_UpdateListner {
    private static DashboardActivity context;
    @Inject
    ViewModelProviderRoom viewModelFactory;
    private StartTripViewModel mRunIdViewModel;
    private ActivityStartTripBinding activityStartTripBinding;
    private String imageFilePath = "";
    private ArrayList<Start_Trip_Image> start_trip_images = new ArrayList<>();
    private int position;
    private Start_Trip_Adapter start_trip_adapter;
    private boolean isGPS = false;
    private LocationCallback locationCallback;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;

    public static StartTripDialog newInstance(DashboardActivity getcontext) {
        StartTripDialog fragment = new StartTripDialog();
        context = getcontext;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityStartTripBinding = DataBindingUtil.inflate(inflater, R.layout.activity_start_trip, container, false);
        mRunIdViewModel = ViewModelProviders.of(this, viewModelFactory).get(StartTripViewModel.class);
        activityStartTripBinding.setViewModel(mRunIdViewModel);
        activityStartTripBinding.cross.setOnClickListener(this);
        activityStartTripBinding.tvStart.setOnClickListener(this);

        start_trip_adapter = new Start_Trip_Adapter(start_trip_images, getActivity(), this);
        activityStartTripBinding.vehicleDetail.setAdapter(start_trip_adapter);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;

            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    }
                }
            }
        };
        getLocation();

        if (mRunIdViewModel.is_Ecom_Vehicle()) {
            mRunIdViewModel.getVehicle_detail().observe(this, startRouteDetails -> {
                if (startRouteDetails != null && startRouteDetails.size() == 0) {
                    Toast.makeText(context, "No vehicle assign", Toast.LENGTH_LONG).show();
                    StartTripDialog.this.dismiss();
                    return;
                }

                if (startRouteDetails != null) {
                    for (LoginResponse.StartRouteDetails rount : startRouteDetails) {
                        Start_Trip_Image dd = new Start_Trip_Image();
                        dd.setVehicleno(rount.getStart_vehicle_number());
                        dd.setMeter_reading(rount.getStart_meter_reading());
                        dd.setVehicle_type(rount.getStart_vehicle_type());
                        start_trip_images.add(dd);
                    }
                    if (startRouteDetails.size() > 0) {
                        activityStartTripBinding.vehicleOwner.setText(startRouteDetails.get(0).getStart_vehicle_owner());
                        start_trip_adapter.ownerType(startRouteDetails.get(0).getStart_vehicle_owner());
                        start_trip_adapter.updateImage(start_trip_images);
                    }
                }
            });
        } else {
            activityStartTripBinding.vehicleOwner.setText("self");
            Start_Trip_Image dd = new Start_Trip_Image();
            dd.setVehicleno("");
            dd.setMeter_reading(0L);
            start_trip_images.add(dd);
            start_trip_adapter.ownerType("self");
            start_trip_adapter.updateImage(start_trip_images);
        }

        mRunIdViewModel.getStartTripResponse().observe(this, startResponse -> {
            hideLoading();
            System.out.println("startResponse " + startResponse.getDescription());
            if (startResponse != null) {
                if (startResponse.getStatus()) {
                    if (!mRunIdViewModel.getDataManager().is_Ecom_Vehicle()
                            && mRunIdViewModel.getDataManager().get_live_Tracking().equalsIgnoreCase("true")) {

                        mRunIdViewModel.startLiveTracking(context);

                    }

                    mRunIdViewModel.getDataManager().setLogout(true);
                    context.handleDialogClose(true);
                    context.synkData(true);
                    StartTripDialog.this.dismiss();
                } else {
                    if (startResponse.getResponse() != null && startResponse.getResponse().getErrors() != null && startResponse.getResponse().getErrors().size() > 0)
                        Toast.makeText(context, startResponse.getResponse().getErrors().get(0), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, startResponse.getDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });
        mRunIdViewModel.getError_msg().observe(this, s -> {
            if (s != null && !s.equals("success")) {
                hideLoading();
                if (!s.equals("hideprogress"))
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            } else {
                showLoading();
                clickStartTrip();
            }
        });

        mRunIdViewModel.getImage_Response().observe(this, imageUplaodResponse -> {
            hideLoading();
            if (imageUplaodResponse != null) {
                if (imageUplaodResponse.isStatus()) {
                    Image_Response imgres = new Image_Response();
                    imgres.setImage_id(imageUplaodResponse.getResponse().getImage_id());
                    imgres.setImage_key(imageUplaodResponse.getResponse().getImage_key());
                    start_trip_images.get(position).setImage_response(imgres);
                    start_trip_images.get(position).setImage_path(imageFilePath);
                    start_trip_adapter.updateImage(start_trip_images);
                } else {
                    Toast.makeText(context, imageUplaodResponse.getDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        activityStartTripBinding.vehicleDetail.setLayoutManager(layoutManager);

        return activityStartTripBinding.getRoot();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    public void clickStartTrip() {
        LocationTracker.deletetable();
        if (mRunIdViewModel.isParent()) {
            ArrayList<Start_Trip_Image> data = start_trip_adapter.getValueDATA();
            StartTripRequest req = new StartTripRequest();
            ArrayList<Start_Trip_Multi_Vehicle> vehicleAll = new ArrayList<>();
            for (Start_Trip_Image dd : data) {
                Start_Trip_Multi_Vehicle vehicle = new Start_Trip_Multi_Vehicle();
                if (activityStartTripBinding.vehicleOwner.getText().equals(Constants.SELF)) {

                }
                vehicle.setType_of_vehicle(dd.getVehicle_type());
                vehicle.setVehicle_meter_reading(dd.getMeter_reading().toString());
                vehicle.setVehicle_number(dd.getVehicleno());
                vehicle.setImage_response(dd.getImage_response());
                vehicleAll.add(vehicle);
            }
            req.setRole("parent");
            req.setVehicle_owner_type(activityStartTripBinding.vehicleOwner.getText().toString());
            req.setStart_trip_multi_vehicle_data(vehicleAll);
            req.setImei_number(CommonUtils.getImei(getActivity()));
            req.setStart_longitude(wayLongitude);
            req.setStart_lattitude(wayLatitude);
            req.setOtc_enabled(Boolean.parseBoolean(mRunIdViewModel.getDataManager().get_live_Tracking()));
            req.setErm_enabled(Boolean.parseBoolean(mRunIdViewModel.getDataManager().get_erm_sync()));
            mRunIdViewModel.submitData(req);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
                mRunIdViewModel.setStart_trip_images(start_trip_adapter.getValueDATA());
                if (mRunIdViewModel.is_Ecom_Vehicle()) {
                    mRunIdViewModel.validationStartTrip();
                } else {
                    mRunIdViewModel.validationStartTripSelf();
                }
                break;
            case R.id.cross:
                this.dismiss();
                break;
            case R.id.image_capture:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    imageFilePath = CommonUtils.dispatchTakePictureIntent(context, this, mRunIdViewModel.empCode());
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            deleteIMG(context);
            File oldsize = new File(imageFilePath);
            File newsize = CommonUtils.saveBitmapToFile(oldsize);
            showLoading();
            Map<String, RequestBody> maprequest = new HashMap<>();

            //File file = new File(imageFilePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), newsize);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", newsize.getName(), requestFile);
            maprequest.put("trip_image_ts", RequestBody.create(MediaType.parse("text/plain"), "" + System.currentTimeMillis()));
            maprequest.put("image_code", RequestBody.create(MediaType.parse("text/plain"), "stopTrip"));
            maprequest.put("image_type", RequestBody.create(MediaType.parse("text/plain"), "START"));
            mRunIdViewModel.imageUploadRealTime(body, maprequest);

        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            imageFilePath = "";
            start_trip_images.get(position).setImage_path(imageFilePath);
            start_trip_adapter.updateImage(start_trip_images);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
    }

    @Override
    public void updateImage(int position) {
        this.position = position;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            imageFilePath = CommonUtils.dispatchTakePictureIntent(context, this, mRunIdViewModel.empCode());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(context, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

        }
    }

}

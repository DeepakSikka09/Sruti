package in.ecomexpress.sruti.ui.dashboard.stoptrip;

import static in.ecomexpress.sruti.utils.AppConstants.CAMERA_REQUEST;
import static in.ecomexpress.sruti.utils.CommonUtils.deleteIMG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityStopTripBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StopTripDialog extends BaseDialog implements View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private StopTripViewModel stopTripViewModel;
    private ActivityStopTripBinding activityStopTripBinding;
    private static DashboardActivity context;
    private static StopTripDialog fragment;
    private String imageFilePath;

    public static StopTripDialog newInstance(DashboardActivity getcontext) {
        fragment = new StopTripDialog();
        context = getcontext;
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, getClass().getName());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityStopTripBinding = DataBindingUtil.inflate(inflater, R.layout.activity_stop_trip, container, false);
        View view = activityStopTripBinding.getRoot();

        stopTripViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(StopTripViewModel.class);
        activityStopTripBinding.setViewModel(stopTripViewModel);
        activityStopTripBinding.cross.setOnClickListener(this);
        activityStopTripBinding.stopImage.setOnClickListener(this);

        stopTripViewModel.getStopAPIResponse().observe(this, stopTrip -> {
            context.handleDialogClose(false);
            StopTripDialog.this.dismiss();

        });
        stopTripViewModel.getError_msg().observe(this, s -> {
            if (!s.equals("success")) {
                showToast(s);
            } else {
                showLoading();
                stopTripClick();
            }

        });
        return view;
    }


    public void stopTripClick() {
        File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        // add another part within the multipart request
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name");
        StopTripRequest stopTripRequest = new StopTripRequest();
//        stopTripRequest.setTripEndKm(Long.parseLong(activityStopTripBinding.etMeter.getText().toString()));
       // stopTripViewModel.imageUpload(body, fullName, stopTripRequest);
    }

  /*  boolean showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        return false;
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            case R.id.stop_image:
                dispatchTakePictureIntent();
                break;
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, "in.ecomexpress.sruti.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imageFilePath = image.getAbsolutePath();
        stopTripViewModel.setImageFilePath(imageFilePath);
        return image;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            System.out.println("CAMERA" + imageFilePath);

            deleteIMG(context);
            Glide.with(context).load(imageFilePath).into(activityStopTripBinding.stopImage);
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            imageFilePath = "";
            stopTripViewModel.setImageFilePath(imageFilePath);

           // LocationTracker.calculateDistanceLibrary()
        }

    }





}

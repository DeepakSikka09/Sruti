package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.journeyapps.barcodescanner.CaptureManager;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityQrcReaderBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IQrNavigator;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.QrcReaderViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class QrcReaderActivity extends BaseActivity<ActivityQrcReaderBinding, QrcReaderViewModel> implements View.OnClickListener, IQrNavigator {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ActivityQrcReaderBinding activityQrcReaderBinding;
    QrcReaderViewModel qrcReaderViewModel;
    private List<ReasonCodeList> pickUpList;
    private CaptureManager capture;
    private ArrayList<Shipment_Detail> shipment_detail;
    private String manifestType;
    private long manifestNo;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrcReaderViewModel.setNavigator(this);
        this.activityQrcReaderBinding = getViewDataBinding();


        //start capture
        capture = new CaptureManager(this, activityQrcReaderBinding.zxingBarcodeScanner);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
        try {
            manifestType = getIntent().getExtras().getString("location_type");
            manifestNo = getIntent().getExtras().getLong("manifest_no");
        } catch (Exception ee) {
            ee.printStackTrace();
        }


        setUp();
        pickUpList = new ArrayList<>();

    }

    private void setUp() {
        activityQrcReaderBinding.btnDrop.setOnClickListener(this);
        activityQrcReaderBinding.imageViewBack.setOnClickListener(this);
        activityQrcReaderBinding.btnCommit.setOnClickListener(this);

        qrcReaderViewModel.getPickupListToView().observe(this, pickupList -> {
            pickUpList.addAll(pickupList);
        });


        qrcReaderViewModel.getAllShipmentList(manifestNo).observe(this, shipment -> {
            if (shipment != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment);
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
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

    }

    @Override
    public QrcReaderViewModel getViewModel() {
        qrcReaderViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(QrcReaderViewModel.class);
        return qrcReaderViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qrc_reader;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnDrop: {
                qrcReaderViewModel.checkAll(shipment_detail);
                showPopup();
                break;
            }
            case R.id.btnCommit: {
                getLocation();
                try {
                    if (qrcReaderViewModel.checkIfAtLeastOneReasonCodeSelected(shipment_detail)) {
                        showToast("Please select reason code first.");
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        AlertDialog dialog = alertDialog.setMessage(R.string.commit)
                                .setTitle(getString(R.string.alert_title))
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                qrcReaderViewModel.createCommitPacketNew(shipment_detail, null, 11l, manifestType, wayLatitude, wayLongitude);
                                                qrcReaderViewModel.failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestNo);
                                                qrcReaderViewModel.failedCommitPacket(1,Constants.PICKED,manifestNo);
                                                qrcReaderViewModel.updateCommit_DataTable(manifestNo);
                                            }
                                        }
                                ).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }
                                )
                                .create();
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                }

                break;
            }
            case R.id.imageViewBack: {
                showSnackbar(getString(R.string.cannot_go_back));
                break;
            }

        }
    }

    @Override
    public void nextScreen(String manifest_no, String sFileBody) {

        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;

            ThreadGeneric.executeCall(() -> {
                qrcReaderViewModel.updateFileUrl(filePath,manifest_no);
            });

            Intent intent = new Intent(QrcReaderActivity.this, SuccessFailActivity.class);
            intent.putExtra("screen_validation", "fail");
            intent.putExtra("manifest_no",manifestNo);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showPopup() {

        try {
            ListPopupWindow popup_new = new ListPopupWindow(QrcReaderActivity.this);
            popup_new.setModal(false);
            popup_new.setAnchorView(activityQrcReaderBinding.btnDrop);
            popup_new.setWidth(1000);
            List<String> rtscodes = new ArrayList<>();

            for (int i = 0; i < pickUpList.size(); i++) {
                rtscodes.add(pickUpList.get(i).getReason_msg());
            }

            popup_new.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtscodes));
            popup_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    qrcReaderViewModel.markUndelivered(shipment_detail, pickUpList.get(i).getReason_id(), pickUpList.get(i).getReason_code(), "");
                    popup_new.dismiss();
                }
            });

            popup_new.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected String getScreenName() {
        return "Qrc Reader Screen";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return activityQrcReaderBinding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(QrcReaderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(QrcReaderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QrcReaderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(QrcReaderActivity.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(QrcReaderActivity.this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }


    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back));
    }


}



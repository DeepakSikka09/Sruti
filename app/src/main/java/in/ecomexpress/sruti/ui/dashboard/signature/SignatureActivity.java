package in.ecomexpress.sruti.ui.dashboard.signature;

import static in.ecomexpress.sruti.utils.CommonUtils.getDisplayValueofSpinner;
import static in.ecomexpress.sruti.utils.CommonUtils.getsubstringAfterPrefix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.background_service.SrutiSyncViewModel;
import in.ecomexpress.sruti.databinding.ActivityPickUpListWithSignatureBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.model.popData.PopManifestList;
import in.ecomexpress.sruti.model.signature.seller_details_list;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.BitmapUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.common_files.ImageHandler;


public class SignatureActivity extends BaseActivity<ActivityPickUpListWithSignatureBinding, SignatureViewModel> implements View.OnClickListener, ISignatureNavigator {
    public static Long manifestNo;
    public ImageHandler imageHandler;
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;


    ActivityPickUpListWithSignatureBinding pickUpListWithSignatureBinding;
    SignatureViewModel signatureViewModel;
    HashMap<Long, List<Shipment_Detail>> shipmentsDetails = new HashMap<>();
    ArrayList<Image_Response> image_responseArrayList = new ArrayList<>();
    private String advance_shipment_count;
    private boolean is_global;
    private String manifest_count;
    private String shipment_count;
    private String picked_count, unpicked_count;
    private String remaining_count;
    private String camera_visible;
    private Bitmap emptyBitmap, well;
    private ArrayList<Recci> recci;
    private long pickup_location_id;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private ArrayList<String> manifest_type;
    private String manifest_type_sw;
    private ArrayList<Long> manifestNoArray;
    private ArrayList<PopData> popDataArrayList;
    private ArrayList<String> mobileNolist;
    private ArrayList<String> mobileNolistforselection = new ArrayList<>();
    private ArrayList<String> mobile_no_list_seller_name = new ArrayList<>();
    private ArrayList<Long> manifestNoArrayIsShared;
    private boolean isSignaturePadVisible = false;
    private boolean isParents;
    private boolean coming_from_Otp = false;

    String mobile_number_type = "";
    long registrd_mobile = 0;
    boolean POP_ENABLE = false;
    String seller_name = "";
    ArrayList<Long> ListOfmanifestno = new ArrayList<>();
    ArrayList<PopManifestList> popManifestLists = new ArrayList<>();
    ArrayList<String> mobileItemArrayList = new ArrayList<>();
    String SelectedSpinerItem = "";
    String SelectedNo = "";
    HashMap<String, String> SpinnerMobileHashData = new HashMap<>();
    private long lastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pickUpListWithSignatureBinding = getViewDataBinding();
        signatureViewModel.setNavigator(this);

        manifest_type = new ArrayList<>();
        ListOfmanifestno = new ArrayList<>();
        mobileNolist = new ArrayList<>();
        mobileNolistforselection = new ArrayList<>();
        setUp();
        /* signatureViewModel.getDataManager().is_POP_Enable(false);*/
        signatureViewModel.getDataManager().is_POP_Enable(POP_ENABLE);


        manage_Pop_Enable(is_global);

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


        getLocation();


        signatureViewModel.getOtpVerificationManifestResponseMediatorLiveData().observe(this, OtpVerificationManifestResponse -> {
            if (OtpVerificationManifestResponse.isStatus()) {

            } else {

            }
        });


        signatureViewModel.getPickupResponseMediatorLiveData().observe(this, proofOfPickupResponse -> {
            if (proofOfPickupResponse.getStatus()) {


                Log.d("checksss", "ok");
            } else {

            }
        });


        //query

        mobileItemArrayList = new ArrayList<>();
        if (!is_global) {
            String masked_no = CommonUtils.maskNo(String.valueOf(registrd_mobile));
            mobileItemArrayList.add(String.valueOf(masked_no) + " " + "-" + seller_name + "#%$" + registrd_mobile);
            mobileNolistforselection.add(String.valueOf(registrd_mobile));
            mobileNolistforselection.add(getString(R.string.free_no));
            ListOfmanifestno.add(manifestNo);
            if (isSignaturePadVisible) {
                pickUpListWithSignatureBinding.signatureTxt.setVisibility(View.VISIBLE);
                pickUpListWithSignatureBinding.signaturePad.setVisibility(View.VISIBLE);
            } else {
                pickUpListWithSignatureBinding.signatureTxt.setVisibility(View.GONE);
                pickUpListWithSignatureBinding.signaturePad.setVisibility(View.INVISIBLE);
            }
        } else {

            mobileItemArrayList.addAll(mobile_no_list_seller_name);
            ListOfmanifestno.addAll(manifestNoArray);
            mobileNolistforselection.addAll(mobileNolist);
            mobileNolistforselection.add(getString(R.string.free_no));

            if (manifestNoArray != null && manifestNoArray.size() > 0) {
                signatureViewModel.getSpecificManifestDetail(manifestNoArray).observe(this, manifest_lists -> {
                    for (int i = 0; i < manifest_lists.size(); i++) {
                        if (manifest_lists.get(i).getManifest_details().isSignature_pad_visible()) {
                            pickUpListWithSignatureBinding.signatureTxt.setVisibility(View.VISIBLE);
                            pickUpListWithSignatureBinding.signaturePad.setVisibility(View.VISIBLE);
                            isSignaturePadVisible = true;
                            return;
                        } else {
                            isSignaturePadVisible = false;
                            pickUpListWithSignatureBinding.signatureTxt.setVisibility(View.GONE);
                            pickUpListWithSignatureBinding.signaturePad.setVisibility(View.INVISIBLE);
                        }
                    }

                });

            }
        }
        ArrayList<String> newMobileItemList = new ArrayList<>();
        // newMobileItemList.add(0,"Select Mobile No");
        newMobileItemList.addAll(mobileItemArrayList);

        SpinnerMobileHashData = new HashMap<>();
        for (int i = 0; i < newMobileItemList.size(); i++) {
            String displayValue = getDisplayValueofSpinner(newMobileItemList.get(i));
            String mobileno = getsubstringAfterPrefix(newMobileItemList.get(i));
            SpinnerMobileHashData.put(displayValue, mobileno);

        }


        setSpinnerData(newMobileItemList);
    }

    @Override
    protected String getScreenName() {
        return "Signature Activtiy";
    }

    private void setSpinnerData(ArrayList<String> newMobileItemList) {


        ArrayList<String> mobilelistforselection = new ArrayList<>();
        for (int i = 0; i < newMobileItemList.size(); i++) {
            String ss = getDisplayValueofSpinner(newMobileItemList.get(i));
            mobilelistforselection.add(ss);

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                } else {

                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };


        adapter.setDropDownViewResource(R.layout.select_dialog_singlechoice_custom);
        adapter.addAll(mobilelistforselection);
        adapter.add(getString(R.string.select_mobile_number));
        SpinnerMobileHashData.put(getString(R.string.select_mobile_number), "");//This is the text that will be displayed as hint.
        pickUpListWithSignatureBinding.spinner.setAdapter(adapter);
        pickUpListWithSignatureBinding.spinner.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
        pickUpListWithSignatureBinding.spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        // It returns the clicked item.

                        SelectedSpinerItem = String.valueOf(parent.getItemAtPosition(position));
                        //SelectedNo=getsubstringAfterPrefix(newMobileItemList.get(position));
                        // Log.d("check_d", SpinnerMobileHashData.get(SelectedSpinerItem).toString());
                        //    Log.d("check_data",SelectedNo);
                        SelectedNo = SpinnerMobileHashData.get(SelectedSpinerItem);
                        // Log.d("check_data",SelectedNo);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        pickUpListWithSignatureBinding.spinner.setSelection(0);
                    }
                });


    }

    private void setUp() {
        try {
            manifestNoArray = (ArrayList<Long>) getIntent().getSerializableExtra("manifestNoArray");
            mobileNolist = (ArrayList<String>) getIntent().getSerializableExtra("mobile_no_list");
            mobile_no_list_seller_name = (ArrayList<String>) getIntent().getSerializableExtra("mobile_no_list_seller_name");
            pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
            recci = getIntent().getParcelableArrayListExtra("RECCIQ");
            advance_shipment_count = getIntent().getExtras().getString("advance_shipment_count");
            manifest_count = getIntent().getExtras().getString("manifest_count");
            shipment_count = getIntent().getExtras().getString("shipment_count");
            picked_count = getIntent().getExtras().getString("picked_count");
            unpicked_count = getIntent().getExtras().getString("unpicked_count");
            remaining_count = getIntent().getExtras().getString("remaining_count");
            camera_visible = getIntent().getExtras().getString("camera_visible");
            manifestNo = getIntent().getExtras().getLong("manifestNo");
            is_global = getIntent().getExtras().getBoolean("is_global");
            isSignaturePadVisible = getIntent().getExtras().getBoolean("signature_pad_visible");

            isParents = signatureViewModel.getDataManager().getParent() || signatureViewModel.getDataManager().getAsParentChild();

            if (is_global) {
                pickUpListWithSignatureBinding.isAdvance.setVisibility(View.GONE);
                Bundle extra = getIntent().getBundleExtra("manifest_type_signature");
                manifest_type = (ArrayList<String>) extra.getSerializable("manifest_type_collection");
            } else {
                manifestNoArrayIsShared = (ArrayList<Long>) getIntent().getExtras().getSerializable("manifestNoArray");
                mobile_number_type = getIntent().getExtras().getString("mobile_number_type");
                registrd_mobile = getIntent().getExtras().getLong("registrd_mobile");
                POP_ENABLE = getIntent().getExtras().getBoolean("pop_enable");
                seller_name = getIntent().getExtras().getString("seller_name");
                coming_from_Otp = getIntent().getExtras().getBoolean("coming_from_Otp");
                pickUpListWithSignatureBinding.isAdvance.setVisibility(View.VISIBLE);
                manifest_type_sw = getIntent().getExtras().getString("manifest_type_signature");
                manifest_type.add(manifest_type_sw);
            }

            if (getIntent().getParcelableArrayListExtra("imageArrayList") == null) {
            } else {
                image_responseArrayList = getIntent().getParcelableArrayListExtra("imageArrayList");
            }

            pickUpListWithSignatureBinding.txtManifest.setText(manifest_count);
            pickUpListWithSignatureBinding.txtShipment.setText(shipment_count);
            pickUpListWithSignatureBinding.txtPicked.setText(picked_count);
            pickUpListWithSignatureBinding.txtUnpicked.setText(unpicked_count);
            pickUpListWithSignatureBinding.txtRemaining.setText(remaining_count);
            pickUpListWithSignatureBinding.txtAdvanceShipment.setText(advance_shipment_count);
            pickUpListWithSignatureBinding.txtOnClear.setOnClickListener(this);
            pickUpListWithSignatureBinding.imageCapture.setOnClickListener(this);
            pickUpListWithSignatureBinding.imageViewBack.setOnClickListener(this);

            if (camera_visible.equals("false")) {
                pickUpListWithSignatureBinding.linearImage.setVisibility(View.GONE);
            }
            if (is_global) {
                if (manifestNoArray != null && manifestNoArray.size() > 0) {
                    for (int i = 0; i < manifestNoArray.size(); i++) {
                        signatureViewModel.getAllParentShipmentlist(manifestNoArray.get(i)).observe(this, shipment -> {
                            if (shipment != null && shipment.size() > 0) {
                                shipmentsDetails.put(shipment.get(0).getManifestNoInchild(), shipment);
                            }
                        });
                    }
                }

            } else {
                signatureViewModel.getAllParentShipmentlist(manifestNo).observe(this, shipment -> {
                    if (shipment != null) {
                        shipmentsDetails.put(manifestNo, shipment);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpImageHandler();

    }

    private void setUpImageHandler() {
        imageHandler = new ImageHandler(SignatureActivity.this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode) {

                if (imgView != null) {
                    imgView.setImageBitmap(bitmap);

                }
                ImageModel imageModel = new ImageModel();
                imageModel.setManifest_id(String.valueOf(manifestNo));
                imageModel.setImage_name(imageName);
                imageModel.setImage_type("others");
                imageModel.setImage_code(imageCode);
                imageModel.setFilePath(imageUri);
                imageModel.setStatus(0);

                signatureViewModel.uploadLocalImage(imageModel);
                Image_Response image_response = new Image_Response();
                image_response.setImage_key(imageCode);
                image_response.setImage_id("-1");
                image_responseArrayList.add(image_response);
            }
        };
    }

    @Override
    public SignatureViewModel getViewModel() {
        signatureViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(SignatureViewModel.class);
        return signatureViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pick_up_list_with_signature;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_onClear: {
                pickUpListWithSignatureBinding.signature.clear();
                break;
            }
            case R.id.imageViewBack: {
                showSnackbar(getString(R.string.cannot_go_back));
                break;
            }
            case R.id.image_capture: {
                imageHandler.captureImage(manifestNo + "_image.png", pickUpListWithSignatureBinding.imageCapture, manifestNo + "_image");

                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageHandler.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            showToast(e.getMessage());
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true;
            }
        }

    }

    @Override
    public void saveSignature() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 3000) {
            showToast("Please wait for 3 seconds");
            return;
        }
        lastClickTime = currentTime;
        if (checkMultiSpace(this, signatureViewModel.getDataManager())) {
            showMultiSpaceDialog();
        } else {
            // if(POP_ENABLE) {
            if (signatureViewModel.getDataManager().get_pop_enable()) {
                if (!signatureViewModel.getDataManager().get_is_child_available()) {
                    if (!SelectedNo.equalsIgnoreCase("")) {
                        if (!coming_from_Otp) {
                            if (!is_global) {
                                try {
                                    if (!mobile_number_type.equalsIgnoreCase("")) {
                                        if (signatureViewModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {

                                            signatureViewModel.hitManifestVerify(registrd_mobile, manifestNoArrayIsShared, this, mobile_number_type);

                                        } else {

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        saveBitmapInFolder(isSignaturePadVisible);
                    } else {
                        showSnackbar(getString(R.string.atleast_one_mbl_no));
                    }
                } else {
                    if (!coming_from_Otp) {
                        if (!is_global) {
                            try {
                                if (!mobile_number_type.equalsIgnoreCase("")) {
                                    if (signatureViewModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {
                                        signatureViewModel.hitManifestVerify(registrd_mobile, manifestNoArrayIsShared, this, mobile_number_type);
                                    } else {

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    saveBitmapInFolder(isSignaturePadVisible);
                }
            } else {
                if (!coming_from_Otp) {
                    if (!is_global) {
                        try {
                            if (!mobile_number_type.equalsIgnoreCase("")) {
                                if (signatureViewModel.getDataManager().get_sruti_enable_otp_for_zero_pickup().equalsIgnoreCase("true")) {
                                    signatureViewModel.hitManifestVerify(registrd_mobile, manifestNoArrayIsShared, this, mobile_number_type);
                                } else {

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                saveBitmapInFolder(isSignaturePadVisible);
            }
        }

    }

    @Override
    public void saveobject(String manifest_no, String sFileBody) {
        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;

            ThreadGeneric.executeCall(() -> {
                System.out.println("filePath  " + filePath);
                signatureViewModel.updateFileUrl(filePath, manifest_no);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextScreen() {

        // Log the button click event
        logButtonClick("Open Success Fail Screen");
        // Measure time to open a new screen
        long clickTime = System.currentTimeMillis();
        Intent intent = new Intent(SignatureActivity.this, SuccessFailActivity.class);
        intent.putExtra("clickTime", clickTime);
        intent.putExtra("screen_validation", "success");
        intent.putExtra("is_global", is_global);
        intent.putExtra("manifest_no", manifestNo);
        intent.putExtra("manifestNoCollection", manifestNoArray);
        startActivity(intent);

    }


    private Boolean saveBitmapInFolder(boolean isSignaturePadVisible) {
        getLocation();

        try {
            File fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress);
            if (!fileDir.exists())
                fileDir.mkdirs();
            File file = new File(fileDir, manifestNo + "_signature.png");
            if (!file.exists())
                file.createNewFile();

            try (FileOutputStream ostream = new FileOutputStream(file)) {
                well = pickUpListWithSignatureBinding.signature.getSignatureBitmap();
                emptyBitmap = pickUpListWithSignatureBinding.signature.getWhiteBackground(well);

                if (well.sameAs(emptyBitmap)) {
                    if (!isSignaturePadVisible) {

                        callProofOfPickupApi();
                        signatureViewModel.createCommitPacketNew(shipmentsDetails, recci, image_responseArrayList, pickup_location_id, wayLatitude, wayLongitude, manifest_type);

                    } else {
                        showSnackbar("Please place signature.");
                        ostream.close();
                        return false;
                    }

                } else {
                    try {
                        BitmapUtils.saveBitmap(file, well);

                        ImageModel imageModel = new ImageModel();
                        imageModel.setManifest_id(String.valueOf(manifestNo));
                        imageModel.setImage_name(file.getName());
                        imageModel.setImage_type("signature");
                        imageModel.setImage_code(manifestNo + "_signature");
                        imageModel.setFilePath(file.getAbsolutePath());
                        imageModel.setStatus(0);
                        signatureViewModel.uploadLocalImage(imageModel);


                        Image_Response image_response = new Image_Response();
                        image_response.setImage_key(manifestNo + "_signature");
                        image_response.setImage_id("-1");
                        image_responseArrayList.add(image_response);
                        callProofOfPickupApi();
                        signatureViewModel.createCommitPacketNew(shipmentsDetails, recci, image_responseArrayList, pickup_location_id, wayLatitude, wayLongitude, manifest_type);

                    } catch (Exception e) {
                        RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
                        restApiErrorHandler.writeErrorLogs(0, e.getMessage());
                        e.printStackTrace();
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } catch (Exception ee) {
            ee.printStackTrace();
        }

        return false;
    }

    private void callProofOfPickupApi() {
        for (long manifestlist : ListOfmanifestno) {
            PopData popData1=new PopData();
            popData1.setRoute_id(signatureViewModel.getDataManager().getRouteID());
            popData1.setSeller_phone_no(SelectedNo);
            popData1.setManifest_ids(manifestlist);
            popData1.setStatus(Constants.POP_PENDING);
          /*  popData1.setPop_enable(signatureViewModel.getDataManager().get_pop_enable());
            popData1.setChild_available(signatureViewModel.getDataManager().get_is_child_available());
         */
            signatureViewModel.PushPopData(popData1);


        }

    }


    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back));
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(SignatureActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SignatureActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignatureActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(SignatureActivity.this, location -> {
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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(SignatureActivity.this, location -> {
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


    public void manage_Pop_Enable(boolean is_global) {
        if (is_global) {
            if (mobileNolist.size() != 0) {
                if (signatureViewModel.getDataManager().get_is_child_available()) {
                    signatureViewModel.getDataManager().is_POP_Enable(false);
                    //  POP_ENABLE=false;
                    pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.GONE);
                } else {
                    signatureViewModel.getDataManager().is_POP_Enable(true);
                    //  POP_ENABLE=true;
                    pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.VISIBLE);
                }
            } else {
                signatureViewModel.getDataManager().is_POP_Enable(false);
                //  POP_ENABLE=false;
                pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.GONE);
            }
        } else {
            if (signatureViewModel.getDataManager().get_pop_enable()) {
                if (signatureViewModel.getDataManager().get_is_child_available()) {
                    pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.GONE);
                } else {
                    pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.VISIBLE);
                }
            } else {
                pickUpListWithSignatureBinding.rlProofOfPickup.setVisibility(View.GONE);
            }
        }


    }


}




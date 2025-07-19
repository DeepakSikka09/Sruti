package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.ActivityShipmentListBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.CommitDialogValidation;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ChildCallback;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.IShipmentNavigator;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class GlobalShipmentListActivity extends BaseActivity<ActivityShipmentListBinding, GlobalShipmentListViewModel> implements View.OnClickListener, IShipmentNavigator, ChildCallback {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private GlobalShipmentListViewModel globalShipmentListViewModel;
    ActivityShipmentListBinding activityShipmentListBinding;
    private GlobalShipmentListAdapter mshipmentListAdapterGlobal;
    private String manifest_count;
    private List<ReasonCodeList> pickUpList;
    private ArrayList<Shipment_Detail> shipment_detail;
    public static Long manifestNo;
    boolean isParent;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    ArrayList<Image_Response> image_responseArrayList;
    boolean isAllCHildCommited = false;
    ArrayList<String> manifest_type;
    HashMap<Long, List<Shipment_Detail>> shipmentsDetails = new HashMap<>();
    public ArrayList<Long> manifestNoArray;
    private ArrayList<Shipment_Detail> mps_shipment_detail;
    private Map<Long, List<Shipment_Detail>> mapWithMps;
    private ProgressDialog dialogOnDataUpdate;
    HashSet<String> mobileItemArrayList = new HashSet<>();
    HashSet<String> mobileNoListWithSellerName = new HashSet<>();
    private HashSet<ReasonCodeList> reasonCodeLists;
    private  GlobalShipmentListActivity activity;
    private long lastClickTime =0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalShipmentListViewModel.setNavigator(this);
        this.activityShipmentListBinding = getViewDataBinding();
        activity=this;
        setUp();
    }

    @Override
    protected String getScreenName() {
        return "Global Shipment Screen";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUp() {
        manifest_type = new ArrayList<>();
        pickUpList = new ArrayList<>();
        reasonCodeLists = new HashSet<>();
        try {
            isParent = globalShipmentListViewModel.getDataManager().getParent();
            manifest_count = getIntent().getExtras().getString("manifest_count");
            image_responseArrayList = getIntent().getParcelableArrayListExtra("imageArrayList");
            Bundle extra = getIntent().getBundleExtra("manifest_type");
            manifest_type = (ArrayList<String>) extra.getSerializable("manifest_type_collection");
            manifestNoArray = (ArrayList<Long>) getIntent().getSerializableExtra("manifestNoArray");

            globalShipmentListViewModel.getScannedManifestListAllData();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        activityShipmentListBinding.txtManifest.setText(manifest_count);
        mshipmentListAdapterGlobal = new GlobalShipmentListAdapter(this, this);
        activityShipmentListBinding.shipmentRecyclerView.setHasFixedSize(true);
        activityShipmentListBinding.shipmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityShipmentListBinding.shipmentRecyclerView.setAdapter(mshipmentListAdapterGlobal);
        activityShipmentListBinding.shipmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        globalShipmentListViewModel.setManifestListLiveData(manifestNoArray).observe(this, shipment_details -> {
            if (shipment_details != null) {
                shipment_detail = new ArrayList<>();
                shipment_detail.addAll(shipment_details);
                mshipmentListAdapterGlobal.setData(shipment_details, activity);
                mshipmentListAdapterGlobal.setReasonCodeMaster(globalShipmentListViewModel.reasonCodeMaster);
                setReasoncode(shipment_details);
                for (int i = 0; i < shipment_details.size(); i++) {
                    manifestNo = shipment_details.get(i).getManifestNoInchild();

                }
            }
        });

        globalShipmentListViewModel.getPickupListToView(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT).observe(this, pickupList -> {
            reasonCodeLists.clear();
            reasonCodeLists.addAll(pickupList);
            mshipmentListAdapterGlobal.setBPReasonCode(new ArrayList(reasonCodeLists));

        });
        activityShipmentListBinding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mshipmentListAdapterGlobal.getFilter().filter(newText);
                return false;
            }
        });

        if (manifestNoArray != null && manifestNoArray.size() > 0) {
            for (int i = 0; i < manifestNoArray.size(); i++) {
                globalShipmentListViewModel.getAllShipmentlist(manifestNoArray.get(i)).observe(GlobalShipmentListActivity.this, shipment -> {
                    if (shipment != null && shipment.size() > 0) {
                        shipmentsDetails.put(shipment.get(0).getManifestNoInchild(), shipment);
                    }
                });
            }
        }

        if (manifestNoArray != null && manifestNoArray.size() > 0) {
            globalShipmentListViewModel.getMpsCondition(manifestNoArray).observe(GlobalShipmentListActivity.this, shipment_details -> {
                if (shipment_details != null && shipment_details.size() > 0) {
                    mps_shipment_detail = new ArrayList<>();
                    mapWithMps = new HashMap<>();
                    mps_shipment_detail.addAll(shipment_details);

                    for (Shipment_Detail shipment_detail1 : mps_shipment_detail) {
                        List<Shipment_Detail> mpsList = mapWithMps.get(shipment_detail1.getMaster_airwaybill_number());
                        if (Objects.isNull(mpsList)) {
                            mpsList = new ArrayList<>();
                        }
                        mpsList.add(shipment_detail1);
                        mapWithMps.put(shipment_detail1.getMaster_airwaybill_number(), mpsList);
                    }
                    System.out.println("Key = " + mapWithMps);

                }
            });
        }
        activityShipmentListBinding.selectAll.setOnClickListener(this);
        activityShipmentListBinding.btnDrop.setOnClickListener(this);
        activityShipmentListBinding.imageViewBack.setOnClickListener(this);
        activityShipmentListBinding.btnNext.setOnClickListener(this);

        if (!isParent) {
            activityShipmentListBinding.btnDrop.setVisibility(View.GONE);
            activityShipmentListBinding.selectAll.setVisibility(View.GONE);
            activityShipmentListBinding.parent.setVisibility(View.GONE);
        } else {
            activityShipmentListBinding.btnDrop.setVisibility(View.VISIBLE);
            activityShipmentListBinding.parent.setVisibility(View.VISIBLE);
            activityShipmentListBinding.selectAll.setVisibility(View.VISIBLE);
        }
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


        mobileItemArrayList = new HashSet<>();
        mobileNoListWithSellerName = new HashSet<>();
        globalShipmentListViewModel.getSpecificManifestDetail(manifestNoArray).observe(this, manifest_lists -> {
            /*for (int i = 0; i<manifest_lists.size(); i++) {
                mobileItemArrayList.add(String.valueOf(manifest_lists.get(i).getManifest_details().getLocation_contact_no()));
            }*/

            for (int i = 0; i < manifest_lists.size(); i++) {
                if (manifest_lists.get(i).getManifest_details().getLocation().isProof_of_pickup_enable()) {

                    String mbl_no = CommonUtils.maskNo(String.valueOf(manifest_lists.get(i).getManifest_details().getLocation_contact_no()));
                    mobileItemArrayList.add(String.valueOf(manifest_lists.get(i).getManifest_details().getLocation_contact_no()));
                    mobileNoListWithSellerName.add(String.valueOf(mbl_no) + " " + "-" + manifest_lists.get(i).getManifest_details().getLocation_name() + "#%$" + manifest_lists.get(i).getManifest_details().getLocation_contact_no());
                } else {
                }
            }

        });


    }

    private void setReasoncode(List<Shipment_Detail> shipment_details) {
        if (shipment_details != null) {
            if (globalShipmentListViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                globalShipmentListViewModel.getPickupListToView(Constants.SHIPMENT_WISE_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    globalShipmentListViewModel.setAllReasonCode(pickUpList);

                });
            } else {
                globalShipmentListViewModel.getPickupListToView(Constants.ALL_REASON_CODE).observe(this, pickupList -> {
                    pickUpList.clear();
                    pickUpList.addAll(pickupList);
                    globalShipmentListViewModel.setAllReasonCode(pickUpList);

                });
            }
        }
    }


    @Override
    public GlobalShipmentListViewModel getViewModel() {
        globalShipmentListViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(GlobalShipmentListViewModel.class);
        return globalShipmentListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shipment_list;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.selectAll: {
                globalShipmentListViewModel.checkAll(shipment_detail);
                break;
            }
            case R.id.btnDrop: {
                showPopup();
                break;
            }
            case R.id.imageViewBack: {
                finish();
                break;
            }
            case R.id.btn_next: {
                if (checkMultiSpace(this, globalShipmentListViewModel.getDataManager())) {
                    showMultiSpaceDialog();
                } else {
                    if (!isParent) {
                        if (mapWithMps != null && mapWithMps.size() > 0) {

                            Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = mapWithMps.entrySet().iterator();
                            Map<Long, Set<String>> authenticated = new HashMap<>();
                            CommitDialogValidation commitDialogValidation = new CommitDialogValidation();

                            List<List<Shipment_Detail>> failedCases = new ArrayList<>();
                            while (ob.hasNext()) {
                                Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();
                                List<Shipment_Detail> shipmentloc = mapWithMps.get(pair.getKey());
                                Set<String> uniqueStatus = new HashSet<>();
                                for (Shipment_Detail ship : shipmentloc) {
                                    uniqueStatus.add(ship.getStatus());
                                }
                                authenticated.put(pair.getKey(), uniqueStatus);
                                if (uniqueStatus.size() > 1) {
                                    failedCases.add(shipmentloc);
                                }
                            }
                            if (failedCases.size() > 0) {
                                commitDialogValidation.ConfirmationDialog(failedCases);
                                commitDialogValidation.show(getSupportFragmentManager(), "dialog");
                            } else {
                                elseFunctionOfChild();
                            }
                        } else {
                            elseFunctionOfChild();
                        }
                    } else {
                        if (!globalShipmentListViewModel.isValidToMoveNext(shipment_detail)) {
                            showToast(getString(R.string.validate));
                            return;
                        }
                        if (mapWithMps != null && mapWithMps.size() > 0) {

                            Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = mapWithMps.entrySet().iterator();
                            Map<Long, Set<String>> authenticated = new HashMap<>();
                            CommitDialogValidation commitDialogValidation = new CommitDialogValidation();

                            List<List<Shipment_Detail>> failedCases = new ArrayList<>();
                            while (ob.hasNext()) {
                                Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();
                                List<Shipment_Detail> shipmentloc = mapWithMps.get(pair.getKey());
                                Set<String> uniqueStatus = new HashSet<>();
                                for (Shipment_Detail ship : shipmentloc) {
                                    uniqueStatus.add(ship.getStatus());
                                }
                                authenticated.put(pair.getKey(), uniqueStatus);
                                if (uniqueStatus.size() > 1) {
                                    failedCases.add(shipmentloc);
                                }
                            }
                            if (failedCases.size() > 0) {
                                commitDialogValidation.ConfirmationDialog(failedCases);
                                commitDialogValidation.show(getSupportFragmentManager(), "dialog");
                            } else {


                                Log.d("checkhere", String.valueOf(mobileItemArrayList));
                                elseFunction();
                            }
                        } else {
                            elseFunction();
                        }
                    }

                }
            }
        }
    }

    private void showPopup() {
        try {
            ListPopupWindow popup_new = new ListPopupWindow(GlobalShipmentListActivity.this);
            popup_new.setModal(false);
            popup_new.setAnchorView(activityShipmentListBinding.btnDrop);
            popup_new.setWidth(600);
            List<String> rtscodes = new ArrayList<>();
            for (int i = 0; i < pickUpList.size(); i++) {
                rtscodes.add(pickUpList.get(i).getReason_msg());
            }

            popup_new.setAdapter(new ArrayAdapter<String>(this, R.layout.white_spinner_single_item, R.id.white_spinner_text_view, rtscodes));
            popup_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        globalShipmentListViewModel.markUndelivered(shipment_detail, pickUpList.get(i).getReason_id(), pickUpList.get(i).getReason_code(), "");
                        popup_new.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            popup_new.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    private void elseFunction() {
        /*   for(int i=0;i<man)*/

        Bundle manifest_data = new Bundle();
        manifest_data.putSerializable("manifest_type_collection", manifest_type);
        Intent intent = new Intent(GlobalShipmentListActivity.this, SignatureActivity.class);
        intent.putExtra("is_global", true);
        intent.putExtra("manifest_count", activityShipmentListBinding.txtManifest.getText());
        intent.putExtra("shipment_count", activityShipmentListBinding.txtShipment.getText());
        intent.putExtra("picked_count", activityShipmentListBinding.txtPicked.getText());
        intent.putExtra("remaining_count", activityShipmentListBinding.txtRemaining.getText());
        intent.putExtra("unpicked_count", activityShipmentListBinding.txtUnpicked.getText().toString());
        intent.putExtra("manifestNo", manifestNo);
        intent.putExtra("advance_shipment_count", "0");
        intent.putExtra("camera_visible", "false");
        intent.putExtra("imageArrayList", image_responseArrayList);
        intent.putExtra("manifest_type_signature", manifest_data);
        intent.putExtra("manifestNoArray", manifestNoArray);
        intent.putExtra("mobile_no_list", new ArrayList<>(mobileItemArrayList));
        intent.putExtra("mobile_no_list_seller_name", new ArrayList<>(mobileNoListWithSellerName));
        startActivity(intent);
    }

    private void elseFunctionOfChild() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(R.string.commit)
                    .setTitle(getString(R.string.alert_title))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (globalShipmentListViewModel.checkIfAtLeastOneScan(shipment_detail)) {
                                        if (manifestNoArray != null && manifestNoArray.size() > 0) {
                                            ArrayList<String>statusList=new ArrayList<>();
                                            statusList.add(Constants.PICKED);
                                            statusList.add(Constants.FAILED);
                                            for (int i = 0; i < manifestNoArray.size(); i++) {
                                                globalShipmentListViewModel.getScannedShipmentList(manifestNoArray.get(i),statusList).observe(GlobalShipmentListActivity.this, shipment_details -> {
                                                    if (shipment_details != null && shipment_details.size() > 0) {
                                                        shipmentsDetails.put(shipment_details.get(0).getManifestNoInchild(), shipment_details);
                                                        globalShipmentListViewModel.createCommitMuiltiPacketNew(shipmentsDetails, image_responseArrayList, wayLatitude, wayLongitude, manifest_type);

                                                    }
                                                });

                                            }
                                        }
                                    } else {
                                        showToast(getString(R.string.scan_atlest_one));
                                    }
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
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
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
                globalShipmentListViewModel.updateFileUrl(filePath, manifest_no);
            });

            Intent intent = new Intent(GlobalShipmentListActivity.this, SuccessFailActivity.class);
            intent.putExtra("screen_validation", "success");
            intent.putExtra("manifestNoCollection", manifestNoArray);
            intent.putExtra("is_global", true);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onErrorMessage(String message) {
        showToast(message);
    }

    @Override
    public void notifyAdapter() {
        mshipmentListAdapterGlobal.notifyDataSetChanged();
    }

    @Override
    public void update(Shipment_Detail shipmentsDetail) {
        try {
            globalShipmentListViewModel.updateShipment(shipmentsDetail.getAirWayBillNumber(), Constants.PENDING, "", false);

            ArrayList<String> shipment = new ArrayList<>();
            for (Shipment_Detail sc : shipment_detail) {
                shipment.add(sc.getStatus());
            }
            if (shipment.get(0).equals(Constants.PENDING)) {
                globalShipmentListViewModel.assignUpdateIsScanStarted(manifestNo);
            } else {
                // Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    @Override
    public void updateCountAfterMarkingFail(Long picked, Long unpicked, Long remaining) {

    }

    @Override
    public void showDialogBox(String response, ProgressDialog dialogOnDataUpdate) {
        dialogOnDataUpdate.dismiss();
        showCommitFailInfoDialog(response);
    }


    @Override
    public void setUpdatedCount(HashSet<Manifest_List> hashValue) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                long totalRemaing = 0, unPicked = 0, total = 0, remaining = 0, picked = 0, totalPicked = 0, totalShipment = 0, totalUnpicked = 0;

                try {
                    if (hashValue != null) {

                        List<Manifest_List> manifestLists = new ArrayList<>();
                        for (Manifest_List co : hashValue) {
                            manifestLists.add(co);

                            // insert
                            String scanned_update = ("(" + "Picked : " + co.getPicked_count() + " /" + "T.Shipment :" + co.getTotalShipmentCount() + ")");
                            globalShipmentListViewModel.updateShipmentCount(co.getManifest_No(), scanned_update);

                            remaining = co.getRemaining_count();
                            picked = co.getPicked_count();
                            unPicked = co.getUnpicked_count();
                            total = co.getTotalShipmentCount();
                            totalRemaing += remaining;
                            totalPicked += picked;
                            totalUnpicked += unPicked;
                            totalShipment += total;
//                            totalShipment = totalPicked + totalRemaing + unPicked;
                        }

                        try {
//                          activityShipmentListBinding.txtManifest.setText(manifest_count);
                            activityShipmentListBinding.txtShipment.setText(String.valueOf(totalShipment));
                            activityShipmentListBinding.txtPicked.setText(String.valueOf(totalPicked));
                            activityShipmentListBinding.txtRemaining.setText(String.valueOf(totalRemaing));
                            activityShipmentListBinding.txtUnpicked.setText(String.valueOf(totalUnpicked));

                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    } else {
                        showToast(getString(R.string.null_value));
                    }
                } catch (Exception e) {
                    showToast(e.getMessage());
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void notifyUpdatedCount() {
        globalShipmentListViewModel.getMaListLiveData().observe(this, new Observer<List<Manifest_List>>() {
            @Override
            public void onChanged(@Nullable List<Manifest_List> manifest_lists) {
                for (int i = 0; i < manifest_lists.size(); i++) {
                    manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                    Set<Long> set = new HashSet<>(manifestNoArray);
                    manifestNoArray.clear();
                    manifestNoArray.addAll(set);

                    // insert
                    String scanned_update = ("(" + "Picked : " + manifest_lists.get(i).getPicked_count() + " /" + "T.Shipment :" + manifest_lists.get(i).getTotalShipmentCount() + ")");
                    globalShipmentListViewModel.updateShipmentCount(manifest_lists.get(i).getManifest_No(), scanned_update);

                }
                if (manifest_lists.size() > 0) {
                    globalShipmentListViewModel.getPickedCount(manifest_lists);
                } else {
                    GlobalShipmentListActivity.this.showToast(getString(R.string.no_data_found));
                }
            }
        });

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(GlobalShipmentListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(GlobalShipmentListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GlobalShipmentListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(GlobalShipmentListActivity.this, location -> {
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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(GlobalShipmentListActivity.this, location -> {
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
    public void onCheckStatusClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 3000) {
            showToast("Please wait for 3 seconds");
            return;
        }
        lastClickTime = currentTime;
        if (isNetworkConnected()) {

            dialogOnDataUpdate = new ProgressDialog(GlobalShipmentListActivity.this);
            dialogOnDataUpdate.show();
            dialogOnDataUpdate.setCancelable(false);
            dialogOnDataUpdate.setMessage("Fetching Data...");
            dialogOnDataUpdate.setIndeterminate(true);

            globalShipmentListViewModel.getChildApiResponse(manifestNoArray, dialogOnDataUpdate, shipment_detail, this);
        } else {
            showToast(getString(R.string.check_internet));
        }
    }


    private void showCommitFailInfoDialog(String description) {
        try {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = alertDialog.setMessage(description)
                    .setTitle(getString(R.string.alert_title))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }


}

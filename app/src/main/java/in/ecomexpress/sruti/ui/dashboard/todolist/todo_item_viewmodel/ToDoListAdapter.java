package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;

import static in.ecomexpress.sruti.utils.CommonUtils.maskNo;
import static in.ecomexpress.sruti.utils.MessageManager.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.WarningDialogBinding;
import in.ecomexpress.sruti.model.menifestdata.Address;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.AsChildParent;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.ToDoAdaptertoActivityListener;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.CallOptionDialog;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ChildAsParentDialog;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.OtpVerificationActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.RecciQuestion;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ToDoListActivity;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;


/**
 * Created by 63091 on 03-07-2019.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> implements Filterable, AsChildParent {//implements Filterable
    public static String userName;
    int Count = 0;
    public List<Manifest_List> manifestListFiltered = new ArrayList<>();
    String myremarks;
    private ToDoListActivity context;
    private List<Manifest_List> manifestList = new ArrayList<>();
    private ToDoAdaptertoActivityListener adaptertoActivityListener;
    double currentlat, currentlng;

    //  String scanned_update;
    public ToDoListAdapter(ToDoListActivity context) {
        this.context = context;
    }

    public void settoDoAdaptertoActivityListener(ToDoAdaptertoActivityListener toDoAdaptertoActivityListener) {
        this.adaptertoActivityListener = toDoAdaptertoActivityListener;
    }

    public void updateView(List<Manifest_List> pickupList) {
        manifestList.clear();
        manifestList.addAll(pickupList);
        manifestListFiltered.clear();
        manifestListFiltered.addAll(pickupList);

        notifyDataSetChanged();
    }

    public void setReasonCode(String remarks) {
        this.myremarks = remarks;
    }

    public void setCountOnDrs(long picked, long total) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_item_pickup_new, parent, false));
            case 1:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_item_recci_pickup, parent, false));
            case 2:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_item_recci, parent, false));
        }
        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            switch (holder.getItemViewType()) {
                //pickup
                case 0:
                    long totalCount = manifestListFiltered.get(position).getTotal_shipment_count();
                    String manifestType = "";

                    if (manifestListFiltered.get(position).is_express_seller()) {
                        manifestType = "EXPRESS " + manifestListFiltered.get(position).getLocation_type();
                    } else {
                        manifestType = manifestListFiltered.get(position).getLocation_type();
                    }
                    if (manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code() != null && manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code().equalsIgnoreCase("EXPP")) {
                        holder.tvExpressPlus.setVisibility(View.VISIBLE);
                        holder.tvExpressPlus.setText(manifestListFiltered.get(position).getManifest_details().getSub_product_Type());
                    } else {
                        holder.tvExpressPlus.setVisibility(View.GONE);
                    }
                    String finalManifestType = manifestType;
                    if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                        context.getViewModel().loadManifest().observe(context, manifest_lists -> {
                            holder.client_type.setText(finalManifestType + notNull(manifest_lists.get(position).getShipment_count()));
                        });

                    } else {
                        try {
                            context.getViewModel().getCount(manifestListFiltered.get(position).getManifest_No()).observe(context, new Observer<Integer>() {
                                @Override
                                public void onChanged(@Nullable Integer integer) {
                                    String scanned_update = ("(" + "Picked : " + String.valueOf(integer) + " /" + "T.Shipment :" + totalCount + ")");
                                    holder.client_type.setText(finalManifestType + scanned_update);
                                }
                            });
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                    if (manifestListFiltered.get(position).getManifest_details().isIs_valid_contact_no() || manifestListFiltered.get(position).getManifest_details().getLocation().isIs_valid_secondary_contact_no()) {
                        holder.call.setImageResource(R.drawable.ic_call_new);

                    } else {
                        holder.call.setImageResource(R.drawable.ic_call_disable);

                    }


                    holder.tv_mid_no.setText(context.getString(R.string.mid) + " " + manifestListFiltered.get(position).getManifest_No());
                    holder.consignee_name.setText(manifestListFiltered.get(position).getManifest_details().getLocationName());
                    holder.client_name.setText(manifestListFiltered.get(position).getCust_name());
                    holder.full_address.setVisibility(View.GONE);
                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                    Address address1 = manifestListFiltered.get(position).getManifest_details().getAddress();
                    String add1 = address1.getLine1() + ", " + address1.getLine2() + ", " + address1.getLine3() + ", " + address1.getState() + ", " + address1.getCity() + ", " + address1.getPincode();
                    String fullAddress = add1.replaceAll(", null", "");
                    String fullAdd = fullAddress.replaceAll("null,", "");
                    String var = TextUtils.isEmpty(manifestListFiltered.get(position).getManifest_details().getConcernedPersonName()) ? "" : manifestListFiltered.get(position).getManifest_details().getConcernedPersonName() + ":";
                    String consta = TextUtils.isEmpty(String.valueOf(manifestListFiltered.get(position).getManifest_details().getLocation_contact_no())) ? "" : manifestListFiltered.get(position).getManifest_details().getLocation_contact_no() + "";
                    String second_number = TextUtils.isEmpty(String.valueOf(manifestListFiltered.get(position).getManifest_details().getLocation().getSecondary_contact_no())) ? "" : manifestListFiltered.get(position).getManifest_details().getLocation().getSecondary_contact_no() + "";
                    String contactdetail = fullAdd.replaceAll("null,", "") + "(" + var + consta + ")";
                      holder.addressline1.setText(contactdetail);

                    userName = manifestListFiltered.get(position).getCust_name();
                    String finalMapAddress = address1.getLine1() + ", " + fullAdd;
                    Remark remark = manifestListFiltered.get(position).getTemporary_remark();

                    if (context.getViewModel().getDataManager().get_enable_calling() == null || context.getViewModel().getDataManager().get_enable_calling().equalsIgnoreCase("false")) {
                        holder.call.setVisibility(View.GONE);
                    }
                    if (remark != null && remark.remark != null && remark.remark.length() > 0) {
                        holder.layoutRemarks.setVisibility(View.VISIBLE);
                        holder.remarks.setText(manifestListFiltered.get(position).getTemporary_remark().remark);
                    } else {
                        holder.layoutRemarks.setVisibility(View.GONE);
                    }
                    try {
                        holder.remarks.setText(myremarks);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (manifestListFiltered.get(position).getSetting().getQr_code_check() == 1) {
                        holder.qrcode_scan.setVisibility(View.VISIBLE);
                    } else {
                        holder.qrcode_scan.setVisibility(View.GONE);
                    }
                    if (manifestListFiltered.get(position).getSetting().getAdvance_pickup_check() == 1) {
                        holder.advance_item.setVisibility(View.VISIBLE);
                    } else {
                        holder.advance_item.setVisibility(View.GONE);
                    }
                    if (manifestListFiltered.get(position).getIs_mps_manifest() == true) {
                        holder.location_verification.setVisibility(View.VISIBLE);
                    } else {
                        holder.location_verification.setVisibility(View.GONE);
                    }

                    if (manifestListFiltered.get(position).getManifest_details().isVerify_geocode()) {
                        holder.verifyGeocode.setVisibility(View.VISIBLE);
                    } else {
                        holder.verifyGeocode.setVisibility(View.GONE);
                    }

                    if (!(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in().isEmpty() && manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out().isEmpty())) {
                        holder.scheduled_time.setVisibility(View.VISIBLE);
                        //    holder.scheduled_time.setText(parseDate(manifestListFiltered.get(position).getManifest_details().getPickup_slot()));
                        holder.scheduled_time.setText(CommonUtils.getTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in()) + "-" + CommonUtils.getTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out()));

                    } else {
                        holder.scheduled_time.setVisibility(View.GONE);
                    }
                    if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED) {
                        holder.Linear_top.setBackgroundResource(R.drawable.delivered_gradient);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_FAILED) {
                        holder.Linear_top.setBackgroundResource(R.drawable.undelivered_gradient);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PENDING) {
                        holder.Linear_top.setBackgroundResource(R.color.white);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                        holder.Linear_top.setBackgroundResource(R.drawable.advance_gradient);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.SELF_DROP) {
                        holder.Linear_top.setBackgroundResource(R.drawable.self_drop_gradient);

                    } else {

                    }

                    holder.navView.setOnClickListener(view -> {
                        try {

                            if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC || manifestListFiltered.get(position).getCommit_status() == Constants.SELF_DROP) {
                                Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                                try {
                                    Uri uri = null;

//                                    double lat = manifestListFiltered.get(position).getSetting().getGeo_code_latitude();
//                                    double lng = manifestListFiltered.get(position).getSetting().getGeo_code_longitude();

                                    double lat = manifestListFiltered.get(position).getManifest_details().getLocation().getLatitude();
                                    double lng = manifestListFiltered.get(position).getManifest_details().getLocation().getLongitude();


                                    if (lng != 0.0 && lat != 0.0) {
                                        String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                                        //    uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", currentlat, currentlng, "MyLocation", lat, lng, "Destination"));
                                        uri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=d&avoid=tf");

                                    } else {
                                        uri = Uri.parse("google.navigation:q=" + finalMapAddress + "&mode=d&avoid=tf");

                                    }
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    if (CommonUtils.isAppInstalled("com.google.android.apps.maps", context)) {
                                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "onMapClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                    });


                    holder.call.setOnClickListener(view -> {
                        if (!manifestListFiltered.get(position).getManifest_details().isIs_valid_contact_no() && !manifestList.get(position).getManifest_details().getLocation().isIs_valid_secondary_contact_no()) {
                            showToast(context, context.getString(R.string.phone_number_is_invalid));
                        } else {
                            if ((context.getViewModel().getDataManager().get_enable_calling().equalsIgnoreCase("true") && manifestListFiltered.get(position).getManifest_details().isIs_valid_contact_no()) && (context.getViewModel().getDataManager().get_enable_calling().equalsIgnoreCase("true") && manifestList.get(position).getManifest_details().getLocation().isIs_valid_secondary_contact_no())) {
                                CallOptionDialog callOptionDialog = new CallOptionDialog(context, consta, second_number, manifestList.get(position), context.getViewModel());
                                callOptionDialog.show();
                            } else {
                                if (second_number.equalsIgnoreCase(" ") || manifestList.get(position).getManifest_details().getLocation().isIs_valid_secondary_contact_no() == false) {
                                    if (context.getViewModel().getDataManager().get_enable_calling().equalsIgnoreCase("true") && manifestListFiltered.get(position).getManifest_details().isIs_valid_contact_no() == true) {
                                        callPhoneNumber(consta);
                                    }
                                } else {
                                    callPhoneNumber(second_number);
                                }
                            }
                        }

                    });

                    holder.itemView.setOnClickListener(view -> {
                         long start_time = 0L;
                        long end_time = 0L;
                        String start = "";
                        String end = "";
                        if (manifestListFiltered.get(position).getManifest_details().getLocation() != null && manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out() != null && manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in() != null) {
                            start_time = CommonUtils.changeDateTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in());
                            end_time = CommonUtils.changeDateTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out());
                            start = CommonUtils.getTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in());
                            end = CommonUtils.getTime(manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out());
                        }
                        if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC || manifestListFiltered.get(position).getCommit_status() == Constants.SELF_DROP) {
                            Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();
                        } else {
                            if (isAllVehicleDepart()) {
                                return;
                            }
                            if (CommonUtils.checkTimeslot(start_time, end_time)) {
                                if (manifestListFiltered.get(position).getLocation_type().equalsIgnoreCase("seller")) {

                                    openOtherACtivityViaSeller(manifestListFiltered.get(position));
                                } else {
                                    try {
                                        openOtherActivtyViaWarehouse(manifestListFiltered.get(position));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                if (manifestListFiltered.get(position).getManifest_details().getLocation() != null && manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_out() != null && manifestListFiltered.get(position).getManifest_details().getLocation().getPickup_slot_time_in() != null) {
                                    if (manifestListFiltered.get(position).getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("W")) {


                                        showManifestWarningDialog("W", start + " - " + end, manifestListFiltered.get(position), manifestListFiltered.get(position).getLocation_type());

                                    } else if (manifestListFiltered.get(position).getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("R")) {
                                        showManifestWarningDialog("R", start + " - " + end, manifestListFiltered.get(position), manifestListFiltered.get(position).getLocation_type());

                                    } else {
                                        if (manifestListFiltered.get(position).getLocation_type().equalsIgnoreCase("seller")) {
                                            openOtherACtivityViaSeller(manifestListFiltered.get(position));
                                        } else {
                                            openOtherActivtyViaWarehouse(manifestListFiltered.get(position));
                                        }
                                    }
                                } else {
                                    if (manifestListFiltered.get(position).getLocation_type().equalsIgnoreCase("seller")) {
                                        openOtherACtivityViaSeller(manifestListFiltered.get(position));
                                    } else {
                                        openOtherActivtyViaWarehouse(manifestListFiltered.get(position));
                                    }
                                }
                            }

                        }

                    });


                    holder.itemView.setOnLongClickListener(view -> {
                        if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                            String message = "Are you sure you want to sync this Manifest to server?";
                            AlertDialog dialog = alertDialog.setMessage(message).setTitle("Server Commit").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            adaptertoActivityListener.commitToserver(manifestListFiltered.get(position).getManifest_No());

                                        }
                                    }

                            ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                            dialog.setCancelable(true);
                            dialog.show();

                        }
                        return false;
                    });
                    break;

                //recce pickup
                case 1:
                    try {
                        holder.consignee_name.setText(manifestListFiltered.get(position).getManifest_details().getLocationName());
                        holder.client_name.setText(manifestListFiltered.get(position).getCust_name());
                        if (manifestListFiltered.get(position).is_express_seller()) {
                            holder.client_type.setText("EXPRESS " + manifestListFiltered.get(position).getLocation_type());
                        } else {
                            holder.client_type.setText(manifestListFiltered.get(position).getLocation_type());
                        }

                        if (manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code() != null && manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code().equalsIgnoreCase("EXPP")) {
                            holder.tvExpressPlus.setVisibility(View.VISIBLE);
                            holder.tvExpressPlus.setText(manifestListFiltered.get(position).getManifest_details().getSub_product_Type());
                        } else {
                            holder.tvExpressPlus.setVisibility(View.GONE);
                        }
                        holder.full_address.setVisibility(View.GONE);
                        if (manifestListFiltered.get(position).getSetting().getQr_code_check() == 1) {
                            holder.qrcode_scan.setVisibility(View.VISIBLE);
                        } else {
                            holder.qrcode_scan.setVisibility(View.GONE);
                        }
                        if (manifestListFiltered.get(position).getSetting().getAdvance_pickup_check() == 1) {
                            holder.advance_item.setVisibility(View.VISIBLE);
                        } else {
                            holder.advance_item.setVisibility(View.GONE);
                        }
                        if (!manifestListFiltered.get(position).getManifest_details().getPickup_slot().isEmpty()) {
                            holder.scheduled_time.setVisibility(View.VISIBLE);
                            holder.scheduled_time.setText(parseDate(manifestListFiltered.get(position).getManifest_details().getPickup_slot()));
                        } else {
                            holder.scheduled_time.setVisibility(View.GONE);
                        }

                        try {
                            holder.remarks.setText(myremarks);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                        Address address = manifestListFiltered.get(position).getManifest_details().getAddress();
                        String add = address.getLine2() + ", " + address.getLine3() + ", " + address.getState() + ", " + address.getCity() + ", " + address.getPincode();
                        String fullAddressReccePickup = add.replaceAll(", null", "");
                        String fullAddReccePic = fullAddressReccePickup.replaceAll("null,", "");
                        String var1 = TextUtils.isEmpty(manifestListFiltered.get(position).getManifest_details().getConcernedPersonName()) ? "" : manifestListFiltered.get(position).getManifest_details().getConcernedPersonName() + ":";
                        String consta1 = TextUtils.isEmpty(String.valueOf(manifestListFiltered.get(position).getManifest_details().getLocation_contact_no())) ? "" : manifestListFiltered.get(position).getManifest_details().getLocation_contact_no() + "";
                        String contactdetail1 = fullAddReccePic.replaceAll("null,", "") + "(" + var1 + consta1 + ")";


                        holder.addressline1.setText(address.getLine1() + "(" + manifestListFiltered.get(position).getManifest_details().getLocation_contact_no() + ")");
                        holder.full_address.setText(contactdetail1);
                        Remark remark1 = manifestListFiltered.get(position).getTemporary_remark();
                        if (remark1 != null && remark1.remark != null && remark1.remark.length() > 0) {
                            holder.layoutRemarks.setVisibility(View.VISIBLE);
                            holder.remarks.setText(manifestListFiltered.get(position).getTemporary_remark().remark);
                        } else {
                            holder.layoutRemarks.setVisibility(View.GONE);
                        }
                        userName = manifestListFiltered.get(position).getCust_name();
                        String finalAddressRecc = address.getLine1() + ", " + fullAddReccePic;

                        if (manifestListFiltered.get(position).getManifest_details().isVerify_geocode()) {
                            holder.verifyGeocode.setVisibility(View.VISIBLE);
                        } else {
                            holder.verifyGeocode.setVisibility(View.GONE);
                        }

                        if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED) {
                            holder.pickup.setBackgroundResource(R.drawable.delivered_gradient);
                            holder.tray.setBackgroundResource(R.drawable.delivered_gradient);
                            holder.innerTray.setBackgroundResource(R.drawable.delivered_gradient);

                        } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_FAILED) {
                            holder.pickup.setBackgroundResource(R.drawable.undelivered_gradient);
                            holder.tray.setBackgroundResource(R.drawable.undelivered_gradient);
                            holder.innerTray.setBackgroundResource(R.drawable.undelivered_gradient);

                        } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PENDING) {
                            holder.pickup.setBackgroundResource(R.color.white);
                            holder.tray.setBackgroundResource(R.color.colorIconTray);
                            holder.innerTray.setBackgroundResource(R.color.colorIconTray);

                        } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                            holder.pickup.setBackgroundResource(R.drawable.advance_gradient);
                            holder.tray.setBackgroundResource(R.drawable.advance_gradient);
                            holder.innerTray.setBackgroundResource(R.drawable.advance_gradient);
                        } else {

                            holder.indicator.setOnClickListener(view -> {
                                try {

                                    if (holder.full_address.getVisibility() == View.VISIBLE) {
                                        holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                        holder.full_address.setVisibility(View.GONE);
                                        holder.more_less.setText("more");
                                    } else {
                                        holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                        holder.more_less.setText("less");
                                        holder.full_address.setVisibility(View.VISIBLE);


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            holder.clickView.setOnClickListener(view -> {
                                try {

                                    if (holder.full_address.getVisibility() == View.VISIBLE) {
                                        holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                        holder.full_address.setVisibility(View.GONE);
                                        holder.more_less.setText("more");
                                    } else {
                                        holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                        holder.more_less.setText("less");
                                        holder.full_address.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }


                        holder.navView.setOnClickListener(view -> {
                            try {

                                if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                                    Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                                    try {
                                        Uri uri = null;

//                                        double lat = manifestListFiltered.get(position).getSetting().getGeo_code_latitude();
//                                        double lng = manifestListFiltered.get(position).getSetting().getGeo_code_longitude();

                                        double lat = manifestListFiltered.get(position).getManifest_details().getLocation().getLatitude();
                                        double lng = manifestListFiltered.get(position).getManifest_details().getLocation().getLongitude();
                                        ;

                                        Log.d("Todolat", String.valueOf(lat));
                                        Log.d("Todolng", String.valueOf(lng));
                                        Log.d("Todoadd", finalAddressRecc);
                                        if (lng != 0.0 && lat != 0.0) {
                                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                                            //   uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", currentlat, currentlng, "MyLocation", lat, lng, "Destination"));
                                            uri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=d&avoid=tf");

                                        } else {
                                            uri = Uri.parse("google.navigation:q=" + finalAddressRecc + "&mode=d&avoid=tf");
                                        }
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", context)) {
                                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, "onMapClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }

                        });
                        holder.tray.setOnClickListener(view -> {
                            try {

                                if (holder.full_address.getVisibility() == View.VISIBLE) {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                    holder.full_address.setVisibility(View.GONE);
                                    holder.more_less.setText("more");
                                } else {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                    holder.more_less.setText("less");
                                    holder.full_address.setVisibility(View.VISIBLE);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        holder.itemView.setOnClickListener(view -> {
                            if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                                Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();

                            } else {
                                if (isAllVehicleDepart()) return;

                                if (manifestListFiltered.get(position).getLocation_type().equalsIgnoreCase("seller")) {

                                    Intent intent = new Intent(context, RecciQuestion.class);
                                    intent.putExtra("pickup_location_id", manifestListFiltered.get(position).getPickup_location_id());
                                    intent.putExtra("location_type", manifestListFiltered.get(position).getLocation_type());
                                    intent.putExtra("manifest_type", manifestListFiltered.get(position).getManifest_type());
                                    intent.putExtra("vender_detail", manifestListFiltered.get(position).getManifest_details());
                                    intent.putExtra("question_ids", manifestListFiltered.get(position).getQuestion_id());
                                    intent.putExtra("data", manifestListFiltered.get(position));
                                    intent.putExtra("custmer_name", manifestListFiltered.get(position).getCust_name());
                                    intent.putExtra("manifest_no", manifestListFiltered.get(position).getManifest_No());
                                    context.startActivity(intent);
                                } else {
                                    if (!context.getViewModel().getDataManager().getParent() && manifestListFiltered.get(position).getLocation_type().equalsIgnoreCase("warehouse")) {
                                        ChildAsParentDialog childAsParentDialog = new ChildAsParentDialog();
                                        childAsParentDialog.ConfirmationDialog(aschild -> {
                                            context.getViewModel().getDataManager().asParentOrChild(aschild);
                                            Intent intent = new Intent(context, RecciQuestion.class);
                                            intent.putExtra("pickup_location_id", manifestListFiltered.get(position).getPickup_location_id());
                                            intent.putExtra("location_type", manifestListFiltered.get(position).getLocation_type());
                                            intent.putExtra("manifest_type", manifestListFiltered.get(position).getManifest_type());
                                            intent.putExtra("vender_detail", manifestListFiltered.get(position).getManifest_details());
                                            intent.putExtra("question_ids", manifestListFiltered.get(position).getQuestion_id());
                                            intent.putExtra("data", manifestListFiltered.get(position));
                                            intent.putExtra("custmer_name", manifestListFiltered.get(position).getCust_name());
                                            intent.putExtra("manifest_no", manifestListFiltered.get(position).getManifest_No());
                                            context.startActivity(intent);

                                        });
                                        childAsParentDialog.show(context.getSupportFragmentManager(), "confirmcmt");
                                    } else {
                                        Intent intent = new Intent(context, RecciQuestion.class);
                                        intent.putExtra("pickup_location_id", manifestListFiltered.get(position).getPickup_location_id());
                                        intent.putExtra("location_type", manifestListFiltered.get(position).getLocation_type());
                                        intent.putExtra("manifest_type", manifestListFiltered.get(position).getManifest_type());
                                        intent.putExtra("vender_detail", manifestListFiltered.get(position).getManifest_details());
                                        intent.putExtra("question_ids", manifestListFiltered.get(position).getQuestion_id());
                                        intent.putExtra("data", manifestListFiltered.get(position));
                                        intent.putExtra("custmer_name", manifestListFiltered.get(position).getCust_name());
                                        intent.putExtra("manifest_no", manifestListFiltered.get(position).getManifest_No());
                                        context.startActivity(intent);
                                    }
                                }


                            }

                        });

                        if (context.getViewModel().getDataManager().getParent()) {
                            holder.itemView.setOnLongClickListener(view -> {

                                return false;
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                //recce
                case 2:
                    holder.consignee_name.setText(manifestListFiltered.get(position).getManifest_details().getLocationName());
                    holder.client_name.setText(manifestListFiltered.get(position).getCust_name());
                    //   holder.client_type.setText(manifestListFiltered.get(position).getLocation_type());
                    if (manifestListFiltered.get(position).is_express_seller()) {
                        holder.client_type.setText("EXPRESS " + manifestListFiltered.get(position).getLocation_type());
                    } else {
                        holder.client_type.setText(manifestListFiltered.get(position).getLocation_type());
                    }


                    if (manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code() != null && manifestListFiltered.get(position).getManifest_details().getProduct_sub_type_code().equalsIgnoreCase("EXPP")) {
                        holder.tvExpressPlus.setVisibility(View.VISIBLE);
                        holder.tvExpressPlus.setText(manifestListFiltered.get(position).getManifest_details().getSub_product_Type());
                    } else {
                        holder.tvExpressPlus.setVisibility(View.GONE);
                    }
                    holder.full_address.setVisibility(View.GONE);
                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                    Address address2 = manifestListFiltered.get(position).getManifest_details().getAddress();
                    String add2 = address2.getLine2() + ", " + address2.getLine3() + ", " + address2.getState() + ", " + address2.getCity() + ", " + address2.getPincode();
                    String fullAddress2 = add2.replaceAll(", null", "");
                    String fullAddRecce3 = fullAddress2.replaceAll("null,", "");
                    String var2 = TextUtils.isEmpty(manifestListFiltered.get(position).getManifest_details().getConcernedPersonName()) ? "" : manifestListFiltered.get(position).getManifest_details().getConcernedPersonName() + ":";
                    String consta2 = TextUtils.isEmpty(String.valueOf(manifestListFiltered.get(position).getManifest_details().getLocation_contact_no())) ? "" : manifestListFiltered.get(position).getManifest_details().getLocation_contact_no() + "";
                    String contactdetail1 = fullAddRecce3.replaceAll("null,", "") + "(" + var2 + consta2 + ")";

                    if (manifestListFiltered.get(position).getManifest_details().isVerify_geocode()) {
                        holder.verifyGeocode.setVisibility(View.VISIBLE);
                    } else {
                        holder.verifyGeocode.setVisibility(View.GONE);
                    }
                    holder.addressline1.setText(address2.getLine1());
                    holder.full_address.setText(contactdetail1);
                    Remark remark2 = manifestListFiltered.get(position).getTemporary_remark();
                    if (remark2 != null && remark2.remark != null && remark2.remark.length() > 0) {
                        holder.layoutRemarks.setVisibility(View.VISIBLE);
                        holder.remarks.setText(manifestListFiltered.get(position).getTemporary_remark().remark);
                    } else {
                        holder.layoutRemarks.setVisibility(View.GONE);
                    }
                    userName = manifestListFiltered.get(position).getCust_name();
                    String finalAddressReccc = address2.getLine1() + ", " + fullAddRecce3;
                    try {
                        holder.remarks.setText(myremarks);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (manifestListFiltered.get(position).getSetting().getQr_code_check() == 1) {
                        holder.qrcode_scan.setVisibility(View.VISIBLE);
                    } else {
                        holder.qrcode_scan.setVisibility(View.GONE);
                    }

                    if (manifestListFiltered.get(position).getSetting().getAdvance_pickup_check() == 1) {
                        holder.advance_item.setVisibility(View.VISIBLE);
                    } else {
                        holder.advance_item.setVisibility(View.GONE);
                    }
                    if (!manifestListFiltered.get(position).getManifest_details().getPickup_slot().isEmpty()) {
                        holder.scheduled_time.setVisibility(View.VISIBLE);
                        holder.scheduled_time.setText(parseDate(manifestListFiltered.get(position).getManifest_details().getPickup_slot()));
                    } else {
                        holder.scheduled_time.setVisibility(View.GONE);
                    }

                    System.out.println("recci_count" + manifestListFiltered.get(position).getCommit_status());
                    if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED) {
                        holder.pickup.setBackgroundResource(R.drawable.delivered_gradient);

                        holder.tray.setBackgroundResource(R.drawable.delivered_gradient);
                        holder.innerTray.setBackgroundResource(R.drawable.delivered_gradient);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_FAILED) {
                        holder.pickup.setBackgroundResource(R.drawable.undelivered_gradient);
                        holder.tray.setBackgroundResource(R.drawable.undelivered_gradient);
                        holder.innerTray.setBackgroundResource(R.drawable.undelivered_gradient);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PENDING) {
                        holder.pickup.setBackgroundResource(R.color.white);

                        holder.tray.setBackgroundResource(R.color.colorIconTray);
                        holder.innerTray.setBackgroundResource(R.color.colorIconTray);

                    } else if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {


                        holder.pickup.setBackgroundResource(R.drawable.advance_gradient);
                        holder.tray.setBackgroundResource(R.drawable.advance_gradient);
                        holder.innerTray.setBackgroundResource(R.drawable.advance_gradient);
                    } else {
                        holder.indicator.setOnClickListener(view -> {
                            try {

                                if (holder.full_address.getVisibility() == View.VISIBLE) {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                    holder.full_address.setVisibility(View.GONE);
                                    holder.more_less.setText("more");
                                } else {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                    holder.more_less.setText("less");
                                    holder.full_address.setVisibility(View.VISIBLE);


                                }
                            } catch (Exception e) {
                            }
                        });
                        holder.clickView.setOnClickListener(view -> {
                            try {

                                if (holder.full_address.getVisibility() == View.VISIBLE) {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                    holder.full_address.setVisibility(View.GONE);
                                    holder.more_less.setText("more");
                                } else {
                                    holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                    holder.more_less.setText("less");
                                    holder.full_address.setVisibility(View.VISIBLE);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    holder.tray.setOnClickListener(view -> {
                        try {

                            if (holder.full_address.getVisibility() == View.VISIBLE) {
                                holder.indicator.setBackgroundResource(R.drawable.ic_arrowdown);
                                holder.full_address.setVisibility(View.GONE);
                                holder.more_less.setText("more");
                            } else {
                                holder.indicator.setBackgroundResource(R.drawable.ic_arrowup);
                                holder.more_less.setText("less");
                                holder.full_address.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    holder.navView.setOnClickListener(view -> {
                        try {

                            if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                                Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                                try {
                                    Uri uri = null;
                                    double lat = manifestListFiltered.get(position).getManifest_details().getLocation().getLatitude();
                                    double lng = manifestListFiltered.get(position).getManifest_details().getLocation().getLongitude();
                                    ;


                                    Log.d("Todolat", String.valueOf(lat));
                                    Log.d("Todolng", String.valueOf(lng));
                                    Log.d("Todoadd", finalAddressReccc);

                                    if (lng != 0.0 && lat != 0.0) {
                                        String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                                        //   uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", currentlat, currentlng, "MyLocation", lat, lng, "Destination"));
                                        uri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=d&avoid=tf");

                                    } else {
                                        uri = Uri.parse("google.navigation:q=" + finalAddressReccc + "&mode=d&avoid=tf");
                                    }
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    if (CommonUtils.isAppInstalled("com.google.android.apps.maps", context)) {
                                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "onMapClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }

                    });
                    holder.itemView.setOnClickListener(view -> {
                        if (manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_PICKED || manifestListFiltered.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
                            Toast.makeText(context, R.string.shipment_commit, Toast.LENGTH_SHORT).show();

                        } else {
                            if (isAllVehicleDepart()) return;
                            Intent intent = new Intent(context, RecciQuestion.class);
                            intent.putExtra("pickup_location_id", manifestListFiltered.get(position).getPickup_location_id());
                            intent.putExtra("location_type", manifestListFiltered.get(position).getLocation_type());
                            intent.putExtra("manifest_type", manifestListFiltered.get(position).getManifest_type());
                            intent.putExtra("vender_detail", manifestListFiltered.get(position).getManifest_details());
                            intent.putExtra("question_ids", manifestListFiltered.get(position).getQuestion_id());
                            intent.putExtra("custmer_name", manifestListFiltered.get(position).getCust_name());
                            intent.putExtra("manifest_no", manifestListFiltered.get(position).getManifest_No());
                            context.startActivity(intent);
                        }
                    });

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callPhoneNumber(String consta) {
        try {
            context.getViewModel().vendorContactNumber.set(consta);
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + consta));
                context.startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + consta));
                context.startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private String parseDate(String startTime) {
        try {
            if (startTime.equals("00:00:00-00:00:00")) {
                return "";
            }
            String[] arr = startTime.split("-");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
            Date start = dateFormatter.parse(arr[0]);
            Date end = dateFormatter.parse(arr[1]);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            String displayValue = timeFormatter.format(start);
            String displayValue1 = timeFormatter.format(end);
            return displayValue + "-" + displayValue1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isAllVehicleDepart() {
        if (context.getViewModel().getDataManager().is_Ecom_Vehicle()) {
            boolean leftvehicaleDepart = false;
            for (int j = 0; j < context.getViewModel().getDataManager().getRouteDetail().size(); j++) {
                if (!context.getViewModel().getDataManager().getRouteDetail().get(j).isDeparted()) {
                    leftvehicaleDepart = true;
                    break;
                }
            }
            if (!leftvehicaleDepart) {
                Toast.makeText(context, R.string.depart_msg, Toast.LENGTH_SHORT).show();
                return true;
            } else return false;
        } else {
            if (context.getViewModel().getDataManager().getDepartSelfVehicle()) {
                Toast.makeText(context, R.string.depart_msg_self, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        Manifest_List manifest_list = manifestListFiltered.get(position);
        System.out.println("TYPE  " + manifest_list.getManifest_type());
        if (manifest_list.getManifest_type() != null) {
            if (manifest_list.getManifest_type().equals("P")) {
                return 0;
            } else if (manifest_list.getManifest_type().equals("PR")) {
                return 1;
            } else if (manifest_list.getManifest_type().equals("R")) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return manifestListFiltered != null ? manifestListFiltered.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    manifestListFiltered = manifestList;
                } else {
                    List<Manifest_List> filteredList = new ArrayList<>();
                    filteredList.clear();
                    for (Manifest_List row : manifestList) {

                        if (String.valueOf(row.getCust_name()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getLocationName()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getAddress().getLine1()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getAddress().getLine2()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getAddress().getLine3()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getLocationContactNo()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getSub_product_Type()).toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getManifest_details().getProduct_sub_type_code()).toLowerCase().contains(charString.toLowerCase())

                        ) {
                            System.out.println("row" + row.getCust_name() + "  " + row.getManifest_details().getLocationName());
                            System.out.println("row" + charString.toLowerCase());
                            filteredList.add(row);
                        }

                    }
                    manifestListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = manifestListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                manifestListFiltered = (ArrayList<Manifest_List>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void asChildOrParent(boolean aschild) {

    }

    public void setLatLng(double wayLatitude, double wayLongitude) {
        this.currentlat = wayLatitude;
        this.currentlng = wayLongitude;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView consignee_name, tv_mid_no, remarks, scanned_count, more_less, client_name, count, full_address, client_type, addressline1, scheduled_time, type_of_task;
        private ImageView indicator, navView, call, qrcode_scan, location_verification, advance_item, verifyGeocode;
        private LinearLayout clickView, action_icon;
        private RelativeLayout pickup;
        private LinearLayout Linear_top1, Linear_top, color;
        private LinearLayout tray, layoutRemarks;
        private LinearLayout innerTray;
        private TextView tvExpressPlus;

        public ViewHolder(View itemView) {
            super(itemView);
            type_of_task = itemView.findViewById(R.id.type_of_task);
            scanned_count = itemView.findViewById(R.id.scanned_count);
            clickView = itemView.findViewById(R.id.clickView);
            action_icon = itemView.findViewById(R.id.action_icon);
            full_address = itemView.findViewById(R.id.full_address);
            more_less = itemView.findViewById(R.id.more_less);
            consignee_name = itemView.findViewById(R.id.consignee_name);
            remarks = itemView.findViewById(R.id.remarks);
            client_name = itemView.findViewById(R.id.client_name);
            client_type = itemView.findViewById(R.id.client_type);
            addressline1 = itemView.findViewById(R.id.addressline1);
            scheduled_time = itemView.findViewById(R.id.scheduled_time);
            navView = itemView.findViewById(R.id.navView);
            indicator = itemView.findViewById(R.id.indicator);
            call = itemView.findViewById(R.id.call);
            qrcode_scan = itemView.findViewById(R.id.qrcode_scan);
            location_verification = itemView.findViewById(R.id.location_verification);
            verifyGeocode = itemView.findViewById(R.id.verifyGeocode);
            advance_item = itemView.findViewById(R.id.advance_item);
            pickup = itemView.findViewById(R.id.pickup);
            Linear_top1 = itemView.findViewById(R.id.Linear_top1);
            Linear_top = itemView.findViewById(R.id.Linear_top);
            color = itemView.findViewById(R.id.color);
            tray = itemView.findViewById(R.id.tray);
            layoutRemarks = itemView.findViewById(R.id.layout_remarks);
            innerTray = itemView.findViewById(R.id.inner_tray);
            tvExpressPlus = itemView.findViewById(R.id.tvExpressPlus);
            tv_mid_no = itemView.findViewById(R.id.tv_mid_no);


        }

    }

    String notNull(String vl) {
        return TextUtils.isEmpty(vl) ? "" : vl;
    }

    public void showManifestWarningDialog(String validate, String pickup_slot, Manifest_List manifest_list, String location_type) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        WarningDialogBinding warningDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.warning_dialog, (ViewGroup) context.getViewDataBinding().getRoot(), false);
        dialog.setContentView(warningDialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (validate.equalsIgnoreCase("W")) {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + context.getString(R.string.warning_message));
        } else {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + context.getString(R.string.restriction_message));
        }
        warningDialogBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate.equalsIgnoreCase("W")) {
                    if (location_type.equalsIgnoreCase("seller")) {
                        openOtherACtivityViaSeller(manifest_list);
                    } else {
                        openOtherActivtyViaWarehouse(manifest_list);
                    }
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }


            }
        });

        dialog.show();

    }

    public void openOtherActivtyViaWarehouse(Manifest_List manifest_list) {
        if (manifest_list.getLocation_type().equalsIgnoreCase("warehouse")) {
            if (manifest_list.getFlags().isOtp_required() == true && manifest_list.getLocation_otp() != null) {
                Intent intent = new Intent(context, OtpVerificationActivity.class);
                intent.putExtra("otp_validation", manifest_list.getLocation_otp());
                intent.putExtra("manifest_type", manifest_list.getManifest_type());
                intent.putExtra("location_type", manifest_list.getLocation_type());
                intent.putExtra("manifest_no", manifest_list.getManifest_No());
                intent.putExtra("data", manifest_list);
                context.startActivity(intent);
            } else if (manifest_list.getSetting().getQr_code_check() == 1) {
                try {
                    adaptertoActivityListener.onBarcodeScanItem(manifest_list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = WarehouseActivity.getStartIntent(context);
                intent.putExtra("data", manifest_list);
                intent.putExtra("manifest_no", manifest_list.getManifest_No());
                intent.putExtra("assigned_count", manifest_list.getTotal_shipment_count());
                context.startActivity(intent);

            }
        } else if (manifest_list.getManifest_type().equalsIgnoreCase("PR")) {

            Intent intent = new Intent(context, RecciQuestion.class);
            intent.putExtra("pickup_location_id", manifest_list.getPickup_location_id());
            intent.putExtra("location_type", manifest_list.getLocation_type());
            intent.putExtra("manifest_type", manifest_list.getManifest_type());
            intent.putExtra("vender_detail", manifest_list.getManifest_details());
            intent.putExtra("question_ids", manifest_list.getQuestion_id());
            intent.putExtra("data", manifest_list);
            intent.putExtra("custmer_name", manifest_list.getCust_name());
            intent.putExtra("manifest_no", manifest_list.getManifest_No());
            context.startActivity(intent);
        }
    }

    public void openOtherACtivityViaSeller(Manifest_List manifest_list) {
        if (manifest_list.getFlags().isOtp_required() == true && manifest_list.getLocation_otp() != null) {
            Intent intent = new Intent(context, OtpVerificationActivity.class);
            intent.putExtra("otp_validation", manifest_list.getLocation_otp());
            intent.putExtra("manifest_type", manifest_list.getManifest_type());
            intent.putExtra("location_type", manifest_list.getLocation_type());
            intent.putExtra("manifest_no", manifest_list.getManifest_No());
            intent.putExtra("data", manifest_list);
            context.startActivity(intent);
        } else if (manifest_list.getSetting().getQr_code_check() == 1) {
            try {
                adaptertoActivityListener.onBarcodeScanItem(manifest_list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Intent intent = SellerActivity.getStartIntent(context);
                intent.putExtra("data", manifest_list);
                intent.putExtra("manifest_no", manifest_list.getManifest_No());
                intent.putExtra("assigned_count", manifest_list.getTotal_shipment_count());
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}

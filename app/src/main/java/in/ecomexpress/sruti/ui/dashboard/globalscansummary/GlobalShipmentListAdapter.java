package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.dashboard.globalscan.GlobalScanScreenActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.IShipmentNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;


/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd
 */

public class GlobalShipmentListAdapter extends RecyclerView.Adapter<GlobalShipmentListAdapter.ViewHolder> implements Filterable {
    Shipment_Detail shipmentDetail;
    IShipmentNavigator shipmentNavigator;
    private Context mContext;
    private List<Shipment_Detail> shipmentDetailsList = new ArrayList<>();
    private List<Shipment_Detail> shipmentDetailsListMain = new ArrayList<>();
    private HashMap<Integer, ReasonCodeList> reasonCodeMaster;
    private ArrayList<ReasonCodeList> BPreasonCode;
    private boolean isParent;
    private GlobalShipmentListActivity globalShipmentListActivity;

    private Manifest_List manifest_list;

    private String sruti_d_link="";

    public GlobalShipmentListAdapter(Context mContext, IShipmentNavigator shipmentNavigator) {
        globalShipmentListActivity = (GlobalShipmentListActivity) mContext;
        isParent = globalShipmentListActivity.getViewModel().getDataManager().getParent();
        this.mContext = mContext;
        this.shipmentNavigator = shipmentNavigator;
    }

    public void setReasonCodeMaster(HashMap<Integer, ReasonCodeList> mapRTSReasonCodeMaster) {
        this.reasonCodeMaster = mapRTSReasonCodeMaster;
    }

    public void setBPReasonCode(ArrayList<ReasonCodeList> reasonCodeLists) {
        this.BPreasonCode = reasonCodeLists;
    }

    @Override
    public GlobalShipmentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_shipment_view, parent, false);
        return new GlobalShipmentListAdapter.ViewHolder(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(GlobalShipmentListAdapter.ViewHolder holder, int position) {
        shipmentDetail = shipmentDetailsList.get(position);
        holder.tvManifestNumber.setText("" + shipmentDetailsList.get(position).getAirWayBillNumber());
        holder.order_no.setText(shipmentDetailsList.get(position).getOrderNo());
        holder.mps_value.setText("" + shipmentDetailsList.get(position).getMaster_airwaybill_number());

        if (shipmentDetail.isIs_mps() == true && shipmentDetail.getMaster_airwaybill_number() != 0) {
            holder.mps.setVisibility(View.VISIBLE);
            holder.mpsCell.setVisibility(View.VISIBLE);
        } else {
            holder.mps.setVisibility(View.GONE);
            holder.mpsCell.setVisibility(View.GONE);
        }

        holder.checkbox_ckb.setTag(position);
        if (isParent)
            holder.checkbox_ckb.setVisibility(View.VISIBLE);
        else
            holder.checkbox_ckb.setVisibility(View.GONE);
        //in some cases, it will prevent unwanted situations
        holder.checkbox_ckb.setOnCheckedChangeListener(null);
        holder.checkbox_ckb.setChecked(shipmentDetail.isChecked());

        try {
            if (shipmentDetail.getStatus().equals(Constants.FAILED)) {
                //   holder.status_tv.setVisibility(View.VISIBLE);
                //  holder.status_tv.setText(reasonCodeMaster.get(shipmentDetail.getReason_id()).getReason_msg());

                if (shipmentDetail.isBp_Reason_Code_Applied()) {
                    Log.d("reasoncoode", shipmentDetail.getBrand_package_id());
                    holder.status_tv.setText(BPreasonCode.get(0).getReason_msg());
                } else {
                    holder.status_tv.setText(reasonCodeMaster.get(shipmentDetail.getReason_id()).getReason_msg());
                }
            } else {
                holder.status_tv.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (shipmentDetailsList.get(position).getStatus() != null) {
            switch (shipmentDetailsList.get(position).getStatus()) {

                case Constants.PENDING:
                    holder.checkbox_ckb.setVisibility(View.VISIBLE);
                    holder.shipment_layout.setBackgroundResource(R.color.white);
                    break;

                case Constants.PICKED:
                    holder.checkbox_ckb.setVisibility(View.GONE);
                    holder.shipment_layout.setBackgroundResource(R.drawable.delivered_gradient);
                    break;

                case Constants.FAILED:
                    holder.checkbox_ckb.setVisibility(View.GONE);
                    holder.shipment_layout.setBackgroundResource(R.drawable.undelivered_gradient);
                    break;


                case Constants.RTO_STATUS_1:
                    holder.checkbox_ckb.setVisibility(View.GONE);
                    holder.shipment_layout.setBackgroundResource(R.drawable.rto_gradient);
                    break;

                case Constants.RTO_STATUS_2:
                    holder.checkbox_ckb.setVisibility(View.GONE);
                    holder.shipment_layout.setBackgroundResource(R.drawable.rto_gradient);
                    break;

                default:
                    holder.checkbox_ckb.setVisibility(View.VISIBLE);
                    holder.shipment_layout.setBackgroundResource(R.color.white);
                    break;

            }


        }
        if (shipmentDetailsList.get(position).isAdvance() == true) {
            holder.checkbox_ckb.setVisibility(View.GONE);
            holder.shipment_layout.setBackgroundResource(R.drawable.advance_gradient);
        }
        if (!shipmentDetailsList.get(position).getStatus().contains(Constants.PENDING)) {
            holder.checkbox_ckb.setVisibility(View.GONE);
            holder.checkbox_ckb.setChecked(false);
        }

        if (shipmentDetailsList.get(position).getStatus().contains(Constants.PENDING)) {
            if (isParent) {
                holder.checkbox_ckb.setVisibility(View.VISIBLE);
                holder.checkbox_ckb.setChecked(shipmentDetailsList.get(position).isChecked());
            } else {
                holder.checkbox_ckb.setVisibility(View.GONE);
                holder.checkbox_ckb.setChecked(shipmentDetailsList.get(position).isChecked());
            }
        }
        holder.checkbox_ckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.checkbox_ckb.getTag();
                if (shipmentDetailsList.get(pos).isChecked()) {
                    shipmentDetailsList.get(pos).setChecked(false);
                } else {
                    shipmentDetailsList.get(pos).setChecked(true);
                }
            }
        });
        // Removal of Sruti_s_link by karan

        holder.shipment_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    if(shipmentDetailsList.get(position).getLocation_type().equalsIgnoreCase("warehouse") && (sruti_d_link.equalsIgnoreCase("true"))  ) {
                        if (shipmentDetailsList.get(position).getStatus().equals(Constants.PICKED) || shipmentDetailsList.get(position).getStatus().equals(Constants.FAILED)) {
                            new AlertDialog.Builder(mContext).setMessage(R.string.mark_assigned)
                                    .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            shipmentDetailsList.get(position).setStatus(Constants.PENDING);
                                            shipmentDetailsList.get(position).setChecked(false);
                                            shipmentNavigator.update(shipmentDetailsList.get(position));

                                            notifyDataSetChanged();
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        } else if (shipmentDetailsList.get(position).getStatus().equals(Constants.RTO_STATUS_1)) {

                        } else if (shipmentDetailsList.get(position).getStatus().equals(Constants.RTO_STATUS_2)) {

                        }
                    }
                    return false;
                } catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return this.shipmentDetailsList.size();
    }


    public void setData(List<Shipment_Detail> shipment_details, GlobalShipmentListActivity activity) {
        this.globalShipmentListActivity=activity;
        this.shipmentDetailsList.clear();
        this.sruti_d_link= this.globalShipmentListActivity.getViewModel().getDataManager().get_sruti_enable_delink_for_wh();
        if (this.shipmentDetailsList != null) {
            this.shipmentDetailsList.addAll(shipment_details);
            this.shipmentDetailsListMain.clear();
            this.shipmentDetailsListMain.addAll(shipment_details);
            notifyDataSetChanged();
            shipmentNavigator.notifyUpdatedCount();


        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout shipment_layout;
        private TextView tvManifestNumber;
        private CheckBox checkbox_ckb;
        private TextView status_tv;
        private TextView order_no;
        private TextView mps_value;
        private LinearLayout mpsCell;
        private ImageView mps;

        public ViewHolder(View itemView) {
            super(itemView);
            tvManifestNumber = itemView.findViewById(R.id.tv_manifest_awb_value);
            checkbox_ckb = itemView.findViewById(R.id.checkbox_ckb);
            shipment_layout = itemView.findViewById(R.id.shipment_layout);
            status_tv = itemView.findViewById(R.id.status_tv);
            order_no = itemView.findViewById(R.id.order_no);
            mps_value = itemView.findViewById(R.id.mps_value);
            mpsCell = itemView.findViewById(R.id.mps_cell);
            mps = itemView.findViewById(R.id.mps);
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    shipmentDetailsList = shipmentDetailsListMain;
                } else {
                    List<Shipment_Detail> filteredList = new ArrayList<>();
                    filteredList.clear();
                    for (Shipment_Detail row : shipmentDetailsListMain) {
                        if (String.valueOf(row.getAirwaybill_number()).toLowerCase().contains(charString.toLowerCase())) {
                            System.out.println("row" + charString.toLowerCase());
                            filteredList.add(row);
                        }
                    }
                    shipmentDetailsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = shipmentDetailsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                shipmentDetailsList = (ArrayList<Shipment_Detail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}

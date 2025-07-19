package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model;


import static in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity.isParent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityWarehouseShipmentSingleItemBinding;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces.IWarehouseAdapterrInterface;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_interfaces.WarehouseShipmentItemViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class WarehouseShipmentAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {
    private HashMap<Integer, ReasonCodeList> reasonCodeMaster;
    private ArrayList<ReasonCodeList> BPreasonCode;
    private List<Shipment_Detail> myreportsList;
    private List<Shipment_Detail> myreportsListMain=new ArrayList<>();
    Shipment_Detail shipmentDetail;
    private IWarehouseAdapterrInterface iSellerAdapterrInterface;
    boolean isCheckboxVisible = false;

     String sruti_d_link="";
   private WarehouseActivity warehouseActivity;

    public void handleCheckboxes() {
        isCheckboxVisible = true;

    }

    public void setReasonCodeMaster(HashMap<Integer, ReasonCodeList> mapRTSReasonCodeMaster) {
        this.reasonCodeMaster = mapRTSReasonCodeMaster;
    }


    public void setBPReasonCode(ArrayList<ReasonCodeList> reasonCodeLists) {
        this.BPreasonCode = reasonCodeLists;
    }

    public void setUpdateStaticListener(IWarehouseAdapterrInterface itUpdateStaticListener) {
        this.iSellerAdapterrInterface = itUpdateStaticListener;
    }


    public WarehouseShipmentAdapter(List<Shipment_Detail> reportsList) {
        this.myreportsList = reportsList;
    }



    public void setData(List<Shipment_Detail> reportsList, WarehouseActivity Activity) {
        warehouseActivity=Activity;
        this.sruti_d_link=warehouseActivity.getViewModel().getDataManager().get_sruti_enable_delink_for_wh();
        this.myreportsList.clear();
        this.myreportsList.addAll(reportsList);
        this.myreportsListMain.clear();
        this.myreportsListMain.addAll(reportsList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    myreportsList = myreportsListMain;
                } else {
                    List<Shipment_Detail> filteredList = new ArrayList<>();
                    filteredList.clear();
                    for (Shipment_Detail row : myreportsListMain) {
                        if (String.valueOf(row.getAirwaybill_number()).toLowerCase().contains(charString.toLowerCase())) {
                            System.out.println("row" + charString.toLowerCase());
                            filteredList.add(row);
                        }
                    }
                    myreportsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = myreportsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                System.out.println();
                myreportsList = (ArrayList<Shipment_Detail>) filterResults.values;
               /* for (Shipment_Detail manifest_list : myreportsList) {
                    System.out.println("DDD   " + manifest_list.getManifest_details().getLocation_name());
                }*/
                notifyDataSetChanged();
            }
        };
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityWarehouseShipmentSingleItemBinding activityFuelListItemsBinding = ActivityWarehouseShipmentSingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityFuelListItemsBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myreportsList.size();//50
    }

    private class MyViewHolder extends BaseViewHolder {
        String veh = null;
        ActivityWarehouseShipmentSingleItemBinding mBinding;
        WarehouseShipmentItemViewModel fuelReimburseItemViewModel;

        public MyViewHolder(ActivityWarehouseShipmentSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            shipmentDetail = myreportsList.get(position);
            fuelReimburseItemViewModel = new WarehouseShipmentItemViewModel(shipmentDetail);
            mBinding.setViewModel(fuelReimburseItemViewModel);
            mBinding.executePendingBindings();
            mBinding.checkboxCkb.setTag(position);
            mBinding.checkboxCkb.setVisibility(View.VISIBLE);
            //in some cases, it will prevent unwanted situations
            mBinding.checkboxCkb.setOnCheckedChangeListener(null);
            mBinding.checkboxCkb.setChecked(shipmentDetail.isChecked());

            if (shipmentDetail.isIs_mps() == true && shipmentDetail.getMaster_airwaybill_number() != 0) {
                mBinding.mps.setVisibility(View.VISIBLE);
                mBinding.mpsCell.setVisibility(View.VISIBLE);
            } else {
                mBinding.mps.setVisibility(View.GONE);
                mBinding.mpsCell.setVisibility(View.GONE);
            }

            try {
                if (shipmentDetail.getStatus().equals(Constants.FAILED)) {
                    mBinding.statusTv.setVisibility(View.VISIBLE);
//                    mBinding.statusTv.setText(shipmentDetail.getStatus() + (reasonCodeMaster.get(shipmentDetail.getReason_code()).getReason_msg() != null
//                            ? (":" + reasonCodeMaster.get(shipmentDetail.getReason_code()).getReason_msg())
//                            : ""));


                        if(shipmentDetail.isBp_Reason_Code_Applied()){
                            Log.d("reasoncoode",shipmentDetail.getBrand_package_id());
                            mBinding.statusTv.setText(BPreasonCode.get(0).getReason_msg());
                        }else {
                            mBinding.statusTv.setText(reasonCodeMaster.get(shipmentDetail.getReason_id()).getReason_msg());
                        }


                } else {
                    mBinding.statusTv.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (myreportsList.get(position).getStatus() != null) {


                switch (myreportsList.get(position).getStatus()) {

                    case Constants.PENDING:
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.shipmentLayout.setBackgroundResource(R.color.white);
                        break;

                    case Constants.PICKED:
                        if (myreportsList.get(position).isAdvance()) {
                            mBinding.checkboxCkb.setVisibility(View.GONE);
                            mBinding.shipmentLayout.setBackgroundResource(R.drawable.advance_gradient);
                        } else {
                            mBinding.checkboxCkb.setVisibility(View.GONE);
                            mBinding.shipmentLayout.setBackgroundResource(R.drawable.delivered_gradient);
                        }
                        break;

                    case Constants.FAILED:
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.shipmentLayout.setBackgroundResource(R.drawable.undelivered_gradient);
                        break;

                    case Constants.RTO_STATUS_1:
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.shipmentLayout.setBackgroundResource(R.drawable.rto_gradient);
                        break;

                    case Constants.RTO_STATUS_2:
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.shipmentLayout.setBackgroundResource(R.drawable.rto_gradient);
                        break;
                    default:
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.shipmentLayout.setBackgroundResource(R.color.white);
//                        mBinding.main.setBackgroundResource(R.color.white);
                        break;

                }

            }

            if (!myreportsList.get(position).getStatus().contains(Constants.PENDING)) {
                mBinding.checkboxCkb.setVisibility(View.GONE);
                mBinding.checkboxCkb.setChecked(false);
            }

            if (myreportsList.get(position).getStatus().contains(Constants.PENDING)) {
                if (isParent) {
                    mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                    mBinding.checkboxCkb.setChecked(myreportsList.get(position).isChecked());
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.checkboxCkb.setChecked(myreportsList.get(position).isChecked());
                }

            }
            mBinding.checkboxCkb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Integer pos = (Integer) mBinding.checkboxCkb.getTag();
                    if (myreportsList.get(pos).isChecked()) {
                        myreportsList.get(pos).setChecked(false);
                    } else {
                        myreportsList.get(pos).setChecked(true);
                    }
                }
            });
            // Removal of sruti_d_link by karan

            mBinding.shipmentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
               if(sruti_d_link.equalsIgnoreCase("true")) {

                   if (myreportsList.get(position).getStatus().equals(Constants.PICKED) || myreportsList.get(position).getStatus().equals(Constants.FAILED)) {

                       if (myreportsList.get(position).isAdvance()) {
                           new AlertDialog.Builder(mBinding.getRoot().getContext()).setMessage(R.string.mark_delete)
                                   .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
//                                        myreportsList.get(position).setStatus(Constants.PENDING);
//                                        myreportsList.get(position).setChecked(false);
                                           iSellerAdapterrInterface.delete(myreportsList.get(position));
                                           notifyDataSetChanged();
                                       }
                                   }).setNegativeButton("Cancel", null).show();
                       } else {
                           new AlertDialog.Builder(mBinding.getRoot().getContext()).setMessage(R.string.mark_assigned)
                                   .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           myreportsList.get(position).setStatus(Constants.PENDING);
//                                                myreportsList.get(position).setReason_code("");
                                           myreportsList.get(position).setChecked(false);
                                           iSellerAdapterrInterface.update(myreportsList.get(position));
                                           notifyDataSetChanged();
                                       }
                                   }).setNegativeButton("Cancel", null).show();
                       }
                   }
               }
                    return false;
                }
            });
        }
    }
}


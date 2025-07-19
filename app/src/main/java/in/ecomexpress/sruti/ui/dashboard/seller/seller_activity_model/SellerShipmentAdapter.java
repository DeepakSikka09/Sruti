package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;


import static in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity.isParents;

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
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivitySellerShipmentSingleItemBinding;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ISellerAdapterrInterface;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class SellerShipmentAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {
    private HashMap<Integer, ReasonCodeList> reasonCodeMaster;
    private List<Shipment_Detail> myreportsListFlter;
    private List<Shipment_Detail> myreportsListMain = new ArrayList<>();
    Shipment_Detail shipmentDetail;
    private ISellerAdapterrInterface iSellerAdapterrInterface;
    private ArrayList<ReasonCodeList> BPreasonCode;

    public void setReasonCodeMaster(HashMap<Integer, ReasonCodeList> mapRTSReasonCodeMaster) {
        this.reasonCodeMaster = mapRTSReasonCodeMaster;
    }

    public void setBPReasonCode(ArrayList<ReasonCodeList> reasonCodeLists) {
        this.BPreasonCode = reasonCodeLists;
    }

    public void setUpdateStaticListener(ISellerAdapterrInterface itUpdateStaticListener) {
        this.iSellerAdapterrInterface = itUpdateStaticListener;
    }


    public SellerShipmentAdapter(List<Shipment_Detail> reportsList) {
        this.myreportsListFlter = reportsList;
    }

    public void setData(List<Shipment_Detail> reportsList) {
        this.myreportsListFlter.clear();
        this.myreportsListFlter.addAll(reportsList);

        this.myreportsListMain.clear();
        this.myreportsListMain.addAll(reportsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivitySellerShipmentSingleItemBinding activityFuelListItemsBinding = ActivitySellerShipmentSingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityFuelListItemsBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myreportsListFlter.size();
    }


    private class MyViewHolder extends BaseViewHolder {
        String veh = null;
        ActivitySellerShipmentSingleItemBinding mBinding;
        SellerShipmentItemViewModel fuelReimburseItemViewModel;

        public MyViewHolder(ActivitySellerShipmentSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            shipmentDetail = myreportsListFlter.get(position);
            fuelReimburseItemViewModel = new SellerShipmentItemViewModel(shipmentDetail);
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
                //    mBinding.statusTv.setText(reasonCodeMaster.get(shipmentDetail.getReason_id()).getReason_msg());
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


            if (myreportsListFlter.get(position).getStatus() != null) {
                switch (myreportsListFlter.get(position).getStatus()) {
                    case Constants.PENDING:
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.shipmentLayout.setBackgroundResource(R.color.white);
                        break;
                    case Constants.PICKED:
                        if (myreportsListFlter.get(position).isAdvance()) {
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
                        break;

                }

            }

            if (!myreportsListFlter.get(position).getStatus().contains(Constants.PENDING)) {
                mBinding.checkboxCkb.setVisibility(View.GONE);
                mBinding.checkboxCkb.setChecked(false);
            }

            if (myreportsListFlter.get(position).getStatus().contains(Constants.PENDING)) {
                if (isParents) {
                    mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                    mBinding.checkboxCkb.setChecked(myreportsListFlter.get(position).isChecked());
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.checkboxCkb.setChecked(myreportsListFlter.get(position).isChecked());
                }
            }
            mBinding.checkboxCkb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Integer pos = (Integer) mBinding.checkboxCkb.getTag();
                    if (myreportsListFlter.get(pos).isChecked()) {
                        myreportsListFlter.get(pos).setChecked(false);
                    } else {
                        myreportsListFlter.get(pos).setChecked(true);
                    }
//                    shipmentDetail.setChecked(!shipmentDetail.isChecked());
//                    notifyDataSetChanged();
                }
            });

            // TODO BY SUMIT --de-link disabled in seller activity
          /*  mBinding.shipmentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (myreportsListFlter.get(position).getStatus().equals(Constants.PICKED) || myreportsListFlter.get(position).getStatus().equals(Constants.FAILED)) {
                        if (myreportsListFlter.get(position).isAdvance()) {
                            new AlertDialog.Builder(mBinding.getRoot().getContext()).setMessage(R.string.mark_delete)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            iSellerAdapterrInterface.delete(myreportsListFlter.get(position));
                                            notifyDataSetChanged();
                                        }
                                    }).setNegativeButton("Cancel", null).show();

                        } else {
                            new AlertDialog.Builder(mBinding.getRoot().getContext()).setMessage(R.string.mark_assigned)
                                    .setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myreportsListFlter.get(position).setStatus(Constants.PENDING);
                                            myreportsListFlter.get(position).setChecked(false);

                                            iSellerAdapterrInterface.update(myreportsListFlter.get(position));
                                            notifyDataSetChanged();
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }
                    }
                    return false;
                }
            });
*/

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    myreportsListFlter = myreportsListMain;
                } else {
                    List<Shipment_Detail> filteredList = new ArrayList<>();
                    filteredList.clear();
                    for (Shipment_Detail row : myreportsListMain) {
                        if (String.valueOf(row.getAirwaybill_number()).toLowerCase().contains(charString.toLowerCase())) {
                            System.out.println("row" + charString.toLowerCase());
                            filteredList.add(row);
                        }
                    }
                    myreportsListFlter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = myreportsListFlter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                myreportsListFlter = (ArrayList<Shipment_Detail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}


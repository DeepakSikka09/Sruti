package in.ecomexpress.sruti.ui.dashboard.globalscan;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityGlobalScanSingleItemBinding;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class GlobalScanAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<Shipment_Detail> myawbList;
    private IGlobalAdapterToActivityInterface globalAdapterToActivityInterface;
    public int selected_item = 0;
    public long Current_AWb_FOR_BP_ID;

    public void setdeleteScanItemListener(IGlobalAdapterToActivityInterface iGlobalAdapterToActivityInterface) {
        this.globalAdapterToActivityInterface = iGlobalAdapterToActivityInterface;
    }


    public GlobalScanAdapter(List<Shipment_Detail> globalScans) {
        this.myawbList = globalScans;
    }

    public void setData(List<Shipment_Detail> awbList, long current_AWb_FOR_BP_ID) {
        if (this.myawbList != null) {
            myawbList.clear();
            this.myawbList.addAll(awbList);
            this.Current_AWb_FOR_BP_ID = current_AWb_FOR_BP_ID;
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityGlobalScanSingleItemBinding activityGlobalScanSingleItemBinding = ActivityGlobalScanSingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityGlobalScanSingleItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myawbList.size();//50
    }

    private class MyViewHolder extends BaseViewHolder implements IGlobalAdapterInterface {

        ActivityGlobalScanSingleItemBinding mBinding;
        GlobalScanItemViewModel globalScanItemViewModel;
        LinearLayout linear_parent;
        TextView tv_scan_bp_id;

        public MyViewHolder(ActivityGlobalScanSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            linear_parent = itemView.findViewById(R.id.linear_global);
            tv_scan_bp_id = itemView.findViewById(R.id.tv_scan_bp_id);
        }


        @Override
        public void onBind(int position) {
            Shipment_Detail globalScan = myawbList.get(position);
            if (myawbList.get(position).isAdvance() == true) {
                tv_scan_bp_id.setVisibility(View.GONE);
                linear_parent.setBackgroundResource(R.drawable.advance_gradient);
            } else if (myawbList.get(position).getStatus().equalsIgnoreCase(Constants.PENDING) && (myawbList.get(position).getAirWayBillNumber() == Current_AWb_FOR_BP_ID)) {

                tv_scan_bp_id.setVisibility(View.VISIBLE);
                linear_parent.setBackgroundResource(R.drawable.pending_cell_gradient);
            } else if (position == selected_item && myawbList.get(position).getStatus().equalsIgnoreCase(Constants.PICKED)) {
                tv_scan_bp_id.setVisibility(View.GONE);
                linear_parent.setBackgroundResource(R.drawable.top_cell_gradient);
            } else if (myawbList.get(position).getStatus().equalsIgnoreCase(Constants.PICKED)) {
                tv_scan_bp_id.setVisibility(View.GONE);
                linear_parent.setBackgroundResource(R.drawable.delivered_gradient);
            }
            globalScanItemViewModel = new GlobalScanItemViewModel(globalScan, this);
            mBinding.setViewModel(globalScanItemViewModel);
            mBinding.executePendingBindings();

        }


    }
}

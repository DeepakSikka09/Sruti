package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 26/8/19.
 */
public class SellerScanListAdapter extends RecyclerView.Adapter<SellerScanListAdapter.ViewHolder> {
    private Context mContext;
    private List<Shipment_Detail> awbNumberList = new ArrayList<>();
    public int selected_item = 0;
    public long Current_AWb_FOR_BP_ID ;

    public SellerScanListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Shipment_Detail> awbNumbers, long Current_AWb_FOR_BP_ID) {
        if (this.awbNumberList != null) {
            awbNumberList.clear();
            this.awbNumberList.addAll(awbNumbers);
            this.Current_AWb_FOR_BP_ID=Current_AWb_FOR_BP_ID;
            notifyDataSetChanged();
        }
    }

    @Override
    public SellerScanListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_awb_view, parent, false);
        return new SellerScanListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellerScanListAdapter.ViewHolder holder, int position) {
//   This code is  repetetive  and also included in both seller and warehouse

        if (awbNumberList.get(position).isAdvance()) {
            holder.tvawbNumber.setText("" + awbNumberList.get(position).getAirWayBillNumber());
            holder.tvScanBpId.setVisibility(View.GONE);
            holder.main.setBackgroundResource(R.drawable.advance_gradient);

        }  else if ( awbNumberList.get(position).getStatus().equalsIgnoreCase(Constants.PENDING) && (awbNumberList.get(position).getAirWayBillNumber()==Current_AWb_FOR_BP_ID)){
            holder.tvawbNumber.setText("" + awbNumberList.get(position).getAirWayBillNumber());
         //   holder.tv_scan_bp_id.set
            holder.tvScanBpId.setVisibility(View.VISIBLE);
            holder.main.setBackgroundResource(R.drawable.pending_cell_gradient);
        }

        else if (position == selected_item && awbNumberList.get(position).getStatus().equalsIgnoreCase(Constants.PICKED)) {
            holder.tvawbNumber.setText("" + awbNumberList.get(position).getAirWayBillNumber());
            holder.tvScanBpId.setVisibility(View.GONE);
            holder.main.setBackgroundResource(R.drawable.top_cell_gradient);

        } else if (awbNumberList.get(position).getStatus().equalsIgnoreCase(Constants.PICKED)) {
            holder.tvawbNumber.setText("" + awbNumberList.get(position).getAirWayBillNumber());
            holder.tvScanBpId.setVisibility(View.GONE);
            holder.main.setBackgroundResource(R.drawable.delivered_gradient);
        }
    }

    @Override
    public int getItemCount() {
        return this.awbNumberList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvawbNumber,tvScanBpId;
        private LinearLayout main;

        public ViewHolder(View itemView) {
            super(itemView);
            tvawbNumber = itemView.findViewById(R.id.tv_awb_value);
            tvScanBpId = itemView.findViewById(R.id.tv_scan_bp_id);
            main = itemView.findViewById(R.id.main);
        }

    }
}

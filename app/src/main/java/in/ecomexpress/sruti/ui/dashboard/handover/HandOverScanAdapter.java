package in.ecomexpress.sruti.ui.dashboard.handover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 9/7/19.
 */

public class HandOverScanAdapter extends RecyclerView.Adapter<HandOverScanAdapter.ViewHolder> {
    private final Context mContext;
    private final IHandOverNavigator iHandOverNavigator;
    private final List<HandOverShipmentList> handOverShipmentListList = new ArrayList<>();


    public HandOverScanAdapter(Context mContext, IHandOverNavigator iHandOverNavigator) {
        this.mContext = mContext;
        this.iHandOverNavigator = iHandOverNavigator;
    }

    public void setData(List<HandOverShipmentList> handOverShipmentLists) {
        if (this.handOverShipmentListList != null) {
            handOverShipmentListList.clear();
            this.handOverShipmentListList.addAll(handOverShipmentLists);
            notifyDataSetChanged();
        }
    }

    public void deleteData(long awbno) {
        Iterator<HandOverShipmentList> loc = handOverShipmentListList.iterator();
        while (loc.hasNext()) {
            HandOverShipmentList dd = loc.next();
            if (dd.getAirWayBillNumber() == awbno) {
                loc.remove();
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_awb_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvawbNumber.setText("" + handOverShipmentListList.get(position).getAirWayBillNumber());
    }

    @Override
    public int getItemCount() {
        return this.handOverShipmentListList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvawbNumber;



        public ViewHolder(View itemView) {
            super(itemView);
            tvawbNumber = itemView.findViewById(R.id.tv_awb_value);

        }

    }
}

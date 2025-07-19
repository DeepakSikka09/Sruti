package in.ecomexpress.sruti.ui.dashboard.handover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.handoverdata.ListOfAwbs;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 17/9/19.
 */
public class HandOverListAdapter extends RecyclerView.Adapter<HandOverListAdapter.ViewHolder> {
    private List<ListOfAwbs> listOfAwbs = new ArrayList<>();
    private final Context mContext;

    public HandOverListAdapter(List<ListOfAwbs> listOfAwbs, Context mContext) {
        this.listOfAwbs = listOfAwbs;
        this.mContext = mContext;
    }

    void updateView(List<ListOfAwbs> handOverListOfAwbs) {
        this.listOfAwbs.clear();
        this.listOfAwbs.addAll(handOverListOfAwbs);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.handover_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HandOverListAdapter.ViewHolder holder, int position) {
        /*holder.tvawbNumber.setText("" + listOfAwbs.get(position).getAwb());*/
    }

    @Override
    public int getItemCount() {
        return this.listOfAwbs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvawbNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            tvawbNumber = itemView.findViewById(R.id.tv_manifest_awb_value);
        }

    }
}

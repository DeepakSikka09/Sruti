package in.ecomexpress.sruti.ui.dashboard.signature;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;

public class CustomerDetailAdapter extends RecyclerView.Adapter<CustomerDetailAdapter.ViewHolder> {
    private List<Manifest_List> manifest_listArrayList = new ArrayList<>();

    void updateView(List<Manifest_List> ListOfDetail) {
        this.manifest_listArrayList.clear();
        this.manifest_listArrayList.addAll(ListOfDetail);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_manifest_userdetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.customer_name.setText(manifest_listArrayList.get(position).getCust_name());
        holder.customer_address.setText(manifest_listArrayList.get(position).getManifest_details().getAddress().getLine1() + "," + notNull(manifest_listArrayList.get(position).getManifest_details().getAddress().getLine2()) + "," + notNull(manifest_listArrayList.get(position).getManifest_details().getAddress().getLine3()));
        holder.city.setText(manifest_listArrayList.get(position).getManifest_details().getAddress().getCity() + " , " + manifest_listArrayList.get(position).getManifest_details().getAddress().getPincode());
    }

    @Override
    public int getItemCount() {
        return this.manifest_listArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customer_name;
        private TextView customer_address;
        private TextView city;

        public ViewHolder(View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_address = itemView.findViewById(R.id.customer_address);
            city = itemView.findViewById(R.id.city);
        }

    }

    String notNull(String vl) {
        return TextUtils.isEmpty(vl) ? "" : vl;
    }

}

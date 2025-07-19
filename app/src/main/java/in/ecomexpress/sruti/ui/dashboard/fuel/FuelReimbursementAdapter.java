package in.ecomexpress.sruti.ui.dashboard.fuel;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityFuelReimburseSingleLayoutBinding;
import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;

public class FuelReimbursementAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Reports> myreportsList;

    public FuelReimbursementAdapter(List<Reports> reportsList) {
        this.myreportsList = reportsList;
    }

    public void setData(List<Reports> reportsList) {
        this.myreportsList.clear();
        this.myreportsList.addAll(reportsList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityFuelReimburseSingleLayoutBinding activityFuelListItemsBinding = ActivityFuelReimburseSingleLayoutBinding
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

    private class MyViewHolder extends BaseViewHolder implements IFuelAdapterInterface {
        String veh = null;
        ActivityFuelReimburseSingleLayoutBinding mBinding;
        FuelReimburseItemViewModel fuelReimburseItemViewModel;

        public MyViewHolder(ActivityFuelReimburseSingleLayoutBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            Reports fuelReimbursementResponse = myreportsList.get(position);
            fuelReimburseItemViewModel = new FuelReimburseItemViewModel(fuelReimbursementResponse, this);
            mBinding.setViewModel(fuelReimburseItemViewModel);
            mBinding.executePendingBindings();
            mBinding.map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mYmAP", "mAP CLICKED");
                }
            });
        }

        @Override
        public void onItemClick(Reports fuelReimbursementResponse) {

        }


        @Override
        public String onVehicleType(String vehicle) {
            veh = vehicle;

            if (veh.equals("1")) {
                setImageResource(mBinding.wheelerType, R.drawable.car);
            }
            if (veh.equals("2")) {
                setImageResource(mBinding.wheelerType, R.drawable.bike);
            }
            return vehicle;
        }


        public void setImageResource(ImageView view, int resource) {
            view.setImageResource(resource);


        }
    }
}


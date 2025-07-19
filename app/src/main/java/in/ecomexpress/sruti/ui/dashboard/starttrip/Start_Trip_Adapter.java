package in.ecomexpress.sruti.ui.dashboard.starttrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.StartTripFieldBinding;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Image;

/**
 * Created by 63091 on 29-07-2019.
 */

public class Start_Trip_Adapter extends RecyclerView.Adapter<Start_Trip_Adapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Start_Trip_Image> start_trip_images;
    private Image_UpdateListner image_updateListner;
    private String owner_type;

    public Start_Trip_Adapter(ArrayList<Start_Trip_Image> start_trip_images, Context context, Image_UpdateListner image_updateListner) {
        this.start_trip_images = start_trip_images;
        this.image_updateListner = image_updateListner;
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }

    public void vehicleDetailServer(List<LoginResponse.StartRouteDetails> routeDetails) {
    }

    public void ownerType(String owner_type) {
        this.owner_type = owner_type;
    }

    public void updateImage(List<Start_Trip_Image> start_trip) {
        notifyDataSetChanged();
    }

    public ArrayList<Start_Trip_Image> getValueDATA() {
        return start_trip_images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StartTripFieldBinding startTripFieldBinding = DataBindingUtil.inflate(layoutInflater, R.layout.start_trip_field, parent, false);
        return new ViewHolder(startTripFieldBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (owner_type.equals("Ecom")) {
            holder.binding.vehicleStarttripNo.setEnabled(false);
            holder.binding.vehicleTypetext.setVisibility(View.VISIBLE);
            holder.binding.spinnerTypeofvehicle.setVisibility(View.GONE);
        } else {
            holder.binding.vehicleTypetext.setVisibility(View.GONE);
            holder.binding.spinnerTypeofvehicle.setVisibility(View.VISIBLE);
            holder.binding.spinnerTypeofvehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionl, long id) {
                    start_trip_images.get(position).setVehicle_type(holder.binding.spinnerTypeofvehicle.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        holder.binding.setFoodata(start_trip_images.get(position));
        holder.binding.imageCapture.setOnClickListener(view -> {
            image_updateListner.updateImage(position);
        });

    }

    @Override
    public int getItemCount() {
        return start_trip_images.size();
    }

    public interface Image_UpdateListner {
        void updateImage(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final StartTripFieldBinding binding;

        public ViewHolder(StartTripFieldBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

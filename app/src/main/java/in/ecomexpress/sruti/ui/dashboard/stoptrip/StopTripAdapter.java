package in.ecomexpress.sruti.ui.dashboard.stoptrip;

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
import in.ecomexpress.sruti.databinding.StopTripFieldBinding;
import in.ecomexpress.sruti.model.starttrip.Start_Trip_Image;

/**
 * Created by 63091 on 29-07-2019.
 */

public class StopTripAdapter extends RecyclerView.Adapter<StopTripAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<Start_Trip_Image> start_trip_images;
    //    private Context context;
    private Image_UpdateListner image_updateListner;
    private String owner_type;

    public StopTripAdapter(ArrayList<Start_Trip_Image> start_trip_images, Context context, Image_UpdateListner image_updateListner) {
        this.start_trip_images = start_trip_images;
//        this.context = context;
        this.image_updateListner = image_updateListner;
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context);
        }
    }

    public void updateImage(List<Start_Trip_Image> start_trip) {
//        System.out.println("start_trip_images DD" + start_trip.size());
//        this.start_trip_images.clear();
//        System.out.println("start_trip_images DD clear" + start_trip.size());
//        this.start_trip_images.addAll(start_trip);
        notifyDataSetChanged();
    }

    public ArrayList<Start_Trip_Image> getValueDATA() {

        return start_trip_images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StopTripFieldBinding startTripFieldBinding = DataBindingUtil.inflate(layoutInflater, R.layout.stop_trip_field, parent, false);
        return new ViewHolder(startTripFieldBinding);
    }

    public void ownerType(String owner_type) {
        this.owner_type = owner_type;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (owner_type.equals("Ecom")) {
//            holder.binding.etMeter.setEnabled(false);
            holder.binding.vehicleStarttripNo.setEnabled(false);
            holder.binding.vehicleTypetext.setVisibility(View.VISIBLE);
            holder.binding.spinnerTypeofvehicle.setVisibility(View.GONE);
            holder.binding.vehicleTypetext.setText(start_trip_images.get(position).getVehicle_type());
//            holder.binding.spinnerTypeofvehicle.setEnabled(false);
//            holder.binding.spinnerTypeofvehicle.setClickable(false);
//            String[] wh= context.getResources().getStringArray(R.array.type_of_veh);
//            int vehicle=0;
//            int i=0;
//            for (String s:wh){
//                if (s.equals(start_trip_images.get(position).vehicle_type.get())){
//                    vehicle=i;
//                }
//                i++;
//            }
//            holder.binding.spinnerTypeofvehicle.setSelection(vehicle);
        } else {
            holder.binding.vehicleTypetext.setVisibility(View.VISIBLE);
            holder.binding.spinnerTypeofvehicle.setVisibility(View.GONE);
            holder.binding.vehicleTypetext.setText(start_trip_images.get(position).getVehicle_type());
            /*if (!TextUtils.isEmpty(start_trip_images.get(position).getVehicleno().get())) {
                holder.binding.vehicleStarttripNo.setEnabled(false);
            }else{
                holder.binding.vehicleStarttripNo.setEnabled(true);
            }*/
//            holder.binding.spinnerTypeofvehicle.setVisibility(View.VISIBLE);
            holder.binding.spinnerTypeofvehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionl, long id) {
               /* if (routeDetails.get(position).getStart_vehicle_type() != null) {
                    if (routeDetails.get(position).getStart_vehicle_type().equals(parent.getSelectedItem().toString())) {
                        start_trip_images.get(position).vehicleno.set(routeDetails.get(position).getStart_vehicle_number());
                    } else {
                        start_trip_images.get(position).vehicleno.set("");
                    }

                }*/

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
        private final StopTripFieldBinding binding;

        public ViewHolder(StopTripFieldBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

package in.ecomexpress.sruti.model.starttrip;

import androidx.databinding.BindingAdapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by 63091 on 29-07-2019.
 */

public class Start_Trip_Image {
//    public final ObservableField<String> vehicleno = new ObservableField<>();
//    public final ObservableField<Long> meter_reading = new ObservableField<>();
//    public final ObservableField<String> vehicle_type = new ObservableField<>();
    private String image_path;
    private String vehicleno;

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public void setMeter_reading(Long meter_reading) {
        this.meter_reading = meter_reading;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    private Long meter_reading;
private String vehicle_type;
    public Image_Response getImage_response() {
        return image_response;
    }

    public void setImage_response(Image_Response image_response) {
        this.image_response = image_response;
    }

    private  Image_Response image_response;
    public String getVehicleno() {
        return vehicleno;
    }

    public Long getMeter_reading() {
        return meter_reading;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    @BindingAdapter("app:captureImage")
    public static void captureImageURL(ImageView view, String imageURL) {
        if (!TextUtils.isEmpty(imageURL))
            Glide.with(view.getContext()).load(imageURL).into(view);
    }

    public AdapterView.OnItemSelectedListener getSpinnerListnerObject() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               /* if (vehicle_type.get().equals(adapterView.getSelectedItem().toString())) {
                    vehicleno.set("JoooJ");
                } else {
                    vehicleno.set("");
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

}

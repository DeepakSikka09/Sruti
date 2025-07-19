package in.ecomexpress.sruti.ui.dashboard.stoptrip;


import android.text.TextUtils;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.stoptrip.StopTrip;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


public class StopTripViewModel extends BaseViewModel {
    private MutableLiveData<StopTrip> stopTripResponse = new MutableLiveData<>();
    public MutableLiveData<List<LoginResponse.StartRouteDetails>> vehicle_detail = new MutableLiveData<>();
    private MutableLiveData<String> imageFilePath = new MutableLiveData<>();
    private ObservableField<String> stop_meter_reading = new ObservableField<>();
    private ObservableField<String> other_expences = new ObservableField<>();
    private String vehicle_no;
    private String vehicle_type;
    private long start_meter_reading;

    public MutableLiveData<StopTrip> getStopAPIResponse() {
        return stopTripResponse;
    }

    public StopTripViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public MutableLiveData<String> getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath.setValue(imageFilePath);
    }

    LiveData<List<LoginResponse.StartRouteDetails>> getVehicle_detail() {
        vehicle_detail.setValue(getDataManager().getRouteDetail());
        return vehicle_detail;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public long getStart_meter_reading() {
//        start_meter_reading = getDataManager().getStartTripMeterReading();
        return start_meter_reading;
    }

    public void setStart_meter_reading(long start_meter_reading) {
        this.start_meter_reading = start_meter_reading;
    }

    public MutableLiveData<String> getError_msg() {
        return error_msg;
    }

    public void setError_msg(MutableLiveData<String> error_msg) {
        this.error_msg = error_msg;
    }

    private MutableLiveData<String> error_msg = new MutableLiveData<>();

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }


    public ObservableField<String> getStop_meter_reading() {
        return stop_meter_reading;
    }

    public void setStop_meter_reading(ObservableField<String> stop_meter_reading) {
        this.stop_meter_reading = stop_meter_reading;
    }

    public ObservableField<String> getOther_expences() {
        return other_expences;
    }

    public void setOther_expences(ObservableField<String> other_expences) {
        this.other_expences = other_expences;
    }

    public boolean validationStopTrip() {
        if (TextUtils.isEmpty(stop_meter_reading.get())) {
            return showToast("Please enter meter reading");
        }
        /*else if (Long.parseLong(stop_meter_reading.get()) <= getDataManager().getStartTripMeterReading()) {
            return showToast("Meter reading is less than start trip meter reading");
        }*/
        else if (TextUtils.isEmpty(imageFilePath.getValue())) {
            return showToast("Please capture image of meter reading");
        } else {
            showToast("success");
        }
        return true;
    }

    private boolean showToast(String msg) {
        error_msg.setValue(msg);
        return false;
    }

    @BindingAdapter("android:toString")
    public static void convertToString(EditText textView, long val) {//, long nval
        String vv = textView.getText().toString();
        System.out.println("CONCCCCC");
        try {
            Long.parseLong(vv);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            if (val == 0) {
                textView.setText("");
            } else {
                textView.setText(Long.toString(val));
            }
            System.out.println("CONCCCCC number");
            return;
        }
        if (Long.parseLong(vv) != val) {//!vv.isEmpty() &&
            if (val == 0) {
                textView.setText("");
            } else {
                textView.setText(String.valueOf(val));
                textView.setSelection(textView.getText().length());
            }
        }
    }

    @BindingAdapter("android:movecursor")
    public static void setSelectionToString(EditText textView, String val) {//, long nval
        // String vv= textView.getText().toString();
        /*if (!vv.isEmpty() && Long.parseLong(vv)!= val) {
            if (val == 0) {
                textView.setText("");
            } else {
                textView.setText(String.valueOf(val));*/
        textView.setSelection(textView.getText().length());
//            }
//        }
    }

    @InverseBindingAdapter(attribute = "android:toString", event = "android:textAttrChanged")
    public static Long getText(EditText view) {
        System.out.println("getText " + view.getText());
        String num = view.getText().toString();
        if (num.isEmpty())
            return 0L;
        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

}

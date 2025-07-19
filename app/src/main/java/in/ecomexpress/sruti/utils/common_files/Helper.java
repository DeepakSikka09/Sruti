package in.ecomexpress.sruti.utils.common_files;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import in.ecomexpress.geolocations.DatabaseHandler;

@SuppressWarnings("resource")
public class Helper {
    public static final String TAG = Helper.class.getSimpleName();


    public static void updateLocationWithData(Context mContext, String manifestNo,String status,String location_latitude,String location_longitude) {
        JSONObject jsonObject = new JSONObject();
        String markerType = "PICKED";
        if (status.equalsIgnoreCase(Constants.FAILED)) {
            markerType = "FAILED";
        }
        try {
            jsonObject.put("awb", manifestNo);
            jsonObject.put("status", status);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
           // if (!Constants.CURRENT_LATITUDE.equalsIgnoreCase("0.0") && !Constants.CURRENT_LONGITUDE.equalsIgnoreCase("0.0")) {
                DatabaseHandler.getInstance(mContext).updateMarkerTypeAndMetadata(markerType, jsonObject.toString(), Double.parseDouble(location_latitude), Double.parseDouble(location_longitude));
            //} else if (!String.valueOf(in.ecomexpress.geolocations.Constants.latitude).equalsIgnoreCase("0.0") && !String.valueOf(in.ecomexpress.geolocations.Constants.longitude).equalsIgnoreCase("0.0")) {
             //   DatabaseHandler.getInstance(mContext).updateMarkerTypeAndMetadata(markerType, jsonObject.toString(), in.ecomexpress.geolocations.Constants.latitude, in.ecomexpress.geolocations.Constants.longitude);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
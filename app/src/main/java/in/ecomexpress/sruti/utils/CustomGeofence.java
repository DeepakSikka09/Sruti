package in.ecomexpress.sruti.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class CustomGeofence {


    public boolean isInGeofenceRadius(Location userLocation, double manifestLat, double manifestLong, double radiusToCheckInMeters) {
        try {
            LatLng startLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            LatLng manifestLatLng = new LatLng(manifestLat, manifestLong);

            double distanceBetweenInMeters = SphericalUtil.computeDistanceBetween(startLatLng, manifestLatLng);
            return distanceBetweenInMeters < radiusToCheckInMeters;
        } catch (Exception e) {
            e.printStackTrace();
            //test comment
            return false;
        }
    }


    public boolean isInRadiusDistanceToMethod(Location userLocation, double manifestLat, double manifestLong, double radiusToCheckInMeters) {
        try {
            Location manifestLocation = new Location("");
            manifestLocation.setLongitude(manifestLong);
            manifestLocation.setLatitude(manifestLat);

            double distanceBetweenInMeters = manifestLocation.distanceTo(userLocation);
            return distanceBetweenInMeters < radiusToCheckInMeters;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

    }
}

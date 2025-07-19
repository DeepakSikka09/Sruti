package in.ecomexpress.sruti.model.starttrip;

/**
 * Created by 63091 on 18-09-2019.
 */

public class Start_Trip_Multi_Vehicle {

    public Image_Response getImage_response() {
        return image_response;
    }

    public void setImage_response(Image_Response image_response) {
        this.image_response = image_response;
    }

    public String getType_of_vehicle() {
        return type_of_vehicle;
    }

    public void setType_of_vehicle(String type_of_vehicle) {
        this.type_of_vehicle = type_of_vehicle;
    }

    public String getVehicle_meter_reading() {
        return vehicle_meter_reading;
    }

    public void setVehicle_meter_reading(String vehicle_meter_reading) {
        this.vehicle_meter_reading = vehicle_meter_reading;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }
    private Image_Response image_response;
    private String type_of_vehicle;
    private String vehicle_meter_reading;
    private String vehicle_number;

    @Override
    public String toString() {
        return "Start_Trip_Multi_Vehicle{" +
                "image_response=" + image_response +
                ", type_of_vehicle='" + type_of_vehicle + '\'' +
                ", vehicle_meter_reading='" + vehicle_meter_reading + '\'' +
                ", vehicle_number='" + vehicle_number + '\'' +
                '}';
    }
}

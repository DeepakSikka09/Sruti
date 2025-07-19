package in.ecomexpress.sruti.model.starttrip;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by 63091 on 18-09-2019.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image_Response  implements Parcelable{
   public Image_Response(){

    }
    protected Image_Response(Parcel in) {
        image_id = in.readString();
        image_key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image_id);
        dest.writeString(image_key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image_Response> CREATOR = new Creator<Image_Response>() {
        @Override
        public Image_Response createFromParcel(Parcel in) {
            return new Image_Response(in);
        }

        @Override
        public Image_Response[] newArray(int size) {
            return new Image_Response[size];
        }
    };

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_key() {
        return image_key;
    }

    public void setImage_key(String image_key) {
        this.image_key = image_key;
    }

    private String image_id;
    private String image_key;

    @Override
    public String toString() {
        return "Image_Response{" +
                "image_id='" + image_id + '\'' +
                ", image_key='" + image_key + '\'' +
                '}';
    }
}

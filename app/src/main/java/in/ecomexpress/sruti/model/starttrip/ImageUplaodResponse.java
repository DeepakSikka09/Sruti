package in.ecomexpress.sruti.model.starttrip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

/**
 * Created by 63091 on 12-06-2019.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageUplaodResponse {

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*public ArrayList<UploadedImage_Response> getArray_image() {
        return array_image;
    }

    public void setArray_image(ArrayList<UploadedImage_Response> array_image) {
        this.array_image = array_image;
    }*/

    private boolean status;
    private String description;

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    private String imageType;

    public Image_Response getResponse() {
        return response;
    }

    public void setResponse(Image_Response response) {
        this.response = response;
    }

    //    private ArrayList<UploadedImage_Response> array_image;
    private Image_Response response;

}

package in.ecomexpress.sruti.utils.common_files;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;


import in.ecomexpress.sruti.ui.base.BaseActivity;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by dhananjayk on 21-06-2018.
 */

public abstract class ImageHandler {
    private Activity activity;
    private String imageName;
    private Bitmap bitmap;
    private ImageView imgView;
    private ImageProcessor imageProcessor;
    private String imageCode;

    private final String TAG = ImageHandler.class.getSimpleName();

//    @Inject
//    SchedulerProvider schedulerProvider;

    public ImageHandler(Activity activity) {
        this.activity = activity;
        imageProcessor = new ImageProcessor(activity);
    }

//    public static WaterMark getTempWaterMark() {
//        WaterMark waterMark = new WaterMark();
//        waterMark.setDate("02/02/2019");
//        waterMark.setEcom_text("Ecom Express Pvt. Ltd.");
//        waterMark.setEmp_code("47785");
//        waterMark.setEmp_name("Parikshit");
//        waterMark.setLat("28.7041");
//        waterMark.setLng("77.1025");
//        waterMark.setOsv_text("OSV");
//        return waterMark;
//    }

    private boolean checkPermission() {
        boolean status = false;
        BaseActivity baseActivity = (BaseActivity) activity;
        baseActivity.requestPermissionsSafely(Constants.permissions, 100);
        return status;
    }

    public void captureImage(String imageName, ImageView imgView, String imageCode) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        imageProcessor.captureImage(imageName);
    }

  /*  public void captureImage(String imageName, ImageView imgView, String imageCode, WaterMark waterMark) {
        this.imageName = imageName;
        this.imgView = imgView;
        this.imageCode = imageCode;
        //remove below line when we are sending watermark from drs list item

        if (waterMark == null) {
            Logger.e(TAG, "water mark is not available");
        }
        imageProcessor.captureImage(imageName, waterMark);
    }
*/
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                // bitmap = imageProcessor.getThumbnail(100, 100);
                String imageLocation = imageProcessor.getSavedFilePath(requestCode, resultCode);
                Bitmap bitmap = BitmapFactory.decodeFile(imageLocation);
                CryptoUtils.encryptFile(imageLocation, imageLocation, Constants.ENC_DEC_KEY);
                //imageUriSaveInDB(imageLocation);
                if (bitmap!=null) {
                    onBitmapReceived(bitmap, imageLocation, imgView, imageName, imageCode);
                }else {
                    Toast.makeText(activity, bitmap.toString(), Toast.LENGTH_SHORT).show();
                }/* bitmap = imageProcessor.getThumbnail(100, 100);
                String imageLocation = imageProcessor.getSavedFilePath(requestCode, resultCode);
                // imageUriSaveInDB(imageLocation);
                onBitmapReceived(bitmap, imageLocation, imgView, imageName, imageCode);
*/

            }
        }
    }

    private void imageUriSaveInDB(String path) {

/*
        KYCImageSaveObject kycImageSaveObject =
                new KYCImageSaveObject(activityId, drsNo, imageType, imageName, path, awb, "0", "");
        DBHelper helper = new DBHelper(activity);
        helper.addKYCImage(kycImageSaveObject);
        helper.close();
        new BackendCallATImageUpload(activity, kycImageSaveObject).execute();*/
    }

    public abstract void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode);

}

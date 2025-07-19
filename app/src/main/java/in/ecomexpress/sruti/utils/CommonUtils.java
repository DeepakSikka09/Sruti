/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package in.ecomexpress.sruti.utils;

import static in.ecomexpress.sruti.utils.AppConstants.CAMERA_REQUEST;
import static in.ecomexpress.sruti.utils.common_files.Constants.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.starttrip.ReturnImage;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseActivity;

public final class CommonUtils {

    private static Pattern pattern;
    private static Matcher matcher;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";

    public static boolean isStringMatch(String newstring, String oldstring) {
        //validate email and password
        if (newstring == null || newstring.isEmpty()) {
            return false;
        }
        if (oldstring == null || oldstring.isEmpty()) {
            return false;
        }
        return newstring.equals(oldstring);

    }


    public static boolean isAllPermissionAllow(BaseActivity baseActivity) {
        for (String permission : permissions) {
            if (!baseActivity.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null == telephonyManager) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManager.getImei(0) + "," + telephonyManager.getImei(1);
        } else {
            return telephonyManager.getDeviceId(0) + "," + telephonyManager.getDeviceId(1);
        }


    }


    /**
     * @param uri
     * @param context
     * @return String deviceId;
     * <p>
     * if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
     * deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
     * } else {
     * final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
     * if (mTelephony.getDeviceId() != null) {
     * deviceId = mTelephony.getDeviceId();
     * } else {
     * deviceId = Settings.Secure.getString(
     * context.getContentResolver(),
     * Settings.Secure.ANDROID_ID);
     * }
     * }
     * <p>
     * return deviceId;
     */

    public static boolean isAppInstalled(String uri, Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        boolean appInstalled = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && inetAddress instanceof Inet6Address) {
                        String ipaddress = inetAddress.getHostAddress().toString();
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (SocketException ex) {
//            Log.e(TAG, ex.getMessage());
        }
        return null;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getTimestamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String loadJSONFromAsset(Context context, String jsonFileName) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static void deleteIMG(Activity context) {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};

        final Cursor cursor = context.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            ContentResolver cr = context.getContentResolver();
            cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
            // you will find the last taken picture here and can delete that
        }
    }


    /**
     * Validate username with regular expression
     *
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public static boolean validate(final String username) {
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();
    }

    /**
     * @param context
     * @param view
     * @param mssg
     * @param color
     * @Deepak Sikka
     */
    public static void showSnackBar(Context context, View view, String mssg, int color) {
        if (view != null && context != null) {
            try {
                Snackbar snackbar = Snackbar.make(view, mssg, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(color);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                snackbar.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dispatchTakePictureIntent(Activity activity, Fragment fragment, String empcode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            ReturnImage photoFile = null;
            try {
                photoFile = createImageFile(activity, empcode);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile.getFile() != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(activity, "in.ecomexpress.sruti.fileprovider", photoFile.getFile());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    fragment.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return photoFile.getFilePath();
        }
        return "";
    }

    private static ReturnImage createImageFile(Context context, String empCode) throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = empCode + "_" + System.currentTimeMillis() + "_STARTTRIP.jpg";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //System.out.println("image " + image.getName());
        // Save a file: path for use with ACTION_VIEW intents
//        imageFilePath = image.getAbsolutePath();
//        mRunIdViewModel.setUriPath(imageFilePath);
        ReturnImage returnImage = new ReturnImage();
        returnImage.setFile(image);
        returnImage.setFilePath(image.getAbsolutePath());
        return returnImage;
    }

    public static boolean isMyServiceRunning(Activity acitivity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) acitivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static long changeDateTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = inputFormat.parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Format the date according to the output string
        Log.d("check_date", String.valueOf(date.getTime()));

        //System.out.println("Today is " +date.getTime());
        return date.getTime();
    }

    public static String getTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = inputFormat.parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
        //  String outputString = outputFormat.format(date.getTime());
        Log.d("check_date", String.valueOf(outputFormat.format(date)));
        return String.valueOf(outputFormat.format(date));
    }

    public static boolean checkTimeslot(long start_time, long end_time) {

        return (System.currentTimeMillis() >= start_time && System.currentTimeMillis() <= end_time);


    }

    public static boolean checkMultiSpace(Context context, IDataManager mDataManager) {
        boolean isNotMulitSpace = false;
        if (mDataManager.getListOfMultiSpace() != null && mDataManager.get_multiSpace_allow().equalsIgnoreCase("true")) {
            if (mDataManager.getListOfMultiSpace() != null) {
                ArrayList<String> arr_fakeApplications = mDataManager.getListOfMultiSpace();
                for (String packagename : arr_fakeApplications) {
                    boolean isPackageInstalled = CommonUtils.isPackageInstalled(packagename, context.getPackageManager());
                    if (isPackageInstalled) {
                        // Log.d("checkmulti",packagename);
                        isNotMulitSpace = true;
                        break;
                    } else {
                        isNotMulitSpace = false;

                    }
                }
            }
        } else {
            isNotMulitSpace = false;
        }
        return isNotMulitSpace;
    }

    public static boolean checkMultiSpace(Activity activity, String multi_space_allow, ArrayList<String> multipspaceList) {
        boolean isNotMulitSpace = false;
        if (multipspaceList != null && multi_space_allow.equalsIgnoreCase("true")) {
            if (multipspaceList != null) {
                ArrayList<String> arr_fakeApplications = multipspaceList;
                for (String packagename : arr_fakeApplications) {
                    boolean isPackageInstalled = CommonUtils.isPackageInstalled(packagename, activity.getPackageManager());
                    if (isPackageInstalled) {
                        // Log.d("checkmulti",packagename);
                        isNotMulitSpace = true;
                        break;
                    } else {
                        isNotMulitSpace = false;

                    }
                }
            }
        } else {
            isNotMulitSpace = false;
        }
        return isNotMulitSpace;
    }

    public static String maskNo(String registrd_mobile) {

        String final_mobile_no = "";
        if (!registrd_mobile.equalsIgnoreCase("")) {
            if (registrd_mobile.length() == 10) {
                StringBuilder mobile_no = new StringBuilder(registrd_mobile);
                int start = 3;
                int end = 7;
                final_mobile_no = String.valueOf(mobile_no.replace(start, end, "XXXX"));
                //  mobile_no.re
            } else {
                //  final_mobile_no = registrd_mobile.replaceAll("\\b(\\d{3})\\d+(\\d)", "$1*******$2");
                // final_mobile_no = registrd_mobile.substring(0, registrd_mobile.length() - 3) + "XXX";
                final_mobile_no = registrd_mobile;
            }
        }
        return final_mobile_no;


    }

  /*  public  static  String getnoFromMaskString(String strng){
        String contactNo="";

       *//* int index=strng.indexOf("#");

        if(index!=-1 && index+1<strng.length()){

            contactNo=strng.substring(index+1);
        }
*//*

        String subStringAfterPrefix=
        return  getsubstringAfterPrefix(strng,prefix);
    }*/

    public static String getsubstringAfterPrefix(String strng) {
        String prefix="#%$";
        int index=strng.indexOf(prefix);
        if(index!=-1 && index +prefix.length()<strng.length()){
            return strng.substring(index+prefix.length());
        }else {
            return "";
        }
    }

    public static String getDisplayValueofSpinner(String strn){
        int index=strn.indexOf("#%$");
        if(index!=-1){
            return strn.substring(0,index);

        }
        else {
            return "";
        }

    }

    public static void deleteNumberFromCallLogs(String number, Context context) {
        try {
            Uri callUri = CallLog.Calls.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(callUri, null, CallLog.Calls.NUMBER + "=?", new String[]{number}, null);
            if (cursor != null) {
                int idColumnIndex = cursor.getColumnIndex(CallLog.Calls._ID);
                if (idColumnIndex != -1 && cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(idColumnIndex);
                        context.getContentResolver().delete(callUri, CallLog.Calls._ID + "=?", new String[]{id});
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


}

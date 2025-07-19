package in.ecomexpress.sruti.model.menifestdata;


import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.model.masterdata.ReasonList;

/**
 * Created by 63091 on 02-07-2019.
 */

public class DataTypeConverterObjectToGson {
    private static Gson gson = new Gson();

    @TypeConverter
    public static Vender_Detail convertJsonToGson(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else
            return gson.fromJson(object, Vender_Detail.class);
    }

    @TypeConverter
    public static String convertGsonToJson(Vender_Detail object) {
        return gson.toJson(object);
    }

    @TypeConverter
    public static Customer_Detail convertJsonToGsonCustomer(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else
            return gson.fromJson(object, Customer_Detail.class);
    }

    @TypeConverter
    public static String convertGsonToJson(Customer_Detail object) {
        return gson.toJson(object);
    }


    @TypeConverter
    public static Manifest_Setting convertJsonToGsonSetting(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else
            return gson.fromJson(object, Manifest_Setting.class);
    }

    @TypeConverter
    public static String convertGsonToJson(Manifest_Setting object) {
        return gson.toJson(object);
    }

    @TypeConverter
    public static Flags convertJsonToGsonFlag(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else
            return gson.fromJson(object, Flags.class);
    }

    @TypeConverter
    public static String convertGsonToJson(Flags object) {
        return gson.toJson(object);
    }

    @TypeConverter
    public static ArrayList<Integer> convertJsonToGsonIds(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else {
            return gson.fromJson(object, new TypeToken<List<Integer>>() {
            }.getType());
        }
    }
    @TypeConverter
    public static String convertGsonToJsonIds(ArrayList<Integer> object) {
        return gson.toJson(object);
    }

    @TypeConverter
    public static ArrayList<String> convertJsonToGsonRecciIds(String object) {
        if (TextUtils.isEmpty(object))
            return null;
        else {
            return gson.fromJson(object, new TypeToken<List<String>>() {
            }.getType());
        }
    }
    @TypeConverter
    public static String convertGsonToJsonRecciIds(ArrayList<String> object) {
        return gson.toJson(object);
    }
}

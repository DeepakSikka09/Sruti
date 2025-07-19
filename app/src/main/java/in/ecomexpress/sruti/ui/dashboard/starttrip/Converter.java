package in.ecomexpress.sruti.ui.dashboard.starttrip;

import android.widget.EditText;

import androidx.databinding.InverseMethod;

/**
 * Created by 63091 on 20-09-2019.
 */

public class Converter {
    @InverseMethod("stringToLong")
    public static String longToString(EditText editText, long oldvalue, long value) {
        System.out.println("jJJJJJ");
        return String.valueOf(editText.getText());
    }

    public static long stringToLong(EditText editText, long oldvalue, String value) {
        if (value.isEmpty())
            return 0L;
        else {
            try {
                return Long.parseLong(value);
            }catch (NumberFormatException e){
                e.printStackTrace();
                return 999999;
            }
        }
    }
}

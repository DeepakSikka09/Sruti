package in.ecomexpress.sruti.utils.fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by dhananjayk on 07-02-2017.
 */

@SuppressLint("AppCompatCustomView")
public class CustomButtonRegular extends Button {
    public CustomButtonRegular(Context context) {
        super(context);
        setFont();
    }

    public CustomButtonRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomButtonRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/cabin_r.otf");
        setTypeface(font, Typeface.NORMAL);
    }
}

package in.ecomexpress.sruti.ui.dashboard.starttrip;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import in.ecomexpress.sruti.R;

/**
 * Created by 63091 on 31-08-2019.
 */

public class NewImage extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);
        String omgg = getIntent().getExtras().getString("imgg");
        ImageView img = findViewById(R.id.img);
        Glide.with(this).load(omgg).into(img);
    }
}

package in.ecomexpress.sruti.utils.MultiSpaceCheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityMultiSpaceBinding;

public class MultiSpaceActivity extends Activity {
    public static Activity finishActivityFlag;
    private ActivityMultiSpaceBinding multiSpaceBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        multiSpaceBinding = ActivityMultiSpaceBinding.inflate(getLayoutInflater());
        setContentView(multiSpaceBinding.getRoot());

     /*  multiSpaceBindingt.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });*/

        try { // this is for alert on every week day according to emp_code to check your app settings
            boolean AppSettingAlert = getIntent().getBooleanExtra("AppSettingAlert", false);
            String type = getIntent().getStringExtra("FlagType");
            if (AppSettingAlert) {
                appSettingAlert(this);
            } /*else if(type.equalsIgnoreCase("FakeLocation")){
                tv_msg.setText(R.string.fakelocationmsg);
                Constants.IS_USING_FAKE_GPS = 1;
                setFinishOnTouchOutside(false);
                tv_ok.setVisibility(View.GONE);
                finishActivityFlag = this;
            }*/
           /* if(type.equalsIgnoreCase("GPS")){
                tv_msg.setText(R.string.GPSONOFF);
                tv_ok.setVisibility(View.VISIBLE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }*/
            if (type.equalsIgnoreCase("MultiSpace")) {
                multiSpaceBinding.tvMsg.setText(R.string.multi_space);
                // multiSpaceBinding.tvOk.setVisibility(View.GONE);
                setFinishOnTouchOutside(false);
                finishActivityFlag = this;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appSettingAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need to check your application setting.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

  /*  @Override
    public void onBackPressed() {

        Toast.makeText(MultiSpaceActivity.this, getString(R.string.cannot_go_back_via_multi),Toast.LENGTH_SHORT).show();
    }

*/
}
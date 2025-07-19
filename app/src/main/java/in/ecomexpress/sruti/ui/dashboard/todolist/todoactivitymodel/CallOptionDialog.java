package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import static in.ecomexpress.sruti.utils.MessageManager.showToast;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;

public class CallOptionDialog extends Dialog {


    private String primary_number;
    Manifest_List manifest_list;
    private String secondary_number;
    private Context context;
    private final ToDoListViewModel viewModel;

    public CallOptionDialog(@NonNull Context context, String primary_number, String secondary_number, Manifest_List manifest_list, ToDoListViewModel viewModel) {
        super(context);
        this.primary_number = primary_number;
        this.secondary_number = secondary_number;
        this.context = context;
        this.manifest_list = manifest_list;
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_option_dialog);

        Button primaryButton = findViewById(R.id.primary_call_button);
        Button secondaryButton = findViewById(R.id.secondary_call_button);
        if(!(manifest_list.getManifest_details().isIs_valid_contact_no()))
        {
            primaryButton.setEnabled(false);
            primaryButton.setBackgroundResource(R.drawable.generic_disable_button);
        }
        if (!(manifest_list.getManifest_details().getLocation().isIs_valid_secondary_contact_no()))
        {
            secondaryButton.setEnabled(false);
            secondaryButton.setBackgroundResource(R.drawable.generic_disable_button);
        }

        primaryButton.setOnClickListener(v -> {
            if (manifest_list.getManifest_details().isIs_valid_contact_no()) {
                viewModel.vendorContactNumber.set(primary_number);
                callPhoneNumber(primary_number);

            }
            dismiss();
        });


        secondaryButton.setOnClickListener(v -> {
            if (manifest_list.getManifest_details().getLocation().isIs_valid_secondary_contact_no()) {
                viewModel.vendorContactNumber.set(secondary_number);
                callPhoneNumber(secondary_number);

            }
            dismiss();
        });
    }

    private void callPhoneNumber(String number) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                context.startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                context.startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

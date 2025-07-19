package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.DepartCallback;

public class DashboardConfirmationDialog extends DialogFragment {
    private DepartCallback departCallback;
    private List<LoginResponse.StartRouteDetails> vehicle;
    private View dialogView;
    private LinearLayout parent;
    private String vehicleowner;

    public void ConfirmationDialog(DepartCallback departCallback, List<LoginResponse.StartRouteDetails> vehicle) {
        this.departCallback = departCallback;
        this.vehicle = vehicle;
    }

    public void vehicle_OwnerType(String vehicleowner) {
        this.vehicleowner = vehicleowner;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setTitle("DEPART");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.departurewheecle, null);
        builder.setView(dialogView);
        LinearLayout baselayout = dialogView.findViewById(R.id.baseui);
        parent = new LinearLayout(getActivity());
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        int i = 123;
        for (LoginResponse.StartRouteDetails dc : vehicle) {
            if (!dc.isDeparted()) {
                CheckBox tv1 = new CheckBox(getActivity());
//                tv1.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
                tv1.setId(i);
                tv1.setTextColor(Color.parseColor("#FFFFFF"));
                tv1.setTag(dc.getStart_vehicle_number());
                tv1.setText(dc.getStart_vehicle_number());
                tv1.setPadding(10, 10, 10, 10);
                tv1.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.checkbox_selector));
                parent.addView(tv1);
                i++;
            }

        }
        baselayout.addView(parent);
        builder.setMessage("Are you sure want to depart Trip");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ArrayList<String> vehicleid = new ArrayList<>();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    CheckBox checkBox = dialogView.findViewById(123 + i);
                    System.out.println(checkBox.isChecked() + "  checkBox  " + checkBox.getText().toString());
                    if (checkBox.isChecked()) {
                        vehicleid.add(checkBox.getTag().toString());
                    }
                }
                if (vehicleid.size() > 0 && vehicleid.size() == 1) {
                    dismiss();
                    departCallback.syncManifestData();
                    departCallback.checkForDepart(vehicleid, vehicleowner);
                } else if (vehicleid.size() > 1)
                    Toast.makeText(getActivity(), "Only one vehicle can select", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Please select vehicle", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();

    }
}

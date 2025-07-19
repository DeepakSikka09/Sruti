package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.DepartCallback;

public class ManifestCommitDialog extends DialogFragment {
    private DepartCallback departCallback;
    private List<Manifest_List> vehicle;
    private View dialogView;
    private LinearLayout parent;

    public void ConfirmationDialog(DepartCallback departCallback, List<Manifest_List> vehicle) {
        this.departCallback = departCallback;
        this.vehicle = vehicle;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.checkmanifest_commit, null);
        builder.setView(dialogView);
        LinearLayout baselayout = dialogView.findViewById(R.id.baseui);
        parent = new LinearLayout(getActivity());
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        int i = 123;
        for (Manifest_List dc : vehicle) {
            RelativeLayout ll = new RelativeLayout(getActivity());
            ll.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            TextView tv1 = new TextView(getActivity());
            tv1.setId(i);
            tv1.setTextColor(getResources().getColor(R.color.black));
            tv1.setPadding(5, 10, 5, 10);
            tv1.setText(dc.getManifest_details().getLocationName());
            ll.addView(tv1);

            TextView menifest_id = new TextView(getActivity());
            menifest_id.setId(i);
            menifest_id.setTextColor(getResources().getColor(R.color.white));
            if (dc.getManifest_No() > 0)
                menifest_id.setText(dc.getManifest_No() + "");
            else
                menifest_id.setText(dc.getPickup_location_id() + "");
            menifest_id.setPadding(5, 5, 5, 5);


            RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, menifest_id.getId());

            ll.addView(menifest_id, lay);
            parent.addView(ll);
            i++;

        }
        baselayout.addView(parent);
        builder.setMessage("Click Ok to commit below mention manifest.");
        Button cancel = dialogView.findViewById(R.id.cancel);
        Button ok = dialogView.findViewById(R.id.ok);
        cancel.setOnClickListener(view -> {
            dismiss();
        });
        ok.setOnClickListener(view -> {
                    dismiss();
                    departCallback.commitMenifest(vehicle);
                }
        );
        return builder.create();

    }
}

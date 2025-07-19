package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

import android.app.Dialog;
import android.graphics.Color;
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
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class CommitDialogValidation extends DialogFragment {
    private List<List<Shipment_Detail>> shipmentDetails;
    private View dialogView;
    private LinearLayout parent;

    public void ConfirmationDialog(List<List<Shipment_Detail>> shipmentDetails) {
        this.shipmentDetails = shipmentDetails;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.checkshipment_commit, null);
        builder.setView(dialogView);

        LinearLayout baselayout = dialogView.findViewById(R.id.baseui);
        parent = new LinearLayout(getActivity());
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        int i = 123;

        for (List<Shipment_Detail> failedCases :shipmentDetails )
        for (Shipment_Detail dc : failedCases) {

            RelativeLayout ll = new RelativeLayout(getActivity());
            ll.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            if (dc.getStatus().contains(Constants.FAILED) || dc.getStatus().contains(Constants.PENDING)) {
                TextView awb_no = new TextView(getActivity());
                awb_no.setId(i);
                awb_no.setTextColor(getResources().getColor(R.color.black));
                awb_no.setPadding(25, 10, 15, 10);
                awb_no.setTextSize(15);
                awb_no.setText("AWB NO :" + String.valueOf(dc.getAirwaybill_number()));
                ll.addView(awb_no);

                TextView master_awb_no = new TextView(getActivity());
                master_awb_no.setId(i);
                master_awb_no.setTextColor(getResources().getColor(R.color.black));
                master_awb_no.setPadding(25, 50, 15, 10);
                master_awb_no.setTextSize(15);
                master_awb_no.setText("Master AWB :" + dc.getMaster_airwaybill_number() + "");

                // insert into main view
                LinearLayout lineLayout = new LinearLayout(getActivity());
                lineLayout.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2);
                params.setMargins(0, 10, 0, 10);
                lineLayout.setLayoutParams(params);
                parent.addView(lineLayout);

                RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                ll.addView(master_awb_no, lay);
                parent.addView(ll);
                i++;

            }
        }
        baselayout.addView(parent);
        builder.setMessage("You can't move forward as all the shipments of master awb have not scanned.");
        builder.setCancelable(false);
        Button cancel = dialogView.findViewById(R.id.cancel);
        Button ok = dialogView.findViewById(R.id.ok);
        cancel.setOnClickListener(view -> {
            dismiss();
        });
        ok.setOnClickListener(view -> {
                    dismiss();
                }
        );
        return builder.create();

    }
}

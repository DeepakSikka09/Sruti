package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

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
import in.ecomexpress.sruti.model.childCommitStatus.Child_details;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_interfaces.ChildCallback;

public class ChildCommitDialog extends DialogFragment {
    private ChildCallback ChildCallback;
    private List<Child_details> details;
    private View dialogView;
    private LinearLayout parent;
    private String description;

    public void ConfirmationDialog(ChildCallback ChildCallback, List<Child_details> details, String description) {
        this.ChildCallback = ChildCallback;
        this.details = details;
        this.description = description;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.childshipment_commit, null);
        builder.setView(dialogView);

        LinearLayout baselayout = dialogView.findViewById(R.id.baseui);
        parent = new LinearLayout(getActivity());
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        int i = 123;

        for (Child_details dc : details) {
            RelativeLayout ll = new RelativeLayout(getActivity());
            ll.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            TextView employeeCode = new TextView(getActivity());
            employeeCode.setId(i);
            employeeCode.setTextColor(getResources().getColor(R.color.black));
            employeeCode.setPadding(25, 10, 15, 10);
            employeeCode.setTextSize(15);
            employeeCode.setText(dc.getEmployee_name() + "(" + dc.getEmployee_code() + ")");
            ll.addView(employeeCode);


            TextView description = new TextView(getActivity());
            description.setId(i);
            description.setTextColor(getResources().getColor(R.color.black));
            description.setPadding(25, 50, 15, 10);
            description.setTextSize(15);
            description.setText(dc.getDescription() + "( Manifest No:" + dc.getManifest_number() + ")");


            // insert into main view
            LinearLayout lineLayout = new LinearLayout(getActivity());
            lineLayout.setBackgroundColor(getResources().getColor(R.color.colorStatusbar));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
            params.setMargins(0, 5, 0, 10);
            lineLayout.setLayoutParams(params);
            parent.addView(lineLayout);

            RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            ll.addView(description, lay);
            parent.addView(ll);
            i++;
        }
        baselayout.addView(parent);
        builder.setMessage(description);
        builder.setCancelable(false);
        Button ok = dialogView.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
                    dismiss();
                }
        );
        return builder.create();

    }
}

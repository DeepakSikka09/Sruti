package in.ecomexpress.sruti.ui.dashboard.starttrip;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.DepartCallback;

public class StartTripConfirmationDialog extends DialogFragment {
    private DepartCallback departCallback;
    private boolean isStartTrip;

    public void ConfirmationDialog(DepartCallback departCallback, boolean isStartTrip) {
        this.departCallback = departCallback;
        this.isStartTrip=isStartTrip;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(isStartTrip? R.string.start_trip:R.string.stop_trip);
        builder.setMessage(isStartTrip? R.string.start_trip_msg:R.string.stop_trip_msg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                departCallback.startStopDialog(isStartTrip);
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

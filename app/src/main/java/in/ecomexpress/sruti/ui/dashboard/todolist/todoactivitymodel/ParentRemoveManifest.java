package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.ParentmanifestRemove;

public class ParentRemoveManifest extends DialogFragment {
    private ParentmanifestRemove departCallback;
    private View dialogView;
    public void ConfirmationDialog(ParentmanifestRemove departCallback) {
        this.departCallback = departCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.manifestion);
        builder.setMessage(R.string.asparent);
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        dialogView = inflater.inflate(R.layout.childasparent, null);
//        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                departCallback.removeManifest();
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

package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.AsChildParent;

public class ChildAsParentDialog extends DialogFragment {
    private AsChildParent departCallback;
    private View dialogView;
    public void ConfirmationDialog(AsChildParent departCallback) {
        this.departCallback = departCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.manifestion);
        builder.setMessage(R.string.aschild);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.childasparent, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            dismiss();
//              RadioButton child= dialogView.findViewById(R.id.aschild);
            RadioButton parent= dialogView.findViewById(R.id.asParent);
            departCallback.asChildOrParent(parent.isChecked());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {dismiss();});
        return builder.create();

    }
}

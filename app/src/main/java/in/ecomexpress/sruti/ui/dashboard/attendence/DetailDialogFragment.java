package in.ecomexpress.sruti.ui.dashboard.attendence;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.FragmentDialogBinding;
import in.ecomexpress.sruti.model.attendance.AttendanceResponseList;

/**
 * Created by 63091 on 22-06-2019.
 */

public class DetailDialogFragment extends DialogFragment {
    private static AttendanceResponseList attendanceResponseListloc;

    static DetailDialogFragment newInstance(AttendanceResponseList attendanceResponseList) {
        DetailDialogFragment f = new DetailDialogFragment();
        attendanceResponseListloc = attendanceResponseList;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDialogBinding fragmentDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        fragmentDialogBinding.setDetailattendence(attendanceResponseListloc);
        return fragmentDialogBinding.getRoot();
    }
}

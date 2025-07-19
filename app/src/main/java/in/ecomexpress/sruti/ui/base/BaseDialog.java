package in.ecomexpress.sruti.ui.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import dagger.android.support.AndroidSupportInjection;
import in.ecomexpress.sruti.R;

public abstract class BaseDialog extends DialogFragment {

    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            mActivity.onFragmentAttached();
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    public void hideKeyboard(Activity activity) {
        if (mActivity != null) {
            mActivity.hideKeyboard(activity);
        }
    }

    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
            mActivity.openActivityOnTokenExpire();
        }
    }
    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
        transaction.addToBackStack(null);
        show(transaction, tag);
    }

    public void dismissDialog(String tag) {
        try {
            dismiss();
            getBaseActivity().onFragmentDetached(tag);
        }catch (Exception e){
//            AppLogs.Companion.writeCrashes(System.currentTimeMillis(),e);
            e.printStackTrace();
        }
    }
    public void showToast(String toast) {
        try {
            LayoutInflater li = getLayoutInflater();
            View layout = li.inflate(R.layout.customtoast, mActivity.findViewById(R.id.custom_toast_layout));
            TextView textView = layout.findViewById(R.id.custom_toast_message);
            textView.setText(toast);
            Toast toast1 = new Toast(mActivity);
            toast1.setDuration(Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast1.setView(layout);//setting the view of custom toast layout
            toast1.show();
        } catch (Exception e) {
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
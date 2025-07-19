package in.ecomexpress.sruti.ui.login.forget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ForgetPasswordActivityBinding;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.ui.login.login.LoginActivity;
import in.ecomexpress.sruti.utils.CommonUtils;


/**
 * Created by dhananjayk on 24-05-2018.
 */

public class ForgetActivity extends BaseDialog
        implements IForgetNavigator {

    private static final String TAG = ForgetActivity.class.getSimpleName();
    @Inject
    ForgetViewModel mForgetViewModel;

    static Context context;
    ForgetPasswordActivityBinding forgetBinding;

    public static ForgetActivity newInstance(Activity mContext) {
        ForgetActivity fragment = new ForgetActivity();
        context = mContext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        forgetBinding = DataBindingUtil.inflate(inflater, R.layout.forget_password_activity, container, false);
        View view = forgetBinding.getRoot();
        AndroidSupportInjection.inject(this);
        forgetBinding.setViewModel(mForgetViewModel);
        mForgetViewModel.setNavigator(this);
        setCancelable(false);
        return view;
    }


    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }


    @Override
    public void onServerLogin() {
        if (mForgetViewModel.isUserIdValid(forgetBinding.
                etEmployeeCode.getText().toString().trim())) {
            if (isNetworkConnected()) {
                if (!forgetBinding.etOtp.isShown()) {
                    mForgetViewModel.forgetPassword(forgetBinding.etEmployeeCode.getText().toString().trim());
                    forgetBinding.btnForgetPassword.setEnabled(false);
                } else {
                    if (!(forgetBinding.etOtp.getText().toString().trim().length() == 6)) {
                        showToast(getString(R.string.enter_valid_otp));
                        return;
                    }

                    if (forgetBinding.etNewPassword.getText().toString().trim().isEmpty() ||
                            forgetBinding.etConfirmPassword.getText().toString().trim().isEmpty()) {
                        showToast(getString(R.string.please_enter_new_password_and_confirm_password));

                        return;
                    }
                    if (!CommonUtils.isStringMatch(forgetBinding.etNewPassword.getText().toString().trim(),
                            forgetBinding.etConfirmPassword.getText().toString().trim())) {
                        showToast(getString(R.string.invalid_confirm_password));
                        return;
                    }
                    mForgetViewModel.otpVerifyWithPassword(forgetBinding.etEmployeeCode.getText().toString().trim(),
                            forgetBinding.etOtp.getText().toString().trim(),
                            forgetBinding.etConfirmPassword.getText().toString().trim());

                }

            } else {
                showToast(getResources().getString(R.string.check_internet));

            }
        } else {
            showToast(getResources().getString(R.string.please_enter_user_id));

        }
    }


    @Override
    public void ViewFlag() {
        forgetBinding.btnForgetPassword.setEnabled(true);
        forgetBinding.etEmployeeCode.setEnabled(false);
        forgetBinding.etOtp.setVisibility(View.VISIBLE);
        forgetBinding.etNewpassLayout.setVisibility(View.VISIBLE);
        forgetBinding.etConfirmPassLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHandleError(String errorDetails) {
        if (errorDetails.equalsIgnoreCase("otp couldn't send")) {
            showToast("Contact number or Email is not registered");
            forgetBinding.btnForgetPassword.setEnabled(true);
            return;
        }

        showToast(errorDetails);
        forgetBinding.btnForgetPassword.setEnabled(true);

    }

    @Override
    public void onSuccessFullyChangePassword(String statusMessage) {
        hideKeyboard(getActivity());
        showToast(statusMessage);
        startActivity(LoginActivity.getStartIntent(context));

        dismissDialog(TAG);
        forgetBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void onBackClicked() {
        dismissDialog(TAG);
        forgetBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)
            showToast(getResources().getString(R.string.http_500_msg));
        else
            showToast(getResources().getString(R.string.server_down_msg));


    }

    @Override
    public void showException(Exception e) {
        showToast(e.getMessage());

    }


    @Override
    public void onForgetPassword() {

    }

}
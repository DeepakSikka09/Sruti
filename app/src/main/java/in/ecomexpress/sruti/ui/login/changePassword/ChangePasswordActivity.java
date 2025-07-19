package in.ecomexpress.sruti.ui.login.changePassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityChangePasswordBinding;
import in.ecomexpress.sruti.model.login.ForgotPasswordResponse;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.ui.login.login.LoginActivity;
import in.ecomexpress.sruti.utils.CommonUtils;


public class ChangePasswordActivity extends BaseDialog implements ChangePasswordNavigator {

    @Inject
    ChangePasswordViewModel changePasswordViewModel;
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private ActivityChangePasswordBinding activityChangePasswordBinding;
    private OnPasswordChangeListener onPasswordChangeListener;
    private boolean otpFlag;
    private boolean forceChangeFlag;
    String username1 = "";

    public static ChangePasswordActivity newInstance(String username) {
        ChangePasswordActivity fragment = new ChangePasswordActivity();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOTPFlag(boolean otpFlag) {
        this.otpFlag = otpFlag;
    }

    public void isForcePassChange(boolean forceChangeFlag) {
        this.forceChangeFlag = forceChangeFlag;
    }

    public void setChangePasswordListener(OnPasswordChangeListener onPasswordChangeListener) {
        this.onPasswordChangeListener = onPasswordChangeListener;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username1 = getArguments().getString("username");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityChangePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.activity_change_password, container, false);
        View view = activityChangePasswordBinding.getRoot();
        AndroidSupportInjection.inject(this);
        activityChangePasswordBinding.setViewModel(changePasswordViewModel);
        changePasswordViewModel.setNavigator(this);
        changePasswordViewModel.setUserId();
        if (forceChangeFlag) {
            activityChangePasswordBinding.cross.setVisibility(View.GONE);
        } else {
            activityChangePasswordBinding.cross.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    public void onServerLogin() {

    }

    @Override
    public void onChangePassword() {
        activityChangePasswordBinding.etEmployeeCode.setText(username1);
        if (activityChangePasswordBinding.etConfirmPassword.getText().toString().trim().isEmpty() || activityChangePasswordBinding.etNewPassword.getText().toString().trim().isEmpty() || activityChangePasswordBinding.etOldPassword.getText().toString().trim().isEmpty()) {
            getBaseActivity().showToast(getString(R.string.all_fields_are_mandatory));
            return;
        }
        if (CommonUtils.isStringMatch(activityChangePasswordBinding.etNewPassword.getText().toString().trim(),
                activityChangePasswordBinding.etConfirmPassword.getText().toString().trim())) {
            if (!isNetworkConnected()) {
                getBaseActivity().showToast(getString(R.string.check_internet));
                return;
            }
            showLoading();
            String user = "";
            if (activityChangePasswordBinding.etEmployeeCode.getText().toString().trim().isEmpty()) {
                user = username1;
                activityChangePasswordBinding.etEmployeeCode.setText(user);
            } else {
                user = activityChangePasswordBinding.etEmployeeCode.getText().toString().trim();
            }
            changePasswordViewModel.changePasswordRequest(username1, activityChangePasswordBinding.etOldPassword.getText().toString().trim(), activityChangePasswordBinding.etConfirmPassword.getText().toString().trim());
        } else {
            getBaseActivity().showToast(getString(R.string.invalid_confirm_password));
        }
    }

    @Override
    public void doLogout(String message) {
        getBaseActivity().showToast("You have logged in from another device. Please login again to use application.");
        changePasswordViewModel.logoutLocal();
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        onPasswordChangeListener.onSuccess();

    }

    @Override
    public void showErrorMessage(boolean status) {
        dismissDialog();
        if (status)
            getBaseActivity().showToast(getResources().getString(R.string.http_500_msg));
        else
            getBaseActivity().showToast(getResources().getString(R.string.server_down_msg));
    }

    @Override
    public void showException(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHandleError(String error) {
        hideLoading();
        dismissDialog();
        getBaseActivity().showToast(error);
    }

    private void dismissDialog() {
        try {
            dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(ForgotPasswordResponse forgotPasswordResponse) {
        dismissDialog(TAG);
        hideLoading();
        showToast(forgotPasswordResponse.getResponse().getDescription());
        activityChangePasswordBinding.popupElement.setVisibility(View.GONE);
        if (forceChangeFlag) {
            return;
        } else {

            if (!this.otpFlag) {
                changePasswordViewModel.updateUserLoggedInState();
            }

            onPasswordChangeListener.onSuccess();
        }
        dismissDialog();

    }

    @Override
    public void onBackClick() {
        dismissDialog(TAG);
        activityChangePasswordBinding.popupElement.setVisibility(View.GONE);
    }

  /*  public void setUsername(String username) {
        this.username1 = username;
    }*/


    public interface OnPasswordChangeListener {
        void onSuccess();
    }

}

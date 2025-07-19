package in.ecomexpress.sruti.ui.dashboard.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityProfileBinding;
import in.ecomexpress.sruti.model.DeviceDetails;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.login.changePassword.ChangePasswordActivity;


public class ProfileActivity extends BaseActivity<ActivityProfileBinding, ProfileViewModel> implements IProfileNavigator {
    @Inject
    ProfileViewModel mProfileViewModel;
    @Inject
    Context context;
    @Inject
    DeviceDetails deviceDetails;
    ActivityProfileBinding mActivityProfileBinding;
    boolean expanded = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileViewModel.setNavigator(this);
        this.mActivityProfileBinding = getViewDataBinding();
        mProfileViewModel.setProfileData();
    }

    @Override
    protected String getScreenName() {
        return "Profile Screen";
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    public ProfileViewModel getViewModel() {
        return mProfileViewModel;

    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void onclickChangePass() {
        openChangePasswordActivity();
    }

    @Override
    public void openChangePasswordActivity() {
        startActivity(ChangePasswordActivity.getStartIntent(this));
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPictureClick() {

       /* TransitionManager.beginDelayedTransition(mActivityProfileBinding.container, new TransitionSet()
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeImageTransform()));

        ViewGroup.LayoutParams params = mActivityProfileBinding.userProfilePhoto.getLayoutParams();

        params.height = expanded ? ViewGroup.LayoutParams.MATCH_PARENT :
                ViewGroup.LayoutParams.WRAP_CONTENT;
        mActivityProfileBinding.userProfilePhoto.setLayoutParams(params);
*/
       /* mActivityProfileBinding.userProfilePhoto.setScaleType(expanded ? ImageView.ScaleType.CENTER_CROP :
                ImageView.ScaleType.FIT_CENTER);*/
    }

}




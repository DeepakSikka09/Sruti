package in.ecomexpress.sruti.ui.dashboard.fuel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityFuelReimburseBinding;
import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.repo.remote.ErrorResponse;
import in.ecomexpress.sruti.ui.base.BaseActivity;


public class FuelReimburseActivity extends BaseActivity<ActivityFuelReimburseBinding, FuelReimburseViewModel> implements IFuelReimburseNavigator {

    @Inject
    FuelReimburseViewModel fuelReimburseViewModel;
    ActivityFuelReimburseBinding activityFuelReimburseBinding;

    @Inject
    Context context;

    @Inject
    FuelReimbursementAdapter fuelReimbursementAdapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuelReimburseViewModel.setNavigator(this);
        activityFuelReimburseBinding = getViewDataBinding();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatusbar));
        }

        fuelReimburseViewModel.getAllFuelList(FuelReimburseActivity.this);
        setUp();
    }

    @Override
    protected String getScreenName() {
        return "Fuel Reimburshment Screen";
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FuelReimburseActivity.class);
    }

    @Override
    public FuelReimburseViewModel getViewModel() {
        return fuelReimburseViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fuel_reimburse;
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onHandleError(ErrorResponse errorDetails) {
        showToast(errorDetails.getEResponse().getDescription());
    }

    @Override
    public void onshowDescription(String error) {
        showToast(error);

        if (error.equalsIgnoreCase("Invalid Authentication Token.")){
            fuelReimburseViewModel.logoutLocal();
        }

        //  fuelReimburseViewModel.logoutLocal();
    }

    @Override
    public void clearStack() {
      /*  showToast(getString(R.string.session_expire));
        Intent intent = new Intent(FuelReimburseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/

    }

    @Override
    public void onshowLogout(String description) {
        fuelReimburseViewModel.logoutLocal();
    }

    @Override
    public void showErrorMessage(boolean status) {

        if(status)
            showToast(getResources().getString(R.string.http_500_msg));
        else
            showToast(getResources().getString(R.string.server_down_msg));

    }

    @Override
    public void showException(Exception e) {
        showToast(e.getMessage());
    }

    @Override
    public void showNoResultMessage() {

    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void OnSetFuelAdapter(List<Reports> reportsList) {
        if (reportsList.size() > 0) {
            activityFuelReimburseBinding.icons.setVisibility(View.VISIBLE);
            fuelReimbursementAdapter.setData(reportsList);
//            fuelReimbursementAdapter.notifyDataSetChanged();
        } else {
            activityFuelReimburseBinding.icons.setVisibility(View.GONE);
        }
    }

    private void setUp() {
        activityFuelReimburseBinding.fuelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityFuelReimburseBinding.fuelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityFuelReimburseBinding.fuelRecyclerView.setAdapter(fuelReimbursementAdapter);
    }


}

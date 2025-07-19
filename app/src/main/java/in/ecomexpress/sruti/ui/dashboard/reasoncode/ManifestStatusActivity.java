package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityManifestPickupStatusBinding;
import in.ecomexpress.sruti.databinding.ActivityReasonCodeBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ToDoListActivity;

public class ManifestStatusActivity extends BaseActivity<ActivityManifestPickupStatusBinding, ManifestStatusViewModel> implements View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ActivityManifestPickupStatusBinding activityManifestPickupStatusBinding;
    ManifestStatusViewModel manifestStatusViewModel;
    Manifest_List manifest_list;
    String selectedReason,full_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManifestPickupStatusBinding = getViewDataBinding();
        manifestStatusViewModel.setNavigator(this);
        manifest_list = getIntent().getParcelableExtra("manifest_list");
        selectedReason = getIntent().getExtras().getString("reason_code");
        full_address = getIntent().getExtras().getString("full_address");
        setUIData();
    }

    @Override
    protected String getScreenName() {
        return "Manifest Status Screen";
    }

    private void setUIData() {
        try {
            activityManifestPickupStatusBinding.tvFailedReason.setText(selectedReason);
            if (manifest_list.getCust_name() != null) {
                activityManifestPickupStatusBinding.tvCustomerName.setText(manifest_list.getCust_name());
            } else {
                activityManifestPickupStatusBinding.tvCustomerName.setText("");
            }
           // activityManifestPickupStatusBinding.tvCustmrAddress.setText(new StringBuilder().append(manifest_list.getManifest_details().getAddress().getLine1()).append(", ").append(manifest_list.getManifest_details().getAddress().getCity()).append(", ").append(manifest_list.getManifest_details().getAddress().getState()).append(", ").append(manifest_list.getManifest_details().getAddress().getPincode()).append("(").append(manifest_list.getManifest_details().getLocation_contact_no()).append(")").toString());
            activityManifestPickupStatusBinding.tvCustmrAddress.setText(full_address);
            activityManifestPickupStatusBinding.toolbar.tvTitle.setText(manifest_list.getManifest_details().getLocationName());
            activityManifestPickupStatusBinding.toolbar.ivBack.setOnClickListener(this);
            activityManifestPickupStatusBinding.btnHome.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ManifestStatusViewModel getViewModel() {
        manifestStatusViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(ManifestStatusViewModel.class);

        return manifestStatusViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manifest_pickup_status;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_home:
                Intent intent = new Intent(this, ToDoListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

        }

    }
    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back));
    }


}
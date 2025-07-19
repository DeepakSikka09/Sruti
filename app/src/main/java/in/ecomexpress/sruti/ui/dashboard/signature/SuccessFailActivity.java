package in.ecomexpress.sruti.ui.dashboard.signature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivitySuccessFailBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.menifestdata.Address;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ToDoListActivity;


public class SuccessFailActivity extends BaseActivity<ActivitySuccessFailBinding, SuccessFailViewModel> implements View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ActivitySuccessFailBinding activitySuccessFailBinding;
    SuccessFailViewModel successFailViewModel;
    private String screenValidation;
    private long manifestNo;
    private boolean is_global;
    private ArrayList<Manifest_List> manifest_lists_response = new ArrayList<>();
    private CustomerDetailAdapter mAdapter = new CustomerDetailAdapter();
    private ArrayList<Long> manifestNoCollection;
    private long pickup_location_id;
    String customerName, addressData, city, pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activitySuccessFailBinding = getViewDataBinding();
        setUp();

        // Get the click time from the intent
        long clickTime = getIntent().getLongExtra("clickTime", 0);
        long openTime = System.currentTimeMillis();
        long duration = openTime - clickTime;

        // Log the time taken to open this screen
        logScreenOpenTime(duration);
    }

    private void logScreenOpenTime(long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", getScreenName());
        bundle.putLong("open_time", duration);
        FirebaseAnalytics.getInstance(this).logEvent("screen_open_time", bundle);
    }

    @Override
    protected String getScreenName() {
        return "Success Fail Screen";
    }

    @SuppressLint("ResourceAsColor")
    private void setUp() {
        screenValidation = getIntent().getExtras().getString("screen_validation");
        manifestNo = getIntent().getExtras().getLong("manifest_no");
        is_global = getIntent().getExtras().getBoolean("is_global");
        pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
        manifestNoCollection = (ArrayList<Long>) getIntent().getSerializableExtra("manifestNoCollection");
        customerName = getIntent().getExtras().getString("customer_name");
        addressData = getIntent().getExtras().getString("address");
        city = getIntent().getExtras().getString("city");
        pincode = getIntent().getExtras().getString("pincode");

        activitySuccessFailBinding.manifestUserDetail.setLayoutManager(new LinearLayoutManager(this));
        activitySuccessFailBinding.manifestUserDetail.setItemAnimator(new DefaultItemAnimator());
        activitySuccessFailBinding.manifestUserDetail.setAdapter(mAdapter);
        activitySuccessFailBinding.btnHome.setOnClickListener(this);
        activitySuccessFailBinding.imageViewBack.setOnClickListener(this);

        if (screenValidation.equals("fail")) {
            activitySuccessFailBinding.imgCross.setVisibility(View.VISIBLE);
            activitySuccessFailBinding.imgTick.setVisibility(View.GONE);
            activitySuccessFailBinding.decideText.setText("Fail");
            activitySuccessFailBinding.decideText.setTextColor(this.getResources().getColor(R.color.red));
        } else {
            activitySuccessFailBinding.imgCross.setVisibility(View.GONE);
            activitySuccessFailBinding.imgTick.setVisibility(View.VISIBLE);
            activitySuccessFailBinding.decideText.setText("Success");
        }
        if (is_global) {
            if (manifestNoCollection != null && manifestNoCollection.size() > 0) {
                successFailViewModel.getSpecificManifestDetail(manifestNoCollection).observe(this, manifest_lists -> {
                    if (manifest_lists != null && manifest_lists.size() > 0) {
                        manifest_lists_response.addAll(manifest_lists);
                        mAdapter.updateView(manifest_lists);
                    }
                });

            }
        } else if (pickup_location_id != 0) {
            List<Manifest_List> manifest_lists = new ArrayList<>();
            Manifest_List manifestList = new Manifest_List();
            manifestList.setCust_name(customerName);
            Vender_Detail venderDetail = new Vender_Detail();
            Address address = new Address();
            address.setCity(city);
            address.setLine1(addressData);
            address.setPincode(pincode);
            venderDetail.setAddress(address);
            manifestList.setManifest_details(venderDetail);
            manifest_lists.add(manifestList);

            manifest_lists_response.addAll(manifest_lists);
            mAdapter.updateView(manifest_lists);

        } else {
            manifestNoCollection= new ArrayList<>();
            manifestNoCollection.add(manifestNo);
            successFailViewModel.getSpecificManifestDetail(manifestNoCollection).observe(this, manifest_lists -> {
                if (manifest_lists != null) {
                    manifest_lists_response.addAll(manifest_lists);
                    mAdapter.updateView(manifest_lists);
                }
            });
        }
    }

    @Override
    public SuccessFailViewModel getViewModel() {
        successFailViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(SuccessFailViewModel.class);
        return successFailViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_success_fail;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home: {
                manifest_lists_response.clear();
                Intent intent = new Intent(SuccessFailActivity.this, ToDoListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.imageViewBack: {
                showSnackbar(getString(R.string.cannot_go_back));
                break;
            }

        }

    }

    @Override
    public void onBackPressed() {
        showSnackbar(getString(R.string.cannot_go_back));
    }


}

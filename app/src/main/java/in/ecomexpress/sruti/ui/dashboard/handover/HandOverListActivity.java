package in.ecomexpress.sruti.ui.dashboard.handover;


import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityHandOverListBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.handoverdata.HandOverRequest;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.handoverdata.ListOfAwbs;
import in.ecomexpress.sruti.ui.base.BaseActivity;

public class HandOverListActivity extends BaseActivity<ActivityHandOverListBinding, HandOverListViewModel> implements View.OnClickListener, IHandOverListNavigator {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private ActivityHandOverListBinding activityHandOverListBinding;
    private HandOverListViewModel handOverListViewModel;
    private HandOverListAdapter mAdapter;
    private String vehicleType;
    private final List<ListOfAwbs> listOfAwbsResponse = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityHandOverListBinding = getViewDataBinding();
        handOverListViewModel.setNavigator(this);
        setup();
    }

    @Override
    protected String getScreenName() {
        return "Handover List Screen";
    }

    @Override
    public HandOverListViewModel getViewModel() {
        handOverListViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(HandOverListViewModel.class);
        return handOverListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return in.ecomexpress.sruti.BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_hand_over_list;
    }

    private void setup() {
        activityHandOverListBinding.handoverRecyclerData.setHasFixedSize(true);
        activityHandOverListBinding.handoverRecyclerData.setItemAnimator(new DefaultItemAnimator());
        activityHandOverListBinding.handoverRecyclerData.setAdapter(mAdapter);
        activityHandOverListBinding.handoverRecyclerData.setLayoutManager(new LinearLayoutManager(this));
        activityHandOverListBinding.btnNext.setOnClickListener(this);
        activityHandOverListBinding.backEvent.setOnClickListener(this);
        mAdapter = new HandOverListAdapter(listOfAwbsResponse, this);
        activityHandOverListBinding.handoverRecyclerData.setAdapter(mAdapter);

        handOverListViewModel.getHandOverLiveData().observe(this, handOverResponse -> {
            try {
               hideLoading();
                if (handOverResponse != null) {
                    activityHandOverListBinding.txtMessage.setVisibility(View.GONE);
                    activityHandOverListBinding.handoverRecyclerData.setVisibility(View.VISIBLE);

                    List<HandOverShipmentList> awbNumbersList = new ArrayList<>();
                    for (int i = 0; i < handOverResponse.getResponse().getListOfAwbs().size(); i++) {
                        HandOverShipmentList localAwb = new HandOverShipmentList();
                        localAwb.setAirWayBillNumber(Long.parseLong(handOverResponse.getResponse().getListOfAwbs().get(i).getAwb()));
                        localAwb.setStatus(handOverResponse.getResponse().getListOfAwbs().get(i).getStatus_code());
                        localAwb.setManifestId(Long.parseLong(handOverResponse.getResponse().getListOfAwbs().get(i).getManifest_id()));
                        localAwb.setOrder_number(handOverResponse.getResponse().getListOfAwbs().get(i).getOrder_number());

                        awbNumbersList.add(localAwb);
                        handOverListViewModel.insertHandOverNumberList(localAwb);
                    }
                    mAdapter.updateView(handOverResponse.getResponse().getListOfAwbs());
                    mAdapter.notifyDataSetChanged();
                } else {
                    activityHandOverListBinding.txtMessage.setVisibility(View.VISIBLE);
                    activityHandOverListBinding.txtMessage.setText(handOverResponse.getDescription());
                    activityHandOverListBinding.handoverRecyclerData.setVisibility(View.GONE);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next: {
                if (!isNetworkConnected()) {
                    showToast(getResources().getString(R.string.no_network_error));
                    return;
                } else {
                    IntentIntegrator integrator = new IntentIntegrator(this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt(this.getString(R.string.scan_bar_code));
                    integrator.setCaptureActivity(HandOverScanActivity.class);
                    integrator.setOrientationLocked(false);
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                }
                break;
            }
            case R.id.back_event: {
                finish();
            }
        }
    }

    @Override
    public void VehicleType(String vehicleType) {
        try {
            this.vehicleType = vehicleType;
            if (isNetworkConnected()) {
                showLoading();
                handOverListViewModel.handOverListData(new HandOverRequest("", "ABC2891"));
            } else {
                showToast(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }
}
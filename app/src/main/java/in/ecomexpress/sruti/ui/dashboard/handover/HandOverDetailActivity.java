package in.ecomexpress.sruti.ui.dashboard.handover;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityHandOverDetailBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;


public class HandOverDetailActivity extends BaseActivity<ActivityHandOverDetailBinding, HandOverDetailViewModel> implements View.OnClickListener {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    ActivityHandOverDetailBinding activityHandOverDetailBinding;
    HandOverDetailViewModel handOverDetailViewModel;
    private String awb_count;
    private List<HandOverShipmentList> handOverShipmentListArrayList;
    private ArrayList<String> awbLongArrayList;
    private ArrayList<String> idArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityHandOverDetailBinding = getViewDataBinding();


        activityHandOverDetailBinding.backEvent.setOnClickListener(this);
        activityHandOverDetailBinding.download.setOnClickListener(this);
        activityHandOverDetailBinding.btnSubmit.setOnClickListener(this);
        setUp();
    }

    @Override
    protected String getScreenName() {
        return "HandOver Detail Screen";
    }

    private void setUp() {
        handOverShipmentListArrayList = new ArrayList<>();
        awbLongArrayList = new ArrayList<>();
        idArrayList = new ArrayList<>();

        awb_count = getIntent().getExtras().getString("awb_count");
        activityHandOverDetailBinding.txtAwb.setText(awb_count);


    }


    @Override
    public HandOverDetailViewModel getViewModel() {
        handOverDetailViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(HandOverDetailViewModel.class);
        return handOverDetailViewModel;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_hand_over_detail;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download: {
                showLoading();
                handOverDetailViewModel.getAllAwbData().observe(this, awbNumbers -> {
                    handOverShipmentListArrayList.addAll(awbNumbers);
                    for (int i = 0; i < handOverShipmentListArrayList.size(); i++) {
                        awbLongArrayList.add("" + handOverShipmentListArrayList.get(i).getAirWayBillNumber());
                        idArrayList.add(handOverShipmentListArrayList.get(i).getStatus());
                    }
                    String[] frnames = awbLongArrayList.toArray(new String[awbLongArrayList.size()]);
                    getViewModel().exportDB(frnames);
                    hideLoading();
                });

                break;
            }
            case R.id.back_event: {
                finish();
                break;
            }
            case R.id.btn_submit: {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }


}

package in.ecomexpress.sruti.ui.dashboard.handover;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityHandOverBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.utils.common_files.Constants;

public class HandOverScanActivity extends BaseActivity<ActivityHandOverBinding, HandOverScanViewModel> implements IHandOverNavigator, in.ecomexpress.barcodelistner.BarcodeResult {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private HandOverScanViewModel handOverScanViewModel;
    private ActivityHandOverBinding activityHandOverBinding;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private BeepManager beepManager;
    private HandOverScanAdapter mAdapter;
    public String lastText = null;
    public int awbCount;
    private BarcodeHandler barcodeHandler;
    private ScanManager mScanMgr;
    public String device;
    private int outputMode = -1;
    private int i = 0;
    private static final String ScannedStatus = "002";

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                return;
            }
            lastText = result.getText();
            try {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));

                    handOverScanViewModel.isValidateAwbNumber(Long.parseLong(lastText));
                    beepManager.playBeepSoundAndVibrate();
                    beepManager.playBeepSound();
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                    handOverScanViewModel.isValidateAwbNumber(Long.parseLong(lastText));
                    beepManager.playBeepSoundAndVibrate();
                    beepManager.playBeepSound();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityHandOverBinding = getViewDataBinding();
        handOverScanViewModel.setNavigator(this);
        device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);

        if (!(device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC_CH) || device.equals(Constants._SEUIC2_CH)
                || device.equals(Constants._ZEBRA) || device.equals(Constants._LUNATE_IDATA) || device.equals(Constants._NEWLAND))) {
            device = "UNKNOWN";
        }
        if (device.equals(Constants._NEWLAND)) {
            activityHandOverBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.enableBeep();
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            Map<String, String> settings = mScanMgr.getScanSettings();
            String sOutputMode = settings.get(ScanSettings.Global.OUT_PUT_MODE); //Acquire
            outputMode = ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST;
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {

            activityHandOverBinding.zxingBarcodeScanner.setVisibility(View.GONE);
            barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
            barcodeHandler.enableScanner();
            barcodeHandler.setBeepEnabled(false);
        } else {
            beepManager = new BeepManager(this);
            barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
            capture = new CaptureManager(this, barcodeScannerView);
            capture.initializeFromIntent(getIntent(), savedInstanceState);
            barcodeScannerView.decodeContinuous(callback);
        }
        mAdapter = new HandOverScanAdapter(this, this);

        activityHandOverBinding.tvAwbCount.setText("");
        activityHandOverBinding.tvAwbCount.setVisibility(View.VISIBLE);
        activityHandOverBinding.tvAwbCount.setText(String.valueOf(mAdapter.getItemCount()));
        if (mAdapter.getItemCount() != 0) {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.generic_button);
            buttonDrawable.mutate();
            activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
        } else {
            Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.login_button);
            buttonDrawable.mutate();
            activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
        }


        setUp();
    }

    private void setUp() {
        activityHandOverBinding.handoverRecyclerData.setHasFixedSize(true);
        activityHandOverBinding.handoverRecyclerData.setItemAnimator(new DefaultItemAnimator());
        activityHandOverBinding.handoverRecyclerData.setAdapter(mAdapter);
        activityHandOverBinding.handoverRecyclerData.setLayoutManager(new LinearLayoutManager(this));


/*
        handOverScanViewModel.getShiListLiveData().observe(HandOverScanActivity.this, new Observer<ShipmentDetail>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChanged(@Nullable ShipmentDetail awbNumbers) {
                if (awbNumbers != null) {
                    List<HandOverShipmentList> awbNumbersList = new ArrayList<>();
                    HandOverShipmentList localAwb = new HandOverShipmentList();
                    localAwb.setAirWayBillNumber( awbNumbers.getAirWayBillNumber());
                    localAwb.setStatus(ScannedStatus);
                    awbNumbersList.add(localAwb);

                    awbCount = mAdapter.getItemCount();
                    handOverScanViewModel.insertAwbNumberList(localAwb);
                } else {
                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(HandOverScanActivity.this, R.style.AppCompatAlertDialogStyle);
                    AlertDialog dialog = aBuilder.setMessage(R.string.invalid_awb)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
*/


/*
        handOverScanViewModel.getAllAwbData().observe(this, awbNumbers -> {

            mAdapter.setData(awbNumbers);
            activityHandOverBinding.tvAwbCount.setVisibility(View.VISIBLE);
            activityHandOverBinding.tvAwbCount.setText(String.valueOf(mAdapter.getItemCount()));
            if (mAdapter.getItemCount() != 0) {
                Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.generic_button);
                buttonDrawable.mutate();
                activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
            } else {
                Drawable buttonDrawable = this.getResources().getDrawable(R.drawable.login_button);
                buttonDrawable.mutate();
                activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
            }

        });
*/


    }

    @Override
    public HandOverScanViewModel getViewModel() {
        handOverScanViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(HandOverScanViewModel.class);
        return handOverScanViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_hand_over;
    }


    @Override
    protected void onResume() {
        if (device.equals(Constants._NEWLAND)) {
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(mResultReceiver, intFilter);
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onResume();
        } else {
            capture.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (device.equals(Constants._NEWLAND)) {
            mScanMgr.stopScan();
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onPause();
        }
        super.onPause();

    }

    @Override
    protected String getScreenName() {
        return "Hand Over Scan Screen";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (device.equals(Constants._NEWLAND)) {
            mScanMgr.stopScan();
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.disableScanner();
        } else {
            capture.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (device.equals(Constants._NEWLAND)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {

        } else {
            capture.onSaveInstanceState(outState);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (device.equals(Constants._NEWLAND)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {

        } else {
            capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (device.equals(Constants._NEWLAND)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (device.equals(Constants._NEWLAND)) {
        } else if (device.equals(Constants._ZEBRA) || device.equals(Constants._95W_IDATA) || device.equals(Constants._FREEDOM)
                || device.equals(Constants._SEUIC2_CH) || device.equals(Constants._SEUIC_CH)
                || device.equals(Constants._LUNATE_IDATA)) {
            barcodeHandler.onKeyDown(keyCode, event);
        } else {
            capture.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (device.equals(Constants._NEWLAND)) {
        } else {
            barcodeHandler.onActivityResult(requestCode, resultCode, data);
        }
    }


    private final BroadcastReceiver mResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                byte[] bvalue = intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                String sValue = intent.getStringExtra("SCAN_BARCODE1");
                try {
                    if (sValue == null && bvalue != null)
                        sValue = new String(bvalue, "GBK");
                    sValue = sValue == null ? "" : sValue;
                    handOverScanViewModel.isValidateAwbNumber(Long.parseLong(sValue));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Decode Fail", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNext() {
        if (mAdapter.getItemCount() != 0) {
            Intent intent = new Intent(HandOverScanActivity.this, HandOverDetailActivity.class);
            intent.putExtra("awb_count", String.valueOf(mAdapter.getItemCount()));
            startActivity(intent);
        } else {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(HandOverScanActivity.this, R.style.AppCompatAlertDialogStyle);
            AlertDialog dialog = aBuilder.setMessage(R.string.awb_validate)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    @Override
    public void showValidationForNull() {

    }

    @Override
    public void showErrorMessage(String b) {

    }

    @Override
    public void showException(Exception e) {

    }

    @Override
    public void isAwbValid(List<Shipment_Detail> shipment_details, long awbNumber) {

    }


    @Override
    public void realTimeCheckInvalid(String lastText) {

    }

    @Override
    public void notifyAdapter() {

    }

    @Override
    public void isAWBRtoLock(Boolean aBoolean,long awbNumber) {

    }

    @Override
    public void checkFirstScan(Long aBoolean) {

    }

    @Override
    public void getShipmentStatus(String aBoolean, long awbNumber) {

    }

    @Override
    public void isAlreadyScanned(Boolean aBoolean, Long awbNumber) {

    }

    @Override
    public void RtoLocked() {

    }

    @Override
    public void ifAWBPresent(Boolean aBoolean, long awb) {

    }

    @Override
    public void getShipmentExist(Boolean manifest_list, long awbNumber) {

    }

    @Override
    public void isBrandPackagingIDisValid(List<Shipment_Detail> shipment_details, String bp_id) {

    }



    @Override
    public void getBP_ID(String bp_id, long awb_no) {

    }

    @Override
    public void getTempKey(String bpId, long awbNo, Boolean tempKey) {


    }


    protected void dialogBox(final long awbNumber) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder.setMessage(R.string.delete_awb);
        alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        handOverScanViewModel.deleteAwbNumber((int) awbNumber).observe(HandOverScanActivity.this, integer -> {
                            if (integer != null && integer != 0)
                                mAdapter.deleteData(awbNumber);
                            activityHandOverBinding.tvAwbCount.setText(String.valueOf(mAdapter.getItemCount()));

                            if (mAdapter.getItemCount() != 0) {
                                Drawable buttonDrawable = getResources().getDrawable(R.drawable.generic_button);
                                buttonDrawable.mutate();
                                activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
                            } else {
                                Drawable buttonDrawable = getResources().getDrawable(R.drawable.login_button);
                                buttonDrawable.mutate();
                                activityHandOverBinding.nextTv.setBackgroundDrawable(buttonDrawable);
                            }

                        });
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onResult(String s) {
        i++;
        if (i == 1) {
            // notifyScanStarted();
        }
        handOverScanViewModel.isValidateAwbNumber(Long.parseLong(s));
    }


}
package in.ecomexpress.sruti.ui.dashboard.sos;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivitySosBinding;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.utils.MessageManager;

public class SOSDialog extends BaseDialog implements SOSCallBack, View.OnClickListener {

    private static final String TAG = SOSDialog.class.getSimpleName();
    @Inject
    SOSViewModel mSOSViewModel;
    ActivitySosBinding activitySOSBinding;
    static Activity context;

    private boolean stopCounterEnable=false;
    static ParentActivityMethodCallbackListner activityMethodCallbackListner;

    public static SOSDialog newInstance(Activity mContext, ParentActivityMethodCallbackListner listner) {
        SOSDialog fragment = new SOSDialog();
        context = mContext;
        activityMethodCallbackListner = listner;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activitySOSBinding = DataBindingUtil.inflate(inflater, R.layout.activity_sos, container, false);
        View view = activitySOSBinding.getRoot();
        AndroidSupportInjection.inject(this);
        activitySOSBinding.setViewModel(mSOSViewModel);
        activitySOSBinding.stopCalling.setOnClickListener(this);
        activitySOSBinding.cancel.setOnClickListener(this);
        mSOSViewModel.setNavigator(this);
        countDownTimer.start();
        setCancelable(false);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    int counter = 4;
    CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (counter == 0) {
                countDownTimer.cancel();
                countDownTimer.onFinish();
            } else {
                if(stopCounterEnable){
                    return;
                }

                counter--;
                activitySOSBinding.txtCount.setText(String.valueOf(counter));
            }
        }

        @Override
        public void onFinish() {
            activitySOSBinding.bottomLayout.setVisibility(View.GONE);
            activitySOSBinding.progressBar.setVisibility(View.VISIBLE);
            if (isNetworkConnected()) {
                mSOSViewModel.callSOSAPI();
            } else {
                try {
                    dismissDialog();
                    MessageManager.showToast(context, getString(R.string.no_network_error));
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            ArrayList<String> list = mSOSViewModel.getSOSNumbers();
            sendSMS(list, SMSTemplate.getSOSSms(mSOSViewModel.getEmpCode(), mSOSViewModel.getLat(), mSOSViewModel.getLang(), mSOSViewModel.getSOSTempalte()));

            activitySOSBinding.buttonCancel.setClickable(false);
        }
    };

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
    }

    @Override
    public void showDescription(String description) {
        MessageManager.showToast(getContext(), description);
    }

    @Override
    public void logout() {
        dismissDialog();
        try {
            activityMethodCallbackListner.logout();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    @Override
    public void showOptionMenu() {
        stopCounterEnable=true;
        activitySOSBinding.bottomLayout.setVisibility(View.VISIBLE);
        activitySOSBinding.buttonCancel.setVisibility(View.GONE);
    }

    @Override
    public void showException(Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                stopCounterEnable=false;
                activitySOSBinding.bottomLayout.setVisibility(View.GONE);
                activitySOSBinding.buttonCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.stopCalling:
                activitySOSBinding.buttonCancel.setClickable(false);
                activitySOSBinding.progressBar.setVisibility(View.GONE);
                activitySOSBinding.bottomLayout.setVisibility(View.GONE);
                activitySOSBinding.buttonCancel.setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                activitySOSBinding.textViewEmergencyCall.setText("Emergency\nCall Cancelled");
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        dismissDialog(TAG);
                    }
                }.start();
                break;
        }
    }


    private void sendSMS(ArrayList<String> numberList, String message) {
        try {
            SmsManager sms = SmsManager.getDefault();
            // using android SmsManager
            for (String number : numberList) {
                if (number != null && number.length() > 0) {
                    sms.sendTextMessage(number, null, message, null, null); // adding number and text
                } else {
//                    Logger.e(TAG, "invalid number: [" + number + "]");
                }
            }

        } catch (Exception e) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}

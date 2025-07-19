package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivitySwitchNumberBinding;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseDialog;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by shivangi sharma on 07-12-2018.
 */

public class SwitchNumberDialog extends BaseDialog implements SwitchNumberCallBack {

    private static final String TAG = SwitchNumberDialog.class.getSimpleName();
    @Inject
    SwitchNumberViewModel switchNumberViewModel;
    ActivitySwitchNumberBinding activitySwitchNumberBinding;
    static Activity context;
    String currentFormat;
    //    ProgressDialog dialog;
    @Inject
    SwitchNumberListAdapter switchNumberListAdapter;
    String setPstnFormat = null;

    public static SwitchNumberDialog newInstance(Activity getcontext) {
        SwitchNumberDialog fragment = new SwitchNumberDialog();
        context = getcontext;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activitySwitchNumberBinding = DataBindingUtil.inflate(inflater, R.layout.activity_switch_number, container, false);
        View view = activitySwitchNumberBinding.getRoot();
        AndroidSupportInjection.inject(this);
        activitySwitchNumberBinding.setViewModel(switchNumberViewModel);
        switchNumberViewModel.setNavigator(this);
        switchNumberViewModel.getCbPstnOptions();

        try {
            currentFormat = switchNumberViewModel.getDataManager().getPstnFormat();
//            if (currentFormat != null || !currentFormat.isEmpty() || !currentFormat.equals("") || !currentFormat.equalsIgnoreCase("null")) {
            if (!currentFormat.isEmpty()) {
                if (currentFormat.contains(Constants.pstn_pin)) {
                    setPstnFormat = currentFormat.replaceAll(",@@PIN@@#", "");
                } else if (currentFormat.contains(Constants.pstn_awb)) {
                    setPstnFormat = currentFormat.replaceAll(",@@AWB@@#", "");
                }
                activitySwitchNumberBinding.current.setText("Current Selected Number : " + setPstnFormat);
            } else {
                activitySwitchNumberBinding.current.setText("Please Sync Data");
            }

        } catch (Exception e) {
            RestApiErrorHandler restApiErrorHandler = new RestApiErrorHandler(e.getCause());
            restApiErrorHandler.writeErrorLogs(0, e.getMessage());
            e.printStackTrace();
        }
        setUp();

        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void dismissDialog() {
        dismissDialog(TAG);
        activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void cancel() {
        dismissDialog(TAG);
        activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
    }

    @Override
    public void onSubmitNumber() {
        String getPstnFormat = switchNumberListAdapter.getPstnFormat();
        if (getPstnFormat != null) {
            switchNumberViewModel.getDataManager().setPstnFormat(getPstnFormat);
            showToast("CallBridge Number Switched");

            dismissDialog(TAG);
            activitySwitchNumberBinding.popupElement.setVisibility(View.GONE);
        } else {
            showToast("Please choose a Number.");
        }
    }

    @Override
    public void showException(Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHandleError(String description) {
        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status)

            Toast.makeText(context, getString(R.string.http_500_msg), Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(context, getString(R.string.server_down_msg), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void OnSetFuelAdapter(List<Post_option> cbPstnOptions) {
        switchNumberListAdapter.setData(cbPstnOptions);
        switchNumberListAdapter.notifyDataSetChanged();
    }

    private void setUp() {
        activitySwitchNumberBinding.fuelRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        activitySwitchNumberBinding.fuelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activitySwitchNumberBinding.fuelRecyclerView.setAdapter(switchNumberListAdapter);
    }
}

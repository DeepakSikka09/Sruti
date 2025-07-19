package in.ecomexpress.sruti.ui.dashboard.profile;

import android.text.TextUtils;

import androidx.databinding.ObservableField;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

public class ProfileViewModel extends BaseViewModel<IProfileNavigator> {

    private final ObservableField<Boolean> profile = new ObservableField<>();
    private final ObservableField<String> empCode = new ObservableField<>();

    private final ObservableField<String> empName = new ObservableField<>();
    private final ObservableField<String> empServicecenter = new ObservableField<>();
    private final ObservableField<String> empLocationCode = new ObservableField<>();
    private final ObservableField<String> empDesignation = new ObservableField<>();
    private final ObservableField<String> empMobile = new ObservableField<>();

    public ProfileViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);

    }

    public ObservableField<String> getCode() {
        return empCode;
    }

    public ObservableField<String> getEmpCodeName() {
        return empName;
    }

    public ObservableField<String> getEmpServicecenter() {
        return empServicecenter;
    }

    public ObservableField<String> getEmpLocationCode() {
        return empLocationCode;
    }

    public ObservableField<String> getEmpDesignation() {
        return empDesignation;
    }

    public ObservableField<String> getEmpMobile() {
        return empMobile;
    }

    public void setProfileData() {
        final String Name = getDataManager().getName();
        if (!TextUtils.isEmpty(Name)) {
            empName.set(Name);
        } else {
            empName.set("Name: NA");
        }

        final String code = getDataManager().getCode();
        if (!TextUtils.isEmpty(code)) {
            empCode.set(code);
        } else {
            empCode.set("Emp Code: NA");
        }

        final String servicecenter = getDataManager().getServiceCenter();
        if (!TextUtils.isEmpty(servicecenter)) {
            empServicecenter.set(servicecenter);
        } else {
            empServicecenter.set("Service Center: NA");
        }

        final String empLocation = getDataManager().getLocationCode();
        if (!TextUtils.isEmpty(empLocation)) {
            empLocationCode.set(code);
        } else {
            empLocationCode.set("Location Code: NA");
        }

        final String designation = getDataManager().getDesignation();
        if (!TextUtils.isEmpty(designation)) {
            empDesignation.set(designation);
        } else {
            empDesignation.set("Designation: NA");
        }

        final String mobile = getDataManager().getMobile();
        if (!TextUtils.isEmpty(mobile)) {
            empMobile.set(String.valueOf(mobile));
        } else {
            empMobile.set("Mobile: NA");
        }

    }

    public void onclickChangePass() {
        getNavigator().openChangePasswordActivity();
    }
    public void onBackClick() {
        getNavigator().onBackClick();
    } public void onPictureClick() {
        getNavigator().onPictureClick();
    }
}

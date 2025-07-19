package in.ecomexpress.sruti.ui.dashboard.attendence;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import in.ecomexpress.sruti.model.attendance.AttendanceRequest;
import in.ecomexpress.sruti.model.attendance.AttendanceResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by 63091 on 20-06-2019.
 */


public class AttendenceViewModel extends BaseViewModel {

    MediatorLiveData<AttendanceResponse> liveDataMerger = new MediatorLiveData<>();

    public AttendenceViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    LiveData<AttendanceResponse> getAttendanceData() {
        return liveDataMerger;
    }

    void getAttendenceMonthData(AttendanceRequest attendanceRequest) {
        try {
            attendanceRequest.setEmp_id(getDataManager().getCode());
            LiveData<AttendanceResponse> att = getDataManager().doAttendanceData(getDataManager().getAuthToken(), attendanceRequest);
            liveDataMerger.addSource(att, attendanceResponse -> {
                liveDataMerger.removeSource(att);
                liveDataMerger.setValue(attendanceResponse);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package in.ecomexpress.sruti.ui.dashboard.attendence;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.AttendenceViewBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.attendance.AttendanceRequest;
import in.ecomexpress.sruti.model.attendance.AttendanceResponseList;
import in.ecomexpress.sruti.ui.base.BaseActivity;

/**
 * Created by 63091 on 20-06-2019.
 */

public class AttendanceActivityView extends BaseActivity<AttendenceViewBinding, AttendenceViewModel> implements AttendenceAdapter.AttendanceCallBack {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private AttendenceViewModel attendenceViewModel;
    private AttendenceViewBinding attendenceViewBinding;
    private RecyclerView attendance_recycleview;
    private AttendenceAdapter attendence_adapter;

    private final List<AttendanceResponseList> attendanceResponseLists = new ArrayList<>();
    private final List<String> weekdays = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendenceViewBinding = getViewDataBinding();
        weekcall();
        List<AttendanceResponseList> weekd = new ArrayList<>();
        for (String day : weekdays) {
            AttendanceResponseList g = new AttendanceResponseList();
            g.setDate(day.substring(0, 3).toUpperCase());
            weekd.add(g);
        }
        GridLayoutManager grd = new GridLayoutManager(AttendanceActivityView.this, 7);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(attendenceViewBinding.weekdaysView.getContext(), grd.getOrientation());
        attendenceViewBinding.weekdaysView.setLayoutManager(grd);
        attendenceViewBinding.weekdaysView.addItemDecoration(dividerItemDecoration);
        attendenceViewBinding.weekdaysView.setAdapter(new AttendenceAdapter(weekd, "weekname", this));

        attendance_recycleview = attendenceViewBinding.attendanceRecycleview;
        attendance_recycleview.setLayoutManager(new GridLayoutManager(AttendanceActivityView.this, 7));
        attendance_recycleview.addItemDecoration(new VerticalSpaceItemDecoration(2));
        attendence_adapter = new AttendenceAdapter(attendanceResponseLists, "", this);
        attendance_recycleview.setAdapter(attendence_adapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, calculateMonths());
        attendenceViewBinding.attendenceMonth.setAdapter(adapter);
        attendenceViewBinding.attendenceMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = attendenceViewBinding.attendenceMonth.getSelectedItem().toString().split(",")[0].toUpperCase();
                String year = attendenceViewBinding.attendenceMonth.getSelectedItem().toString().split(",")[1];
                System.out.println("SETEC  " + month + " " + year);
                if (isNetworkConnected()) {
                    showLoading();
                    attendenceViewModel.getAttendenceMonthData(new AttendanceRequest("", month, year));
                } else {
                    showToast(getString(R.string.check_internet));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //new AttendanceRequest("20806", "JUN", "2019")
        attendenceViewModel.getAttendanceData().observe(this, attendanceResponse -> {
            hideLoading();
            if (attendanceResponse != null && attendanceResponse.getStatus()) {
                String startday = attendanceResponse.getResponse().getStart_day_of_month();
                int pos = weekdays.indexOf(startday);
                List<AttendanceResponseList> obj = attendanceResponse.getResponse().getAttendanceResponseList();
                for (int i = 0; i < pos; i++) {
                    AttendanceResponseList ob = new AttendanceResponseList();
                    ob.setDummy(true);
                    obj.add(0, ob);
                }
                if (obj != null && obj.size() > 0)
                    attendence_adapter.updateView(obj);
                System.out.println(attendanceResponse.getResponse().getAvg_working_hours());

            } else {
                Toast.makeText(this, attendanceResponse.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
//        attendenceViewModel.getAttendenceMonthData();


        attendenceViewBinding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected String getScreenName() {
        return "Attendence Screen";
    }

    private void weekcall() {
        weekdays.add("sunday");
        weekdays.add("monday");
        weekdays.add("tuesday");
        weekdays.add("wednesday");
        weekdays.add("thursday");
        weekdays.add("friday");
        weekdays.add("saturday");

    }

    private ArrayList<String> calculateMonths() {
        ArrayList<String> listMonths = new ArrayList<>();
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        listMonths.add(sdf.format(c.getTime()).split(" ")[0] + "," + sdf.format(c.getTime()).split(" ")[1]);
        c.add(Calendar.MONTH, -1);
        listMonths.add(sdf.format(c.getTime()).split(" ")[0] + "," + sdf.format(c.getTime()).split(" ")[1]);
        c.add(Calendar.MONTH, -1);
        listMonths.add(sdf.format(c.getTime()).split(" ")[0] + "," + sdf.format(c.getTime()).split(" ")[1]);
        c.add(Calendar.MONTH, -1);
        listMonths.add(sdf.format(c.getTime()).split(" ")[0] + "," + sdf.format(c.getTime()).split(" ")[1]);

       /* for (String dd : listMonths) {
            System.out.println("XXXX  " + dd);
        }*/

        return listMonths;
    }

    @Override
    public AttendenceViewModel getViewModel() {
        attendenceViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(AttendenceViewModel.class);
        return attendenceViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.attendencemodel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.attendence_view;
    }

    @Override
    public void sendAttendanceDataAttendanceResponseList(AttendanceResponseList attendanceResponseList) {
        if (attendanceResponseList.isDummy())
            return;
        DetailDialogFragment dd = DetailDialogFragment.newInstance(attendanceResponseList);
        dd.show(getSupportFragmentManager(), "de");
    }
}

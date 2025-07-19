package in.ecomexpress.sruti.ui.dashboard.attendence;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.attendance.AttendanceResponseList;

/**
 * Created by 63091 on 21-06-2019.
 */

public class AttendenceAdapter extends RecyclerView.Adapter<AttendenceAdapter.ViewHolder> {
    private List<AttendanceResponseList> attendanceResponseLists=new ArrayList<>();
    private final String weekday;
    private final AttendanceCallBack attendanceCallBack;

    AttendenceAdapter(List<AttendanceResponseList> attendanceResponseLists, String weekday, AttendanceCallBack attendanceCallBack) {
        this.attendanceResponseLists = attendanceResponseLists;
        this.weekday = weekday;
        this.attendanceCallBack = attendanceCallBack;
    }

    void updateView(List<AttendanceResponseList> attendanceResponseLists) {
        this.attendanceResponseLists.clear();
        this.attendanceResponseLists.addAll(attendanceResponseLists);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return weekday.equals("weekname") ? 0 : 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        System.out.println("viewType  " + viewType);
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item_day_sec, parent, false);

                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item_day, parent, false);

                break;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        System.out.println("XXX  " + attendanceResponseLists.get(position).getDate());
        if (holder.getItemViewType() == 0)
            holder.date.setText(attendanceResponseLists.get(position).getDate());
        else {
            holder.leaveday.setText(attendanceResponseLists.get(position).getAttendance_status());
            holder.date.setText(attendanceResponseLists.get(position).getDate());
            holder.itemView.setOnClickListener(v -> attendanceCallBack.sendAttendanceDataAttendanceResponseList(attendanceResponseLists.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return attendanceResponseLists.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView leaveday;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            leaveday = view.findViewById(R.id.leaveday);
        }
    }

    interface AttendanceCallBack {
        void sendAttendanceDataAttendanceResponseList(AttendanceResponseList attendanceResponseList);
    }
}

package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;


/**
 * Created by 63091 on 03-07-2019.
 */

public class ReasonCodeAdapter extends RecyclerView.Adapter<ReasonCodeAdapter.ViewHolder> {//implements Filterable
    Activity activity;
    public ManifestLevelReasonCodeActivity manifestLevelReasonCodeActivity;
    ArrayList<ReasonCodeList> reasonCode;
    int selectedPosition=-1;

    public ReasonCodeAdapter(ManifestLevelReasonCodeActivity reasonCodeFragment, Activity acttivity, ArrayList<ReasonCodeList> reasonCode) {
        this.activity= acttivity;
        this.reasonCode= reasonCode;
        this.manifestLevelReasonCodeActivity= reasonCodeFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_reason_code, parent, false);
        return new ViewHolder(view1);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_reason.setText(reasonCode.get(position).getReason_msg());
        if (selectedPosition == position) {
           holder.tv_reason.setTextColor(activity.getResources().getColor(R.color.rtstext));
            holder.constrnt_reason.setBackgroundResource(R.drawable.reason_code_selected_background);
            holder.iv_tick.setVisibility(View.VISIBLE);
        } else {

            holder.tv_reason.setTextColor(activity.getResources().getColor(R.color.text_color_dark_grey));
            holder.constrnt_reason.setBackgroundResource(R.drawable.reason_code_background);
            holder.iv_tick.setVisibility(View.GONE);

        }

        holder.constrnt_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = position;
                manifestLevelReasonCodeActivity.selectedReason(reasonCode.get(position).getReason_msg(),reasonCode.get(position).getReason_code(),reasonCode.get(position).getReason_id());
                notifyDataSetChanged();

            }
        });
    }


    @Override
    public int getItemCount() {

        return reasonCode.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
       TextView tv_reason;
       ImageView iv_tick;
       ConstraintLayout constrnt_reason;
        public ViewHolder(View itemView) {
            super(itemView);

            tv_reason = itemView.findViewById(R.id.tv_reason);
            iv_tick = itemView.findViewById(R.id.iv_tick);
            constrnt_reason = itemView.findViewById(R.id.constrnt_reason);


        }

    }


}

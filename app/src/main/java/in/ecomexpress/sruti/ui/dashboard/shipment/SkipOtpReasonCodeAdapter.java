package in.ecomexpress.sruti.ui.dashboard.shipment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityShipmentOtpBinding;
import in.ecomexpress.sruti.model.masterdata.ReasonList;


/**
 * Created by sumit raj.
 */

public class SkipOtpReasonCodeAdapter extends RecyclerView.Adapter<SkipOtpReasonCodeAdapter.ViewHolder> {//implements Filterable
    Activity activity;
    public ShipmentOtpActivity shipmentOtpActivity;
    ArrayList<ReasonList> reasonCode;
    int selectedPosition = -1;

    public SkipOtpReasonCodeAdapter(ArrayList<ReasonList> reasonCode, ShipmentOtpActivity shipmentOtpActivity) {
        this.reasonCode = reasonCode;
        this.shipmentOtpActivity = shipmentOtpActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.white_reason_single_item, parent, false);
        return new ViewHolder(view1);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_reason.setText(reasonCode.get(position).getReason());
        if (selectedPosition == position) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
        holder.constrnt_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReasonCode(position);
            }
        });
        holder.radioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setReasonCode(position);
            }
        });
    }


    @Override
    public int getItemCount() {

        return reasonCode.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_reason;
        ConstraintLayout constrnt_reason;
        AppCompatRadioButton radioButton;
        Button button;


        public ViewHolder(View itemView) {
            super(itemView);

            tv_reason = itemView.findViewById(R.id.tv_reason);
            constrnt_reason = itemView.findViewById(R.id.constrnt_reason);
            radioButton = itemView.findViewById(R.id.radio_button);
            button = itemView.findViewById(R.id.bt_submit);

        }

    }

    public void setReasonCode(int position) {
        int lastpositioncheck = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(lastpositioncheck);
        notifyItemChanged(selectedPosition);
        shipmentOtpActivity.selectedReason(reasonCode.get(position).getReason(), reasonCode.get(position).getReason_code());
    }


}

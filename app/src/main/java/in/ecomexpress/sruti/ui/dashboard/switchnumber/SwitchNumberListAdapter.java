package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivitySwitchnumbersinglelistitemBinding;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;


/**
 * Created by shivangis on 12/10/2018.
 */

public class SwitchNumberListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<Post_option> mycbPstnOptions;
    String myPstnFormat;
    String highlight;
    //    int position;
    int mLastSelectedIndex = -1;

    public void setData(List<Post_option> cbPstnOptions) {
        this.mycbPstnOptions.clear();
        this.mycbPstnOptions.addAll(cbPstnOptions);
        notifyDataSetChanged();
    }

    public SwitchNumberListAdapter(List<Post_option> cbPstnOptions) {
        this.mycbPstnOptions = cbPstnOptions;
    }

    public String getPstnFormat() {
        return myPstnFormat;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivitySwitchnumbersinglelistitemBinding activitySwitchnumbersinglelistitemBinding = ActivitySwitchnumbersinglelistitemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activitySwitchnumbersinglelistitemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mycbPstnOptions.size();//50
    }

    private class MyViewHolder extends BaseViewHolder implements ItemListener {
        String veh = null;
        ActivitySwitchnumbersinglelistitemBinding mBinding;
        SwitchNumberItemViewModel switchNumberViewModel;

        public MyViewHolder(ActivitySwitchnumbersinglelistitemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            Post_option postoption = mycbPstnOptions.get(position);
            switchNumberViewModel = new SwitchNumberItemViewModel(postoption, this);
            mBinding.setViewModel(switchNumberViewModel);
            mBinding.executePendingBindings();
            if (mLastSelectedIndex == position) {
                mBinding.popupElement.setBackgroundResource(R.color.colorAccent1);
            } else {
                mBinding.popupElement.setBackgroundResource(R.color.colorAccent2);
            }
        }


        @Override
        public void onItemClick(Post_option postoption) {
            mLastSelectedIndex = getAdapterPosition();
            notifyDataSetChanged();
            myPstnFormat = postoption.getPstn_format();


        }

    }
}


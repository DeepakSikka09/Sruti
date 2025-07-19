package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ecomexpress.sruti.databinding.ActivityScanSummarySingleItemBinding;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.base.BaseViewHolder;


public class GlobalManifestSummaryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Manifest_List> myawbList;
    GlobalManifestSummaryViewModel globalManifestSummaryViewModel;


    public GlobalManifestSummaryAdapter(List<Manifest_List> globalScans) {
        this.myawbList = globalScans;
    }

    public void setData(List<Manifest_List> awbList, GlobalManifestSummaryViewModel globalManifestSummaryViewModel) {
        try {
            this.globalManifestSummaryViewModel = globalManifestSummaryViewModel;
            this.myawbList.clear();
            this.myawbList.addAll(awbList);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityScanSummarySingleItemBinding activityGlobalScanSingleItemBinding = ActivityScanSummarySingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GlobalManifestSummaryAdapter.MyViewHolder(activityGlobalScanSingleItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myawbList.size();//50
    }

    private class MyViewHolder extends BaseViewHolder implements IScanSummaryItemAdapterInterface {

        ActivityScanSummarySingleItemBinding mBinding;
        GlobalManifestSummaryAdapterViewModel globalManifestSummaryAdapterViewModel;

        public MyViewHolder(ActivityScanSummarySingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            Manifest_List globalScan = myawbList.get(position);

            globalManifestSummaryAdapterViewModel = new GlobalManifestSummaryAdapterViewModel(globalScan, this, position);
            mBinding.setViewModel(globalManifestSummaryAdapterViewModel);
            mBinding.executePendingBindings();

        }


        @Override
        public void onCameraClick(Manifest_List manifest_list, int position) {
//            iScanSummaryAdapterListener.onCameraClick(manifest_list);
            globalManifestSummaryViewModel.passImageView(mBinding.cam, position, manifest_list.getManifest_No());

        }


    }
}


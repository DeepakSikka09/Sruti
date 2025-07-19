package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityGlobalScanDetailBinding;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.starttrip.Image_Response;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.utils.common_files.ImageHandler;


public class GlobalManifestSummaryActivity extends BaseActivity<ActivityGlobalScanDetailBinding, GlobalManifestSummaryViewModel> implements IScanSummaryNavigator {
    @Inject
    public GlobalManifestSummaryViewModel globalManifestSummaryViewModel;
    public ActivityGlobalScanDetailBinding activityGlobalScanDetailBinding;
    List<Manifest_List> manifest_l = new ArrayList<>();
    ArrayList<String> manifest_type;
    List<Manifest_List> hashManifestList = new ArrayList<>();
    @Inject
    GlobalManifestSummaryAdapter globalManifestSummaryAdapter;
    public ImageHandler imageHandler;
    public ArrayList<Long> manifestNoArray;
    private String vehicleType;
    ArrayList<Image_Response> image_responseArrayList = new ArrayList<>();
    long clikpos;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalManifestSummaryViewModel.setNavigator(this);
        this.activityGlobalScanDetailBinding = getViewDataBinding();

        manifestNoArray = new ArrayList<Long>();
        manifest_type = new ArrayList<>();
        vehicleType = getIntent().getExtras().getString("vehicleType");


        globalManifestSummaryViewModel.getScannedManifestListAllData();

        globalManifestSummaryViewModel.getMaListLiveData().observe(this, new Observer<List<Manifest_List>>() {
            @Override
            public void onChanged(@Nullable List<Manifest_List> manifest_lists) {
                for (int i = 0; i < manifest_lists.size(); i++) {
                    manifestNoArray.add(manifest_lists.get(i).getManifest_No());
                    Set<Long> set = new HashSet<>(manifestNoArray);
                    manifestNoArray.clear();
                    manifestNoArray.addAll(set);
                    manifest_l = manifest_lists;
                    manifest_type.add(manifest_l.get(i).getManifest_type());
                }
                if (manifest_lists.size() > 0) {
                    manifest_l.addAll(manifest_lists);
                    globalManifestSummaryViewModel.getPickedCount(manifest_lists);
                    hashManifestList = manifest_lists;
                } else {
                    GlobalManifestSummaryActivity.this.showToast(getString(R.string.no_data_found));
                }
            }

        });

        setUpImageHandler();
        setUp();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected String getScreenName() {
        return "Global Manifest Screen";
    }

    private void setUpImageHandler() {
        imageHandler = new ImageHandler(GlobalManifestSummaryActivity.this) {
            @Override
            public void onBitmapReceived(Bitmap bitmap, String imageUri, ImageView imgView, String imageName, String imageCode) {
                try {
                    if (imgView != null) {
                        imgView.setImageBitmap(bitmap);

                    }
                    ImageModel imageModel = new ImageModel();
                    imageModel.setManifest_id(String.valueOf(clikpos));
                    imageModel.setImage_name(imageName);
                    imageModel.setImage_type("others");
                    imageModel.setImage_code(imageCode);
                    imageModel.setFilePath(imageUri);
                    imageModel.setStatus(0);

                    // local database save
                    globalManifestSummaryViewModel.uploadLocalImage(imageModel);

                    Image_Response image_response = new Image_Response();
                    image_response.setImage_id("-1");
                    image_response.setImage_key(imageCode);
                    image_responseArrayList.add(image_response);
                } catch (Exception e) {
                    Toast.makeText(GlobalManifestSummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    @Override
    public GlobalManifestSummaryViewModel getViewModel() {
        return globalManifestSummaryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_global_scan_detail;
    }

    private void setUp() {
        activityGlobalScanDetailBinding.fuelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityGlobalScanDetailBinding.fuelRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityGlobalScanDetailBinding.fuelRecyclerView.setAdapter(globalManifestSummaryAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imageHandler.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    @Override
    public void notifyAdapter() {
        globalManifestSummaryAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String s) {
        showToast(s);
    }


    @Override
    public void onNext() {
        try {

            Bundle manifest_data = new Bundle();
            manifest_data.putSerializable("manifest_type_collection", manifest_type);
            Intent intent = new Intent(GlobalManifestSummaryActivity.this, GlobalShipmentListActivity.class);
            intent.putExtra("manifest_count", activityGlobalScanDetailBinding.getViewModel().getTotalManifestCount().get());
            intent.putExtra("picked_count", activityGlobalScanDetailBinding.getViewModel().getPickedShipmentCount().get());
            intent.putExtra("unpicked_shipment_count", activityGlobalScanDetailBinding.getViewModel().getUnpickedShipmentCount().get());
            intent.putExtra("pending_count", activityGlobalScanDetailBinding.getViewModel().getPendingShipmentCount().get());
            intent.putExtra("vehicleType", vehicleType);
            intent.putExtra("imageArrayList", image_responseArrayList);
            intent.putExtra("manifestNoArray", manifestNoArray);
            intent.putExtra("manifest_type", manifest_data);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAdapter(HashSet<Manifest_List> hashValue) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                long totalRemaing = 0, total = 0, unPicked = 0, remaining = 0, picked = 0, totalPicked = 0, totalShipment = 0, totalUnpicked = 0;

                try {
                    if (hashValue != null) {
//                        hashManifestList = hashValue;
                        List<Manifest_List> manifestLists = new ArrayList<>();
                        hashManifestList = manifestLists;
                        for (Manifest_List co : hashValue) {
                            manifestLists.add(co);

                            remaining = co.getRemaining_count();
                            picked = co.getPicked_count();
                            unPicked = co.getUnpicked_count();
                            total = co.getTotalShipmentCount();
                            totalRemaing += remaining;
                            totalPicked += picked;
                            totalUnpicked += unPicked;
//                            totalShipment = totalPicked + totalRemaing + unPicked;
                            totalShipment += total;
                        }

                        globalManifestSummaryAdapter.setData(manifestLists, globalManifestSummaryViewModel);
                        try {
                            String getTotalManifest = String.valueOf(globalManifestSummaryAdapter.getItemCount());
                            globalManifestSummaryViewModel.totalManifestCount.set(getTotalManifest);
                            globalManifestSummaryViewModel.pendingShipmentCount.set(String.valueOf(totalRemaing));
                            globalManifestSummaryViewModel.pickedShipmentCount.set(String.valueOf(totalPicked));
                            globalManifestSummaryViewModel.totalShipmentCount.set(String.valueOf(totalShipment));
                            globalManifestSummaryViewModel.unpickedShipmentCount.set(String.valueOf(totalUnpicked));
                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    } else {
                        showToast(getString(R.string.null_value));
                    }
                } catch (Exception e) {
                    showToast(e.getMessage());
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onBackClick() {
        super.onBackPressed();
    }

    @Override
    public void captureImage(ImageView cam, int position, long manifest_list) {
        clikpos = manifest_list;
        imageHandler.captureImage(manifest_list + "_image.png", cam, manifest_list + "_image");


    }
}
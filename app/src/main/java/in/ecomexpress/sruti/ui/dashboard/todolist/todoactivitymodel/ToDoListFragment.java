package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.TodoListFragmentBinding;
import in.ecomexpress.sruti.databinding.WarningDialogBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.ToDoAdaptertoActivityListener;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.TodoFragmentToActivityLIstener;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListAdapter;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.common_files.swipe_list_item.RecyclerItemTouchHelper;
import in.ecomexpress.sruti.utils.common_files.swipe_list_item.SwipeControllerActions;

/**
 * Created by 63091 on 09-07-2019.
 */
public class ToDoListFragment extends Fragment {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    private RecyclerView todo_list;
    private ToDoListAdapter toDoListAdapter;
    private ToDoListViewModel toDoListViewModel;
    private TodoListFragmentBinding viewDataBinding;
    private IntentIntegrator qrScan;
    private DRSListRemarkListener listRemarkListener;
    private List<Manifest_List> manifest_list1;
    private Manifest_List manifest_list;
    public double wayLatitude = 0.0, wayLongitude = 0.0;
    int positionlft;
    int selectManifestPickedShipmentCount;
    String mdc_GeoLoc_Validation = "";
    private boolean allowFeToApplyReasonCode = false;


    RecyclerItemTouchHelper swipeController = new RecyclerItemTouchHelper(new SwipeControllerActions() {
        @Override
        public void onRightClicked(int position) {
            long start_time = CommonUtils.changeDateTime(manifest_list1.get(position).getManifest_details().getLocation().getPickup_slot_time_in());
            long end_time = CommonUtils.changeDateTime(manifest_list1.get(position).getManifest_details().getLocation().getPickup_slot_time_out());
            String start = CommonUtils.getTime(manifest_list1.get(position).getManifest_details().getLocation().getPickup_slot_time_in());
            String end = CommonUtils.getTime(manifest_list1.get(position).getManifest_details().getLocation().getPickup_slot_time_out());


            if (CommonUtils.checkTimeslot(start_time, end_time)) {

                openReasonActivity(manifest_list1.get(position), position);

            } else {
                if (manifest_list1.get(position).getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("W")) {


                    showManifestWarningDialog("W", start + " - " + end, manifest_list1.get(position), position);

                } else if (manifest_list1.get(position).getManifest_details().getLocation().getValidate_pickup_slot().equalsIgnoreCase("R")) {
                    showManifestWarningDialog("R", start + " - " + end, manifest_list1.get(position), position);

                } else {
                    openReasonActivity(manifest_list1.get(position), position);
                }
            }
        }
    });

    public void openReasonActivity(Manifest_List manifest_list1, int position) {
        if (listRemarkListener != null) {
            if (toDoListViewModel.getDataManager().getParent()) {
                Location feLocation = new Location("");
                feLocation.setLatitude(wayLatitude);
                feLocation.setLongitude(wayLongitude);
                Vender_Detail vendorDetails = manifest_list1.getManifest_details();

                toDoListViewModel.getPickedShipmentCountTest(manifest_list1);
                toDoListViewModel.pickedShipmentCount.observe(getActivity(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {

                        if (integer > 0 || ((ToDoListActivity) getActivity()).isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), toDoListViewModel.getGeoFenceRadius())) {
                            commitPacketConditionalProcess(position);
                        } else if (ToDoListFragment.this.manifest_list1.get(position).getManifest_details().isVerify_geocode()) {
                            showFeGeoLocWarningDialog(((ToDoListActivity) getActivity()).getFe_distanceFromSeller(), ToDoListFragment.this.manifest_list1.get(position), mdc_GeoLoc_Validation, position);
                        } else {
                            commitPacketConditionalProcess(position);
                        }
                        toDoListViewModel.pickedShipmentCount.removeObservers(getActivity());
                    }
                });


//                    toDoListViewModel.getCount(manifest_list1.get(position).getManifest_No()).observe(getActivity(), new Observer<Integer>() {
//                        @Override
//                        public void onChanged(Integer integer) {
//
//
//                            if (integer > 0) {
//                                commitPacketConditionalProcess(manifest_list1, position, true);
//                            } else if (((ToDoListActivity) getActivity()).isInManifestRadiusDistanceToMethod(feLocation, vendorDetails.getLocation().getLatitude(), vendorDetails.getLocation().getLongitude(), 65)) {
//                                commitPacketConditionalProcess(manifest_list1, position, false);
//                            } else {
//                                Toast.makeText(getActivity(), "Please reach Seller location for approximately " + ((ToDoListActivity) getActivity()).getFe_distanceFromSeller() + " meters", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    });
            } else {
                Toast.makeText(getActivity(), R.string.rights_to_apply_reason_code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void commitPacketConditionalProcess(int position) {
        if (this.manifest_list1.get(position).getCommit_status() == Constants.COMMIT_PICKED || this.manifest_list1.get(position).getCommit_status() == Constants.COMMIT_SERVER_SYNC) {
            toDoListAdapter.notifyItemChanged(position);
            Toast.makeText(getActivity(), R.string.shipment_commit, Toast.LENGTH_SHORT).show();
        } else {
            positionlft = position;
            System.out.println("FGFGFG 1");
            toDoListViewModel.getAllShipmentList(this.manifest_list1.get(position).getManifest_No());
        }
    }


    public void setSwipeListener() {
        if (todo_list != null) {
            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
            itemTouchhelper.attachToRecyclerView(todo_list);
            todo_list.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    if (swipeController != null)
                        swipeController.onDraw(c);
                }
            });
        }
    }

    public void setLatLng(double lat, double lng) {
        this.wayLatitude = lat;
        this.wayLongitude = lng;
        toDoListAdapter.setLatLng(wayLatitude, wayLongitude);
    }


    public interface DRSListRemarkListener {
        void addReasonCode(int position);
    }

    TodoFragmentToActivityLIstener todoFragmentToActivityLIstener;

    public static ToDoListFragment getInstance() {
        return new ToDoListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        toDoListViewModel.pickedShipmentCount.removeObservers(this);
    }

    public void setCountOnDrs(long picked, long total) {
        toDoListAdapter.setCountOnDrs(picked, total);
    }

    public void setListRemarkListener(DRSListRemarkListener listener) {
        this.listRemarkListener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            todoFragmentToActivityLIstener = (TodoFragmentToActivityLIstener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TodoFragmentToActivityLIstener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.todo_list_fragment, container, false);
        toDoListViewModel = ViewModelProviders.of(getActivity(), viewModelProviderRoom).get(ToDoListViewModel.class);
        todo_list = viewDataBinding.todoList.findViewById(R.id.todo_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        todo_list.setLayoutManager(layoutManager);
        toDoListAdapter = new ToDoListAdapter((ToDoListActivity) getActivity());
        todo_list.setAdapter(toDoListAdapter);


        if (toDoAdaptertoActivityListener != null) {
            toDoListAdapter.settoDoAdaptertoActivityListener(toDoAdaptertoActivityListener);
        } else {
            Log.d("onCreateView: ", "null");
        }
        qrScan = new IntentIntegrator(getActivity());

        return viewDataBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toDoListViewModel.loadManifest().observe(getActivity(), manifest_lists -> {
            try {
                if (manifest_lists != null) {
                    toDoListAdapter.updateView(manifest_lists);
                    todoFragmentToActivityLIstener.onGettingManifest(manifest_lists);
                    manifest_list1 = manifest_lists;

                    mdc_GeoLoc_Validation = toDoListViewModel.getDataManager().get_pickup_geofencing_mode();
                    setSwipeListener();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        toDoListViewModel.getAllShipment().observe(getViewLifecycleOwner(), shipment -> {
            if (shipment != null) {
                if (toDoListViewModel.checkIfAtLeastScanSuccessfully(shipment)) {
                    Toast.makeText(getActivity(), R.string.scan_validate_reason_code, Toast.LENGTH_SHORT).show();
                } else {
                    listRemarkListener.addReasonCode(positionlft);
                }
            }
        });

    }

    public void setReasonCode(String remarks) {
        toDoListAdapter.setReasonCode(remarks);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void callToAdapter(String data) {
        toDoListAdapter.getFilter().filter(data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    ToDoAdaptertoActivityListener toDoAdaptertoActivityListener = new ToDoAdaptertoActivityListener() {
        @Override
        public void onBarcodeScanItem(Manifest_List manifest) {
            manifest_list = manifest;
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setCaptureActivity(QrcReaderActivity.class);
            integrator.addExtra("location_type", manifest_list.getManifest_type());
            integrator.addExtra("manifest_no", manifest_list.getManifest_No());
            integrator.initiateScan();
            integrator.setBeepEnabled(true);
        }


        @Override
        public void commitToserver(long manifest_no) {
            toDoListViewModel.commitToserver(getActivity(), manifest_no);
        }
    };

    public void validateScanResult(String contents) {
        toDoListViewModel.updateShipment(Constants.PENDING, "", Calendar.getInstance().getTimeInMillis() + "", false, false, manifest_list.getManifest_No());

        try {
            if (manifest_list.getSetting().getQr_code().equalsIgnoreCase(contents)) {
                if (manifest_list.getLocation_type().equalsIgnoreCase("warehouse")) {
                    Intent intent = WarehouseActivity.getStartIntent(getContext());
                    intent.putExtra("data", manifest_list);
                    intent.putExtra("manifest_no", manifest_list.getManifest_No());
                    getContext().startActivity(intent);
                } else if (manifest_list.getLocation_type().equalsIgnoreCase("seller")) {
                    Intent intent = SellerActivity.getStartIntent(getContext());
                    intent.putExtra("data", manifest_list);
                    intent.putExtra("manifest_no", manifest_list.getManifest_No());
                    getContext().startActivity(intent);
                }
            } else {
                Toast.makeText(getContext(), "QR CODE NOT MATCHED", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFilteredAdapter(List<Manifest_List> filteredManifestTypeList) {
        try {
            if (filteredManifestTypeList != null) {
                toDoListAdapter.updateView(filteredManifestTypeList);
                Log.e("manifest_lists ", "" + filteredManifestTypeList.size());
                // setSwipeListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isFeGeoLocWarningDialogVisible = false;

    public void showFeGeoLocWarningDialog(String distanceFromDC, Manifest_List manifest, String warningRestriction, int position) {
        if (!isFeGeoLocWarningDialogVisible) {
            isFeGeoLocWarningDialogVisible = true;
            String message = "";
            if (warningRestriction.equalsIgnoreCase("W") || warningRestriction.trim().length() == 0) {
                message = "You are " + distanceFromDC + " mtrs away from Pickup location.\n" + "Do you want to continue?";
            } else if (warningRestriction.equalsIgnoreCase("R")) {
                message = "Not allowed to update reason code as\nactivity is performed " + distanceFromDC + "mtrs\noutside of geofence.";
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (warningRestriction.equalsIgnoreCase("W") && manifest.getManifest_details().isVerify_geocode()) {
                                commitPacketConditionalProcess(position);
                            } else if (warningRestriction.equalsIgnoreCase("R") && manifest.getManifest_details().isVerify_geocode()) {
                            } else if (warningRestriction.trim().length() == 0) {
                                commitPacketConditionalProcess(position);
                            }
                            dialog.dismiss();
                            isFeGeoLocWarningDialogVisible = false;
                        }
                    })
                    .setCancelable(false);
            alertDialog.show();
        }
    }


    public void showManifestWarningDialog(String validate, String pickup_slot, Manifest_List manifest_list, int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        WarningDialogBinding warningDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.warning_dialog, (ViewGroup) viewDataBinding.getRoot(), false);
        dialog.setContentView(warningDialogBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (validate.equalsIgnoreCase("W")) {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + getActivity().getString(R.string.warning_message));
        } else {
            warningDialogBinding.tvMsg.setText("Pickup time slot is " + pickup_slot + " " + getActivity().getString(R.string.restriction_message));
        }
        warningDialogBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate.equalsIgnoreCase("W")) {
                    openReasonActivity(manifest_list, position);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }


            }
        });

        dialog.show();

    }
}

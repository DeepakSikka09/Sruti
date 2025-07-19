package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;


import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import androidx.lifecycle.LiveData;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IQrRecciView;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by 63091 on 22-08-2019.
 */

public class RecciViewModel extends BaseViewModel<IQrRecciView> {
    public RecciViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public LiveData<List<General_Question>> getGeneral_question(List<Integer> ids) {
        return getDataManager().getRecciQuestion(ids);
    }

    public void createCommitPacketNew(ArrayList<Recci> recci, long pickup_location_id, double wayLatitude, double wayLongitude) {
        try {
            CommitPacketData commitPacketData = new CommitPacketData();
            commitPacketData.setTrip_id(Long.valueOf(getDataManager().getTripID()));
            commitPacketData.setEmp_code(getDataManager().getCode());
            if (getDataManager().getParent()) {
                commitPacketData.setFe_type("parent");
            } else if (getDataManager().getChild()) {
                commitPacketData.setFe_type("child");
            }

            ArrayList<Manifest_process> manifest_commit_package = new ArrayList<>();
            Manifest_process manifest_process = new Manifest_process();

            if (pickup_location_id != 0)
                manifest_process.setPickup_location_id(pickup_location_id);

            manifest_process.setManifest_type("R");
            manifest_process.setLocation_longitude(String.valueOf(wayLongitude));
            manifest_process.setLocation_latitude(String.valueOf(wayLatitude));
            manifest_process.setCommit_location_radius("100");
            manifest_process.setCommit_time(Calendar.getInstance().getTimeInMillis() + "");
            manifest_process.setParentmanifestNo("NA");
            manifest_process.setStatus_code("406");


            manifest_process.setRecci(recci);
            commitPacketData.setManifest_process(manifest_commit_package);
            manifest_commit_package.add(manifest_process);

            saveCommit(commitPacketData);

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void saveCommit(CommitPacketData commitPacketData) {
        PushApi pushApi = new PushApi();
        pushApi.setCompositeKey("0" + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());

        pushApi.setAuthtoken(getDataManager().getAuthToken());
        try {
            // pushApi.setRequestData(new ObjectMapper().writeValueAsString(commitPacketData));
            pushApi.setShipmentStatus(0);
            pushApi.setEmpId(getDataManager().getCode());
            pushApi.setApiVer(version);
            pushApi.setAppId("1");
            pushApi.setApiVer("mobile");

            getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    updateRecciList("0" + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());
                    getNavigator().nextScreen("0" + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));
                    getDataManager().inScanCommitRecciPacket(1, "0" + "_" + commitPacketData.getManifest_process().get(0).getPickup_location_id());
                }
            }));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecciList(String compositeKey) {
        getDataManager().updateRecciList(String.valueOf(Constants.COMMIT_PICKED), compositeKey);
    }

    public void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }


}

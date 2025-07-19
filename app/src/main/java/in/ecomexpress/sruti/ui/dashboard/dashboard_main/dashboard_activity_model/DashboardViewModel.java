package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model;


import static in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity.version;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import in.ecomexpress.sruti.model.DashboardBanner;
import in.ecomexpress.sruti.model.Departure.CountManifest;
import in.ecomexpress.sruti.model.Departure.DepartVehicles;
import in.ecomexpress.sruti.model.Departure.Departure_Response;
import in.ecomexpress.sruti.model.Departure.Depature_Request;
import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.commitdata.Manifest_process;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.commitdata.ShipmentDetail;
import in.ecomexpress.sruti.model.login.LoginResponse;
import in.ecomexpress.sruti.model.login.LogoutRequest;
import in.ecomexpress.sruti.model.login.LogoutResponse;
import in.ecomexpress.sruti.model.masterdata.Global_Config_Item;
import in.ecomexpress.sruti.model.masterdata.Master_Data_Response;
import in.ecomexpress.sruti.model.masterdata.User_Data;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Menifest_Data_Master;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.selfDrop.SelfDropRequest;
import in.ecomexpress.sruti.model.selfDrop.SelfDropResponse;
import in.ecomexpress.sruti.model.starttrip.StartTripRequest;
import in.ecomexpress.sruti.model.starttrip.StartTripResponse;
import in.ecomexpress.sruti.model.stoptrip.StopTrip;
import in.ecomexpress.sruti.model.stoptrip.StopTripRequest;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.IDashboardNavigator;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;


public class DashboardViewModel extends BaseViewModel<IDashboardNavigator> {

    String multi_space_allow = "false";
    ArrayList<String> multipspaceList = new ArrayList<>();
    private MediatorLiveData<Master_Data_Response> mediatorLiveDataMaster = new MediatorLiveData<>();
    private MediatorLiveData<LogoutResponse> liveDataMerger = new MediatorLiveData<>();
    private MutableLiveData<List<DashboardBanner>> dashboardbanner = new MutableLiveData<>();
    private MediatorLiveData<StopTrip> stopTripresponse = new MediatorLiveData<StopTrip>();
    private MediatorLiveData<StartTripResponse> startTripresponse = new MediatorLiveData<StartTripResponse>();
    private MediatorLiveData<Departure_Response> detartureTime = new MediatorLiveData<Departure_Response>();
    private MediatorLiveData<Menifest_Data_Master> liveMenifestData = new MediatorLiveData<>();
    private MutableLiveData<String> isloadingcomplet = new MutableLiveData<>();
    private MediatorLiveData<List<Manifest_List>> unpickedmanifest = new MediatorLiveData<>();
    private MediatorLiveData<List<Shipment_Detail>> uncommit_manifestdata = new MediatorLiveData<List<Shipment_Detail>>();
    private MediatorLiveData<List<LoginResponse.StartRouteDetails>> started_vehicle = new MediatorLiveData<>();
    private MediatorLiveData<SelfDropResponse> selfDrop_manifest = new MediatorLiveData<>();
    private MediatorLiveData<Integer> ismanifestscanning = new MediatorLiveData<>();
    private MediatorLiveData<Integer> ismanifestscanningForChild = new MediatorLiveData<>();
    // private ArrayList<Long> manifestCollection;

    public DashboardViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    // Return from LiveData
    public LiveData<Integer> getIsmanifestScanning() {
        return ismanifestscanning;
    }

    public LiveData<Integer> getIsmanifestScanningForChild() {
        return ismanifestscanningForChild;
    }

    public MediatorLiveData<List<Manifest_List>> getUnpickedmanifest() {
        return unpickedmanifest;
    }

    public MediatorLiveData<SelfDropResponse> getSelfDropManifest() {
        return selfDrop_manifest;
    }

    public MediatorLiveData<List<Shipment_Detail>> getUncommit_manifestdata() {
        return uncommit_manifestdata;
    }

    public LiveData<Master_Data_Response> getFetched() {
        return mediatorLiveDataMaster;
    }

    public LiveData<String> isDataloading() {
        return isloadingcomplet;
    }

    public MediatorLiveData<Departure_Response> getDetartureTime() {
        return detartureTime;
    }

    public LiveData<Menifest_Data_Master> getMenifest() {
        return liveMenifestData;
    }

    LiveData<StartTripResponse> getStartTripResponse() {
        return startTripresponse;
    }

    public LiveData<LogoutResponse> getLiveDataMerger() {
        return liveDataMerger;
    }

    LiveData<List<LoginResponse.StartRouteDetails>> getStartedVehicle() {
        return started_vehicle;
    }

    LiveData<StopTrip> getStopTripResponse() {
        return stopTripresponse;
    }

    public LiveData<CountManifest> getStatusCommit() {
        return getDataManager().checkAllManifestStatus();
    }

    public int getTripID() {
        return getDataManager().getTripID();
    }

    public boolean isParent() {
        return getDataManager().getParent();
    }

    // Click event
    public void onPickupClick() {
        getNavigator().openTodo();
    }

    public void onHandoverClick() {
        getNavigator().onHandoverClick();
    }

    public void onFuelClick() {
        getNavigator().openFuelReimburse();
    }

    public void onStatisticsClick() {
        getNavigator().openstatisticsActivity();
    }

    public void onStartStopTripClick() {

        getNavigator().onStartStopTrip();

    }


    public void deleteDirectory(Context context) {
        try {
            File dir = context.getFilesDir();

            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                   /* if (
                  file.isDirectory()) {
                         deleteDirectory(context);
                    } else {*/
                 /*   if(file.getAbsolutePath().contains("crashlytics") || file.getAbsolutePath().contains("generatefid.lock") || file.getAbsolutePath().contains("json")) {

                    } else {*/
                    file.delete();
                    /* }*/
                }
            }
            dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAttendanceClick() {
        getNavigator().onAttendanceClick();
    }

    public void onTrainingClick() {
        getNavigator().onTrainingClick();
    }

    public void onSosClick() {
        getNavigator().onSosClick();
    }

    public void onLogoutClick() {
        getNavigator().onLogoutClick();
    }

    public void executeDataForManifestScanning() {
        LiveData<Integer> ob = getDataManager().getStartedManifestCount();
        ismanifestscanning.addSource(ob, integer -> {
            ismanifestscanning.removeSource(ob);
            ismanifestscanning.setValue(integer);
        });
    }

    public void executeDataForManifestScanningForChild() {
        LiveData<Integer> ob = getDataManager().getStartedManifestCount();
        ismanifestscanningForChild.addSource(ob, integer -> {
            ismanifestscanningForChild.removeSource(ob);
            ismanifestscanningForChild.setValue(integer);
        });
    }

    public LiveData<List<DashboardBanner>> getBannerData() {
        dashboardbanner.setValue(getDataManager().getDashBoardBanner());
        return dashboardbanner;
    }

    public LiveData<LogoutResponse> getAuthToken() {
        return getDataManager().callAuthToken(getDataManager().getAuthToken());
    }

    void fetchStartedVehicle() {
        LiveData<List<LoginResponse.StartRouteDetails>> ob = getDataManager().fetchStartedVehicle(getDataManager().getAuthToken(), getDataManager().getRouteID(), getDataManager().getCode());
        started_vehicle.addSource(ob, stopResponse -> {
            started_vehicle.removeSource(ob);
            started_vehicle.setValue(stopResponse);
        });
    }


    public LiveData<List<Manifest_List>> loadManifest() {
        return getDataManager().getManifestDetailStatus();
    }

    void selfDrop(ArrayList<Long> manifestCollection) {
        SelfDropRequest selfDropRequest = new SelfDropRequest();
        selfDropRequest.setEmp_code(getDataManager().getCode());
        selfDropRequest.setPickup_route_id(getDataManager().getRouteID());
        selfDropRequest.setManifest_ids(manifestCollection);

        LiveData<SelfDropResponse> obj = getDataManager().selfDrop(getDataManager().getAuthToken(), selfDropRequest);
        selfDrop_manifest.addSource(obj, selfDropResponse -> {
            selfDrop_manifest.removeSource(obj);
            selfDrop_manifest.setValue(selfDropResponse);
        });
    }

    void combineManifestData() {
        try {
            LiveData<Menifest_Data_Master> manifestDetail = getDataManager().getManifestDetail(getDataManager().getAuthToken(), getDataManager().getRouteID());
            liveMenifestData.addSource(manifestDetail, menifest_data -> {
                liveMenifestData.removeSource(manifestDetail);
                if (menifest_data != null) {
                    if (menifest_data.getStatus()) {
                        try {
                            ArrayList<Shipment_Detail> shipment_detailsinsert = new ArrayList<>();
                            if (menifest_data.getResponse() != null && menifest_data.getResponse().getManifest_list_data() != null) {
                                for (Manifest_List manifest_list : menifest_data.getResponse().getManifest_list_data()) {
                                    ArrayList<Shipment_Detail> shipment_details = manifest_list.getShipment_details();

                                    if (shipment_details != null && shipment_details.size() > 0) {
                                        for (Shipment_Detail shipment_detail : shipment_details) {
                                            shipment_detail.setManifestNoInchild(manifest_list.getManifest_No());
                                            shipment_detail.setLocation_type(manifest_list.getLocation_type());
                                        }

                                        shipment_detailsinsert.addAll(shipment_details);
                                    }
                                }
                            }
                            getDataManager().setCutOffTime(menifest_data.getResponse().getManifest_cutoff_time());
                            getDataManager().setRouteID(menifest_data.getResponse().getRoute_id());

                            List<Manifest_List> tempList = new ArrayList<>();
                            List<Manifest_List> manifestList = menifest_data.getResponse().getManifest_list_data();
                            for (Manifest_List mList : manifestList) {
                                mList.setLast_sync_time(menifest_data.getResponse().getLast_sync_time());
                                tempList.add(mList);
                            }
                            getDataManager().insertManifestShipmentDetail(tempList, shipment_detailsinsert);
                            isloadingcomplet.postValue("success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            isloadingcomplet.postValue(menifest_data.getDescription());
                        }
                    } else {
                        isloadingcomplet.postValue(menifest_data.getDescription());
                    }
                } else {
                    isloadingcomplet.postValue(menifest_data.getDescription());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isloadingcomplet.postValue(e.getMessage());
        }

    }

    public void deleteData() {
        getDataManager().deleteData();
    }

    public void deleteDataAtLogout() {
        getDataManager().deleteDataAtLogout();
    }

    void logoutRequest() {
        setIsLoading(true);
        try {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setUsername(getDataManager().getCode());
            logoutRequest.setLogoutLat(getDataManager().getCurrentLatitude());
            logoutRequest.setLogoutLng(getDataManager().getCurrentLongitude());

            LiveData<LogoutResponse> att = getDataManager().doLogout(getDataManager().getAuthToken(), logoutRequest);
            liveDataMerger.addSource(att, logoutResponse -> {
                if (liveDataMerger != null) {
                    liveDataMerger.removeSource(att);
                    getDataManager().clearPrefrence();
                    getDataManager().clearPopPrefrence();
                    liveDataMerger.setValue(logoutResponse);
                    System.out.println("response" + logoutResponse);
                    getDataManager().clearPrefrence();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            setIsLoading(false);
        }
    }

    void combineData() {
        try {
            User_Data ob = new User_Data();
            ob.setUsername(getDataManager().getCode());
            LiveData<Master_Data_Response> master_data_responseLiveData = getDataManager().doToDoList(getDataManager().getAuthToken(), ob);
            if (master_data_responseLiveData != null) {
                mediatorLiveDataMaster.addSource(master_data_responseLiveData, master_data_reason -> {
                    mediatorLiveDataMaster.removeSource(master_data_responseLiveData);
                    if (master_data_reason.isStatus()) {
                        try {
                            ThreadGeneric.executeCall(() -> {
                                System.out.println("DDDDDDDD");
                                getDataManager().insertToDoList(master_data_reason.getResponse().getReason_code_list(), master_data_reason.getResponse().getCallbridge_configuration().getCb_pstn_options());
                                getDataManager().setDashBoardBanner(new Gson().toJson(master_data_reason.getResponse().getBanner_configuration().getDashboard_banner()));
                                setDefaultcallbridgeNo(master_data_reason);
                                setGlobalConfigurations(master_data_reason);
                                setApplicationConfigration(master_data_reason);

                                getDataManager().setRecciQuestion(master_data_reason.getResponse().getGeneral_question());
                                dashboardbanner.postValue(getDataManager().getDashBoardBanner());
                                checkandOpenMultiSpace();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Master Data " + master_data_reason.getDescription());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkandOpenMultiSpace() {
        getNavigator().checkMultiSpace(multipspaceList, multi_space_allow);


    }

    private void setApplicationConfigration(Master_Data_Response master_data_reason) {
        multipspaceList = new ArrayList<>();
        if (master_data_reason.getResponse().getApplication_configurations() != null && master_data_reason.getResponse().getApplication_configurations().getSrutiMultiSpaceAppList() != null) {
            multipspaceList.addAll(master_data_reason.getResponse().getApplication_configurations().getSrutiMultiSpaceAppList());
            getDataManager().setListOfMultiSpace(master_data_reason.getResponse().getApplication_configurations().getSrutiMultiSpaceAppList());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setGlobalConfigurations(Master_Data_Response master_data_reason) {
        try {
            if (master_data_reason.getResponse().getGlobal_configuration() != null) {

                //Map<String, Object> keys = new HashMap<>();
                for (Global_Config_Item global_config_item : master_data_reason.getResponse().getGlobal_configuration()) {
                    //keys.put(global_config_item.getConfig_group(), global_config_item.getConfig_value());

                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.FIELD_RETUN_ALERT_TIME)) {
                        getDataManager().set_cutOffAlertTime(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SCAN_AFTER_FIELD_RETURN_CUTOFF)) {
                        getDataManager().set_cutOffActionValue(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ENABLE_LIVE_TRACKING)) {
                        getDataManager().set_live_tracking(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ENABLE_ERM_SYNC)) {
                        getDataManager().set_erm_sync(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ENABLE_CALLING)) {
                        getDataManager().set_enable_calling(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.PICKUP_LOCATION_GEOFENCING_MODE)) {
                        getDataManager().set_pickup_geofencing_mode(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.PICKUP_LOCATION_GEOFENCING_RADIUS)) {
                        getDataManager().set_pickup_geofencing_radius(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP)) {
                        getDataManager().set_sruti_enable_otp_for_zero_pickup(global_config_item.getConfig_value());
                    }
                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ENABLE_DELINK_SHIPMENT_FOR_WH)) {
                        getDataManager().set_sruti_enable_delink_for_wh(global_config_item.getConfig_value());
                    }
                    // TODO SCAD2-15900

                    if (global_config_item.getConfig_group().equalsIgnoreCase(Constants.SRUTI_ALLOW_MULTI_SPACE_APPS)) {
                        multi_space_allow = global_config_item.getConfig_value();
                        getDataManager().set_sruti_allow_multispace(global_config_item.getConfig_value());
                    }

                    // getDataManager().setMultiSpaceApps("com.excean.parallelspace,com.lbe.parallel.intl,com.ludashi.dualspace,io.va.exposed,com.applisto.appcloner,com.oasisfeng.island,net.typeblog.shelter,com.excelliance.multiaccount,com.excelliance.multiaccounttwo,de.robv.android.xposed.installer,com.vmos.glb,com.cellcute.cloneapp,com.lbe.magiclollipop,com.dualspace.dual,com.peterpham.superclone,com.jiubang.commerce.gomultiple,multi.parallel.dualspace,com.excelliance.multiaccount,com.excelliance.multispace,com.ludashi.dualspace_lite,com.jumobile.multiapp,com.samsung.android.messaging:alternate,com.clonezap.clonezaplite,com.whatsclone.clone,com.wilcoxon.clone,com.raghuappcloner.superclone,com.excelliance.multispace,com.qlauncher.multiaccount,com.cloneapp.chat,com.ludashi.superboost,com.excelliance.multiaccountpro,com.excelliance.multispacepro,com.parrallelu.space,com.parallelaccounts.parallelaccounts,com.excelliance.multispacepro,com.qlauncher.dupspace,com.ludashi.superboost,com.parallel.browser,com.whatsclone.line,com.ludashi.dualspace64,com.excelliance.multispace,com.cellcute.cloneapp,com.dualspace.multiaccount.freshblue,com.multi.messenger.whatsapp,com.parallel.space.lite,com.ludashi.dualspace64,com.jumobile.multiapp,com.dualspace.pink,com.excelliance.multispace64,com.dualspace.golauncher,com.lbe.parallel.intl.dualapp,com.parallel.browser.chrome,com.excelliance.multispace.multiaccount,com.dualspace.parrallelaccounts,com.jumobile.multiapp64,com.ludashi.dualspace64.clean,com.multi.messenger.facebook,com.parallel.dualapp,com.dualspace.cloneapp,com.excelliance.multiaccount.pro,com.dualspace.golauncher.theme,com.jumobile.multiapp.pro,com.ludashi.dualspace64.green,com.multi.messenger.instagram,com.ludashi.dualspace64.cleanpro,com.lbe.parallel.intl.lite,com.dualspace.cloneapp.freshblue,com.excelliance.multiaccount.lite,com.ludashi.dualspace64.pink,com.dualspace.lite.cloneapp,com.jumobile.multiapp.cloneapp,com.ludashi.dualspace64.red,com.multi.messenger.telegram,com.ludashi.dualspace64.greenpro,com.lbe.parallel.intl.pro,com.dualspace.cloneapp.red,com.excelliance.multispace.parallel,com.ludashi.dualspace64.blue,com.dualspace.lite.cloneapp.freshblue,com.jumobile.multiapp.pro.cloneapp,com.ludashi.dualspace64.purple,com.multi.messenger.snapchat,com.ludashi.dualspace64.pinkpro,com.ludashi.superboost.cloneapp,com.ludashi.dualspace64.yellow,com.dualspace.lite.cloneapp.multiaccount,com.jumobile.multiapp.pro.clean,com.dualspace.cloneapp.yellow,com.excelliance.multispace.cloneapp,com.ludashi.dualspace64.orange,com.ludashi.dualspace64.redpro,com.ludashi.superboost.cloneapp.freshblue,com.dualspace.cloneapp.orange,com.excelliance.multispace64.clean,com.ludashi.dualspace64.green,com.ludashi.dualspace64.bluepro,com.ludashi.superboost.cloneapp.red,com.excelliance.multispace64.freshblue,com.dualspace.cloneapp.green,com.excelliance.multispace64.pink,com.ludashi.dualspace64.purplepro");


                }
                // getDataManager().set_sruti_enable_otp_for_zero_pickup(keys.getOrDefault(Constants.SRUTI_ENABLE_OTP_FOR_ZERO_PICKUP, "false"));
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void setDefaultcallbridgeNo(Master_Data_Response master_data_reason) {
        if (master_data_reason.getResponse().getCallbridge_configuration() != null)

            if (master_data_reason.getResponse().getCallbridge_configuration().getCb_pstn_options() != null && master_data_reason.getResponse().getCallbridge_configuration().getCb_pstn_options().size() > 0) {
                if (getDataManager().getPstnFormat() == null || getDataManager().getPstnFormat().isEmpty() || getDataManager().getPstnFormat().equals("")) {
                    getDataManager().setPstnFormat(master_data_reason.getResponse().getCallbridge_configuration().getCb_pstn_options().get(0).getPstn_format());
                } else {
                    Log.d("TAG", "getPstnformat" + getDataManager().getPstnFormat());
                }
            }
    }

    void submitData(StartTripRequest startTripRequest) {
        startTripRequest.setEmployee_code(getDataManager().getCode());
        int route_id = getDataManager().getRouteID();
        startTripRequest.setPickup_route_id(route_id);

        LiveData<StartTripResponse> ob = getDataManager().doStartTrip(getDataManager().getAuthToken(), startTripRequest);

        startTripresponse.addSource(ob, startResponse -> {
            startTripresponse.removeSource(ob);
            if (startResponse.getStatus()) {
                getDataManager().setTripID(startResponse.getResponse().getTrip_id());
                startTripresponse.setValue(startResponse);
            } else {
                startTripresponse.setValue(startResponse);
            }
        });

    }

    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedManifestQuery(commit_status, commit_failed, manifestno);
    }

    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        getDataManager().failedCommitPacket(commit_status, commit_failed, manifestno);
    }

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }

    void submitData(StopTripRequest stopTripRequest) {
        stopTripRequest.setEmployee_code(getDataManager().getCode());
        stopTripRequest.setVehicle_trip_id(getDataManager().getTripID());
        int route_id = getDataManager().getRouteID();
        stopTripRequest.setPickup_route_id(route_id);

        LiveData<StopTrip> ob = getDataManager().doStopTrip(getDataManager().getAuthToken(), stopTripRequest);

        stopTripresponse.addSource(ob, stopResponse -> {
            stopTripresponse.removeSource(ob);
            if (stopResponse.getStatus()) {
                getDataManager().setTripID(-1);
                stopTripresponse.setValue(stopResponse);
            } else {
                stopTripresponse.setValue(stopResponse);
            }
        });
    }


    public void getUnPickedManifestList() {
        LiveData<List<Manifest_List>> ob = getDataManager().getUnPickedManifestList();
        unpickedmanifest.addSource(ob, manifest_lists -> {
            unpickedmanifest.removeSource(ob);
            unpickedmanifest.setValue(manifest_lists);
        });

    }

    public void getAllShipmentlist(long manifest_no) {
        LiveData<List<Shipment_Detail>> ob = getDataManager().getAllShipmentlist(manifest_no);
        uncommit_manifestdata.addSource(ob, shipment_details -> {
            uncommit_manifestdata.removeSource(ob);
            System.out.println(manifest_no + "  manifest_no remove  " + "   " + shipment_details.size());
            uncommit_manifestdata.setValue(shipment_details);
        });
    }

    public synchronized void createCommitPacketNew(HashMap<Long, List<Shipment_Detail>> shipmentsDetails, String manifest_type) {
        try {
            Iterator<Map.Entry<Long, List<Shipment_Detail>>> ob = shipmentsDetails.entrySet().iterator();
            while (ob.hasNext()) {
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
                ArrayList<ShipmentDetail> list_ShipmentDetails = new ArrayList<>();
                Map.Entry<Long, List<Shipment_Detail>> pair = ob.next();

                List<Shipment_Detail> shipmentloc = shipmentsDetails.get(pair.getKey());
                for (int i = 0; i < shipmentloc.size(); i++) {
                    ShipmentDetail shipment_detail = new ShipmentDetail();
                    shipment_detail.setAirway_bill_number(shipmentloc.get(i).getAirwaybill_number());
                    shipment_detail.setStatus_code(Constants.FAILED);
                    shipment_detail.setDate_time(Calendar.getInstance().getTimeInMillis());
                    shipment_detail.setReason_code("RSN_80_40");//RSN_40_140
                    shipment_detail.setAdvance(false);
                    shipment_detail.setIs_bp_validated(shipmentloc.get(i).getIs_bp_validated());
                    list_ShipmentDetails.add(shipment_detail);
                }
                manifest_process.setManifest_no(pair.getKey());
                manifest_process.setManifest_type(manifest_type);
                manifest_process.setLocation_longitude(String.valueOf(getDataManager().getCurrentLongitude()));
                manifest_process.setLocation_latitude(String.valueOf(getDataManager().getCurrentLatitude()));
                manifest_process.setCommit_location_radius("100");
                manifest_process.setCommit_time(Calendar.getInstance().getTimeInMillis() + "");
                manifest_process.setParentmanifestNo("NA");
                manifest_process.setStatus_code("406");
                manifest_process.setShipments(list_ShipmentDetails);
                manifest_commit_package.add(manifest_process);

                commitPacketData.setManifest_process(manifest_commit_package);
                saveCommit(commitPacketData);

            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void saveCommit(CommitPacketData commitPacketData) {
        try {
            int i = 0;
            for (Manifest_process manifestProcess : commitPacketData.getManifest_process()) {
                PushApi pushApi = new PushApi();
                pushApi.setCompositeKey(commitPacketData.getManifest_process().get(i).getManifest_no() + "_" + commitPacketData.getManifest_process().get(i).getPickup_location_id());
                i++;
                pushApi.setManifestNo(manifestProcess.getManifest_no());
                pushApi.setAuthtoken(getDataManager().getAuthToken());
                pushApi.setEmpId(getDataManager().getCode());

                // pushApi.setRequestData(new ObjectMapper().writeValueAsString(commitPacketData));
                pushApi.setShipmentStatus(0);
                pushApi.setApiVer(version);
                pushApi.setAppId("1");
                pushApi.setApiVer("mobile");


                getCompositeDisposable().add(getDataManager().saveCommitPacket(pushApi).subscribeOn(getSchedulerProvider().io()).observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        //manifest_list SET manifest_failed
                        failedManifestQuery(Constants.COMMIT_FAILED, Constants.PICKED, manifestProcess.getManifest_no());
                        //commit_data SET firstInscanStatus=
                        //failedCommitPacket(1,Constants.PICKED,manifestProcess.getManifest_no());
                        // if (getDataManager().getParent()){
                        inScanCommitPacket(manifestProcess.getManifest_no());
                        //}
                        //manifest_list SET commit_status=
                        updateManifestList(manifestProcess.getManifest_no());//commitPacketData.getManifest_process().get(0).getManifest_no()
                        getNavigator().nextScreen(manifestProcess.getManifest_no() + "_" + manifestProcess.getPickup_location_id(), new ObjectMapper().writeValueAsString(commitPacketData));


                    }
                }));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateCommit_DataTable(long manifestno) {
        getDataManager().updateCommit_DataTable(manifestno);
    }

    public void updateManifestList(long manifestNo) {
        ArrayList<Long> ob = new ArrayList<Long>();
        ob.add(manifestNo);

        getDataManager().updateManifestList(String.valueOf(Constants.COMMIT_PICKED), ob);
    }


    boolean iscompleteDepart() {
        if (!getDataManager().is_Ecom_Vehicle())
            return getDataManager().getDepartSelfVehicle();
        List<LoginResponse.StartRouteDetails> startRouteDetails = getDataManager().getRouteDetail();
        boolean completedepart = true;
        for (LoginResponse.StartRouteDetails ff : startRouteDetails) {
            if (!ff.isDeparted())
                completedepart = false;

        }
        return completedepart;
    }

    int isLastDepart() {
        if (!getDataManager().is_Ecom_Vehicle())
            return 1;
        List<LoginResponse.StartRouteDetails> startRouteDetails = getDataManager().getRouteDetail();
        int completedepart = 0;
        for (LoginResponse.StartRouteDetails ff : startRouteDetails) {
            if (!ff.isDeparted())
                completedepart = completedepart + 1;

        }
        return completedepart;
    }

    void DepartTimeAPICall(ArrayList<String> vehicleid, String ownertype) {
        ThreadGeneric.executeCall(() -> {
            Depature_Request depature_request = new Depature_Request();
            depature_request.setVehicle_owner(ownertype);
            depature_request.setEmp_code(getDataManager().getCode());
            depature_request.setPickup_route_id(getDataManager().getRouteID());
            ArrayList<DepartVehicles> departVehiclesList = new ArrayList<>();
            for (String d : vehicleid) {
                DepartVehicles departVehicles = new DepartVehicles();
                departVehicles.setDep_timestamp(System.currentTimeMillis());
                departVehicles.setVehicle_number(d);
                departVehicles.setDep_timestamp(System.currentTimeMillis());
                departVehiclesList.add(departVehicles);

            }
            depature_request.setDepart_vehicles(departVehiclesList);
            LiveData<Departure_Response> departure_responseLiveData = getDataManager().departuredTimeUpdate(getDataManager().getAuthToken(), depature_request);
            try {
                detartureTime.addSource(departure_responseLiveData, departure_response -> {
                    detartureTime.removeSource(departure_responseLiveData);
                    if (departure_response != null && departure_response.isStatus()) {

                        if (getDataManager().is_Ecom_Vehicle()) {
                            List<LoginResponse.StartRouteDetails> ob = getDataManager().getRouteDetail();
                            for (LoginResponse.StartRouteDetails f : ob) {
                                for (String d : vehicleid) {
                                    if (f.getStart_vehicle_number().equals(d)) {
                                        f.setDeparted(true);
                                    }
                                }
                            }
                            getDataManager().setRouteDetail(new Gson().toJson(ob));
                        } else {
                            getDataManager().setDepartSelfVehicle(true);

                        }
                        detartureTime.setValue(departure_response);
                        getDataManager().setLogout(true);
                    } else {
                        Departure_Response departure_response1 = new Departure_Response();
                        departure_response1.setStatus(false);
                        detartureTime.setValue(departure_response1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void updateFileUrl(String fileUrl, String mani) {
        getDataManager().updateFileUrl(fileUrl, mani);
    }


    public void updateManifestSelfList(ArrayList<Long> ob) {
        getDataManager().updateManifestSelfList(String.valueOf(Constants.SELF_DROP), ob);
    }


}


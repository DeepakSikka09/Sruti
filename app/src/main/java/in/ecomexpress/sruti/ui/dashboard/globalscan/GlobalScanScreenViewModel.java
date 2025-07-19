package in.ecomexpress.sruti.ui.dashboard.globalscan;


import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.ecomexpress.sruti.model.RtoLockResponse;
import in.ecomexpress.sruti.model.RtoRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPRequest;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPResponse;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;

public class GlobalScanScreenViewModel extends BaseViewModel<IScanScreenNavigator> {
    private static final String TAG = GlobalScanScreenViewModel.class.getSimpleName();
    private MediatorLiveData<Shipment_Detail> mSectionLive = new MediatorLiveData<>();


    private MediatorLiveData<List<Shipment_Detail>> mediatorLiveData = new MediatorLiveData<>();
    private MutableLiveData<List<Shipment_Detail>> mutableLiveData = new MutableLiveData<>();
    private LiveData<List<Shipment_Detail>> dbLiveData;
    private LiveData<List<Shipment_Detail>> sdbLiveData;


    long manifestNum = 0;
    static long tempManifestNo=0;
    String  came_from="";


    public Manifest_List scannedManifest = new Manifest_List();

    public GlobalScanScreenViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onNext() {
        getNavigator().onNext();
    }

    public MutableLiveData<Shipment_Detail> getShiListLiveData() {
        return mSectionLive;
    }

    String vehicle;

    public void validateAWB(long awbNumber) {
        LiveData<Shipment_Detail> scannedAWBValid = getDataManager().isScannedAWBValid(awbNumber);
        mSectionLive.addSource(scannedAWBValid, new Observer<Shipment_Detail>() {
            @Override
            public void onChanged(@Nullable Shipment_Detail sectionList) {
                mSectionLive.removeSource(scannedAWBValid);
                if (sectionList == null) {
                    mSectionLive.setValue(sectionList);
                } else {
                    mSectionLive.removeSource(scannedAWBValid);
                    mSectionLive.setValue(sectionList);
                }
            }
        });
    }

    public void updateShipment(Long awb, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated) {
        getDataManager().updateScannedAWBStatus(awb, status, vehicle, date_time, isAdvance, reason_code,is_bp_validated,false);
        getManifestIdFromAwb(awb, "update",false);
    }


    public void updateShipmentTemp(Long awb, String status, String vehicle, long date_time, boolean isAdvance, String reason_code,int is_bp_validated,boolean tempKey) {
        tempManifestNo=awb;
        came_from="bp";

        getDataManager().updateScannedAWBStatusTemp(awb, status, vehicle, date_time, isAdvance, reason_code,is_bp_validated,false,tempKey);
        getManifestIdFromAwb(awb, "update",false);
    }
    public void getManifestIdFromAwb(long awb, String came_from,boolean bp_exist) {
        getCompositeDisposable().add(getDataManager()
                .getManifestIdFromAwb(awb).subscribeOn
                        (getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long value) {
//                        getNavigator().checkFirstScan(value);
                        if (came_from.equalsIgnoreCase("update")) {
                            isFirstScan(value, awb, came_from, bp_exist);
                        } else {
                            isFirstScan(value, awb, "",bp_exist);
                        }

                    }
                }));
    }


    public void isFirstScan(long manifestNumber, long awb, String came_from, boolean bp_exist) {
        getCompositeDisposable().add(getDataManager()
                .isFirstScan(manifestNumber).subscribeOn
                        (getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long isFirst) {
                        getCompositeDisposable().add(getDataManager().getSingleManifestDetail(manifestNumber).subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui())
                                .subscribe(new Consumer<Manifest_List>() {
                                    @Override
                                    public void accept(Manifest_List manifest_list) throws Exception {
                                        if (came_from.equalsIgnoreCase("update")) {
                                            scannedManifest = manifest_list;
                                            getNavigator().checkFirstScan(isFirst, manifestNumber, scannedManifest,came_from,awb,bp_exist);
                                        } else {
                                            getNavigator().checkFirstScan(isFirst, manifestNumber, manifest_list,came_from, awb,bp_exist);

                                        }

                                    }
                                }));
                    }
                }));
    }

// Here the new code changes for pending
    public LiveData<List<Shipment_Detail>> getAllAwbList() {

      return    getDataManager().getAllGlobalScanAWBlist(Constants.PICKED, 0);
    }
    public LiveData<List<Shipment_Detail>> getPendingAWb() {
        //  return getDataManager().getAllGlobalScanAWBlist(Constants.PICKED, 0);
        ArrayList<String> arr = new ArrayList<>();
        arr.add(Constants.PENDING);

        LiveData<List<Shipment_Detail>>scannedAWBValid = getDataManager().getAllGlobalScanAWBlistTemp(arr, 0, tempManifestNo);
        final MutableLiveData<Boolean> dataEmitted = new MutableLiveData<>(false);
        mediatorLiveData.addSource(scannedAWBValid, new Observer<List<Shipment_Detail>>() {
            @Override
            public void onChanged(List<Shipment_Detail> shipment_details) {
                mediatorLiveData.removeSource(scannedAWBValid);
                if (shipment_details == null) {
                    mediatorLiveData.setValue(shipment_details);
                } else {
                    mediatorLiveData.removeSource(scannedAWBValid);
                    mediatorLiveData.setValue(shipment_details);
                }
            }


        });
        return mediatorLiveData;
    }

    public LiveData<List<Shipment_Detail>> getAllAwbList1() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(Constants.PICKED);

        dbLiveData = getDataManager().getAllGlobalScanAWBlist(Constants.PICKED, 0);

        mediatorLiveData.addSource(dbLiveData, new Observer<List<Shipment_Detail>>() {
            @Override
            public void onChanged(List<Shipment_Detail> shipmentDetails) {
                if (!came_from.equalsIgnoreCase("bp")) {
                    came_from = "";
                    mediatorLiveData.setValue(shipmentDetails);
                }
            }
        });
        arr.add(Constants.PENDING);
        sdbLiveData=getDataManager().getAllGlobalScanAWBlistTemp(arr, 0, tempManifestNo);

        mediatorLiveData.addSource(sdbLiveData, new Observer<List<Shipment_Detail>>() {
            @Override
            public void onChanged(List<Shipment_Detail> shipmentDetails) {
                if (came_from.equalsIgnoreCase("bp")) {
                    mediatorLiveData.setValue(shipmentDetails);
                }
            }
        });

        return mediatorLiveData;

    }

    public void setManifestNumber(long manifestNo) {
        manifestNum = manifestNo;
    }


    public void updateInscanStatus() {
        getDataManager().updateInscanStatus(manifestNum);

    }

    public void insertFirstScanData(FirstInscan firstInscan) {
        getDataManager().insertFirstScanData(firstInscan);
    }

    public void updateIsScanStarted(long manifestno) {
        getDataManager().updateIsScanStarted(manifestno);

    }

    public void updateInscanToFirstScanTable(long manifestNumber) {
        getDataManager().updateInscanToFirstScanTable(manifestNumber);
    }

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }

    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return getDataManager().callFirstScanApi(authToken, firstScanResponse);
    }

    public void updateRtoShipment(long manifestNo, long awb, String shipment_status) {
        getDataManager().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
    }

    public void updateBPID(long manifestNo, long awb, String bp_id) {
        getDataManager().updateBPID(manifestNo, awb, bp_id);
    }
    public LiveData<RtoLockResponse> getAllRTOShipmentList(String authToken, RtoRequest manifest_no) {
        return getDataManager().getAllRTOShipmentList(authToken, manifest_no);
    }
    public LiveData<UpdatedBPResponse> getUpdatedBpAwbRto(String authToken, UpdatedBPRequest updatedBPRequest) {
        return getDataManager().getUpdatedBpAwbRto(authToken, updatedBPRequest);
    }

    public void insertShipment(ArrayList<Shipment_Detail> listOfShipment) {
        getDataManager().insertShipment(listOfShipment);
    }

/*
    public void insertShipment(ArrayList<Shipment_Detail> shipment_detail) {
        */
    /*getDataManager().insertShipment(shipment_detail);*//*


        try {
            getCompositeDisposable().add(getDataManager()
                    .insertShipmentUsingRx(shipment_detail).subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (aBoolean) {
                                getNavigator().notifyAdapter();
                                // insertOrUpdateDataStatus(dialogOnDataUpdate);
                            }
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().onErrorMessage(e.getMessage());
        }


    }
*/

    public LiveData<Manifest_List> getUpdatedTime(ArrayList<Long> manifestNo) {
        return getDataManager().getUpdatedTime(manifestNo);
    }

    public double getGeoFenceRadius() {
        return Double.parseDouble(getDataManager().get_pickup_geofencing_radius());
        // return 65;
    }

    public double getLastLatitudeFromPref() {
        return getDataManager().getCurrentLatitude();
    }

    public double getLastLongitudeFromPref() {
        return getDataManager().getCurrentLongitude();
    }


    public void ifBrandPackagingIDexists(long awb_no, String bp_id) {
        getDataManager().ifAWBbrandPackagingIDexists( awb_no, bp_id).subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<List<Shipment_Detail>>() {
                    @Override
                    public void accept(List<Shipment_Detail> shipment_details) throws Exception {

                        getNavigator().isBrandPackagingIDisValid(shipment_details, bp_id);

                    }
                });

    }
    public void getbp_id_from_awb_no(long awb_no) {
        try {
            getDataManager()
                    .getSpecificBpId(awb_no).subscribeOn
                            (getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String bp_id) {

                            getNavigator().getBP_ID(bp_id,awb_no);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<ReasonCodeList>> getPickupListToView(String reasonCode) {
        return getDataManager().getPickupList(reasonCode);
    }

    public void markUndelivered(long awb, int reason_id, String reason_code, String vehicleType) {
        getDataManager().UpdateAWBViaBpReasonID(awb, Constants.FAILED, vehicleType, Calendar.getInstance().getTimeInMillis(), reason_id, reason_code,true);

    }
    public  void  get_temp_key(long awbNo){
        try {
            getDataManager()
                    .getTempKey(awbNo).subscribeOn
                            (getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean tempValue) {
                            getNavigator().getTempKey(awbNo,tempValue);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

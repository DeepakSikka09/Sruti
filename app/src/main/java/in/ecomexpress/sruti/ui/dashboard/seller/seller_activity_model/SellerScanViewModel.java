package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.ScanRequest;
import in.ecomexpress.sruti.model.ScanResponse;
import in.ecomexpress.sruti.model.firstScan.FirstScanRequest;
import in.ecomexpress.sruti.model.firstScan.FirstScanResponse;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.remote.RestApiErrorHandler;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.IHandOverNavigator;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 23/8/19.
 */

@Module
public class SellerScanViewModel extends BaseViewModel<IHandOverNavigator> {
    private MediatorLiveData<List<Shipment_Detail>> scannedVendorStatus = new MediatorLiveData<>();
    private MediatorLiveData<List<Shipment_Detail>> mediatorLiveData = new MediatorLiveData<>();
    long manifestNumber = 0;
    static long tempManifestNo=0;
    public void setManifestNumber(long manifestNo) {
        manifestNumber = manifestNo;
    }

    public SellerScanViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public LiveData<List<Shipment_Detail>> getScannedVendorStatus() {
        return scannedVendorStatus;
    }

    public void onBack() {
        getNavigator().onNext();
    }


    public void setScannedVendorStatus(long manifestNumber, long awbNumber) {
        LiveData<List<Shipment_Detail>> dd = getDataManager().getScannedVendorStatus(manifestNumber, awbNumber);
        scannedVendorStatus.addSource(dd, new Observer<List<Shipment_Detail>>() {
            @Override
            public void onChanged(List<Shipment_Detail> shipment_details) {
                if (shipment_details == null) {

                } else {
                    scannedVendorStatus.removeSource(dd);
                    scannedVendorStatus.setValue(shipment_details);
                    System.out.println("data_value" + shipment_details.size());
                }
            }
        });
    }

    public void updateShipment(Long awb, String status, String vehicle, boolean isAdvance, String reason_code,int  is_bp_validated) {
        if (reason_code.isEmpty() && vehicle.isEmpty()) {
            Log.d("check_ss", "5");
            getNavigator().showValidationForNull();
        } else {
            Log.d("check_ss", "6");
            Long time = Calendar.getInstance().getTimeInMillis();

            getDataManager().updateScannedAWBStatus(awb, status, vehicle, time, isAdvance, reason_code,is_bp_validated,false);
            isFirstScan(manifestNumber);
        }
    }

    public void updateShipmentTemp(Long awb, String status, String vehicle, boolean isAdvance, String reason_code,int  is_bp_validated,boolean tempKey) {

        tempManifestNo=awb;
            Long time = Calendar.getInstance().getTimeInMillis();

            getDataManager().updateScannedAWBStatusTemp(awb, status, vehicle, time, isAdvance, reason_code,is_bp_validated,false,tempKey);


    }

    public LiveData<List<Shipment_Detail>> getAllAwbData(Long manifest_No) {
        ArrayList arr=new ArrayList<>();

        arr.add(Constants.PENDING);
        arr.add(Constants.PICKED);

           // return getDataManager().getAllScanAWBlistTemp(arr, manifest_No);
        return getDataManager().getAllScanAWBlist(Constants.PICKED, manifest_No);

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

    public void isFirstScan(long manifestNumber) {
      getDataManager()
                .isFirstScan(manifestNumber).subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long value) {
                        getNavigator().checkFirstScan(value);
                    }
                });
    }

    public void ifAWBexists(long manifestNumber, long awbNumber, String status) {
      getDataManager()
                .ifAWBexists(manifestNumber, awbNumber, status).subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<List<Shipment_Detail>>() {
                    @Override
                    public void accept(List<Shipment_Detail> shipment_details) throws Exception {
                        getNavigator().isAwbValid(shipment_details, awbNumber);
                    }
                });

    }


    public void insertAdvanceShipment(long manifestNumber, long awbNumber, String vehicle) {
        try {
            Shipment_Detail shipment_detail = new Shipment_Detail();
            shipment_detail.setAirwaybill_number(awbNumber);
            shipment_detail.setStatus(Constants.PICKED);
            shipment_detail.setVehicle(vehicle);
            shipment_detail.setDateTime(Calendar.getInstance().getTimeInMillis());
            shipment_detail.setReason_code("");
            shipment_detail.setManifestNoInchild(manifestNumber);
            shipment_detail.setAdvance(true);

          getDataManager()
                    .insertAdvanceShipmentNew(shipment_detail).subscribeOn
                            (getSchedulerProvider().io()).
                            observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void callScanApi(String lastText, String cust_code, long manifest_no, String vehicle, Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Data...");
        dialog.setIndeterminate(true);
        try {
            ScanRequest scanRequest = new ScanRequest();
            scanRequest.setAwb(Long.parseLong(lastText));
//            scanRequest.setCode(Long.parseLong(cust_code));
            scanRequest.setManifest_id(manifest_no);
          getDataManager()
                    .callScanApi(getDataManager().getAuthToken(), scanRequest)
                    .doOnSuccess(new Consumer<ScanResponse>() {
                        @Override
                        public void accept(ScanResponse scanResponse) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            Log.d(TAG, scanResponse.toString());
                        }

                    })
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<ScanResponse>() {
                        @Override
                        public void accept(ScanResponse response) {
                            Log.d(TAG, "login: " + response.toString());
                            SellerScanViewModel.this.setIsLoading(false);
                            if (dialog.isShowing())
                                dialog.dismiss();
                            //apk update functionality working fine already check dummy apk download and install
                            try {
                                if (response.isStatus()) {
                                    if (response.getResponse().getShipment_status().equals(Constants.RTO_STATUS_1) || response.getResponse().getShipment_status().equals(Constants.RTO_STATUS_2)) {
                                        updateRtoShipment(manifest_no, Long.parseLong(lastText), response.getResponse().getShipment_status());
                                        getNavigator().RtoLocked();
                                    } else {
                                        ifAWBexists(manifest_no, Long.parseLong(lastText), Constants.PENDING);
//                                        updateShipment(Long.valueOf(lastText), Constants.PICKED, vehicle, false);
                                    }

                                } else {
                                    getNavigator().realTimeCheckInvalid(lastText);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, throwable ->

                    {
                        setIsLoading(false);
                        String error;
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
//                            writeErrors(timeStamp, new Exception(throwable));
                            error = new RestApiErrorHandler(throwable).getErrorDetails().getEResponse().getDescription();
                            if (error.contains("HTTP 500 ")) {
                                getNavigator().showErrorMessage("Failed to connect,please check your connectivity or contact admin.");
                            } else {
                                //   getNavigator().showErrorMessage(error);
                            }
                        } catch (Exception e) {
                            //  getNavigator().showException(e);
                            e.printStackTrace();
                            if (dialog.isShowing())
                                dialog.dismiss();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            // getNavigator().showException(e);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void updateRtoShipment(Long manifestNo, Long awb, String shipment_status) {
        getDataManager().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
    }

    public void isAWBRtoLock(long manifestNumber, long awbNumber) {
      getDataManager()
                .isAWBRtoLock(manifestNumber, awbNumber).subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {

                        getNavigator().isAWBRtoLock(aBoolean, awbNumber);
                    }
                });
    }

    public void getShipmentStatus(long awbNumber, Long manifestNo) {
        try {
          getDataManager()
                    .getShipmentStatus(manifestNo, awbNumber).subscribeOn
                            (getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
//                    .switchIfEmpty (Log.d(TAG, "IS EMPTY")
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String manifest_list) {
                            if (manifest_list != null) {
                                getNavigator().getShipmentStatus(manifest_list, awbNumber);
                            } else {

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getShipmentExist(long awbNumber, Long manifestNo) {
        try {
          getDataManager()
                    .getShipmentExist(manifestNo, awbNumber).subscribeOn
                            (getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            System.out.println("aBoolean " + aBoolean);

                            getNavigator().getShipmentExist(aBoolean, awbNumber);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateIsScanStarted(long manifestno) {
        getDataManager().updateIsScanStarted(manifestno);
    }

    public void ifAWBPresent(long manifestNumber, long awb, String status) {
      getDataManager()
                .ifAWBPresentNew(manifestNumber, awb, status).subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        System.out.println("aBoolean " + aBoolean);

                        getNavigator().ifAWBPresent(aBoolean, awb);
                    }
                });

    }

    public LiveData<FirstScanResponse> callFirstScanApi(String authToken, FirstScanRequest firstScanResponse) {
        return getDataManager().callFirstScanApi(authToken, firstScanResponse);
    }

    public void updateInscanStatus() {
        getDataManager().updateInscanStatus(manifestNumber);

    }


    public void insertFirstScanData(FirstInscan firstInscan) {
        getDataManager().insertFirstScanData(firstInscan);
    }

    public void updateInscanToFirstScanTable(long manifestNumber) {
        getDataManager().updateInscanToFirstScanTable(manifestNumber);
    }

    public void inScanCommitPacket(long manifestno) {
        getDataManager().inScanCommitPacket(1, manifestno);
    }

    public void isAlreadyScanned(Long manifestNo, Long awbNumber) {
      getDataManager()
                .isAlreadyScanned(manifestNo, awbNumber).subscribeOn
                        (getSchedulerProvider().io()).
                        observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        getNavigator().isAlreadyScanned(aBoolean, awbNumber);
                    }
                });
    }

    public double getGeoFenceRadius() {
        return Double.parseDouble(getDataManager().get_pickup_geofencing_radius());
        // return 65;
    }

    public double getLastLatitudeFromPref(){
        return getDataManager().getCurrentLatitude();
    }

    public double getLastLongitudeFromPref(){
        return getDataManager().getCurrentLongitude();
    }
    public void ifBrandPackagingIDexists(long manifestNumber, long awb_no, String bp_id) {
        getDataManager().ifBrandPackagingIDexists(manifestNumber, awb_no, bp_id).subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).subscribe(new Consumer<List<Shipment_Detail>>() {
                    @Override
                    public void accept(List<Shipment_Detail> shipment_details) throws Exception {

                        getNavigator().isBrandPackagingIDisValid(shipment_details, bp_id);

                    }
                });

    }
    public void markUndelivered(long awb, int reason_id, String reason_code, String vehicleType) {
        getDataManager().UpdateAWBViaBpReasonID(awb, Constants.FAILED, vehicleType, Calendar.getInstance().getTimeInMillis(), reason_id, reason_code,true);

    }

//    public void getAwbAssociatedBP_ID(String bp_id, long awb_no, boolean tempid) {
//        try {
//            getDataManager()
//                    .getSpecificBpId(awb_no).subscribeOn
//                            (getSchedulerProvider().io()).
//                    observeOn(getSchedulerProvider().ui())
//                    .subscribe(new Consumer<String>() {
//                        @Override
//                        public void accept(String bp_id) {
//                            getNavigator().getAwbAssociatedBP_ID(bp_id, awb_no, tempid);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

    public  void  get_temp_key(long awbNo,String bpId){
        try {
            getDataManager()
                    .getTempKey(awbNo).subscribeOn
                            (getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean tempValue) {
                            getNavigator().getTempKey(bpId,awbNo,tempValue);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<ReasonCodeList>> getPickupListToView(String reasonCode) {
        return getDataManager().getPickupList(reasonCode);
    }
}
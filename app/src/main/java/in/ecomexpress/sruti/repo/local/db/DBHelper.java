package in.ecomexpress.sruti.repo.local.db;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sruti.model.Departure.CountManifest;
import in.ecomexpress.sruti.model.commitdata.ImageModel;
import in.ecomexpress.sruti.model.commitdata.PushApi;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.model.inScanData.FirstInscan;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.model.masterdata.ReasonCodeList;
import in.ecomexpress.sruti.model.menifestdata.Flags;
import in.ecomexpress.sruti.model.menifestdata.ManifestAndShipment;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.model.popData.PopData;
import in.ecomexpress.sruti.repo.local.db.model.Remark;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.utils.common_files.Constants;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@Singleton
public class DBHelper implements IDBHelper {
    private final SrutiDatabase mAppDatabase;

    @Inject
    public DBHelper(SrutiDatabase mAppDatabase) {
        this.mAppDatabase = mAppDatabase;
    }

    @Override
    public Observable<Boolean> deleteAllTables() {
        return null;
    }

    // LiveData
    @Override
    public void insertToDoList(List<ReasonCodeList> reasonCodeLists, List<in.ecomexpress.sruti.model.masterdata.Post_option> post_options) {
        try {
            new Thread(() -> {
                try {
                    mAppDatabase.doTOdoList().insertTodoList(reasonCodeLists, post_options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    @Override
    public LiveData<List<ReasonCodeList>> getPickupList(String differenceState) {
        if (differenceState.equalsIgnoreCase(Constants.OTP_WISE_REASON_CODE)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getPickupListForOtp(true));
        }
        if (differenceState.equalsIgnoreCase(Constants.QR_WISE_REASON_CODE)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getPickupListForQr(true));
        }
        if (differenceState.equalsIgnoreCase(Constants.MANIFEST_WISE_REASON_CODE)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getPickupListManifest(true));
        }
        if (differenceState.equalsIgnoreCase(Constants.SHIPMENT_WISE_REASON_CODE)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getPickupListShipment(true));
        }
        if (differenceState.equalsIgnoreCase(Constants.ALL_REASON_CODE)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getPickupList(true));
        }
        if (differenceState.equalsIgnoreCase(Constants.BRANDED_PACKAGE_ID_MISMATCH_INCORRECT)) {
            return ThreadGeneric.runThread(mAppDatabase.doTOdoList().getBpReasonCodeList(true));
        }


        return null;
    }


    @Override
    public void insertManifestShipmentDetail(List<Manifest_List> manifest_lists, List<Shipment_Detail> response) {
        try {
            ThreadGeneric.executeCall(() -> {
                mAppDatabase.callManifestDao().insertShipmentDetail(manifest_lists, response);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LiveData<List<Shipment_Detail>> getShipmentDetail() {
        return mAppDatabase.callManifestDao().getShipmentDetail();
    }

    @Override
    public LiveData<List<Manifest_List>> getManifestDetail() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getManifestDetailList());
    }

    /*@Override
    public LiveData<List<PopData>> getPopData() {
        return null;
    }*/

  /*  @Override
    public LiveData<List<PopData>> getPopData() {
        return ThreadGeneric.runThread(mAppDatabase.popData().getPopList());
    }*/



    @Override
    public LiveData<Manifest_List> getUpdatedTime(ArrayList<Long> manifestNo) {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getUpdatedTime(manifestNo));
    }

    @Override
    public LiveData<List<Manifest_List>> getManifestDetailStatus() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getManifestDetail(Constants.COMMIT_SERVER_SYNC));
    }

    @Override
    public void updateScannedAWBStatus(Long awb, String status, String vehicle, long currentdate, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateScannedAWBStatus(awb, status, vehicle, currentdate, isAdvance, reason_code, is_bp_validated, reason_code_applied);
        }).start();
    }

    @Override
    public void updateScannedAWBStatusTemp(Long awb, String status, String vehicle, long currentdate, boolean isAdvance, String reason_code,int is_bp_validated,boolean reason_code_applied,boolean tempKey) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateScannedAWBStatusTemp(awb, status, vehicle, currentdate, isAdvance, reason_code, is_bp_validated, reason_code_applied,tempKey);
        }).start();
    }



    @Override
    public void updateOtpShipment(String status, String vehicle, String date_time, boolean isAdvance, boolean checked, long manifestNo) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateOtpShipment(status, vehicle, date_time, isAdvance, checked, manifestNo);
        }).start();

    }

    @Override
    public LiveData<Shipment_Detail> isScannedAWBValid(long awb) {
        return mAppDatabase.awbNumberDao().validateAwbNumber(awb);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getAllScanAWBlist(String Status, Long manifest_No) {
        return mAppDatabase.callManifestDao().getAllAWBList(Status, manifest_No);

    }
    @Override
    public LiveData<List<Shipment_Detail>> getAllScanAWBlistTemp(List<String> Status, Long manifest_No) {
        return mAppDatabase.callManifestDao().getAllAWBListTemp(Status, manifest_No);

    }


    @Override
    public LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlist(String status, int commit_status) {
        return mAppDatabase.callManifestDao().getAllGlobalScanAWBlist(status, commit_status);
    }
    @Override
    public LiveData<List<Shipment_Detail>> getAllGlobalScanAWBlistTemp(List<String> status, int commit_status,long  tempManifestNo) {
        return mAppDatabase.callManifestDao().getAllGlobalScanAWBlistTemp( status, commit_status,tempManifestNo);
    }


    @Override
    public LiveData<List<Shipment_Detail>> getTempGlobalScanAWBlistTemp(String status, int commit_status,long  tempManifestNo) {
        return mAppDatabase.callManifestDao().getTempGlobalScanAWBlistTemp( status,commit_status,tempManifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllShipmentlist(long manifest) {
        return mAppDatabase.callManifestDao().getAllShipmentList(manifest);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getMpsCondition(long manifestNo) {
        return mAppDatabase.callManifestDao().getMpsCondition(manifestNo, true);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getMpsConditionUsingGlobalScan(ArrayList<Long> manifestCollection) {
        return mAppDatabase.callManifestDao().getMpsConditionUsingGlobalScan(manifestCollection, true);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllParentShipmentlist(long manifest_no) {
        return mAppDatabase.callManifestDao().getAllParentShipmentlist(manifest_no);
    }

    @Override
    public LiveData<List<Manifest_List>> getSpecificManifestDetail(ArrayList<Long> manifestNo) {
        return mAppDatabase.callManifestDao().getSpecificManifestDetail(manifestNo);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getAllScannedShipmentList(long manifest_No, List<String> status) {
        return mAppDatabase.callManifestDao().getAllScannedShipmentList(manifest_No, status);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getCommitAPICall(String vehcileId) {
        return mAppDatabase.callManifestDao().getCommitAPICall(vehcileId);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getScannedShipmentList(String picked) {
        return mAppDatabase.callManifestDao().getScannedShipmentList(picked);
    }

    @Override
    public LiveData<List<Shipment_Detail>> getTotalShipmentList(List<Long> manifest_no) {
        return mAppDatabase.callManifestDao().getTotalShipmentList(manifest_no);
    }


    @Override
    public LiveData<Integer> getScannedShipmentCount(String status, long manifestNo) {
        return mAppDatabase.callManifestDao().getScannedShipmentCount(status, manifestNo);
    }

    @Override
    public LiveData<Integer> getUnSyncManifestList() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getUnSyncManifestList(1));
    }


    @Override
    public Flowable<List<Manifest_List>> getLastManifestdata(String status) {
        return Flowable.fromCallable(new Callable<List<Manifest_List>>() {
            @Override
            public List<Manifest_List> call() throws Exception {
                return mAppDatabase.callManifestDao().getLastManifestdata(status);
            }
        });

    }

    @Override
    public void failedCommitPacket(int commit_status, String commit_failed, long manifestno) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().failedCommitPacket(commit_status, commit_failed, manifestno);
        });
    }

    @Override
    public void inScanCommitPacket(int commit_status, long manifestno) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().inScanCommitPacket(commit_status, manifestno);
        });
    }

    @Override
    public void updateCommit_DataTable(long manifestno) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().updateCommit_DataTable(manifestno);
        });
    }

    @Override
    public void inScanCommitRecciPacket(int scan_status, String comkey) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().inScanCommitRecciPacket(scan_status, comkey);
        });
    }

    @Override
    public void updateIsScanStarted(long manifestNumber) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().updateIsScanStarted(manifestNumber);
        });
    }

    @Override
    public void assignUpdateIsScanStarted(long manifestNumber) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().assignUpdateIsScanStarted(manifestNumber);
        });
    }

    @Override
    public LiveData<Integer> getStartedManifestCount() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getStartedManifestCount());
    }

    @Override
    public LiveData<List<PushApi>> UnSyncCommitListAtLogin(int status) {
        return mAppDatabase.pushApiDAO().UnSyncCommitListAtLogin(0);
    }

    @Override
    public void deleteCommitedShipment(ArrayList<Long> manifestIds) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().deleteCommitedShipment(manifestIds);
        });
    }

    @Override
    public void updateManifestListWithRecci(String valueOf, String s) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().updateManifestListWithRecci(valueOf, s);
        });
    }

    @Override
    public void updateCommitStatusWithRecci(String s, String s1) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.pushApiDAO().updateCommitStatusWithRecci(s, s1);
        });
    }

    @Override
    public void updateFirstInScan(int i, long i1) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.firstInscanDao().updateFirstInScan(i, i1);
        });

    }


    @Override
    public LiveData<List<Shipment_Detail>> fetchToDoFilterData(String searchdata) {
        return mAppDatabase.doTOdoList().fetchToDoFilterData(searchdata);
    }

    @Override
    public LiveData<List<Shipment_Detail>> fetchManifestData(List<Long> manifestNumber) {
        return mAppDatabase.callManifestDao().fetchManifestData(manifestNumber);
    }

    @Override
    public void insertAwbNumberList(HandOverShipmentList handOverShipmentList) {
        new Thread(() -> {
            mAppDatabase.awbNumberDao().insertAwbNumber(handOverShipmentList);
        }).start();

    }

    @Override
    public LiveData<Integer> deleteAwbData(int awbNumber) {
        MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
        try {
            System.out.println("getLoc() ");
            Demo demo = new Demo(awbNumber);
            demo.start();
            demo.join();
            System.out.println("getLoc() " + demo.getLoc());
            mutableLiveData.setValue(demo.getLoc());
            System.out.println("getLoc() " + demo.getLoc());
            return mutableLiveData;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mutableLiveData.setValue(-1);
        return mutableLiveData;

    }

    /**
     * @return Validate AWB Number
     */
    @Override
    public LiveData<HandOverShipmentList> isValidateAwb(long awbNumber) {
        LiveData<HandOverShipmentList> ob = mAppDatabase.awbNumberDao().validateHandOverNumber(awbNumber);
        return ob;
    }

    @Override
    public LiveData<List<HandOverShipmentList>> getAllAwbData() {
        LiveData<List<HandOverShipmentList>> ob = mAppDatabase.awbNumberDao().getAllAwbData();
        return ob;
    }

    @Override
    public LiveData<List<Shipment_Detail>> getScannedVendorStatus(long manifestNumber, long awbNumber) {
        LiveData<List<Shipment_Detail>> ob = mAppDatabase.callManifestDao().getScannedVendorStatus(manifestNumber, awbNumber);
        return ob;
    }

    @Override
    public LiveData<Flags> getFlagDetail() {
//        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getFlagDetail());
        return null;
    }

    @Override
    public void setRecciQuestion(List<General_Question> recciQuestion) {
        try {
            ThreadGeneric.executeCall(() -> {
                mAppDatabase.doTOdoList().insertRecciQuestion(recciQuestion);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public LiveData<List<General_Question>> getRecciQuestion(List<Integer> ids) {
        return mAppDatabase.doTOdoList().fetchQuestionList(ids);
    }

    @Override
    public Observable<Boolean> insertAdvanceShipmentNew(Shipment_Detail shipment_detail) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.callManifestDao().insertAdvanceShipment(shipment_detail);
                return true;
            }
        });


    }

    @Override
    public void saveImage(ImageModel imageModel) {
        new Thread(() -> {
            mAppDatabase.imageDAO().insertIMG(imageModel);
        }).start();

    }

    @Override
    public void updateRtoShipmentStatus(long manifestNo, long awb, String shipment_status) {
        /*new Thread(() -> {
            mAppDatabase.callManifestDao().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
        }).start();*/

        try {
            ThreadGeneric.executeCall(() -> {
                mAppDatabase.callManifestDao().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void updateBPID(long manifestNo, long awb, String BP_ID) {
       /* new Thread(() -> {
            mAppDatabase.callManifestDao().updateRtoShipmentStatus(manifestNo, awb, shipment_status);
        }).start();
*/
        try {
            ThreadGeneric.executeCall(() -> {
                mAppDatabase.callManifestDao().updateBPID(manifestNo, awb, BP_ID);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void updateSharedManifestStatus(List<Long> manifestNo, String mobileNoType, Boolean shipment_status) {
        try {
            ThreadGeneric.executeCall(() -> {
                mAppDatabase.callManifestDao().updateSharedManifestStatus(manifestNo, mobileNoType, shipment_status);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void insertShipment(List<Shipment_Detail> listOfShipment) {
        try {
            new Thread(() -> {
                mAppDatabase.callManifestDao().insertShipmentUsingRx(listOfShipment);
            }).start();

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public Observable<Boolean> insertShipmentUsingRx(List<Shipment_Detail> listOfShipment) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.callManifestDao().insertShipmentUsingRx(listOfShipment);
                return true;
            }
        });

    }

    @Override
    public void updateCommitStatus(String shipmentStatus, String manifestNo) {
        new Thread(() -> {
//            mAppDatabase.pushApiDAO().updateCommitStatus(shipmentStatus, manifestNo);
        }).start();

    }

    @Override
    public void updateManifestList(String commitStatus, ArrayList<Long> manifestNo) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateManifestList(commitStatus, manifestNo);
        }).start();

    }

    @Override
    public void updatePopStatus(String commitStatus, ArrayList<Long> manifestNo) {
        new Thread(() -> {
            mAppDatabase.popData().UpdatePopStatus(commitStatus, manifestNo);
        }).start();

    }

    @Override
    public void updateManifestSelfList(String commitStatus, ArrayList<Long> manifestNo) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateManifestSelfList(commitStatus, manifestNo);
        }).start();
    }

    @Override
    public void updateFileUrl(String fileUrl, String mani) {
        new Thread(() -> {
            mAppDatabase.pushApiDAO().updateFileUrl(fileUrl, mani);
        }).start();

    }

    @Override
    public void updateRecciList(String setShipmentStatus, String compositeKey) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateRecciList(setShipmentStatus, compositeKey);
        }).start();
    }

    @Override
    public void updateShipmentCount(long manifestNo, String shipmentCount) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateShipmentCount(manifestNo, shipmentCount);
        }).start();
    }

    @Override
    public LiveData<List<ImageModel>> getImageStatus(String manifestNo) {
        return ThreadGeneric.runThread(mAppDatabase.imageDAO().getImageStatus(manifestNo));
    }

    @Override
    public void failedManifestQuery(int commit_status, String commit_failed, long manifestno) {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.callManifestDao().failedManifestQuery(commit_status, commit_failed, manifestno);
        });
    }

    @Override
    public LiveData<CountManifest> checkAllManifestStatus() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().checkAllManifestStatus(0));
    }

    @Override
    public void deleteData() {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.doTOdoList().deleteApiUrlDataTable();
            mAppDatabase.doTOdoList().deletecallbridge_pstnsTable();
            //   mAppDatabase.doTOdoList().deleteFirstInscanTable();
            mAppDatabase.doTOdoList().deleteglobal_scan_awbTable();
            mAppDatabase.doTOdoList().deletehandOver_shipment_listTable();
            mAppDatabase.doTOdoList().deletemanifest_shipment_detailTable();
            mAppDatabase.doTOdoList().deletereason_code_listTable();
            mAppDatabase.doTOdoList().deleterecci_question_listTable();
            mAppDatabase.doTOdoList().deleteManifestTable();
        });
    }


    @Override
    public void deleteDataAtLogout() {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.doTOdoList().deleteApiUrlDataTable();
            mAppDatabase.doTOdoList().deletecallbridge_pstnsTable();
            mAppDatabase.doTOdoList().deleteFirstInscanTable();
            mAppDatabase.doTOdoList().deleteglobal_scan_awbTable();
            mAppDatabase.doTOdoList().deletehandOver_shipment_listTable();
            mAppDatabase.doTOdoList().deletemanifest_shipment_detailTable();
            mAppDatabase.doTOdoList().deletereason_code_listTable();
            mAppDatabase.doTOdoList().deleterecci_question_listTable();
            mAppDatabase.doTOdoList().deleteManifestTableForcefully();
            mAppDatabase.doTOdoList().deletePushTable();
            mAppDatabase.doTOdoList().deletePopData();
        });
    }

    @Override
    public void deleteForcefully() {
        ThreadGeneric.executeCall(() -> {
            mAppDatabase.doTOdoList().deletePushTable();
            mAppDatabase.doTOdoList().deletePopData();
            mAppDatabase.doTOdoList().deleteManifestTableForcefully();
        });
    }

    @Override
    public LiveData<List<Manifest_List>> getUnPickedManifestList() {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().getUnPickedManifestList(0));
    }


    @Override
    public void updateInscanStatus(long manifestNumber) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().updateInscanStatus(manifestNumber);
        }).start();
    }

    @Override
    public void insertFirstScanData(FirstInscan firstInscan) {
        new Thread(() -> {
            mAppDatabase.firstInscanDao().insertFirstScanShipment(firstInscan);
        }).start();
    }

    @Override
    public void updateInscanToFirstScanTable(long manifestNumber) {
        new Thread(() -> {
            mAppDatabase.firstInscanDao().updateInscanToFirstScanTable(manifestNumber);
        }).start();

    }

    @Override  // un-used
    public LiveData<List<ManifestAndShipment>> getManifestAndShipment(String searchdata) {
        return null;//mAppDatabase.doTOdoList().getManifestAndShipment(searchdata);
    }

    @Override  // un-used
    public LiveData<List<Shipment_Detail>> getScannedManifestList(String status) {
        return null;
    }

    @Override // un-used
    public LiveData<List<Manifest_List>> getAllManifestShipments(long manifestids) {
        return null;
    }

    @Override
    public Observable<List<Post_option>> getCbPstnOptions() {
        return Observable.fromCallable(new Callable<List<Post_option>>() {
            @Override
            public List<Post_option> call() {
                return mAppDatabase.callBridgePSTNDao().getAllCbPstnoptions();
            }
        });
    }

    @Override
    public Observable<List<Shipment_Detail>> ifAWBexists(long manifestNumber, long awbNo, String status) {

        return Observable.fromCallable(new Callable<List<Shipment_Detail>>() {
            @Override
            public List<Shipment_Detail> call() throws Exception {
                return mAppDatabase.callManifestDao().isValidAWB(manifestNumber, awbNo, status);
            }
        });
    }


    @Override
    public LiveData<Shipment_Detail> ifAWBPresent(long manifestNumber, long awbNo, String status) {
        return ThreadGeneric.runThread(mAppDatabase.callManifestDao().isAWBPresent(manifestNumber, awbNo));
       /*
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override;
            public Boolean call() throws Exception {
                return mAppDatabase.callManifestDao().isAWBPresent(manifestNumber, awbNo);
            }
        });*/
    }

    @Override
    public Observable<Boolean> ifAWBPresentNew(long manifestNumber, long awbNo, String status) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mAppDatabase.callManifestDao().ifAWBPresentNew(manifestNumber, awbNo);
            }
        });
    }

    @Override
    public Observable<Long> isFirstScan(long manifestNumber) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().isFirstScan(manifestNumber);
            }
        });
    }

    @Override
    public Observable<Manifest_List> getSingleManifestDetail(long manifestNumber) {
        return Observable.fromCallable(new Callable<Manifest_List>() {
            @Override
            public Manifest_List call() throws Exception {
                return mAppDatabase.callManifestDao().getSingleManifestDetail(manifestNumber);
            }
        });
    }

    @Override
    public Observable<Long> getShipmentCount(String status, long manifestNo) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.callManifestDao().getShipmentCount(status, manifestNo);

            }
        });
    }

    @Override
    public Observable<Long> getGlobalCount(String status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.callManifestDao().getGlobalCount(status);

            }
        });
    }

    @Override
    public Observable<Long> getTotalCount(long manifestNo) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.callManifestDao().getTotalCount(manifestNo);

            }
        });
    }

    @Override
    public Observable<Long> getAdvanceCount(boolean b, long manifest_no) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.callManifestDao().getAdvanceCount(b, manifest_no);

            }
        });
    }

    @Override
    public Observable<Boolean> insertRemark(Remark remark) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.daoRemarks().insert(remark);
                return true;
            }
        });
    }

    @Override
    public Observable<String> getShipmentStatus(long manifestNumber, long awbNumber) {
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    return mAppDatabase.callManifestDao().getShipmentStatus(manifestNumber, awbNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        });
    }

    @Override
    public Observable<Boolean> getShipmentExist(long manifestNumber, long awbNumber) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    return mAppDatabase.callManifestDao().getShipmentExist(manifestNumber, awbNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        });
    }

    @Override
    public Observable<Boolean> isAlreadyScanned(Long manifestNo, Long awbNumber) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
//                   return mAppDatabase.callManifestDao().isAlreadyScanned(manifestNo,awbNumber);
                    return mAppDatabase.callManifestDao().isAlreadyScanned(awbNumber);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    @Override
    public Observable<Long> getVendorStatusCount(int status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getVendorStatusCount(status);
            }
        });

    }

    @Override
    public Observable<Long> getVendorStatusPickedSyncedCount(int picked, int synced) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getVendorStatusPickedSyncedCount(picked, synced);
            }
        });
    }

    @Override
    public Observable<Long> getWarehouseStatusPickedSyncedCount(int picked, int synced) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getWarehouseStatusPickedSyncedCount(picked, synced);
            }
        });
    }

    @Override
    public Observable<Long> getRecciStatusPickedSyncedCount(int picked, int synced) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciStatusPickedSyncedCount(picked, synced);
            }
        });
    }

    @Override
    public Observable<Long> getRecciVenStatusPickedSyncedCount(int picked, int synced) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciVenStatusPickedSyncedCount(picked, synced);
            }
        });
    }

    @Override
    public Observable<Long> getRecciWrhStatusPickedSyncedCount(int picked, int synced) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciWrhStatusPickedSyncedCount(picked, synced);
            }
        });
    }

    @Override
    public Observable<Long> getWarehouseStatusCount(int status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getWarehouseStatusCount(status);
            }
        });

    }

    @Override
    public Observable<Long> getRecciStatusCount(int status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciStatusCount(status);
            }
        });
    }

    @Override
    public Observable<Long> getRecciVenStatusCount(int status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciVenStatusCount(status);
            }
        });
    }

    @Override
    public Observable<Long> getRecciWrhStatusCount(int status) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getRecciWrhStatusCount(status);
            }
        });
    }


    @Override
    public Observable<Boolean> markUndelivered(Shipment_Detail... shipmentDetails) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.callManifestDao().insertAllShipment(shipmentDetails);
                return true;
            }
        });
    }

    @Override
    public Observable<Boolean> deleteAdvanceShipment(long manifestNoInchild, long awb) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.callManifestDao().deleteAdvanceShipment(manifestNoInchild, awb);
                return true;
            }
        });
    }


    @Override
    public Observable<Boolean> isAWBRtoLock(long manifestNumber, long awbNumber) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return mAppDatabase.callManifestDao().isAWBRtoLock(manifestNumber, awbNumber);
            }
        });
    }

    @Override
    public Observable<Boolean> updatechildCommitShipment(Long awb, String shipment_status, String vehicle, long manifest_no, boolean isChild) {

        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.callManifestDao().updateShipmentStatus(manifest_no, awb, vehicle, shipment_status, isChild);
                return true;
            }
        });

    }

    //for global scan
    @Override
    public Observable<Long> getManifestIdFromAwb(long awb) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                return mAppDatabase.callManifestDao().getManifestIdFromAwb(awb);
            }
        });
    }

    @Override
    public Observable<Boolean> saveCommitPacket(PushApi pushApi) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.pushApiDAO().insert(pushApi);
                return true;
            }
        });

    }


    @Override
    public Observable<Boolean> pushPopData(PopData popData) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
               mAppDatabase.popData().insert(popData);
                return true;
            }
        });

    }

    @Override
    public Observable<List<PushApi>> getUnSyncCommitManifest(long manifest_no, int shipmentStatus) {
        return Observable.fromCallable(new Callable<List<PushApi>>() {
            @Override
            public List<PushApi> call() throws Exception {
                return mAppDatabase.pushApiDAO().getUnSyncCommitManifest(manifest_no, shipmentStatus);
            }
        });
    }

    @Override
    public Observable<List<PopData>> getPopData() {
        return Observable.fromCallable(new Callable<List<PopData>>() {
            @Override
            public List<PopData> call() throws Exception {
                return mAppDatabase.popData().getPopList();
            }
        });
    }

    class Demo extends Thread {
        int loc;

        Demo(int loc) {
            this.loc = loc;
        }

        @Override
        public void run() {
            super.run();
            loc = mAppDatabase.awbNumberDao().delete(loc);
        }

        int getLoc() {
            return loc;
        }
    }


    @Override
    public Observable<List<Shipment_Detail>> ifBrandPackagingIDexists(long manifest_no, long awbNo, String Bp_id) {

        return Observable.fromCallable(new Callable<List<Shipment_Detail>>() {
            @Override
            public List<Shipment_Detail> call() throws Exception {
                return mAppDatabase.callManifestDao().BrandPackagingIDvalid(manifest_no, awbNo, Bp_id);
            }
        });
    }

    @Override
    public Observable<List<Shipment_Detail>> ifAWBbrandPackagingIDexists(long awbNo, String Bp_id) {

        return Observable.fromCallable(new Callable<List<Shipment_Detail>>() {
            @Override
            public List<Shipment_Detail> call() throws Exception {
                return mAppDatabase.awbNumberDao().BrandPackagingIDvalidForAWB( awbNo, Bp_id);
            }
        });
    }


    @Override
    public void UpdateAWBViaBpReasonID(Long awb, String status, String vehicle, long currentdate, int reason_id, String reason_code,boolean reason_code_applied) {
        new Thread(() -> {
            mAppDatabase.callManifestDao().UpdateAWBViaBpReasonID(awb, status, vehicle, currentdate, reason_id, reason_code, reason_code_applied);
        }).start();
    }


    @Override
    public Observable<String> getSpecificBpId(long awb) {
        return Observable.fromCallable(new Callable<String>() {

            @Override
            public String call() throws Exception {
                try {
                    return mAppDatabase.callManifestDao().getSpecificBpId(awb);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

        });
    }


    @Override
    public Observable<Boolean> getTempKey(long awb) {
        return Observable.fromCallable(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {
                    return mAppDatabase.callManifestDao().getTempKey(awb);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            }

        });
    }







}

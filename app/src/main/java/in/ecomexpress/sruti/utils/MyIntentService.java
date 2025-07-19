package in.ecomexpress.sruti.utils;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import in.ecomexpress.sruti.model.UpdatedBP.UpdatedBPResponse;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.MutableStateFlow;

public class MyIntentService extends IntentService {

    //MutableStateFlow<ProgreasBarState> progreasBarStateMutableStateFlow = MutableStateFlow<>(ProgreasBarState.EMPTY);

//    private final MutableLiveData<Boolean> loadingStateLiveData = new MutableLiveData<>();

    private ArrayList<Shipment_Detail> rtoShipmentResponses;
    String cameFrom="";
    @Inject
    SrutiDatabase mAppDatabase;
   /* private final IDataManager mDataManager;
    public IDataManager getDataManager() {
        return mDataManager;
    }*/

    /*  public MyIntentService(IDataManager mDataManager) {
          super("MyIntentService");
          this.mDataManager = mDataManager;
      }*/
    public MyIntentService() {
        super("MyIntentService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        // Initialize your ViewModel here
        /*  sellerViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(SellerViewModel.class);
         */
    }
//    public LiveData<Boolean> getLoadingStateLiveData() {
//        return loadingStateLiveData;
//    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // Get the data from the intent, if needed
            // For example:
            // String data = intent.getStringExtra("key");

            //rtoShipmentResponses=new ArrayList<>();
            //   List<Shipment_Detail> response = (List<Shipment_Detail>) intent.getSerializableExtra("extra_data");

            // Start a transaction

            // progreasBarStateMutableStateFlow.emit(ProgreasBarState.LOADING)
        Intent startIntent=new Intent(Constants.ACTION_START_WORK);
        sendBroadcast(startIntent);
            cameFrom = intent.getStringExtra("come_from");
            if (cameFrom.equalsIgnoreCase("global")) {
                UpdatedBPResponse responseData = (UpdatedBPResponse) intent.getSerializableExtra("extra_data");
                for (int i = 0; i < responseData.getResponse().size(); i++) {
                    for (UpdatedBPResponse.bp_shipment_details listofUpdate : responseData.getResponse().get(i).getBpShipmentDetails()) {
                        mAppDatabase.callManifestDao().updateBPID(responseData.getResponse().get(i).getManifest_id(), listofUpdate.getAirwaybill_number(), listofUpdate.getBrand_package_id());
                    }
                }
                Intent stopIntent = new Intent(Constants.ACTION_STOP_WORK);
                sendBroadcast(stopIntent);

            } else {
                UpdatedBPResponse.ResponseData responseData = (UpdatedBPResponse.ResponseData) intent.getSerializableExtra("extra_data");
                for (UpdatedBPResponse.bp_shipment_details listofUpdate : responseData.getBpShipmentDetails()) {
                    mAppDatabase.callManifestDao().updateBPID(responseData.getManifest_id(), listofUpdate.getAirwaybill_number(), listofUpdate.getBrand_package_id());

                }
                Intent stopIntent = new Intent(Constants.ACTION_STOP_WORK);
                sendBroadcast(stopIntent);
            }

            //            loadingStateLiveData.postValue(true);

          /*  Manifest_List manifest_lists =  mAppDatabase.callManifestDao().getManifestList();
            Log.d("check+_data", String.valueOf(responseData.getManifest_id()));
*/

            /*for (int i = 0; i <5000; i++) {
                mAppDatabase.callManifestDao().updateBPID(responseData.getManifest_id(),117121530, "xyz"+i);
            }*/
         /*   try {
                int i = 0;
                for (UpdatedBPResponse.bp_shipment_details listofUpdate : responseData.getBpShipmentDetails()) {
             *//*   for (int i =0;i<manifest_lists.getShipment_details().size();i++){
                    if (listofUpdate.airwaybill_number==manifest_lists.getShipment_details().get(i).airwaybill_number){
                        manifest_lists.getShipment_details().get(i).setBrand_package_id(listofUpdate.brand_package_id);
                    }
                }*//*
                    // Update database within the transaction
                    i++;
                    Log.d("time", String.valueOf(i));
                    Log.d("bp_id",listofUpdate.getBrand_package_id());
                    mAppDatabase.callManifestDao().updateBPID(responseData.getManifest_id(), listofUpdate.getAirwaybill_number(), listofUpdate.getBrand_package_id());

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();

            }
*/

//            loadingStateLiveData.postValue(false);

            // mAppDatabase.callManifestDao().Update_table(manifest_lists);


        }
        /* UpdatedBPResponse response = (UpdatedBPResponse) intent.getSerializableExtra("extra_data");
         */
        // Retrieve the response from the intent extras
           /*     UpdatedBPResponse response = intent.getParcelableExtra("extra_data");




            try {
                // Perform the loop iterations
                for (UpdatedBPResponse.bp_shipment_details listofUpdate : response.getResponse().get(0).getBpShipmentDetails()) {
                    // Update database within the transaction
                    mAppDatabase.callManifestDao().updateBPID(response.getResponse().get(0).getManifest_id(), listofUpdate.getAirwaybill_number(), listofUpdate.brand_package_id);
                }

                // Commit the transaction

            } catch (Exception e) {
                // Rollback the transaction in case of an error

                // Handle the exception
                e.printStackTrace();
            }*/

    }
}

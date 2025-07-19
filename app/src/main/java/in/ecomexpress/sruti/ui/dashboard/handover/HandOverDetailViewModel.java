package in.ecomexpress.sruti.ui.dashboard.handover;


import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import dagger.Module;
import in.ecomexpress.sruti.model.handoverdata.HandOverShipmentList;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.common_files.CSVCreation;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd  on 11/7/19.
 */

@Module
public class HandOverDetailViewModel extends BaseViewModel {
    private static final String TAG = HandOverDetailViewModel.class.getSimpleName();
    private final MediatorLiveData<List<HandOverShipmentList>> total_data = new MediatorLiveData<>();

    public HandOverDetailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public MutableLiveData<List<HandOverShipmentList>> getAwbDetailLiveData() {
        return total_data;
    }


    public LiveData<List<HandOverShipmentList>> getAllAwbData() {
        LiveData<List<HandOverShipmentList>> loc = getDataManager().getAllAwbData();
        total_data.addSource(loc, new Observer<List<HandOverShipmentList>>() {
            @Override
            public void onChanged(List<HandOverShipmentList> handOverShipmentLists) {
                if (handOverShipmentLists == null) {

                } else {
                    total_data.removeSource(loc);
                    total_data.setValue(handOverShipmentLists);
                }
            }
        });
        return total_data;
    }



    void exportDB(String[] awbCollection) {
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),"");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "AWBNumber.csv");
        try {
            file.createNewFile();
            CSVCreation csvWrite = new CSVCreation(new FileWriter(file));

            csvWrite.writeNext(awbCollection);
/*
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
*/
            csvWrite.close();
//            curCSV.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", e.getMessage(), e);

        }
    }
}

package in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


@Module
public class WarehouseActivityModule {

    @Provides
    WarehouseViewModel provideSellerActivityViewModel(IDataManager dataManager,
                                                      ISchedulerProvider schedulerProvider) {
        return new WarehouseViewModel(dataManager, schedulerProvider);
    }

    @Provides
    WarehouseShipmentAdapter provideWarehouseShipmentAdapter() {
        return new WarehouseShipmentAdapter(new ArrayList<Shipment_Detail>());
    }
}

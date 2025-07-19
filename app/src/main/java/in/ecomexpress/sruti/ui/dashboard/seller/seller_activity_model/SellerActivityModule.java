package in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

@Module
public class SellerActivityModule {

    @Provides
    SellerViewModel provideSellerActivityViewModel(IDataManager dataManager,
                                                      ISchedulerProvider schedulerProvider) {
        return new SellerViewModel(dataManager, schedulerProvider);
    }

    @Provides
    SellerShipmentAdapter provideFuelReimbursementAdapter() {
        return new SellerShipmentAdapter(new ArrayList<Shipment_Detail>());
    }
}

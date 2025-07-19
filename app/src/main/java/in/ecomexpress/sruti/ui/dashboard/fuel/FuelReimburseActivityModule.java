package in.ecomexpress.sruti.ui.dashboard.fuel;


import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.model.fuel.response.Reports;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

@Module
public class FuelReimburseActivityModule {

    @Provides
    FuelReimburseViewModel provideForwardDetailViewModel(IDataManager dataManager,
                                                         ISchedulerProvider schedulerProvider) {
        return new FuelReimburseViewModel(dataManager, schedulerProvider);
    }

    @Provides
    FuelReimbursementAdapter provideFuelReimbursementAdapter() {
        return new FuelReimbursementAdapter(new ArrayList<Reports>());
    }
}

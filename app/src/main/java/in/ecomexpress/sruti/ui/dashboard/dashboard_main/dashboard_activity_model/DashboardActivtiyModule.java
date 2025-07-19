package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model;


import androidx.lifecycle.ViewModelProvider;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.factory.ViewModelProviderFactory;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


@Module
public class DashboardActivtiyModule {

    @Provides
    ViewModelProvider.Factory dashboardViewModelProvider(DashboardViewModel dashboardViewModel) {
        return new ViewModelProviderFactory<>(dashboardViewModel);
    }

    @Provides
    DashboardViewModel provideDashboardViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        return new DashboardViewModel(dataManager, schedulerProvider);
    }


}

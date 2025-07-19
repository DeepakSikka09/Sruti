package in.ecomexpress.sruti.ui.dashboard.performance;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

@Module
public class PerformanceActivityModule {

    @Provides
    PerformanceViewModel providePerformancefViewModel(IDataManager dataManager,
                                                 ISchedulerProvider schedulerProvider) {
        return new PerformanceViewModel(dataManager, schedulerProvider);
    }
}

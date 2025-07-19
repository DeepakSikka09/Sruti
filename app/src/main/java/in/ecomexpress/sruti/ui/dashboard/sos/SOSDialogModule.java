package in.ecomexpress.sruti.ui.dashboard.sos;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


@Module
public class SOSDialogModule {
    @Provides
    SOSViewModel provideSOSViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        return new SOSViewModel(dataManager, schedulerProvider);
    }


}

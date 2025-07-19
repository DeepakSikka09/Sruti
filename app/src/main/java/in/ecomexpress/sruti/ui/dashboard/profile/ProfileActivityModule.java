package in.ecomexpress.sruti.ui.dashboard.profile;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


@Module
public class ProfileActivityModule {
    @Provides
    ProfileViewModel provideProfileViewModel(IDataManager dataManager,
                                             ISchedulerProvider schedulerProvider) {
        return new ProfileViewModel(dataManager, schedulerProvider);
    }
}

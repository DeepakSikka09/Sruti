package in.ecomexpress.sruti.ui.login.changePassword;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangePasswordModule {

    @Provides
    ChangePasswordViewModel provideAboutViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        return new ChangePasswordViewModel(dataManager, schedulerProvider);
    }
}

package in.ecomexpress.sruti.ui.login.forget;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dhananjayk on 24-05-2018.
 */
@Module
public class ForgetActivityModule {
    @Provides
    ForgetViewModel provideForgetViewModel(IDataManager dataManager,
                                           ISchedulerProvider schedulerProvider) {
        return new ForgetViewModel(dataManager, schedulerProvider);
    }
}
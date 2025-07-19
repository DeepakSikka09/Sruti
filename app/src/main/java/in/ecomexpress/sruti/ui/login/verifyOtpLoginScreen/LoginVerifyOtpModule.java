package in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dhananjayk on 29-01-2019.
 */
@Module
public class LoginVerifyOtpModule {
    @Provides
    LoginVerifyOtpViewModel provideLoginVerifyOtpViewModel(IDataManager dataManager,
                                           ISchedulerProvider schedulerProvider) {
        return new LoginVerifyOtpViewModel(dataManager, schedulerProvider);
    }
}

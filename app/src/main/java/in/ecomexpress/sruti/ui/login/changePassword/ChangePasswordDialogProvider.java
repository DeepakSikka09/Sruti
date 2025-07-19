package in.ecomexpress.sruti.ui.login.changePassword;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by dhananjayk on 04-07-2018.
 */
@Module
public abstract class ChangePasswordDialogProvider {
    @ContributesAndroidInjector(modules = ChangePasswordModule.class)
    abstract ChangePasswordActivity provideRunIdDialogFactory();
}

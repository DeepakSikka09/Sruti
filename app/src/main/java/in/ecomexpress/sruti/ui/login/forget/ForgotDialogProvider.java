package in.ecomexpress.sruti.ui.login.forget;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ForgotDialogProvider {
    @ContributesAndroidInjector(modules = ForgetActivityModule.class)
    abstract ForgetActivity provideDialogFactory();
}
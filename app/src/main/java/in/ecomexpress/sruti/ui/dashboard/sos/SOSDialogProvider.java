package in.ecomexpress.sruti.ui.dashboard.sos;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SOSDialogProvider {
    @ContributesAndroidInjector(modules = SOSDialogModule.class)
    abstract SOSDialog provideDialogFactory();
}
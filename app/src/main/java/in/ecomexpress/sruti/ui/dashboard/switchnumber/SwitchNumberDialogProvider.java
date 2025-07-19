package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SwitchNumberDialogProvider {
    @ContributesAndroidInjector(modules = SwitchNumberDialogModule.class)
    abstract SwitchNumberDialog provideRunIdDialogFactory();
}
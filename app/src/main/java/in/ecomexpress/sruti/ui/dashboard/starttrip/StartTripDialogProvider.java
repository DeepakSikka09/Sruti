package in.ecomexpress.sruti.ui.dashboard.starttrip;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class StartTripDialogProvider {
    @ContributesAndroidInjector
    abstract StartTripDialog provideRunIdDialogFactory();
}
package in.ecomexpress.sruti.ui.dashboard.stoptrip;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class StopTripDialogProvider {
//    @ContributesAndroidInjector
//    abstract StopTripDialog provideRunIdDialogFactory();

    @ContributesAndroidInjector
    abstract StopTripDialogMultiVehicle provideStopFactory();
}
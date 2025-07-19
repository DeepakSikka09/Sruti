package in.ecomexpress.sruti.ui.dashboard.switchnumber;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;


@Module
public class SwitchNumberDialogModule {
    @Provides
    SwitchNumberViewModel provideSwitchNumberViewModel(IDataManager dataManager, ISchedulerProvider
            schedulerProvider) {
        return new SwitchNumberViewModel(dataManager, schedulerProvider);
    }
    @Provides
    SwitchNumberListAdapter provideSwitchNumberListAdapter() {
        return new SwitchNumberListAdapter(new ArrayList<Post_option>());
    }

}

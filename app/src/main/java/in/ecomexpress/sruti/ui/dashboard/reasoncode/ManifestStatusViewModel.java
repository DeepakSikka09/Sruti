package in.ecomexpress.sruti.ui.dashboard.reasoncode;

import dagger.Module;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IReasonCodeNavigation;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
@Module
public class ManifestStatusViewModel extends BaseViewModel {


    public ManifestStatusViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
}
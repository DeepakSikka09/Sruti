package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

public class PickUpFailViewModel extends BaseViewModel {
    public PickUpFailViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
}

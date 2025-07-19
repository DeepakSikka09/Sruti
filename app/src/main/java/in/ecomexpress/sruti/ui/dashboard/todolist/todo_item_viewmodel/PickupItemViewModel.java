package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel;

import in.ecomexpress.sruti.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.DRSItemViewModelListener;

public class PickupItemViewModel {

    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;
    public void onItemClick() {
        drsItemViewModelListener.onItemClick(mCommonDRSListItem);
    }

    public void onMapClick() {
        drsItemViewModelListener.onMapClick(mCommonDRSListItem);
    }

    public void onCallClick() {
        drsItemViewModelListener.onCallClick(mCommonDRSListItem);
    }
    public void onTrayClick() {
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }
    public void onIndicatorClick() {
        drsItemViewModelListener.onIndicatorClick(mCommonDRSListItem);
    }
}

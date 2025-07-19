package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener;

import in.ecomexpress.sruti.repo.local.db.model.CommonDRSListItem;

public interface DRSItemViewModelListener {

    void onItemClick(CommonDRSListItem commonDRSListItem);

    void onMapClick(CommonDRSListItem commonDRSListItem);

    void onCallClick(CommonDRSListItem mCommonDRSListItem);

    void onIndicatorClick(CommonDRSListItem mCommonDRSListItem);

    void onTrayClick(CommonDRSListItem mCommonDRSListItem);
}
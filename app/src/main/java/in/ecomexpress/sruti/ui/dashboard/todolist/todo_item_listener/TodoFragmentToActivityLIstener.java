package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener;

import java.util.List;

import in.ecomexpress.sruti.model.menifestdata.Manifest_List;

/**
 * Created by shivangi on 21/10/19.
 */

public interface TodoFragmentToActivityLIstener {

    void onGettingManifest(List<Manifest_List> manifest_lists);
}

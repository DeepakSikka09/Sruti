package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener;

import java.util.ArrayList;

import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Shipment_Detail;


public interface ToDoAdaptertoActivityListener {
    void onBarcodeScanItem(Manifest_List manifest_list);

    void commitToserver(long manifest_no);
}

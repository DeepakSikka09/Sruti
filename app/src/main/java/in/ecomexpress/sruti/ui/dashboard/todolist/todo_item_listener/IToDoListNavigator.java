package in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener;

import android.content.Context;

import java.util.List;

import in.ecomexpress.sruti.model.commitdata.CommitPacketData;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;


public interface IToDoListNavigator {
    void onBackClick();
    void onErrorMessage(String message);

    void onIndicatorClick();

    void onScanClick();

    void onSynClickEvent();
    void onFilterClick();

    void onApplyClick();

    void setFilteredAdapter(List<Manifest_List> filteredManifestTypeList);

    void onClearFilterClick();

    void showError(String message);

    void updateProgressBar();

    void nextScreen(String manifest_no,String sFileBody);


    void uploadCommitPacket(CommitPacketData commitPacketData, String fileName);
    // void setCountOnDrs(Long picked, long total);
}

package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import android.widget.ImageView;

import java.util.HashSet;

import in.ecomexpress.sruti.model.menifestdata.Manifest_List;

/**
 * Created by shivangi on 8/7/19.
 */

public interface IScanSummaryNavigator {
    void notifyAdapter();

    void showMessage(String s);
    void onNext();

    void setAdapter(HashSet<Manifest_List> countValue);

    void onBackClick();

    void captureImage(ImageView cam, int position, long manifest_list);
}

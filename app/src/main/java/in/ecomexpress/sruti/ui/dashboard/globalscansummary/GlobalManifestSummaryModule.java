package in.ecomexpress.sruti.ui.dashboard.globalscansummary;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

@Module
public class GlobalManifestSummaryModule {
    @Provides
    GlobalManifestSummaryViewModel provideScanSummaryViewModel(IDataManager iDataManager, ISchedulerProvider iSchedulerProvider) {
        return new GlobalManifestSummaryViewModel(iDataManager, iSchedulerProvider);
    }
    @Provides
    GlobalManifestSummaryAdapter provideGlobalScanAdapter() {
        return new GlobalManifestSummaryAdapter(new ArrayList<Manifest_List>());

    }
}

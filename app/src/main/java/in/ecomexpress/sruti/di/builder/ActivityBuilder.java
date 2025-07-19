package in.ecomexpress.sruti.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.ecomexpress.sruti.background_service.SrutiSyncService;
import in.ecomexpress.sruti.ui.dashboard.attendence.AttendanceActivityView;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivity;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_activity_model.DashboardActivtiyModule;
import in.ecomexpress.sruti.ui.dashboard.fuel.FuelReimburseActivity;
import in.ecomexpress.sruti.ui.dashboard.fuel.FuelReimburseActivityModule;
import in.ecomexpress.sruti.ui.dashboard.globalscan.GlobalScanScreenActivity;
import in.ecomexpress.sruti.ui.dashboard.globalscan.GlobalScanScreenModule;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalManifestSummaryActivity;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalManifestSummaryModule;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalShipmentListActivity;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalShipmentListViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverDetailActivity;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverDetailViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverListActivity;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverListViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverScanActivity;
import in.ecomexpress.sruti.ui.dashboard.performance.PerformanceActivity;
import in.ecomexpress.sruti.ui.dashboard.performance.PerformanceActivityModule;
import in.ecomexpress.sruti.ui.dashboard.profile.ProfileActivity;
import in.ecomexpress.sruti.ui.dashboard.profile.ProfileActivityModule;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ManifestStatusActivity;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ManifestStatusViewModel;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivityModule;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerScanActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerScanViewModel;
import in.ecomexpress.sruti.ui.dashboard.shipment.ShipmentOtpActivity;
import in.ecomexpress.sruti.ui.dashboard.shipment.ShipmentOtpViewModel;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureViewModel;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailViewModel;
import in.ecomexpress.sruti.ui.dashboard.sos.SOSDialogProvider;
import in.ecomexpress.sruti.ui.dashboard.starttrip.StartTripDialogProvider;
import in.ecomexpress.sruti.ui.dashboard.stoptrip.StopTripDialogProvider;
import in.ecomexpress.sruti.ui.dashboard.switchnumber.SwitchNumberDialogProvider;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.OtpReasonCodeViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.OtpVerficationViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.QrcReaderViewModel;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ReasonCodeViewModel;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ManifestLevelReasonCodeActivity;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.OtpReasonCodeActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.OtpVerificationActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.QrcReaderActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.RecciQuestion;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.ToDoListActivity;
import in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel.TodoFragmentBind;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingActivity;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingViewModel;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivityModule;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseScanActivity;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseScanViewModel;
import in.ecomexpress.sruti.ui.login.changePassword.ChangePasswordDialogProvider;
import in.ecomexpress.sruti.ui.login.forget.ForgotDialogProvider;
import in.ecomexpress.sruti.ui.login.login.LoginActivity;
import in.ecomexpress.sruti.ui.login.login.LoginActivityModule;
import in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen.LoginVerifyOtpActivity;
import in.ecomexpress.sruti.ui.login.verifyOtpLoginScreen.LoginVerifyOtpModule;
import in.ecomexpress.sruti.utils.MyIntentService;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = {TodoFragmentBind.class, SwitchNumberDialogProvider.class})
    abstract ToDoListActivity bindToDoListActivity();

    @ContributesAndroidInjector(modules = {DashboardActivtiyModule.class, SwitchNumberDialogProvider.class, StartTripDialogProvider.class, ChangePasswordDialogProvider.class, StopTripDialogProvider.class, SOSDialogProvider.class})
    abstract DashboardActivity bindDashboardActivity();




    @ContributesAndroidInjector(modules = PerformanceActivityModule.class)
    abstract PerformanceActivity bindPerformanceActivity();

    @ContributesAndroidInjector(modules = SellerActivityModule.class)
    abstract SellerActivity bindSellerActivity();

    @ContributesAndroidInjector(modules = WarehouseActivityModule.class)
    abstract WarehouseActivity bindWarehouseActivity();

    @ContributesAndroidInjector(modules = FuelReimburseActivityModule.class)
    abstract FuelReimburseActivity bindFuelReimburseActivity();

    @ContributesAndroidInjector(modules = {LoginActivityModule.class, ForgotDialogProvider.class, ChangePasswordDialogProvider.class})
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = {LoginVerifyOtpModule.class})
    abstract LoginVerifyOtpActivity bindLoginVerifyOtpActivity();

    @ContributesAndroidInjector(modules = {})
    abstract AttendanceActivityView bindAttendenceView();

    @ContributesAndroidInjector(modules = {ProfileActivityModule.class})
    abstract ProfileActivity bindProfileActivity();

    @ContributesAndroidInjector(modules = {GlobalScanScreenModule.class})
    abstract GlobalScanScreenActivity bindScanScreenActivity();

    @ContributesAndroidInjector(modules = {GlobalManifestSummaryModule.class})
    abstract GlobalManifestSummaryActivity bindScanSummaryActivity();

    @ContributesAndroidInjector(modules = {})
    abstract HandOverScanActivity bindHandOverActivity();

    @ContributesAndroidInjector(modules = {HandOverDetailViewModel.class})
    abstract HandOverDetailActivity bindHandOverDetailViewModel();


    @ContributesAndroidInjector(modules = {GlobalShipmentListViewModel.class})
    abstract GlobalShipmentListActivity bindShipmentListActivity();

    @ContributesAndroidInjector(modules = {SignatureViewModel.class})
    abstract SignatureActivity bindPickUpListWithSignatureActivity();

    @ContributesAndroidInjector(modules = {SellerScanViewModel.class})
    abstract SellerScanActivity bindScanVendorActivity();

    @ContributesAndroidInjector(modules = {WarehouseScanViewModel.class})
    abstract WarehouseScanActivity bindScanWarehouseActivity();

    @ContributesAndroidInjector
    abstract RecciQuestion bindRecci();

    @ContributesAndroidInjector(modules = {HandOverListViewModel.class})
    abstract HandOverListActivity bindHandOverListActivity();

    @ContributesAndroidInjector(modules = {ReasonCodeViewModel.class})
    abstract ManifestLevelReasonCodeActivity manifestLevelReasonCodeActivity();

    @ContributesAndroidInjector(modules = {OtpReasonCodeViewModel.class})
    abstract OtpReasonCodeActivity otpReasonCodeActivity();

    @ContributesAndroidInjector(modules = {ManifestStatusViewModel.class})
    abstract ManifestStatusActivity manifestStatusActivity();


    @ContributesAndroidInjector(modules = {ShipmentOtpViewModel.class})
    abstract ShipmentOtpActivity shipmentOtpActivity();


   /* @ContributesAndroidInjector(modules = {PickUpFailViewModel.class})
    abstract PickUpFailActivity pickUpFailActivity();*/

    @ContributesAndroidInjector(modules = {SuccessFailViewModel.class})
    abstract SuccessFailActivity bSuccessFailActivity();

    @ContributesAndroidInjector(modules = {OtpVerficationViewModel.class})
    abstract OtpVerificationActivity bOtpVerificationActivity();

    @ContributesAndroidInjector(modules = {QrcReaderViewModel.class})
    abstract QrcReaderActivity bQrcReaderActivity();

    @ContributesAndroidInjector(modules = {TrainingViewModel.class})
    abstract TrainingActivity trainingActivity();

    @ContributesAndroidInjector
    abstract SrutiSyncService sysnkService();

    @ContributesAndroidInjector
    abstract MyIntentService intentService();

}

package in.ecomexpress.sruti.di;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.dashboard.attendence.AttendenceViewModel;
import in.ecomexpress.sruti.ui.dashboard.globalscansummary.GlobalShipmentListViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverDetailViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverListViewModel;
import in.ecomexpress.sruti.ui.dashboard.handover.HandOverScanViewModel;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ManifestStatusViewModel;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerScanViewModel;
import in.ecomexpress.sruti.ui.dashboard.shipment.ShipmentOtpViewModel;
import in.ecomexpress.sruti.ui.dashboard.signature.SignatureViewModel;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailViewModel;
import in.ecomexpress.sruti.ui.dashboard.starttrip.StartTripViewModel;
import in.ecomexpress.sruti.ui.dashboard.stoptrip.StopTripViewModel;
import in.ecomexpress.sruti.ui.dashboard.stoptrip.StopTripViewModelMultiVehicle;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.OtpReasonCodeViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.OtpVerficationViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.PickUpFailViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.QrcReaderViewModel;
import in.ecomexpress.sruti.ui.dashboard.reasoncode.ReasonCodeViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.RecciViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;
import in.ecomexpress.sruti.ui.dashboard.training.TrainingViewModel;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseScanViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;

/**
 * Created by 63091 on 11-06-2019.
 */

public class ViewModelProviderRoom extends ViewModelProvider.NewInstanceFactory {
    private final IDataManager dataManager;
    private final ISchedulerProvider schedulerProvider;

    @Inject
    public ViewModelProviderRoom(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        this.dataManager = dataManager;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StartTripViewModel.class)) {
            return (T) new StartTripViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(StopTripViewModel.class)) {
            return (T) new StopTripViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(AttendenceViewModel.class)) {
            return (T) new AttendenceViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(ToDoListViewModel.class)) {
            return (T) new ToDoListViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(HandOverScanViewModel.class)) {
            return (T) new HandOverScanViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(HandOverDetailViewModel.class)) {
            return (T) new HandOverDetailViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(GlobalShipmentListViewModel.class)) {
            return (T) new GlobalShipmentListViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(SignatureViewModel.class)) {
            return (T) new SignatureViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(SellerScanViewModel.class)) {
            return (T) new SellerScanViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(WarehouseScanViewModel.class)) {
            return (T) new WarehouseScanViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(RecciViewModel.class)) {
            return (T) new RecciViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(HandOverListViewModel.class)) {
            return (T) new HandOverListViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(StopTripViewModelMultiVehicle.class)) {
            return (T) new StopTripViewModelMultiVehicle(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(SuccessFailViewModel.class)) {
            return (T) new SuccessFailViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(OtpVerficationViewModel.class)) {
            return (T) new OtpVerficationViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(QrcReaderViewModel.class)) {
            return (T) new QrcReaderViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(ReasonCodeViewModel.class)) {
            return (T) new ReasonCodeViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(OtpReasonCodeViewModel.class)) {
            return (T) new OtpReasonCodeViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(PickUpFailViewModel.class)) {
            return (T) new PickUpFailViewModel(dataManager, schedulerProvider);
        } else if (modelClass.isAssignableFrom(ManifestStatusViewModel.class)) {
            return (T) new ManifestStatusViewModel(dataManager, schedulerProvider);
        }
        else if (modelClass.isAssignableFrom(ShipmentOtpViewModel.class)) {
            return (T) new ShipmentOtpViewModel(dataManager, schedulerProvider);
        }

        else if (modelClass.isAssignableFrom(TrainingViewModel.class)) {
            return (T) new TrainingViewModel(dataManager, schedulerProvider);
        }
        throw new IllegalArgumentException("NOT FOUND VIEWMODEL");
    }

}

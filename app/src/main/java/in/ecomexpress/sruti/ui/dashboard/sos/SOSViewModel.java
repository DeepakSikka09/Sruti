package in.ecomexpress.sruti.ui.dashboard.sos;


import java.util.ArrayList;
import java.util.Arrays;

import in.ecomexpress.sruti.model.sos.SOSRequest;
import in.ecomexpress.sruti.model.sos.SOSResponse;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.ui.base.BaseViewModel;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

public class SOSViewModel extends BaseViewModel<SOSCallBack> {

    public SOSViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    private double getLatitude() {
        return getDataManager().getCurrentLatitude();
    }

    private double getLongitude() {
        return getDataManager().getCurrentLongitude();
    }

    private long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public String getEmpCode() {
        return getDataManager().getCode();
    }


    public void cancelSOS() {
        getNavigator().showOptionMenu();
    }

    private String getAuthToken() {
        return getDataManager().getAuthToken();
    }

    public void callSOSAPI() {
        setIsLoading(true);
        final long timeStamp = System.currentTimeMillis();
//        writeEvent(timeStamp, "start calling sos api");
        SOSRequest request = new SOSRequest(getEmpCode(), getLatitude(), getLongitude(), getCurrentTimeStamp());
        try {
            getCompositeDisposable().add(getDataManager().doSOSApiCall(getAuthToken(), request)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<SOSResponse>() {
                        @Override
                        public void accept(SOSResponse sosResponse) throws Exception {
                            setIsLoading(false);
//                            writeEvent(timeStamp, "sosResponse: " + sosResponse.toString());
                            try {
                                if (sosResponse.getStatus()) {
                                    getNavigator().showDescription(sosResponse.getResponse().getDescription());
                                } else {
                                    if (sosResponse.getResponse().getStatusCode() == 107) {
                                        getNavigator().showDescription(sosResponse.getResponse().getDescription());
                                        getNavigator().dismissDialog();
                                        getNavigator().logout();
                                    } else {
                                        getNavigator().showDescription(sosResponse.getResponse().getDescription());
                                    }
                                }
                            } catch (Exception e) {
                                getNavigator().showException(e);
//                                writeErrors(timeStamp, e);
                                e.printStackTrace();
                            }
                            getNavigator().dismissDialog();
                        }
                    }, throwable -> {
                        setIsLoading(false);
                        getNavigator().dismissDialog();
                        writeErrors(timeStamp, String.valueOf(new Exception(throwable)));
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showException(e);
//            writeErrors(timeStamp, e);
            getNavigator().dismissDialog();
        }
    }

    public double getLat() {
        return getDataManager().getCurrentLatitude();
    }

    public double getLang() {
        return getDataManager().getCurrentLongitude();
    }

    public ArrayList<String> getSOSNumbers() {
        String[] array = getDataManager().getSOSNumbers().split(",");
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        return arrayList;
    }

    public String getSOSTempalte() {
        return getDataManager().getSOSSMSTemplate();
//        return "@@EMPCODE has raised a SOS Alert from https://maps.google.com/?q=@@LATITUDE,@@LONGITUDE location.";
    }
}

package in.ecomexpress.sruti.di.appmodule;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.room.Room;

import java.util.concurrent.TimeUnit;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.ecomexpress.sruti.di.ApplicationInfo;
import in.ecomexpress.sruti.di.DatabaseInfo;
import in.ecomexpress.sruti.di.RestApiInfo;
import in.ecomexpress.sruti.model.DeviceDetails;
import in.ecomexpress.sruti.repo.DataManager;
import in.ecomexpress.sruti.repo.IDataManager;
import in.ecomexpress.sruti.repo.local.db.DBHelper;
import in.ecomexpress.sruti.repo.local.db.IDBHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPopPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.IPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.PopPreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.prefs.PreferenceHelper;
import in.ecomexpress.sruti.repo.local.db.roomdb.SrutiDatabase;
import in.ecomexpress.sruti.repo.local.storage.IStorageHelper;
import in.ecomexpress.sruti.repo.local.storage.StorageHelper;
import in.ecomexpress.sruti.repo.remote.IRestApiHelper;
import in.ecomexpress.sruti.repo.remote.RestApiHelper;
import in.ecomexpress.sruti.repo.remote.RetrofitService;
import in.ecomexpress.sruti.utils.CommonUtils;
import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.rx.ISchedulerProvider;
import in.ecomexpress.sruti.utils.rx.SchedulerProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


@Module
public class AppModule {

    /**
     * Provide context context.
     *
     * @param application the application
     * @return the context
     */
    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    /**
     * Provide scheduler provider scheduler provider.
     *
     * @return the scheduler provider
     */
    @Provides
    ISchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider();
    }

    /**
     * Provide app database sathi database.
     *
     * @param dbName  the db name
     * @param context the context
     * @return the sathi database
     */
    @Provides
    @Singleton
    SrutiDatabase provideAppDatabase(@DatabaseInfo("Name") String dbName, Context context) {
        // SafeHelperFactory safeHelperFactory = new SafeHelperFactory(dbPassword.toCharArray());
        return Room.databaseBuilder(context, SrutiDatabase.class, dbName)
                .fallbackToDestructiveMigration()
                .build();
    }


    /**
     * Provide database name string.
     *
     * @return the string
     */
    @Provides
    @DatabaseInfo("Name")
    String provideDatabaseName() {
        return Constants.DB_NAME;
    }


    @Qualifier
    public @interface Pref1 {
    }

    @Qualifier
    public @interface Pref2 {
    }

    /**
     * Provide preference name string.
     *
     * @return the string
     */
    @Provides
    @Pref1
//        @PreferenceInfol()
    String providePreferenceName() {
        return Constants.PREF_NAME;
    }

    @Provides
    @Pref2
//        @PreferenceInfol()
    String providePopPreferenceName() {
        return Constants.POP_PREF_NAME;
    }

    /**
     * Provide package name string.
     *
     * @param context the context
     * @return the string
     */
    @Provides
    @ApplicationInfo("PackageName")
    String providePackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * Provide app version name string.
     *
     * @return the string
     */
    @Provides
    @ApplicationInfo("VersionName")
    String provideAppVersionName() {
        return Constants.VERSION_NAME;
    }

    /**
     * Provide app version code integer.
     *
     * @return the integer
     */
    @Provides
    @ApplicationInfo("VersionCode")
    Integer provideAppVersionCode() {
        return Constants.VERSION_CODE;
    }


    /**
     * Provide api url string.
     *
     * @return the string
     */
    @Provides
    @RestApiInfo("ServerUrl")
    String provideApiUrl() {
        return Constants.SERVER_URL;
    }

    /**
     * Provide data manager data manager.
     *
     * @param dataHelper the data helper
     * @return the data manager
     */
    @Provides
    @Singleton
    IDataManager provideDataManager(DataManager dataHelper) {
        return dataHelper;
    }


    /**
     * Provide database helper idb helper.
     *
     * @param databaseHelper the database helper
     * @return the idb helper
     */
    @Provides
    @Singleton
    IDBHelper provideDatabaseHelper(DBHelper databaseHelper) {
        return databaseHelper;
    }

    /**
     * Provide file helper storage helper.
     *
     * @param storageHelper the storage helper
     * @return the storage helper
     */
    @Provides
    @Singleton
    IStorageHelper provideFileHelper(StorageHelper storageHelper) {
        return storageHelper;
    }


    /**
     * Provide preference helper preference helper.
     *
     * @param preferenceHelper the preference helper
     * @return the preference helper
     */
    @Provides
    @Singleton
    IPreferenceHelper providePreferenceHelper(PreferenceHelper preferenceHelper) {
        return preferenceHelper;
    }

    @Provides
    @Singleton
    IPopPreferenceHelper providePopPreferenceHelper(PopPreferenceHelper poppreferenceHelper) {
        return poppreferenceHelper;
    }

    /**
     * Provide rest api helper rest api helper.
     *
     * @param restApiHelper the rest api helper
     * @return the rest api helper
     */
    @Provides
    @Singleton
    IRestApiHelper provideRestApiHelper(RestApiHelper restApiHelper, StorageHelper storageHelper) {
//        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
        //  return restApiHelper;
//        }
//        if (BuildConfig.DEBUG) {
//        return storageHelper;
//        } else {
        return restApiHelper;

//        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Provides
    @Singleton
    DeviceDetails provideDeviceDetails(Context context) {
        DeviceDetails deviceDetails = new DeviceDetails();

        if (CommonUtils.getLocalIpAddress() == null) {
            deviceDetails.setIpAddress("127.0.0.1");
        } else {
            deviceDetails.setIpAddress(CommonUtils.getLocalIpAddress());
        }

        deviceDetails.setIsOtgEnabled(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST));
        deviceDetails.setManufacturer(Build.MANUFACTURER);
        deviceDetails.setModelNumber(Build.MODEL);
        deviceDetails.setSdkVersion(Build.VERSION.RELEASE);
        deviceDetails.setSdkVersionCode(Build.VERSION.SDK_INT);
        deviceDetails.setApp_version(Constants.VERSION_NAME);
        return deviceDetails;

    }


    @Provides
    OkHttpClient provideOkHttpClient(Context context) {
        return new OkHttpClient().newBuilder()
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor(context))
                .build();
    }

    @Provides
    Retrofit provideRetrofit(@RestApiInfo("ServerUrl") String url, OkHttpClient okHttpClient) {
     /*   HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    */
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Provides
    RetrofitService provideRetrofitService(Retrofit retrofit) {
        return retrofit.create(RetrofitService.class);
    }


}


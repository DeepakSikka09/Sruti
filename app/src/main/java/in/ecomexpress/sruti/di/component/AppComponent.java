package in.ecomexpress.sruti.di.component;

import android.app.Application;


import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import in.ecomexpress.sruti.SrutiApplication;
import in.ecomexpress.sruti.di.appmodule.AppModule;
import in.ecomexpress.sruti.di.builder.ActivityBuilder;

@Singleton
@Component(modules = {ActivityBuilder.class, AppModule.class, AndroidInjectionModule.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(SrutiApplication app);
}

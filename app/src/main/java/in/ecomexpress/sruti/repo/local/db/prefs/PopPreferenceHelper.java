package in.ecomexpress.sruti.repo.local.db.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.ecomexpress.sruti.di.appmodule.AppModule;

@Singleton
public class PopPreferenceHelper implements IPopPreferenceHelper {
    private SharedPreferences mPref;
    private final String PREF_KEY_IS_CHILD_AVIALABLE = "is_child_available";
    private final String IS_POP_ENABLE = "IS_POP_ENABLE";

    @Inject
    public PopPreferenceHelper(Context context,@AppModule.Pref2 String prefFileName) {
        mPref = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    public PopPreferenceHelper() {
        super();
    }


    @Override
    public void is_child_available(boolean is_child_available) {
        mPref.edit().putBoolean(PREF_KEY_IS_CHILD_AVIALABLE, is_child_available).apply();

    }


    @Override
    public boolean get_is_child_available() {
        return mPref.getBoolean(PREF_KEY_IS_CHILD_AVIALABLE, false);
    }


    @Override
    public void is_POP_Enable(boolean is_pop_enable) {
        mPref.edit().putBoolean(IS_POP_ENABLE, is_pop_enable).apply();

    }

    @Override
    public boolean get_pop_enable() {
        return mPref.getBoolean(IS_POP_ENABLE, false);
    }

    @Override
    public boolean clearPopPrefrence() {
        mPref.edit().clear().commit();
        return true;
    }
}

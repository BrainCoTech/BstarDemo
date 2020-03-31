package tech.brainco.bstardemo;

import android.app.Application;

import tech.brainco.bstarsdk.core.BstarSDK;
import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant();
        }
        BstarSDK.init(this);
    }
}

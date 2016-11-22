package com.example.seele.retrofit;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by SEELE on 2016/11/22.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*//使用默认配置初始化Stetho
        Stetho.initializeWithDefaults(this);*/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}

package com.wislie.customview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-12 09:56
 * desc   :
 * version: 1.0
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}

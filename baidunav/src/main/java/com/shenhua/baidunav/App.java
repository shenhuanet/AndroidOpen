package com.shenhua.baidunav;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        System.out.println("app is start!");
    }

}
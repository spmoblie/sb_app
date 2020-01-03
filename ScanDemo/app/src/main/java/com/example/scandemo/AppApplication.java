package com.example.scandemo;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class AppApplication extends Application {

    String TAG = AppApplication.class.getSimpleName();


    //必须注册，Android框架调用Application
    @Override
    public void onCreate() {
        super.onCreate();

        ZXingLibrary.initDisplayOpinion(this);

    }

}
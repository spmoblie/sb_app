package com.songbao.sampo_b.utils;

import android.util.Log;

import com.songbao.sampo_b.AppConfig;


public class LogUtil {

    public static final String LOG_HTTP = "Retrofit";
    public static final String LOG_TAG = "log_activity";

    public static void i(String tag, String msg) {
        if (!AppConfig.IS_PUBLISH) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, Object msg) {
        i(tag, String.valueOf(msg));
    }

}

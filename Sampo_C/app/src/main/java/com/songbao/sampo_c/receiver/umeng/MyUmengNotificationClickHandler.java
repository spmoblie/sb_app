package com.songbao.sampo_c.receiver.umeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.activity.MainActivity;
import com.songbao.sampo_c.utils.DeviceUtil;
import com.songbao.sampo_c.utils.LogUtil;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class MyUmengNotificationClickHandler extends UmengNotificationClickHandler {

    @Override
    public void autoUpdate(Context context, UMessage uMessage) {
        super.autoUpdate(context, uMessage);
    }

    @Override
    public void openUrl(Context context, UMessage uMessage) {
        super.openUrl(context, uMessage);
    }

    @Override
    public void openActivity(Context context, UMessage uMessage) {
        super.openActivity(context, uMessage);
    }

    @Override
    public void launchApp(Context context, UMessage uMessage) {
        //页面跳转路径参数设置
        Editor editor = AppApplication.getSharedPreferences().edit();
        editor.putBoolean(AppConfig.KEY_JUMP_PAGE, true);
        editor.putInt(AppConfig.KEY_MAIN_CURRENT_INDEX, 2);
        editor.putBoolean(AppConfig.KEY_OPEN_MESSAGE, true);
        editor.apply();
        //判断app进程是否存活
        if(DeviceUtil.isAppAlive(context, context.getPackageName())){
            LogUtil.i("PushManager", "the app process is alive");
            //如果App进程存活，就直接启动Activity。
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }else {
            LogUtil.i("PushManager", "the app process is dead");
            //如果App进程已经被杀死，则重新启动App。
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }
    }

    @Override
    public void dealWithCustomAction(Context context, UMessage uMessage) {
        super.dealWithCustomAction(context, uMessage);
    }
}

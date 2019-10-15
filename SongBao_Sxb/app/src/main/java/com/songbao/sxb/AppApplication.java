package com.songbao.sxb;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.songbao.sxb.activity.MainActivity;
import com.songbao.sxb.utils.SharedUtil;
import com.songbao.sxb.utils.BitmapUtil;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.DeviceUtil;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.PushManager;
import com.songbao.sxb.utils.UserManager;
import com.songbao.sxb.utils.retrofit.HttpRequests;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class AppApplication extends Application {

    String TAG = AppApplication.class.getSimpleName();

    private static AppApplication spApp = null;
    private static SharedPreferences shared;
    private static PushManager pushManager;
    private static RequestOptions showOptions, headOptions;

    public static String version_name = ""; //当前版本号
    public static boolean isWXShare = false; //记录是否微信分享

    //必须注册，Android框架调用Application
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onCreate");

        spApp = this;
        shared = getSharedPreferences();
        Editor editor = shared.edit();

        // 初始化推送服务
        pushManager = PushManager.getInstance();
        pushManager.initPushService();

        // 获取手机型号及屏幕的宽高
        int screenWidth = DeviceUtil.getDeviceWidth(this);
        int screenHeight = DeviceUtil.getDeviceHeight(this);
        editor.putInt(AppConfig.KEY_SCREEN_WIDTH, screenWidth);
        editor.putInt(AppConfig.KEY_SCREEN_HEIGHT, screenHeight);
        // 判定是否为Pad
        LogUtil.i("device", "手机型号：" + DeviceUtil.getModel() + " 宽：" + screenWidth + " / 高：" + screenHeight);

        // 设置每天第一次启动App时清除与日期关联的缓存标志
        long newDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        long oldDay = shared.getLong(AppConfig.KEY_LOAD_SV_DATA_DAY, 0);
        if ((newDay == 1 && oldDay != 1) || newDay - oldDay > 0) {
            clearSharedLoadSVData();
            editor.putLong(AppConfig.KEY_LOAD_SV_DATA_DAY, newDay);
        }
        editor.apply();

        // 程序崩溃时触发线程
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);

        // 设置App字体不随系统字体变化
        initDisplayMetrics();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onTrimMemory");
        super.onTrimMemory(level);
    }

    public static Context getAppContext() {
        return spApp.getApplicationContext();
    }

    public static SharedPreferences getSharedPreferences() {
        if (shared == null) {
            shared = SharedUtil.getInstance(getAppContext());
        }
        return shared;
    }

    /**
     * 全局展示图片加载器
     */
    public static RequestOptions getShowOptions() {
        if (showOptions == null) {
            showOptions = new RequestOptions()
                    .placeholder(R.drawable.icon_default_show) //图片加载出来前，显示的图片
                    .fallback(R.drawable.icon_default_null) //url为空的时候,显示的图片
                    .error(R.drawable.icon_default_error); //图片加载失败后，显示的图片
        }
        return showOptions;
    }

    /**
     * 全局头像图片加载器
     */
    public static RequestOptions getHeadOptions() {
        if (headOptions == null) {
            headOptions = new RequestOptions()
                    .placeholder(R.drawable.icon_default_head) //图片加载出来前，显示的图片
                    .fallback(R.drawable.icon_default_head) //url为空的时候,显示的图片
                    .error(R.drawable.icon_default_head); //图片加载失败后，显示的图片
        }
        return headOptions;
    }

    /**
     * 清除Glide图片缓存
     */
    public static void clearGlideCache() {
        // clearMemory()方法必须运行在主线程
        Glide.get(getAppContext()).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // clearDiskCache()方法必须运行在子线程
                Glide.get(AppApplication.getAppContext()).clearDiskCache();
            }
        }).start();
    }

    /**
     * 清除加载数据控制符的缓存
     */
    public void clearSharedLoadSVData() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
				SortDBService.getInstance(spApp).deleteAll(); //清空数据库
				clearGlideCache(); //清除图片缓存
				CleanDataManager.cleanAppTemporaryData(spApp); //清除临时缓存
				CleanDataManager.cleanCustomCache(AppConfig.SAVE_PATH_MEDIA_DICE); //清除视频缓存
            }
        }).start();*/
        updateUserData(true);
    }

    /**
     * 刷新用户信息-状态标记
     */
    public static void updateUserData(boolean isState) {
        shared.edit().putBoolean(AppConfig.KEY_UPDATE_USER_DATA, isState).apply();
    }

    /**
     * 刷新"我的"数据-状态标记
     */
    public static void updateMineData(boolean isState) {
        shared.edit().putBoolean(AppConfig.KEY_UPDATE_MINE_DATA, isState).apply();
    }

    /**
     * 设置App字体不随系统字体变化
     */
    public static void initDisplayMetrics() {
        DisplayMetrics displayMetrics = getAppContext().getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
    }

    /**
     * 保存图片对象到指定文件并通知相册更新相片
     */
    public static void saveBitmapFile(Bitmap bm, File file, int compress) {
        if (bm == null || file == null) {
            CommonTools.showToast(getAppContext().getResources().getString(R.string.photo_show_save_fail));
            return;
        }
        try {
            BitmapUtil.save(bm, file, compress);
            if (file.getAbsolutePath().contains(AppConfig.SAVE_PATH_IMAGE_SAVE)) {
                updatePhoto(file); //需要保存的图片通知更新相册
            }
        } catch (IOException e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * 通知相册更新相片
     */
    public static void updatePhoto(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            MediaScannerConnection.scanFile(getAppContext(), new String[]{file.getAbsolutePath()}, null, null);
        } else {
            final Intent intent;
            if (file.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
            }
            getAppContext().sendBroadcast(intent);
        }
    }

    /**
     * 应用数据统计之页面启动
     */
    public static void onPageStart(String pageName) {
        onPageStart(null, pageName);
    }

    /**
     * 应用数据统计之页面启动
     */
    public static void onPageStart(Context ctx, String pageName) {
        if (ctx != null) {
            MobclickAgent.onResume(ctx);
        }
        MobclickAgent.onPageStart(pageName);
    }

    /**
     * 应用数据统计之页面关闭
     */
    public static void onPageEnd(Context ctx, String pageName) {
		MobclickAgent.onPageEnd(pageName);
		MobclickAgent.onPause(ctx);
    }

    /**
     * 推送服务统计应用启动数据
     */
    public static void onPushAppStartData() {
		pushManager.onPushAppStartData();
    }

    /**
     * 初始化推送服务状态
     */
    public static void onPushDefaultStatus() {
		pushManager.onPushDefaultStatus();
    }

    /**
     * 设置推送服务的权限
     */
    public static void setPushStatus(boolean isStatus) {
		pushManager.setPushStatus(isStatus);
    }

    /**
     * 获取推送服务的权限
     */
	public static boolean getPushStatus() {
		return pushManager.getPushStatus();
	}

    /**
     * 注册或注销用户信息至推送服务
     */
    public static void onPushRegister(boolean isRegister) {
		if (isRegister) {
			pushManager.registerPush();
		} else {
			pushManager.unregisterPush();
		}
    }

    /**
     * App注销登出统一入口
     */
    public static void AppLogout() {
        // 远程退出
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", UserManager.getInstance().getUserId());
        HttpRequests.getInstance().loadData("url_head:pay", AppConfig.URL_AUTH_LOGOUT, map, HttpRequests.HTTP_POST);
        // 本地退出
        AppManager.getInstance().AppLogout(getAppContext());
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restartApp(); //发生崩溃异常时,重启应用
        }
    };

    // 重启应用
    public void restartApp() {
        Intent intent = new Intent(getAppContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getAppContext().startActivity(intent);
		AppManager.getInstance().AppExit(getAppContext());
		// 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
		android.os.Process.killProcess(android.os.Process.myPid());
    }

}
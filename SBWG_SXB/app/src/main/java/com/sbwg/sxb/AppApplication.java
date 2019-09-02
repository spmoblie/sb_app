package com.sbwg.sxb;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbwg.sxb.config.SharedConfig;
import com.sbwg.sxb.utils.BitmapUtil;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.DeviceUtil;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AppApplication extends Application {

    private static AppApplication spApp = null;
    private static SharedPreferences shared;
    private static RequestOptions showOptions, headOptions;
//	private static PushManager pushManager;

    public static String version_name = ""; //当前版本号
    public static boolean isWXShare = false; //记录是否微信分享

    //必须注册，Android框架调用Application
    @Override
    public void onCreate() {
        super.onCreate();
        spApp = this;
        shared = getSharedPreferences();
//		pushManager = PushManager.getInstance();
//		// 初始化推送服务SDK
//		pushManager.initPushService();
//		// 初始化应用统计SDK
//		MobclickAgent.setDebugMode(!AppConfig.IS_PUBLISH); //设置调试模式
//		// 设置是否对日志信息进行加密, 默认false(不加密).
//		MobclickAgent.enableEncrypt(true);
//		// 禁止默认的页面统计方式，在onResume()和onPause()手动添加代码统计;
//		MobclickAgent.openActivityDurationTrack(false);
//		MobclickAgent.setScenarioType(spApp, MobclickAgent.EScenarioType.E_UM_NORMAL);

        Editor editor = shared.edit();
        // 获取手机型号及屏幕的宽高
        int screenWidth = DeviceUtil.getDeviceWidth(spApp);
        int screenHeight = DeviceUtil.getDeviceHeight(spApp);
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

        // Facebook SDK初始化
        //FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public static synchronized AppApplication getInstance() {
        return spApp;
    }

    public static Context getAppContext() {
        return spApp.getApplicationContext();
    }

    public static SharedPreferences getSharedPreferences() {
        if (shared == null) {
            shared = new SharedConfig(spApp).GetConfig();
        }
        return shared;
    }

    /**
     * 全局展示图片加载器
     */
    public static RequestOptions getShowOpeions() {
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
    public static RequestOptions getHeadOpeions() {
        if (headOptions == null) {
            headOptions = new RequestOptions()
                    .placeholder(R.drawable.icon_default_head) //图片加载出来前，显示的图片
                    .fallback(R.drawable.icon_default_head) //url为空的时候,显示的图片
                    .error(R.drawable.icon_default_head); //图片加载失败后，显示的图片
        }
        return headOptions;
    }

    /**
     * 清除联网加载数据控制符的缓存
     */
    public void clearSharedLoadSVData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//				SortDBService.getInstance(spApp).deleteAll(); //清空数据库
//				clearGlideCache(); //清除图片缓存
//				CleanDataManager.cleanAppTemporaryData(spApp); //清除临时缓存
//				CleanDataManager.cleanCustomCache(AppConfig.SAVE_PATH_MEDIA_DICE); //清除视频缓存
            }
        }).start();
        shared.edit().putBoolean(AppConfig.KEY_LOAD_SORT_DATA, true).apply();
    }

    /**
     * 设置App字体不随系统字体变化
     */
    public static void initDisplayMetrics() {
        DisplayMetrics displayMetrics = spApp.getResources().getDisplayMetrics();
        displayMetrics.scaledDensity = displayMetrics.density;
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
     * 保存图片对象到指定文件并通知相册更新相片
     */
    public static void saveBitmapFile(Bitmap bm, File file, int compress) {
        if (bm == null || file == null) {
            CommonTools.showToast(spApp.getResources().getString(R.string.photo_show_save_fail), 2000);
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
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        spApp.sendBroadcast(intent);
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
    public static void onPageStart(Activity activity, String pageName) {
//        if (activity != null) {
//            MobclickAgent.onResume(activity);
//        }
//        MobclickAgent.onPageStart(pageName);
    }

    /**
     * 应用数据统计之页面关闭
     */
    public static void onPageEnd(Context ctx, String pageName) {
//		MobclickAgent.onPageEnd(pageName);
//		MobclickAgent.onPause(ctx);
    }

    /**
     * 推送服务统计应用启动数据
     */
    public static void onPushAppStartData() {
//		pushManager.onPushAppStartData();
    }

    /**
     * 初始化推送服务状态
     */
    public static void onPushDefaultStatus() {
//		pushManager.onPushDefaultStatus();
    }

    /**
     * 设置推送服务的权限
     */
    public static void setPushStatus(boolean isStatus) {
//		pushManager.setPushStatus(isStatus);
    }

    /**
     * 获取推送服务的权限
     */
	public static boolean getPushStatus() {
//		return pushManager.getPushStatus();
		return false;
	}

    /**
     * 注册或注销用户信息至推送服务
     */
    public static void onPushRegister(boolean isRegister) {
//		if (isRegister) {
//			pushManager.registerPush();
//		} else {
//			pushManager.unregisterPush();
//		}
    }

    /**
     * App注销登出统一入口
     */
    public static void AppLogout(boolean isSend) {
        AppManager.getInstance().AppLogout(spApp);
        if (isSend) {
            //通知服务器登出
        }
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restartApp(); //发生崩溃异常时,重启应用
        }
    };

    // 重启应用
    public void restartApp() {
        /*Intent intent = new Intent(spApp, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		spApp.startActivity(intent);
		AppManager.getInstance().AppExit(spApp);
		// 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
		android.os.Process.killProcess(android.os.Process.myPid());*/
    }

}
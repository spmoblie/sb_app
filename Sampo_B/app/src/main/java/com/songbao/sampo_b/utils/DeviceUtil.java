package com.songbao.sampo_b.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.songbao.sampo_b.AppApplication;

import java.util.List;
import java.util.Locale;

/**
 * 手机硬件配置信息工具类
 */
public class DeviceUtil {
	
	/**
	 * 获得系统版本ID
	 */
	protected String getClientOs() 
	{
		return android.os.Build.ID;
	}
	
	/**
	 * 获得系统版本号
	 */
	protected String getClientOsVer() 
	{
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获得系统语言包
	 */
	protected String getLanguage() 
	{
		return Locale.getDefault().getLanguage();
	}
	
	/**
     * 获得系统国家区域
     */
    protected String getCountry() 
    {
		return Locale.getDefault().getCountry();
	}
	
	/**
	 * 获取手机型号
	 */
	public static String getModel()
	{
		return android.os.Build.MODEL;
	}

	/**
	 * 获取手机屏幕宽度
	 */
	public static int getDeviceWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (manager != null) {
			Display display = manager.getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			return point.x;
		} else {
			return 0;
		}
	}

	/**
	 * 获取手机屏幕高度
	 */
	public static int getDeviceHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (manager != null) {
			Display display = manager.getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			return point.y;
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取屏幕状态栏高度
	 */
	public static int getStatusBarHeight(Activity ctx){
		if (ctx != null) {
			Rect rect= new Rect();
			ctx.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);   
			return rect.top;
		}else {
			return 0;
		}
	}

	/**
	 * 获取手机屏幕物理密度
	 */
	public static float getDeviceDensity(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.density;
	}

	/**
	 * 判断是否为平板
	 *
	 * @return
	 */
	public static boolean isPad(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (manager != null) {
			Display display = manager.getDefaultDisplay();
			Point point = new Point();
			display.getSize(point);
			float screenWidth = point.x;
			float screenHeight = point.y;
			DisplayMetrics dm = new DisplayMetrics();
			display.getMetrics(dm);
			double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
			double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
			// 屏幕尺寸
			double screenInches = Math.sqrt(x + y);
			// 大于6尺寸则为Pad
			return screenInches >= 6.0;
		}
		return false;
	}

	/**
	 * 获得当前程序版本信息
	 */
	public static String getVersionName(Context context) {
		PackageManager manager = context.getPackageManager();
		try
		{
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			if (info != null)
				return info.versionName;
		}
		catch (NameNotFoundException e)
		{
			ExceptionUtil.handle(e);
		}
		return null;

	}
	
	/**
	 * 检测是否安装程序
	 * @param uri - package name
	 */
	public static boolean checkAppInstalled(Context ctx, String uri){
        try {
            ctx.getPackageManager().getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (NameNotFoundException e) {
            return false;
        }
    }

	/**
	 * 判断应用是否已经启动
	 * @param context 一个context
	 * @param packageName 要判断应用的包名
	 */
	public static boolean isAppAlive(Context context, String packageName){
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		if (activityManager != null) {
			List<ActivityManager.RunningAppProcessInfo> processInfo = activityManager.getRunningAppProcesses();
			for(int i = 0; i < processInfo.size(); i++){
				if(processInfo.get(i).processName.equals(packageName)){
					LogUtil.i("NotificationLaunch", String.format("the %s is running, isAppAlive return true", packageName));
					return true;
				}
			}
		}
		LogUtil.i("NotificationLaunch", String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

	public static boolean isLocationServiceAllowed() {
		LocationManager locationManager = (LocationManager) AppApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
		return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}
	
}

package com.songbao.sampo_b;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.songbao.sampo_b.utils.CleanDataManager;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.UserManager;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

/**
 * 负责记录Activity的启动情况并控制Activity的退出
 */
public class AppManager {
	
	private static Stack<WeakReference<Activity>> mActivityStack;
	private static AppManager mAppManager;

	private AppManager() {

	}

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (mAppManager == null) {
			synchronized (AppManager.class) {
				if (mAppManager == null) {
					mAppManager = new AppManager();
				}
			}
		}
		return mAppManager;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<>();
		}
		mActivityStack.add(new WeakReference<>(activity));
	}

	/**
	 * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
	 */
	public void checkWeakReference() {
		if (mActivityStack != null) {
			// 使用迭代器进行安全删除
			for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
				WeakReference<Activity> activityReference = it.next();
				Activity temp = activityReference.get();
				if (temp == null) {
					it.remove();
				}
			}
		}
	}

	/**
	 * 获取当前Activity（栈中最后一个压入的）
	 */
	public Activity getTopActivity() {
		checkWeakReference();
		if (mActivityStack != null && !mActivityStack.isEmpty()) {
			return mActivityStack.lastElement().get();
		}
		return null;
	}
	/**
	 * 关闭当前Activity（栈中最后一个压入的）
	 */
	public void closeTopActivity() {
		Activity activity = getTopActivity();
		if (activity != null) {
			finishActivity(activity);
		}
	}

	/**
	 * 关闭指定的Activity
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null && mActivityStack != null) {
			// 使用迭代器进行安全删除
			for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
				WeakReference<Activity> activityReference = it.next();
				Activity temp = activityReference.get();
				// 清理掉已经释放的activity
				if (temp == null) {
					it.remove();
					continue;
				}
				if (temp == activity) {
					it.remove();
				}
			}
			activity.finish();
		}
	}

	/**
	 * 关闭指定类名的所有Activity
	 */
	public void finishActivity(Class<?> cls) {
		if (mActivityStack != null) {
			// 使用迭代器进行安全删除
			for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
				WeakReference<Activity> activityReference = it.next();
				Activity activity = activityReference.get();
				// 清理掉已经释放的activity
				if (activity == null) {
					it.remove();
					continue;
				}
				if (activity.getClass().equals(cls)) {
					it.remove();
					activity.finish();
				}
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		if (mActivityStack != null) {
			for (WeakReference<Activity> activityReference : mActivityStack) {
				Activity activity = activityReference.get();
				if (activity != null) {
					activity.finish();
				}
			}
			mActivityStack.clear();
		}
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			// 清除临时缓存
			CleanDataManager.cleanAppTemporaryData(context);
			// 关闭所有Activity
			finishAllActivity();
			// 退出JVM,释放所占内存资源,0表示正常退出
			System.exit(0);
			// 从系统中kill掉应用程序
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		}
	}
	
	/**
	 * App注销登录
	 */
	public void AppLogout(Context ctx) {
		clearAllCookie(ctx);
		// 清除临时缓存
		CleanDataManager.cleanAppTemporaryData(ctx);
		// 清除用户数据
		UserManager.getInstance().clearUserLoginInfo(ctx);
	}
	
	/**
	 * 清除所有缓存Cookie
	 */
	public void clearAllCookie(Context ctx) {
		CleanDataManager.cleanCustomCache(AppConfig.SAVE_PATH_TXT_SAVE + AppConfig.cookiesFileName);
		CookieSyncManager.createInstance(ctx);
		CookieSyncManager.getInstance().startSync(); 
        CookieManager.getInstance().removeAllCookie();
	}

}

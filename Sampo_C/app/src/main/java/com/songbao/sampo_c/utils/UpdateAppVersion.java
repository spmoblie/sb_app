package com.songbao.sampo_c.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.dialog.AppVersionDialog;
import com.songbao.sampo_c.dialog.DialogManager;
import com.songbao.sampo_c.dialog.LoadDialog;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.UpdateVersionEntity;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.ResponseBody;
import rx.Observer;


public class UpdateAppVersion {

	private static UpdateAppVersion instance;
	private WeakReference<Context> weakContext;
	private SharedPreferences shared;
	private DialogManager dm;
	private String curVersionName;
	private long curVersionCode;
	private boolean isHomeIndex = false;

	private UpdateAppVersion(Context context, boolean isHome) {
		weakContext = new WeakReference<>(context);
		shared = AppApplication.getSharedPreferences();
		dm = DialogManager.getInstance(context);
		isHomeIndex = isHome;
		startCheckAppVersion();
	}

	public static UpdateAppVersion getInstance(Context context, boolean isHomeIndex) {
		if (instance == null) {
			synchronized (UpdateAppVersion.class) {
				if (instance == null){
					instance = new UpdateAppVersion(context, isHomeIndex);
				}
			}
		}
		return instance;
	}

	public static void clearInstance() {
		instance = null;
		DialogManager.clearInstance();
	}

	private void startCheckAppVersion() {
		getAppVersionInfo();
		// 检测网络状态
		if (NetworkUtil.networkStateTips()) {
			startCheckVersion();
		} else {
			CommonTools.showToast(weakContext.get().getString(R.string.network_fault));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					clearInstance();
				}
			}, 2000);
		}
	}

	/**
	 * 获取App当前版本信息
	 */
	private void getAppVersionInfo() {
		try {
			PackageManager pm = weakContext.get().getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(weakContext.get().getPackageName(), PackageManager.GET_CONFIGURATIONS);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
				curVersionCode = packageInfo.versionCode;
			} else {
				curVersionCode = packageInfo.getLongVersionCode();
			}
			curVersionName = packageInfo.versionName;
			AppApplication.version_name = curVersionName;
		} catch (NameNotFoundException e) {
			ExceptionUtil.handle(e);
			clearInstance();
		}
	}

	/**
	 * 开启版本校验任务
	 */
	private void startCheckVersion() {
		startAnimation();
		HashMap<String, Object> map = new HashMap<>();
		map.put("version", curVersionName);
		HttpRequests.getInstance()
				.loadData(AppConfig.BASE_TYPE, AppConfig.URL_AUTH_VERSION, map, HttpRequests.HTTP_GET)
				.subscribe(new Observer<ResponseBody>() {
					@Override
					public void onNext(ResponseBody body) {
						try {
							BaseEntity<UpdateVersionEntity> baseEn = JsonUtils.checkVersionUpdate(new JSONObject(body.string()));
							if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
								handlerVersionAnalyse(baseEn.getData());
							} else {
								CommonTools.showToast(baseEn.getErrMsg());
							}
						} catch (Exception e) {
							loadFailHandle();
							ExceptionUtil.handle(e);
						}
						LogUtil.i(LogUtil.LOG_HTTP, "onNext");
					}

					@Override
					public void onError(Throwable throwable) {
						loadFailHandle();
						LogUtil.i(LogUtil.LOG_HTTP, "error message : " + throwable.getMessage());
					}

					@Override
					public void onCompleted() {
						// 结束处理
						stopAnimation();
						LogUtil.i(LogUtil.LOG_HTTP, "onCompleted");
					}
				});
	}

	/**
	 * 网络加载失败
	 */
	private void loadFailHandle() {
		stopAnimation();
		CommonTools.showToast(AppApplication.getAppContext().getString(R.string.toast_server_busy));
	}

	/**
	 * 显示缓冲动画
	 */
	protected void startAnimation() {
		if (!isHomeIndex) { //非首页
			LoadDialog.show(weakContext.get());
		}
	}

	/**
	 * 停止缓冲动画
	 */
	private void stopAnimation() {
		if (!isHomeIndex) {
			LoadDialog.hidden();
		}
		clearInstance();
	}

	/**
	 * 版本分析处理
	 */
	private void handlerVersionAnalyse(UpdateVersionEntity uvEn) {
		AppVersionDialog appDialog = new AppVersionDialog(weakContext.get(), dm);
		if (uvEn != null) {
			String version = uvEn.getVersion();
			String description = uvEn.getDescription();
			String address = uvEn.getUrl();
			boolean isForce = uvEn.isForce();
			boolean isUpdate;
			if (version.contains(".")) { //检查是否需要更新
				isUpdate = compareVersion(version);
			} else {
				isUpdate = compareVersionCode(version);
			}
			if (isUpdate) { //检测到新版本
				if (isForce) { //是否强制更新
					appDialog.updateNewVersion(address, description, true);
				} else {
					long newTime = System.currentTimeMillis();
					long oldTime = shared.getLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, 0);
					if (newTime - oldTime > 86400000) { //设置首页检测版本的频率为一天
						appDialog.updateNewVersion(address, description, false);
						shared.edit().putLong(AppConfig.KEY_UPDATE_VERSION_LAST_TIME, newTime).apply();
					} else {
						if (!isHomeIndex) {
							appDialog.updateNewVersion(address, description, false);
						}
					}
				}
			} else {
				if (!isHomeIndex) {
					appDialog.showStatusDialog(weakContext.get().getString(R.string.dialog_version_new), false); //提示已是最新版本
				}
			}
		} else {
			if (!isHomeIndex) {
				appDialog.showStatusDialog(weakContext.get().getString(R.string.toast_server_busy), false);
			}
		}
	}

	/**
	 * 比较纯数字版本号判定是否需要更新
	 */
	private boolean compareVersionCode(String minVersion) {
		if (curVersionCode == 0) {
			getAppVersionInfo();
		}
		int newVersion = StringUtil.getInteger(minVersion);
		return curVersionCode < newVersion;
	}

	/**
	 * 比较常规版本号判定是否需要更新
	 */
	private boolean compareVersion(String minVersion) {
		boolean isUpdate = false;
		if (StringUtil.isNull(minVersion)){
			return false;
		}
		if (StringUtil.isNull(curVersionName)) {
			getAppVersionInfo();
		}
		String[] minValues = minVersion.split("\\.");
		int minLength = minValues.length;
		String[] curValues = curVersionName.split("\\.");
		int curLength = curValues.length;
		if (minLength > 1 && curLength > 1) {
			int minFirst = Integer.parseInt(minValues[0]);
			int curFirst = Integer.parseInt(curValues[0]);
			if (curFirst < minFirst) {
				isUpdate = true; //版本号第一位数小于时更新
			} else if (curFirst == minFirst) {
				int minSecond = Integer.parseInt(minValues[1]);
				int curSecond = Integer.parseInt(curValues[1]);
				if (curSecond < minSecond) {
					isUpdate = true; //版本号第二位数小于时更新
				} else if (curSecond == minSecond) {
					int minThree = 0;
					int curThree = 0;
					if (curLength > 2) {
						curThree = Integer.parseInt(curValues[2]);
					}
					if (minLength > 2) {
						minThree = Integer.parseInt(minValues[2]);
					}
					if (curThree < minThree) {
						isUpdate = true; //版本号第三位数小于时更新
					} 
				}
			}
		}
		return isUpdate;
	}

}

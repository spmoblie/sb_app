package com.songbao.sampo_b.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.receiver.umeng.MyUmengMessageHandler;
import com.songbao.sampo_b.receiver.umeng.MyUmengNotificationClickHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

public class PushManager {

	private static final String TAG = "PushManager";
	private static final String ALIAS_TYPE = "SongBao";

	private static PushManager instance = null;
	private SharedPreferences shared;
	private Context mContext;
	private PushAgent mPushAgent;
	private UserManager mUserManager;

	public static PushManager getInstance(){
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private static synchronized void syncInit(){
		if (instance == null) {
			instance = new PushManager();
		}
	}

	private PushManager(){
		mContext = AppApplication.getAppContext();
		shared = AppApplication.getSharedPreferences();
		mUserManager = UserManager.getInstance();
	}

	/**
	 * 初始化推送服务
	 */
	public void initPushService() {
		// 初始化SDK
		UMConfigure.init(mContext, UMConfigure.DEVICE_TYPE_PHONE, AppConfig.UM_MESSAGE_SECRET);
		// Log开关设置
		UMConfigure.setLogEnabled(!AppConfig.IS_PUBLISH);

		mPushAgent = PushAgent.getInstance(mContext);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {
			@Override
			public void onSuccess(String deviceToken) {
				mUserManager.saveDeviceToken(deviceToken);
				LogUtil.i(TAG, "注册成功：deviceToken：-------->  " + deviceToken);
			}
			@Override
			public void onFailure(String s, String s1) {
				LogUtil.i(TAG, "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
			}
		});
		// 自定义消息的处理
		mPushAgent.setMessageHandler(new MyUmengMessageHandler());
		// 自定义通知的处理
		mPushAgent.setNotificationClickHandler(new MyUmengNotificationClickHandler());
	}

	/**
	 * 统计应用启动数据
	 * 如果不调用此方法，不仅会导致按照"几天不活跃"条件来推送失效，
	 * 还将导致广播发送不成功以及设备描述红色等问题发生。
	 */
	public void onPushAppStartData() {
		mPushAgent.onAppStart();
	}

	/**
	 * 获取推送服务状态
	 */
	public Boolean getPushStatus() {
		return shared.getBoolean(AppConfig.KEY_PUSH_STATUS, true);
	}

	/**
	 * 初始化推送服务状态(开启或关闭)
	 */
	public void onPushDefaultStatus() {
		if (getPushStatus()) {
			startPushService();
		} else {
			closePushService();
		}
	}

	/**
	 * 设置推送服务的权限
	 */
	public void setPushStatus(boolean status) {
		if (status) {
			shared.edit().putBoolean(AppConfig.KEY_PUSH_STATUS, true).apply();
			startPushService(); //先修改状态标记再开启、注册
		} else {
			closePushService(); //先注销、关闭再修改状态标记
			shared.edit().putBoolean(AppConfig.KEY_PUSH_STATUS, false).apply();
		}
	}

	/**
	 * 开启推送服务
	 */
	private void startPushService() {
		// 开启推送服务
		mPushAgent.enable(new IUmengCallback() {
			@Override
			public void onSuccess() {
				LogUtil.i(TAG, "Push Service 开启成功");
			}

			@Override
			public void onFailure(String s, String s1) {
				LogUtil.i(TAG, "Push Service 开启失败");
			}
		});
		// 推送服务异步开启，所以需要延时注册
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// 注册推送相关
				registerPush();
			}
		}, 5000);
	}

	/**
	 * 关闭推送服务
	 */
	private void closePushService() {
		// 注销推送相关
		unregisterPush();
		// 关闭推送服务
		mPushAgent.disable(new IUmengCallback() {
			@Override
			public void onSuccess() {
				LogUtil.i(TAG, "Push Service 关闭成功");
			}

			@Override
			public void onFailure(String s, String s1) {
				LogUtil.i(TAG, "Push Service 关闭失败");
			}
		});
	}

	/**
	 * 注册账户信息
	 */
	public void registerPush(){
		if (getPushStatus() && mUserManager.checkIsLogin()) {
			try {
				// 账号统计登入
				MobclickAgent.onProfileSignIn(mUserManager.getUserId());
				// 添加Alias标签
				mPushAgent.addAlias(mUserManager.getUserId(), ALIAS_TYPE, new UTrack.ICallBack() {
					@Override
					public void onMessage(boolean b, String s) {
						LogUtil.i(TAG, "b = " + b + " s = " + s);
					}
				});
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}

	/**
	 * 注销账户信息
	 */
	public void unregisterPush(){
		if (getPushStatus() && mUserManager.checkIsLogin()) {
			try {
				// 账号统计登出
				MobclickAgent.onProfileSignOff();
				// 移除Alias标签
				mPushAgent.deleteAlias(mUserManager.getUserId(), ALIAS_TYPE, new UTrack.ICallBack() {
					@Override
					public void onMessage(boolean b, String s) {
						LogUtil.i(TAG, "b = " + b + " s = " + s);
					}
				});
			} catch (Exception e) {
				ExceptionUtil.handle(e);
			}
		}
	}
	
}

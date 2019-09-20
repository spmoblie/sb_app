package com.sbwg.sxb.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.utils.DeviceUtil;
import com.sbwg.sxb.utils.LogUtil;

/**
 * App首页欢迎界面
 */
public class SplashActivity extends BaseActivity {

	public static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 隐藏父类组件
		setHeadVisibility(View.GONE);

		// 初始化推送服务状态(开启或关闭)
		AppApplication.onPushDefaultStatus();
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		// 检查授权
		if (checkPermission()) {
			// 延迟跳转页面
			goHomeActivity();
		}

		super.onResume();
	}

	private void goHomeActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				int statusHeight = DeviceUtil.getStatusBarHeight(SplashActivity.this);
				editor.putInt(AppConfig.KEY_STATUS_HEIGHT, statusHeight).apply();

				openActivity(MainActivity.class);
				finish();
				// 设置Activity的切换效果
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		}, 1000);
	}

	@Override
	protected void onPause() {
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void permissionsResultCallback(boolean result) {
		if (result) {
			// 延迟跳转页面
			goHomeActivity();
		} else {
			finish();
		}
	}
}

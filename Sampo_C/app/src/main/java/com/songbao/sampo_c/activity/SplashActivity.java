package com.songbao.sampo_c.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.utils.DeviceUtil;
import com.songbao.sampo_c.utils.LogUtil;

import java.lang.ref.WeakReference;

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
		//AppApplication.onPushDefaultStatus();
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		// 检查授权
		if (checkPermission()) {
			// 检测SD存储卡
			if (AppApplication.getAppContext().getExternalFilesDir(null) != null) {
				goHomeActivity();
			} else {
				showErrorDialog(getString(R.string.dialog_error_card_null), false, new MyHandler(this));
			}
		}

		super.onResume();
	}

	/**
	 * 延迟跳转到首页
	 */
	private void goHomeActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				AppApplication.status_height = DeviceUtil.getStatusBarHeight(SplashActivity.this);

				openActivity(MainActivity.class);
				finish();
				// 设置Activity的切换效果
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		}, 1000);
	}

	@Override
	protected void onPause() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onPause");
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

	static class MyHandler extends Handler {

		WeakReference<SplashActivity> mActivity;

		MyHandler(SplashActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SplashActivity theActivity = mActivity.get();
			if (theActivity != null) {
				switch (msg.what) {
					case AppConfig.DIALOG_CLICK_OK:
						theActivity.finish();
						break;
				}
			}
		}
	}
}

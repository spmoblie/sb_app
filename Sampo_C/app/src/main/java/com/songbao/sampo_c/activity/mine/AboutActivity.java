package com.songbao.sampo_c.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.utils.LogUtil;

import java.util.Calendar;

import butterknife.BindView;


public class AboutActivity extends BaseActivity implements OnClickListener {

	String TAG = AboutActivity.class.getSimpleName();

	@BindView(R.id.about_rl_user_agreement)
	RelativeLayout rl_user_agreement;

	@BindView(R.id.about_rl_privacy_policy)
	RelativeLayout rl_privacy_policy;

	@BindView(R.id.about_tv_version)
	TextView tv_version;

	@BindView(R.id.about_tv_copyright)
	TextView tv_copyright;

	private String contentStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		initView();
	}

	private void initView() {
		setTitle(R.string.setting_about_us);

		rl_user_agreement.setOnClickListener(this);
		rl_privacy_policy.setOnClickListener(this);

		tv_version.setText(getString(R.string.setting_version_show, AppApplication.version_name));
		tv_copyright.setText(getString(R.string.setting_copyright, Calendar.getInstance().get(Calendar.YEAR)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.about_rl_user_agreement:
				openWebViewActivity(getString(R.string.setting_user_agreement), AppConfig.USER_AGREEMENT);
				break;
			case R.id.about_rl_privacy_policy:
				openWebViewActivity(getString(R.string.setting_privacy_policy), AppConfig.PRIVACY_POLICY);
				break;
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
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

}

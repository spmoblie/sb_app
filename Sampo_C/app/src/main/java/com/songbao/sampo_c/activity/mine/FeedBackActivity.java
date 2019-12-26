package com.songbao.sampo_c.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;

import butterknife.BindView;


public class FeedBackActivity extends BaseActivity implements OnClickListener {

	String TAG = FeedBackActivity.class.getSimpleName();

	@BindView(R.id.feed_back_et_content)
	EditText et_content;

	@BindView(R.id.feed_back_btn_submit)
	Button btn_submit;

	private String contentStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);

		initView();
	}

	private void initView() {
		setTitle(R.string.setting_feedback);
		btn_submit.setOnClickListener(this);
	}

	private boolean checkData() {
		contentStr = et_content.getText().toString();
		// 校验非空
		if (StringUtil.isNull(contentStr)) {
			CommonTools.showToast(getString(R.string.setting_input_error_feedback), Toast.LENGTH_SHORT);
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_back_btn_submit:
			if (checkData()) {
				postData();
			}
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

	private void postData() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CommonTools.showToast(getString(R.string.setting_feedback_post_ok));
				stopAnimation();
			}
		}, AppConfig.LOADING_TIME);
	}
	
}

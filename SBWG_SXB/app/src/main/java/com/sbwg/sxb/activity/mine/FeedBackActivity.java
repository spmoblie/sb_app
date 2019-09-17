package com.sbwg.sxb.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;

import butterknife.BindView;


public class FeedBackActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = FeedBackActivity.class.getSimpleName();

	@BindView(R.id.feed_back_et_content)
	EditText et_feed_cotent;

	@BindView(R.id.feed_back_btn_submit)
	Button btn_submit;

	private String cotentStr;
	
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

	private void backSubmit() {
		cotentStr = et_feed_cotent.getText().toString();
		// 输入非空
		if (cotentStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.setting_input_error_feedback), 1000);
			return;
		}
		postResetData();
	}
	
	private void postResetData() {
		startAnimation();
//		request(AppConfig.REQUEST_SV_POST_FEED_BACK_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feed_back_btn_submit:
			backSubmit();
			break;
		}
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);

		super.onResume();
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
	
}

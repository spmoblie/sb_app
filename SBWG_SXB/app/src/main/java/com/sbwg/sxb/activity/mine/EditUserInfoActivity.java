package com.sbwg.sxb.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;


public class EditUserInfoActivity extends BaseActivity {
	
	private static final String TAG = "EditUserInfoActivity";
	
	private EditText et_content;
	private ImageView iv_clear;
	private TextView tv_reminder;
	
	private boolean isChange = false;
	private boolean isPost = true;
	private String titleStr, showStr, hintStr, reminderStr, changeTypeKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		Intent intent = getIntent();
		titleStr = intent.getExtras().getString("titleStr");
		showStr = intent.getExtras().getString("showStr");
		hintStr = intent.getExtras().getString("hintStr");
		reminderStr = intent.getExtras().getString("reminderStr");
		changeTypeKey = intent.getExtras().getString("changeTypeKey");
		
		findViewById();
		initView();
	}
	
	private void findViewById() {
		et_content = findViewById(R.id.edit_info_et_content);
		iv_clear = findViewById(R.id.edit_info_iv_clear);
		tv_reminder = findViewById(R.id.edit_info_tv_reminder);
	}

	private void initView() {
		setTitle(titleStr);
		setRightViewText(getString(R.string.save));
		
		et_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					iv_clear.setVisibility(View.GONE);
				}else {
					iv_clear.setVisibility(View.VISIBLE);
				}
			}
		});
		et_content.setHint(hintStr);
		et_content.setText(showStr);
		if (!StringUtil.isNull(showStr)) {
			et_content.setSelection(showStr.length());
		}
		if (!StringUtil.isNull(reminderStr)) {
			tv_reminder.setText(reminderStr);
		}
		
		iv_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showStr = "";
				et_content.setText(showStr);
			}
		});
	}
	
	@Override
	public void OnListenerLeft() {
		super.OnListenerLeft();
		finish();
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		showStr = et_content.getText().toString();
		if (showStr.isEmpty()) {
			CommonTools.showToast(hintStr, 1000);
			return;
		}
		if ("email".equals(changeTypeKey) && !StringUtil.isEmail(showStr)) {
			CommonTools.showToast(getString(R.string.login_email_format_error), 1000);
			return;
		}
		postChangeContent();
	}

	private void postChangeContent() {
		if (isPost) {
//			request(AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE);
//			isPost = false;
			//Evan临时修改
			isChange = true;
			finish();
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
		LogUtil.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		if (isChange && !StringUtil.isNull(showStr)) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_CHANGE_USER_CONTENT, showStr);
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}
	
}

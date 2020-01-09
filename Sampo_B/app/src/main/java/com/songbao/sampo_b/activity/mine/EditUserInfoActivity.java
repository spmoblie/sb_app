package com.songbao.sampo_b.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonUtils;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;


public class EditUserInfoActivity extends BaseActivity {

	String TAG = EditUserInfoActivity.class.getSimpleName();

	public static final String KEY_TITLE = "titleStr";
	public static final String KEY_SHOW = "showStr";
	public static final String KEY_HINT = "hintStr";
	public static final String KEY_TIPS = "tipsStr";
	public static final String KEY_USER = "userKey";

	@BindView(R.id.edit_info_et_content)
	EditText et_content;

	@BindView(R.id.edit_info_iv_clear)
	ImageView iv_clear;

	@BindView(R.id.edit_info_tv_reminder)
	TextView tv_reminder;
	
	private boolean isChange = false;
	private boolean isPost = true;
	private String titleStr, showStr, hintStr, tipsStr, userKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);

		Intent intent = getIntent();
		titleStr = intent.getStringExtra(KEY_TITLE);
		showStr = intent.getStringExtra(KEY_SHOW);
		hintStr = intent.getStringExtra(KEY_HINT);
		tipsStr = intent.getStringExtra(KEY_TIPS);
		userKey = intent.getStringExtra(KEY_USER);
		
		initView();
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
				if (StringUtil.isNull(s.toString())) {
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
		if (!StringUtil.isNull(tipsStr)) {
			tv_reminder.setText(tipsStr);
		}
		
		iv_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showStr = "";
				et_content.setText(showStr);
			}
		});
	}

	private boolean checkData() {
		showStr = et_content.getText().toString();
		if (StringUtil.isNull(showStr)) {
			CommonTools.showToast(hintStr);
			return false;
		}
		if ("email".equals(userKey) && !StringUtil.isEmail(showStr)) {
			CommonTools.showToast(getString(R.string.login_email_format_error));
			return false;
		}
		return true;
	}
	
	@Override
	public void OnListenerLeft() {
		super.OnListenerLeft();
		finish();
	}
	
	@Override
	public void OnListenerRight() {
		super.OnListenerRight();
		if (checkData()) {
			if (isPost) {
				isPost = false;
				saveUserInfo();
			} else {
				CommonTools.showToast(getString(R.string.loading_process, getString(R.string.save)));
			}
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
	
	@Override
	public void finish() {
		if (isChange && !StringUtil.isNull(showStr)) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(AppConfig.ACTIVITY_KEY_USER_INFO, showStr);
			setResult(RESULT_OK, returnIntent);
		}
		super.finish();
	}

	/**
	 * 修改用户资料
	 */
	private void saveUserInfo() {
		HashMap<String, String> map = new HashMap<>();
		map.put(userKey, showStr);
		loadSVData(AppConfig.URL_USER_SAVE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_USER_SAVE);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_USER_SAVE:
					isPost = true;
					baseEn = JsonUtils.getUploadResult(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						AppApplication.updateUserData(true);
						isChange = true;
						finish();
					} else {
						handleErrorCode(baseEn);
					}
					break;
			}
		} catch (Exception e) {
			loadFailHandle();
			ExceptionUtil.handle(e);
		}
	}

	@Override
	protected void loadFailHandle() {
		super.loadFailHandle();
		isPost = true;
		handleErrorCode(null);
	}
	
}

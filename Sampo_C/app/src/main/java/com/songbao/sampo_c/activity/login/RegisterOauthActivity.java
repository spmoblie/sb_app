package com.songbao.sampo_c.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.AppManager;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.UserInfoEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonLogin;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;


/**
 * 第三方账号绑定页面
 */
public class RegisterOauthActivity extends BaseActivity implements OnClickListener {
	
	String TAG = RegisterOauthActivity.class.getSimpleName();

	@BindView(R.id.register_oauth_et_phone)
	EditText et_phone;

	@BindView(R.id.register_oauth_et_code)
	EditText et_code;

	@BindView(R.id.register_oauth_iv_phone_clear)
	ImageView iv_phone_clear;

	@BindView(R.id.register_oauth_tv_phone_error)
	TextView tv_phone_error;

	@BindView(R.id.register_oauth_tv_verify_code)
	TextView tv_verify_code;

	@BindView(R.id.register_oauth_btn_oauth)
	Button btn_oauth;
	
	private UserInfoEntity infoEn;
	private int send_number = 0;
	private boolean isPhone_Ok = false;
	private boolean send_Again = true;
	private boolean isSendCode = false;
	private boolean isOauth_Ok = false;
	private String phoneStr, codeStr, loginType, uidStr, nickname, avatar, gender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_oauth);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		infoEn = (UserInfoEntity) getIntent().getSerializableExtra("oauthEn");
		
		initView();
	}

	private void initView() {
		setTitle(R.string.login_bound);

		btn_oauth.setOnClickListener(this);
		iv_phone_clear.setOnClickListener(this);
		tv_verify_code.setOnClickListener(this);

		if (infoEn != null) {
			loginType = infoEn.getUserArea();
			uidStr = infoEn.getUserId();
			nickname = infoEn.getUserNick();
			avatar = infoEn.getUserHead();
			gender = infoEn.getGenderStr(); //属性借用
		}

		initEditText();
	}

	private void initEditText() {
		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s == null || s.length() == 0) return;
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < s.length(); i++) {
					if (i != 3 && i != 8 && s.charAt(i) == ' ') {
						break;
					} else {
						sb.append(s.charAt(i));
						if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
							sb.insert(sb.length() - 1, ' ');
						}
					}
				}
				if (!sb.toString().equals(s.toString())) {
					int index = start + 1;
					if (sb.charAt(start) == ' ') {
						if (before == 0) {
							index++;
						} else {
							index--;
						}
					} else {
						if (before == 1) {
							index--;
						}
					}
					et_phone.setText(sb.toString());
					et_phone.setSelection(index);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setSendCodeState(false);
				setOauthState(false);
				isPhone_Ok = false;
				phoneStr = s.toString();
				if (StringUtil.isNull(phoneStr)) {
					iv_phone_clear.setVisibility(View.GONE);
				}else {
					iv_phone_clear.setVisibility(View.VISIBLE);
					if (phoneStr.length() >= 13) {
						// 号码去空
						if (phoneStr.contains(" ")) {
							phoneStr = phoneStr.replace(" ", "");
						}
						// 校验格式
						if (!StringUtil.isMobileNO(phoneStr)) {
							tv_phone_error.setVisibility(View.VISIBLE);
							tv_phone_error.setText(getString(R.string.login_phone_input_error));
							return;
						} else {
							tv_phone_error.setVisibility(View.GONE);
						}
						isPhone_Ok = true;
						if (isTimeFinish) {
							setSendCodeState(true);
						}
						editTextFocusAndClear(et_code);
					}
				}
			}
		});

		et_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setOauthState(false);
				if (s.toString().length() >= 6) {
					if (isPhone_Ok) {
						setOauthState(true);
					}
				}
			}
		});
	}

	@Override
	protected void onTimerFinish() {
		super.onTimerFinish();
		if (isPhone_Ok) {
			setSendCodeState(true);
		}
		if (send_number >= 3) {
			send_number = 0;
			shared.edit().putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
		}
		if (tv_verify_code != null) {
			tv_verify_code.setText(getString(R.string.login_verify_code_gain));
		}
	}

	private void setOauthState(boolean isState) {
		isOauth_Ok = isState;
		changeViewState(btn_oauth, isOauth_Ok);
	}

	private void setSendCodeState(boolean isState) {
		isSendCode = isState;
		changeViewState(tv_verify_code, isSendCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register_oauth_iv_phone_clear:
				editTextFocusAndClear(et_phone);
				break;
			case R.id.register_oauth_tv_verify_code:
				if (send_number >= 3) {
					CommonTools.showToast(getString(R.string.login_verify_code_send_3));
				}
				if (isSendCode && send_Again) {
					send_Again = false;
					sendMessageAuth();
				}
				break;
			case R.id.register_oauth_btn_oauth:
				if (checkData() && isOauth_Ok) {
					postOauthData();
				}
				break;
		}
	}

	private boolean checkData() {
		// 验证非空
		//phoneStr = et_phone.getText().toString();
		if (StringUtil.isNull(phoneStr)) {
			CommonTools.showToast(getString(R.string.login_phone_input));
			return false;
		}
		// 格式去空
		if (phoneStr.contains(" ")) {
			phoneStr = phoneStr.replace(" ", "");
		}
		// 校验格式
		if (!StringUtil.isMobileNO(phoneStr)) {
			tv_phone_error.setVisibility(View.VISIBLE);
			tv_phone_error.setText(getString(R.string.login_phone_input_error));
			return false;
		} else {
			tv_phone_error.setVisibility(View.GONE);
		}
		// 验证非空
		codeStr = et_code.getText().toString();
		if (StringUtil.isNull(codeStr)) {
			CommonTools.showToast(getString(R.string.login_verify_code_input));
			return false;
		}
		// 校验格式
		if (codeStr.length() < 6) {
			CommonTools.showToast(getString(R.string.login_verify_code_error));
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		// 验证码-倒计时
		initTimeState();

		super.onResume();
	}

	private void initTimeState() {
		long send_last_time = shared.getLong(AppConfig.KEY_SEND_VERIFY_LAST_TIME, 0);
		send_number = shared.getInt(AppConfig.KEY_SEND_VERIFY_NUMBER, 0);
		long intervalTime = AppConfig.SEND_TIME; //间隔1分钟
		if (send_number >= 3) {
			intervalTime = AppConfig.SEND_TIME * 10;  //间隔10分钟
		}
		long quantumTime = System.currentTimeMillis() - send_last_time;
		long surplusTime = intervalTime - quantumTime;
		if (surplusTime > 0) { //剩余时间大于0
			setSendCodeState(false);
			startTimer(tv_verify_code, surplusTime);
		} else {
			if (send_number > 0 && quantumTime >= AppConfig.SEND_TIME * 10) { //每隔10分钟清零
				send_number = 0;
				shared.edit().putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
			}
		}
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

	private void sendMessageAuth() {
		HashMap<String, String> map = new HashMap<>();
		map.put("mobile", phoneStr);
		map.put("type", "2020611");
		loadSVData(AppConfig.URL_AUTH_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_MESSAGE);
	}

	private void postOauthData() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("nickName", nickname);
					jsonObj.put("avatarUrl", avatar);
					jsonObj.put("gender", gender);

					HashMap<String, String> map = new HashMap<>();
					map.put("mobile", phoneStr);
					map.put("code", codeStr);
					map.put("codeType", "2020611");
					map.put("type", loginType);
					map.put("otherId", uidStr);
					map.put("json", jsonObj.toString());
					loadSVData(AppConfig.URL_AUTH_OAUTH_REG, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_OAUTH_REG);
				} catch (JSONException e) {
					ExceptionUtil.handle(e);
				}
			}
		}, AppConfig.LOADING_TIME);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		BaseEntity baseEn;
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_AUTH_MESSAGE:
					send_Again = true;
					baseEn = JsonLogin.getBaseErrorData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						setSendCodeState(false);
						send_number++;
						if (send_number < 3) {
							startTimer(tv_verify_code, AppConfig.SEND_TIME);
						} else {
							startTimer(tv_verify_code, AppConfig.SEND_TIME * 10);
						}
						shared.edit().putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
						shared.edit().putLong(AppConfig.KEY_SEND_VERIFY_LAST_TIME, System.currentTimeMillis()).apply();
						CommonTools.showToast(getString(R.string.login_verify_code_send));
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_AUTH_OAUTH_REG:
					baseEn = JsonLogin.getLoginData(jsonObject);
					if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
						userManager.saveUserLoginSuccess((UserInfoEntity) baseEn.getData());
						closeLoginActivity();
						CommonTools.showToast(getString(R.string.login_oauth_ok));
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
		send_Again = true;
		handleErrorCode(null);
	}
	
}

package com.sbwg.sxb.activity.login;

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

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.ExceptionUtil;
import com.sbwg.sxb.utils.JsonLogin;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

import static com.sbwg.sxb.AppConfig.SEND_TIME;


public class RegisterActivity extends BaseActivity implements OnClickListener {

	String TAG = RegisterActivity.class.getSimpleName();

	@BindView(R.id.register_et_phone)
	EditText et_phone;

	@BindView(R.id.register_et_code)
	EditText et_code;

	@BindView(R.id.register_et_password)
	EditText et_password;

	@BindView(R.id.register_iv_phone_clear)
	ImageView iv_phone_clear;

	@BindView(R.id.register_iv_password_check)
	ImageView iv_password_check;

	@BindView(R.id.register_tv_phone_error)
	TextView tv_phone_error;

	@BindView(R.id.register_tv_password_error)
	TextView tv_password_error;

	@BindView(R.id.register_tv_verify_code)
	TextView tv_verify_code;

	@BindView(R.id.register_btn_register)
	Button btn_register;

	private int send_number = 0;
	private boolean isPhone_Ok = false;
	private boolean isCodes_Ok = false;
	private boolean isPassword_Ok = false;
	private boolean send_Again = true;
	private boolean isSendCode = false;
	private boolean isRegister = false;
	private String phoneStr, codeStr, passwordStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();
	}

	private void initView() {
		setTitle(R.string.login_phone_register);

		iv_phone_clear.setOnClickListener(this);
		tv_verify_code.setOnClickListener(this);
		iv_password_check.setOnClickListener(this);
		iv_password_check.setSelected(false);//设置默认隐藏密码
		btn_register.setOnClickListener(this);

		initEditText();
	}

	private void initEditText() {
		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count == 1) {
					int length = s.toString().length();
					if (length == 3 || length == 8) {
						et_phone.setText(s + " ");
						et_phone.setSelection(et_phone.getText().length());
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setSendCodeState(false);
				setRegisterState(false);
				isPhone_Ok = false;
				phoneStr = s.toString();
				if (phoneStr.isEmpty()) {
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
				setRegisterState(false);
				isCodes_Ok = false;
				if (s.toString().length() >= 6) {
					isCodes_Ok = true;
					if (isPhone_Ok) {
						if (isPassword_Ok) {
							setRegisterState(true);
						} else {
							editTextFocusAndClear(et_password);
						}
					}
				}
			}
		});

		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//获取输入框中的数据
				String edit = et_password.getText().toString();
				//获取过滤特殊字符后的数据
				String stringFilter = StringUtil.stringFilter(edit);
				if (!edit.equals(stringFilter)) {
					et_password.setText(stringFilter);
					et_password.setSelection(et_password.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setRegisterState(false);
				isPassword_Ok = false;
				passwordStr = s.toString();
				if (passwordStr.length() >= 6) {
					// 校验格式
					if (!StringUtil.isPassword(passwordStr)) {
						tv_password_error.setVisibility(View.VISIBLE);
						return;
					} else {
						tv_password_error.setVisibility(View.GONE);
					}
					isPassword_Ok = true;
					if (isPhone_Ok && isCodes_Ok) {
						setRegisterState(true);
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

	private void setRegisterState(boolean isState) {
		isRegister = isState;
		changeViewState(btn_register, isRegister);
	}

	private void setSendCodeState(boolean isState) {
		isSendCode = isState;
		changeViewState(tv_verify_code, isSendCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register_iv_phone_clear:
				editTextFocusAndClear(et_phone);
				break;
			case R.id.register_tv_verify_code:
				if (send_number >= 3) {
					CommonTools.showToast(getString(R.string.login_verify_code_send_3));
				}
				if (isSendCode && send_Again) {
					send_Again = false;
					sendMessageAuth();
				}
				break;
			case R.id.register_iv_password_check:
				if (!iv_password_check.isSelected()) {
					iv_password_check.setSelected(true);
					changeEditTextPassword(et_password, true);
				}else {
					iv_password_check.setSelected(false);
					changeEditTextPassword(et_password, false);
				}
				break;
			case R.id.register_btn_register:
				if (checkData() && isRegister) {
					postRegisterData();
				}
				break;
		}
	}

	private boolean checkData() {
		// 验证非空
		//phoneStr = et_phone.getText().toString();
		if (phoneStr.isEmpty()) {
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
		if (codeStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_verify_code_input));
			return false;
		}
		// 校验格式
		if (codeStr.length() < 6) {
			CommonTools.showToast(getString(R.string.login_verify_code_error));
			return false;
		}
		// 密码非空
		passwordStr = et_password.getText().toString();
		if (passwordStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_password_input));
			return false;
		}
		// 校验格式
		if (passwordStr.length() < 6 || !StringUtil.isPassword(passwordStr)) {
			tv_password_error.setVisibility(View.VISIBLE);
			return false;
		} else {
			tv_password_error.setVisibility(View.GONE);
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
		long intervalTime = SEND_TIME; //间隔1分钟
		if (send_number >= 3) {
			intervalTime = SEND_TIME * 10;  //间隔10分钟
		}
		long quantumTime = System.currentTimeMillis() - send_last_time;
		long surplusTime = intervalTime - quantumTime;
		if (surplusTime > 0) { //剩余时间大于0
			setSendCodeState(false);
			startTimer(tv_verify_code, surplusTime);
		} else {
			if (send_number > 0 && quantumTime >= SEND_TIME * 10) { //每隔10分钟清零
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
		loadSVData(AppConfig.URL_AUTH_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_MESSAGE);
	}

	private void postRegisterData() {
		startAnimation();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<>();
				map.put("mobile", phoneStr);
				map.put("code", codeStr);
				map.put("password", passwordStr);
				loadSVData(AppConfig.URL_AUTH_REGISTER, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_REGISTER);
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
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						setSendCodeState(false);
						send_number++;
						if (send_number < 3) {
							startTimer(tv_verify_code, SEND_TIME);
						} else {
							startTimer(tv_verify_code, SEND_TIME * 10);
						}
						shared.edit().putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
						shared.edit().putLong(AppConfig.KEY_SEND_VERIFY_LAST_TIME, System.currentTimeMillis()).apply();
						CommonTools.showToast(getString(R.string.login_verify_code_send));
					} else {
						handleErrorCode(baseEn);
					}
					break;
				case AppConfig.REQUEST_SV_AUTH_REGISTER:
					baseEn = JsonLogin.getLoginData(jsonObject);
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
						userManager.saveUserLoginSuccess((UserInfoEntity) baseEn.getData());
						CommonTools.showToast(getString(R.string.login_register_ok));
						closeLoginActivity();
					} else
					if (baseEn.getErrno() == AppConfig.ERROR_CODE_PHONE_REGISTERED) {
						tv_phone_error.setVisibility(View.VISIBLE);
						tv_phone_error.setText(getString(R.string.login_phone_registered));
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

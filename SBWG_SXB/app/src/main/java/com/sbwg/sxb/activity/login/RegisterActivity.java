package com.sbwg.sxb.activity.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;

import butterknife.BindView;


public class RegisterActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "RegisterActivity";

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

	@BindView(R.id.register_tv_verify_code)
	TextView tv_verify_code;

	@BindView(R.id.register_btn_register)
	Button btn_register;

	private int send_number = 0;
	private boolean isPhone_Ok = false;
	private boolean isCodes_Ok = false;
	private boolean isPassw_Ok = false;
	private boolean isSendCode = false;
	private boolean isRegister = false;
	private String phoneStr, verifyCodeStr, passwordStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		initView();
	}

	private void initView() {
		setTitle(R.string.login_phone_register);

		iv_phone_clear.setOnClickListener(this);
		tv_verify_code.setOnClickListener(this);
		iv_password_check.setOnClickListener(this);
		btn_register.setOnClickListener(this);

		iv_password_check.setSelected(false);//设置默认隐藏密码

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
							CommonTools.showToast(getString(R.string.login_input_phone_error));
							return;
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
					if (isPhone_Ok && isPassw_Ok) {
						setRegisterState(true);
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
				}
				et_password.setSelection(et_password.length());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setRegisterState(false);
				isPassw_Ok = false;
				passwordStr = s.toString();
				if (passwordStr.length() >= 6) {
					// 校验格式
					if (!StringUtil.isPassword(passwordStr)) {
						CommonTools.showToast(getString(R.string.login_password_form), Toast.LENGTH_LONG);
						return;
					}
					isPassw_Ok = true;
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
			editor.putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
		}
		if (tv_verify_code != null) {
			tv_verify_code.setText(getString(R.string.login_gain_verify_code));
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
				if (isSendCode) {
					setSendCodeState(false);
					send_number++;
					if (send_number < 3) {
						startTimer(tv_verify_code, SEND_TIME);
					} else {
						startTimer(tv_verify_code, SEND_TIME * 10);
						CommonTools.showToast(getString(R.string.login_send_verify_code));
					}
					editor.putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
					editor.putLong(AppConfig.KEY_SEND_VERIFY_LAST_TIME, System.currentTimeMillis()).apply();
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
				// 温馨提示
				if (isPhone_Ok && isCodes_Ok && !isPassw_Ok) {
					if (StringUtil.isNull(passwordStr)) {
						CommonTools.showToast(getString(R.string.login_input_password));
					} else {
						if (passwordStr.length() < 6) {
							CommonTools.showToast(getString(R.string.login_password_type));
						} else {
							CommonTools.showToast(getString(R.string.login_password_form));
						}
					}
				}
				// 提交注册
				if (isRegister) {
					register();
				}
				break;
		}
	}

	private void register() {
		//phoneStr = et_phone.getText().toString();
		// 手机非空
		if (phoneStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_phone));
			return;
		}
		// 手机去空
		if (phoneStr.contains(" ")) {
			phoneStr = phoneStr.replace(" ", "");
		}
		// 校验手机号码格式
		if (!StringUtil.isMobileNO(phoneStr)) {
			CommonTools.showToast(getString(R.string.login_input_phone_error));
			return;
		}
		// 验证码非空
		verifyCodeStr = et_code.getText().toString();
		if (verifyCodeStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_verify_code));
			return;
		}
		// 密码非空
		passwordStr = et_password.getText().toString();
		if (passwordStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.login_input_password));
			return;
		}
		// 校验密码格式
		if (!StringUtil.isPassword(passwordStr)) {
			CommonTools.showToast(getString(R.string.login_password_form), Toast.LENGTH_LONG);
			return;
		}
		postRegisterData();
	}

	private void postRegisterData() {
		startAnimation();
//		request(AppConfig.REQUEST_SV_POST_RESET_PASSWORD_CODE);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
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
				editor.putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
			}
		}
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

}

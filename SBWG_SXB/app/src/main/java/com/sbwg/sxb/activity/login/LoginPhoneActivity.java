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

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.AppManager;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.utils.CommonTools;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.StringUtil;
import com.sbwg.sxb.utils.UserManager;

import butterknife.BindView;


public class LoginPhoneActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "LoginPhoneActivity";
    private static final long SEND_TIME = 60000;

    @BindView(R.id.login_phone_et_phone)
    EditText et_phone;

    @BindView(R.id.login_phone_et_code)
    EditText et_code;

    @BindView(R.id.login_phone_et_password)
    EditText et_password;

    @BindView(R.id.login_iv_phone_clear)
    ImageView iv_phone_clear;

    @BindView(R.id.login_phone_iv_password_check)
    ImageView iv_password_check;

    @BindView(R.id.login_phone_tv_verify_code)
    TextView tv_verify_code;

    @BindView(R.id.login_phone_tv_password_change)
    TextView tv_password_change;

    @BindView(R.id.login_phone_tv_password_retrieve)
    TextView tv_password_retrieve;

    @BindView(R.id.login_phone_tv_register)
    TextView tv_register;

    @BindView(R.id.login_phone_btn_login)
    Button btn_login;

    private UserManager um;
    private int send_number = 0;
    private boolean isPhone_Ok = false;
    private boolean isSendCode = false;
    private boolean isPassword = false;
    private boolean isCanLogin = false;
    private String phoneStr, verifyCodeStr, passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        LogUtil.i(TAG, "onCreate");
        AppManager.getInstance().addActivity(this); //添加Activity到堆栈

        um = UserManager.getInstance();
        phoneStr = um.getLoginAccount();

        initView();
    }

    private void initView() {
        setTitle(R.string.login_phone_code);

        iv_phone_clear.setOnClickListener(this);
        tv_verify_code.setOnClickListener(this);
        iv_password_check.setOnClickListener(this);
        tv_password_change.setOnClickListener(this);
        tv_password_retrieve.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        iv_password_check.setSelected(false);//设置默认隐藏密码

        initEditText();

        et_phone.setText("156 9696 9696");
        isPhone_Ok = true;

        if (!isPassword) { //验证码登录
            if (isPhone_Ok) {
                setSendCodeState(true);
            }
            long send_last_time = shared.getLong(AppConfig.KEY_SEND_VERIFY_LAST_TIME, 0);
            send_number = shared.getInt(AppConfig.KEY_SEND_VERIFY_NUMBER, 0);
            long intervalTime = SEND_TIME; //间隔1分钟
            if (send_number >= 3) {
                intervalTime = SEND_TIME * 10;  //间隔10分钟
            }
            long surplusTime = intervalTime - (System.currentTimeMillis() - send_last_time);
            if (surplusTime > 0) { //剩余时间大于0
                setSendCodeState(false);
                startTimer(tv_verify_code, surplusTime);
            } else {
                if (send_number >= 3) {
                    send_number = 0;
                    editor.putInt(AppConfig.KEY_SEND_VERIFY_NUMBER, send_number).apply();
                }
            }
        }

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
                isPhone_Ok = false;
                phoneStr = s.toString();
                if (phoneStr.isEmpty()) {
                    iv_phone_clear.setVisibility(View.GONE);
                    setSendCodeState(false);
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
                        if (!isPassword) {
                            setSendCodeState(true);
                            editTextFocusAndClear(et_code);
                        } else {
                            editTextFocusAndClear(et_password);
                        }
                    } else {
                        setSendCodeState(false);
                    }
                }
            }
        });

        if (!StringUtil.isNull(phoneStr)) { //显示偏好设置
            et_phone.setText(phoneStr);
            et_phone.setSelection(et_phone.length());
        }

        et_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.isEmpty()) return;
                if (str.length() > 6) {
                    str = str.substring(0, 6);
                    et_code.setText(str);
                    et_code.setSelection(et_code.getText().length());
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

    private void setSendCodeState(boolean isState) {
        isSendCode = isState;
        changeViewState(tv_verify_code, isSendCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_iv_phone_clear:
                editTextFocusAndClear(et_phone);
                break;
            case R.id.login_phone_tv_verify_code:
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
            case R.id.login_phone_iv_password_check:
                if (!iv_password_check.isSelected()) {
                    iv_password_check.setSelected(true);
                    changeEditTextPassword(et_password, true);
                }else {
                    iv_password_check.setSelected(false);
                    changeEditTextPassword(et_password, false);
                }
                break;
            case R.id.login_phone_tv_password_change:
                isPassword = !isPassword;
                if (isPassword) {
                    et_code.setVisibility(View.GONE);
                    tv_verify_code.setVisibility(View.GONE);
                    et_password.setVisibility(View.VISIBLE);
                    iv_password_check.setVisibility(View.VISIBLE);
                    editTextFocusAndClear(et_password);
                    tv_password_change.setText(getString(R.string.login_mode_verify_code));
                } else {
                    et_code.setVisibility(View.VISIBLE);
                    tv_verify_code.setVisibility(View.VISIBLE);
                    et_password.setVisibility(View.GONE);
                    iv_password_check.setVisibility(View.GONE);
                    editTextFocusAndClear(et_code);
                    tv_password_change.setText(getString(R.string.login_mode_password));
                }
                break;
            case R.id.login_phone_tv_password_retrieve:
                break;
            case R.id.login_phone_tv_register:
                break;
            case R.id.login_phone_btn_login:
                login();
                break;
        }
    }

    private void login() {
        /*phoneStr = et_phone.getText().toString();
        // 手机非空
        if (phoneStr.isEmpty()) {
            CommonTools.showToast(getString(R.string.login_input_phone));
            return;
        }
        // 手机去空
        if (phoneStr.contains(" ")) {
            phoneStr = phoneStr.replace(" ", "");
        }
        // 校验格式
        if (!StringUtil.isMobileNO(phoneStr)) {
            CommonTools.showToast(getString(R.string.login_input_phone_error));
            return;
        }*/
        if (isPassword) {
            // 密码非空
            passwordStr = et_password.getText().toString();
            if (passwordStr.isEmpty()) {
                CommonTools.showToast(getString(R.string.login_input_password));
                return;
            }
        } else {
            // 验证码非空
            verifyCodeStr = et_code.getText().toString();
            if (verifyCodeStr.isEmpty()) {
                CommonTools.showToast(getString(R.string.login_input_verify_code));
                return;
            }
        }
        postLoginData();
    }

    private void postLoginData() {
        startAnimation();
        CommonTools.showToast(phoneStr);
//		request(AppConfig.REQUEST_SV_POST_RESET_PASSWORD_CODE);
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

}

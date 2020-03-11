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

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;


public class LoginPhoneActivity extends BaseActivity implements OnClickListener {

    String TAG = LoginPhoneActivity.class.getSimpleName();

    @BindView(R.id.login_account_et_phone)
    EditText et_phone;

    @BindView(R.id.login_account_et_code)
    EditText et_code;

    @BindView(R.id.login_account_et_password)
    EditText et_password;

    @BindView(R.id.login_iv_phone_clear)
    ImageView iv_phone_clear;

    @BindView(R.id.login_account_iv_password_check)
    ImageView iv_password_check;

    @BindView(R.id.login_account_tv_phone_error)
    TextView tv_phone_error;

    @BindView(R.id.login_account_tv_verify_code)
    TextView tv_verify_code;

    @BindView(R.id.login_account_tv_password_change)
    TextView tv_password_change;

    @BindView(R.id.login_account_tv_password_reset)
    TextView tv_password_reset;

    @BindView(R.id.login_account_tv_register)
    TextView tv_register;

    @BindView(R.id.login_account_btn_login)
    Button btn_login;

    private int send_number = 0;
    private boolean isPhone_Ok = false;
    private boolean isSendCode = false;
    private boolean send_Again = true;
    private boolean isPassword = false;
    private boolean isCanLogin = false;
    private String phoneStr = "";
    private String codeStr = "";
    private String passwordStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        initView();
    }

    private void initView() {
        setTitle(R.string.login_phone_login);

        iv_phone_clear.setOnClickListener(this);
        tv_verify_code.setOnClickListener(this);
        iv_password_check.setOnClickListener(this);
        tv_password_change.setOnClickListener(this);
        tv_password_reset.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        iv_password_check.setSelected(false);//设置默认隐藏密码

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
                setLoginState(false);
                setSendCodeState(false);
                isPhone_Ok = false;
                phoneStr = et_phone.getText().toString();
                if (StringUtil.isNull(phoneStr)) {
                    iv_phone_clear.setVisibility(View.GONE);
                }else {
                    iv_phone_clear.setVisibility(View.VISIBLE);
                    int length = phoneStr.length();
                    if (length >= 13) {
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
                        if (!isPassword) {
                            editTextFocusAndClear(et_code);
                            if (isTimeFinish) {
                                setSendCodeState(true);
                            }
                        } else {
                            editTextFocusAndClear(et_password);
                        }
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
                setLoginState(false);
                if (isPhone_Ok && s.toString().length() >= 6) {
                    setLoginState(true);
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
                setLoginState(false);
                if (isPhone_Ok && s.toString().length() >= 6) {
                    setLoginState(true);
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

    private void setLoginState(boolean isState) {
        isCanLogin = isState;
        changeViewState(btn_login, isCanLogin);
    }

    private void setSendCodeState(boolean isState) {
        isSendCode = isState;
        changeViewState(tv_verify_code, isSendCode);
    }

    private void changeViewState() {
        if (isPassword) {
            setTitle(R.string.login_phone_password);
            et_code.setVisibility(View.GONE);
            tv_verify_code.setVisibility(View.GONE);
            et_password.setVisibility(View.VISIBLE);
            iv_password_check.setVisibility(View.VISIBLE);
            tv_password_change.setText(getString(R.string.login_mode_verify_code));
            if (isPhone_Ok) {
                editTextFocusAndClear(et_password);
            } else {
                editTextFocusAndClear(et_phone);
            }
        } else {
            setTitle(R.string.login_phone_login);
            et_code.setVisibility(View.VISIBLE);
            tv_verify_code.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.GONE);
            iv_password_check.setVisibility(View.GONE);
            tv_password_change.setText(getString(R.string.login_mode_password));
            if (isPhone_Ok) {
                editTextFocusAndClear(et_code);
                if (isTimeFinish) {
                    setSendCodeState(true);
                }
            } else {
                editTextFocusAndClear(et_phone);
                setSendCodeState(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_iv_phone_clear:
                editTextFocusAndClear(et_phone);
                break;
            case R.id.login_account_tv_verify_code:
                if (send_number >= 3) {
                    CommonTools.showToast(getString(R.string.login_verify_code_send_3));
                }
                if (isSendCode && send_Again) {
                    send_Again = false;
                    sendMessageAuth();
                }
                break;
            case R.id.login_account_iv_password_check:
                if (!iv_password_check.isSelected()) {
                    iv_password_check.setSelected(true);
                    changeEditTextPassword(et_password, true);
                }else {
                    iv_password_check.setSelected(false);
                    changeEditTextPassword(et_password, false);
                }
                break;
            case R.id.login_account_tv_password_change:
                isPassword = !isPassword;
                changeViewState();
                break;
            case R.id.login_account_tv_password_reset:
                openActivity(ResetPasswordActivity.class);
                break;
            case R.id.login_account_tv_register:
                openActivity(RegisterActivity.class);
                break;
            case R.id.login_account_btn_login:
                if (checkData() && isCanLogin) {
                    postLoginData();
                }
                break;
        }
    }

    private boolean checkData() {
        phoneStr = "";
        codeStr = "";
        passwordStr = "";
        // 手机非空
        phoneStr = et_phone.getText().toString();
        if (StringUtil.isNull(phoneStr)) {
            CommonTools.showToast(getString(R.string.login_phone_input));
            return false;
        }
        // 手机去空
        if (phoneStr.contains(" ")) {
            phoneStr = phoneStr.replace(" ", "");
        }
        // 校验手机
        if (!StringUtil.isMobileNO(phoneStr)) {
            tv_phone_error.setVisibility(View.VISIBLE);
            tv_phone_error.setText(getString(R.string.login_phone_input_error));
            return false;
        } else {
            tv_phone_error.setVisibility(View.GONE);
        }
        if (isPassword) {
            // 密码非空
            passwordStr = et_password.getText().toString();
            if (StringUtil.isNull(passwordStr)) {
                CommonTools.showToast(getString(R.string.login_password_input));
                return false;
            }
        } else {
            // 验证码非空
            codeStr = et_code.getText().toString();
            if (StringUtil.isNull(codeStr)) {
                CommonTools.showToast(getString(R.string.login_verify_code_input));
                return false;
            }
            // 校验验证码
            if (codeStr.length() < 6) {
                CommonTools.showToast(getString(R.string.login_verify_code_error));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);
        // 偏好设置-手机号
        if (StringUtil.isNull(phoneStr)) {
            phoneStr = userManager.getLoginAccount();
            String phoneNew = StringUtil.changeMobileNo(phoneStr);
            if (!StringUtil.isNull(phoneNew)) {
                isPassword = true;
                isPhone_Ok = true;
                et_phone.setText(phoneNew);
                et_phone.setSelection(et_phone.length());
            }
        }
        changeViewState();
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", phoneStr);
        loadSVData(AppConfig.URL_AUTH_MESSAGE, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_MESSAGE);
    }

    private void postLoginData() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("sourceType", AppConfig.DATA_TYPE);
                map.put("mobile", phoneStr);
                map.put("code", codeStr);
                map.put("password", passwordStr);
                loadSVData(AppConfig.URL_AUTH_LOGIN, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_LOGIN);
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
                case AppConfig.REQUEST_SV_AUTH_LOGIN:
                    baseEn = JsonLogin.getLoginData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserLoginSuccess((UserInfoEntity) baseEn.getData());
                        closeLoginActivity();
                    } else
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_PHONE_UNREGISTERED) {
                        tv_phone_error.setVisibility(View.VISIBLE);
                        tv_phone_error.setText(getString(R.string.login_phone_unregistered));
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

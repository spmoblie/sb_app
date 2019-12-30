package com.songbao.sampo_b.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.AppManager;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.activity.BaseActivity;
import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.UserInfoEntity;
import com.songbao.sampo_b.utils.CommonTools;
import com.songbao.sampo_b.utils.ExceptionUtil;
import com.songbao.sampo_b.utils.JsonLogin;
import com.songbao.sampo_b.utils.LogUtil;
import com.songbao.sampo_b.utils.StringUtil;
import com.songbao.sampo_b.utils.retrofit.HttpRequests;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;


public class LoginAccountActivity extends BaseActivity implements OnClickListener {

    String TAG = LoginAccountActivity.class.getSimpleName();

    @BindView(R.id.login_phone_et_account)
    EditText et_account;

    @BindView(R.id.login_phone_et_password)
    EditText et_password;

    @BindView(R.id.login_iv_account_clear)
    ImageView iv_account_clear;

    @BindView(R.id.login_phone_iv_password_check)
    ImageView iv_password_check;

    @BindView(R.id.login_phone_btn_login)
    Button btn_login;

    private String accountStr = "";
    private String passwordStr = "";
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        initView();
    }

    private void initView() {
        setHeadVisibility(View.GONE);

        iv_account_clear.setOnClickListener(this);
        iv_password_check.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        iv_password_check.setSelected(false);//设置默认隐藏密码

        initEditText();
    }

    private void initEditText() {
        et_account.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                accountStr = et_account.getText().toString();
                if (StringUtil.isNull(accountStr)) {
                    iv_account_clear.setVisibility(View.GONE);
                }else {
                    iv_account_clear.setVisibility(View.VISIBLE);
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

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_iv_account_clear:
                editTextFocusAndClear(et_account);
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
            case R.id.login_phone_btn_login:
                if (checkData()) {
                    postLoginData();
                }
                break;
        }
    }

    private boolean checkData() {
        // 账户非空
        accountStr = et_account.getText().toString();
        if (StringUtil.isNull(accountStr)) {
            CommonTools.showToast(getString(R.string.login_account_input));
            return false;
        }
        // 密码非空
        passwordStr = et_password.getText().toString();
        if (StringUtil.isNull(passwordStr)) {
            CommonTools.showToast(getString(R.string.login_password_input));
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exit) {
                AppManager.getInstance().AppExit(getApplicationContext());
            } else {
                exit = Boolean.TRUE;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = Boolean.FALSE;
                    }
                }, 2000);
                CommonTools.showToast(getString(R.string.toast_exit_prompt), 1000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void postLoginData() {
        startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<>();
                map.put("mobile", accountStr);
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
                case AppConfig.REQUEST_SV_AUTH_LOGIN:
                    baseEn = JsonLogin.getLoginData(jsonObject);
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_SUCCESS) {
                        userManager.saveUserLoginSuccess((UserInfoEntity) baseEn.getData());
                        closeLoginActivity();
                    } else
                    if (baseEn.getErrno() == AppConfig.ERROR_CODE_PHONE_UNREGISTERED) {
                        CommonTools.showToast(getString(R.string.login_account_error));
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
        handleErrorCode(null);
    }
}

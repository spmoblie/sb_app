package com.songbao.sampo_c.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.activity.common.MyWebViewActivity;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.UpdateAppVersion;

import java.lang.ref.WeakReference;


public class SettingActivity extends BaseActivity implements OnClickListener {

    String TAG = SettingActivity.class.getSimpleName();

    RelativeLayout rl_feedback, rl_address, rl_version, rl_about_us, rl_logout;
    TextView tv_version_no, tv_logout;
    ImageView iv_push_status;

    private boolean pushStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pushStatus = AppApplication.getPushStatus();

        findViewById();
        initView();
    }

    private void findViewById() {
        rl_feedback = findViewById(R.id.setting_rl_feedback);
        rl_address = findViewById(R.id.setting_rl_address);
        rl_version = findViewById(R.id.setting_rl_version);
        rl_about_us = findViewById(R.id.setting_rl_about_us);
        rl_logout = findViewById(R.id.setting_rl_logout);
        tv_version_no = findViewById(R.id.setting_tv_version_content);
        tv_logout = findViewById(R.id.setting_tv_logout);
        iv_push_status = findViewById(R.id.setting_iv_push_control_btn);
    }

    private void initView() {
        setTitle(R.string.setting);

        rl_feedback.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_about_us.setOnClickListener(this);
        rl_logout.setOnClickListener(this);

        tv_version_no.setText(getString(R.string.version_no, AppApplication.version_name));

        iv_push_status.setSelected(pushStatus);
        iv_push_status.setOnClickListener(this);
    }

    @Override
    public void OnListenerLeft() {
        super.OnListenerLeft();
    }

    @Override
    public void OnListenerRight() {
        super.OnListenerRight();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.setting_rl_about_us:
                openActivity(AboutActivity.class);
                break;
            case R.id.setting_rl_feedback:
                openWebViewActivity(getString(R.string.setting_feedback), AppConfig.SALE_HELP);
                break;
            case R.id.setting_rl_address:
                if (isLogin()) {
                    openActivity(AddressActivity.class);
                } else {
                    openLoginActivity();
                }
                break;
            case R.id.setting_iv_push_control_btn:
                pushStatus = !pushStatus;
                iv_push_status.setSelected(pushStatus);
                AppApplication.setPushStatus(pushStatus);
                break;
            case R.id.setting_rl_version:
                UpdateAppVersion.getInstance(mContext, false);
                break;
            case R.id.setting_rl_logout:
                if (isLogin()) {
                    showConfirmDialog(getString(R.string.setting_logout_confirm), new MyHandler(this));
                } else {
                    openLoginActivity();
                }
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);

        checkLogin();

        super.onResume();
    }

    private void checkLogin() {
        if (isLogin()) {
            tv_logout.setText(getString(R.string.setting_logout));
        } else {
            tv_logout.setText(getString(R.string.login_login));
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

    static class MyHandler extends Handler {

        WeakReference<SettingActivity> mActivity;

        MyHandler(SettingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SettingActivity theActivity = mActivity.get();
            switch (msg.what) {
                case AppConfig.DIALOG_CLICK_OK:
                    AppApplication.AppLogout();
                    theActivity.startAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            theActivity.stopAnimation();
                            theActivity.finish();
                        }
                    }, AppConfig.LOADING_TIME);
                    break;
            }
        }
    }

}

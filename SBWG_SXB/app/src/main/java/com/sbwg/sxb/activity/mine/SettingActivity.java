package com.sbwg.sxb.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.R;
import com.sbwg.sxb.activity.BaseActivity;
import com.sbwg.sxb.activity.common.MyWebViewActivity;
import com.sbwg.sxb.utils.LogUtil;
import com.sbwg.sxb.utils.UpdateAppVersion;


public class SettingActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = SettingActivity.class.getSimpleName();

    private RelativeLayout rl_feedback, rl_version, rl_about_us, rl_logout;
    private TextView tv_feedback, tv_version_title, tv_version_no;
    private TextView tv_about_us, tv_push_title, tv_logout;
    private ImageView iv_push_status;

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
        rl_version = findViewById(R.id.setting_rl_version);
        rl_about_us = findViewById(R.id.setting_rl_about_us);
        rl_logout = findViewById(R.id.setting_rl_logout);
        tv_feedback = findViewById(R.id.setting_tv_feedback_title);
        tv_version_title = findViewById(R.id.setting_tv_version_title);
        tv_version_no = findViewById(R.id.setting_tv_version_content);
        tv_about_us = findViewById(R.id.setting_tv_about_us_title);
        tv_push_title = findViewById(R.id.setting_tv_push_control_title);
        tv_logout = findViewById(R.id.setting_tv_logout);
        iv_push_status = findViewById(R.id.setting_iv_push_control_btn);
    }

    private void initView() {
        setTitle(R.string.setting);
        tv_feedback.setText(getString(R.string.setting_feedback));
        tv_about_us.setText(getString(R.string.setting_about_us));
        tv_version_title.setText(getString(R.string.setting_version));
        tv_push_title.setText(getString(R.string.setting_push));
        rl_feedback.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_about_us.setOnClickListener(this);
        rl_logout.setOnClickListener(this);

        tv_version_no.setText("V" + AppApplication.version_name);

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
                intent = new Intent(mContext, MyWebViewActivity.class);
                intent.putExtra("title", getString(R.string.setting_about_us));
                intent.putExtra("lodUrl", AppConfig.ABOUT_US);
                break;
            case R.id.setting_rl_feedback:
                intent = new Intent(mContext, FeedBackActivity.class);
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
                    showConfirmDialog(getString(R.string.setting_logout_confirm), getString(R.string.cancel),
                            getString(R.string.confirm), true, true, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    switch (msg.what) {
                                        case AppConfig.DIALOG_CLICK_NO:
                                            break;
                                        case AppConfig.DIALOG_CLICK_OK:
                                            AppApplication.AppLogout();
                                            startAnimation();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    stopAnimation();
                                                    finish();
                                                }
                                            }, AppConfig.LOADING_TIME);
                                            break;
                                    }
                                }
                            });
                } else {
                    openLoginActivity(TAG);
                }
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
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

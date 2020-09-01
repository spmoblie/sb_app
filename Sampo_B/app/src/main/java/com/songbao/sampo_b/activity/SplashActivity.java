package com.songbao.sampo_b.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.songbao.sampo_b.AppApplication;
import com.songbao.sampo_b.AppConfig;
import com.songbao.sampo_b.R;
import com.songbao.sampo_b.utils.DeviceUtil;
import com.songbao.sampo_b.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * App首页欢迎界面
 */
public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 隐藏父类组件
        setHeadVisibility(View.GONE);

        // 默认进入首页
        AppApplication.jumpToHomePage(1);

        // 初始化推送服务状态(开启或关闭)
        //AppApplication.onPushDefaultStatus();
    }

    @Override
    protected void onResume() {
        LogUtil.i(LogUtil.LOG_TAG, TAG + ": onResume");
        // 页面开始
        AppApplication.onPageStart(this, TAG);
        // 检查授权
        if (checkPermission()) {
            checkPermissionPass();
        }

        super.onResume();
    }

    private void checkPermissionPass() {
        // 检测SD存储卡
        if (AppApplication.getAppContext().getExternalFilesDir(null) != null) {
            if (userManager.isUserAgree()) {
                goHomeActivity();
            } else {
                showUserAgreeDialog("用户协议与隐私政策", getClickableSpan(), "拒绝", "同意", new MyHandler(this), 6969);
            }
        } else {
            showErrorDialog(getString(R.string.dialog_error_card_null), false, new MyHandler(this));
        }
    }

    /**
     * 获取可点击的SpannableString
     * @return
     */
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString("请您务必审慎阅读《用户协议》和《隐私政策》了解详细信息，如您同意，请点击“同意”开始接受我们的服务。");

        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 8, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                openWebViewActivity(getString(R.string.setting_user_agreement), AppConfig.USER_AGREEMENT);
            }
        }, 8, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 8, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                openWebViewActivity(getString(R.string.setting_privacy_policy), AppConfig.PRIVACY_POLICY);
            }
        }, 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 延迟跳转到首页
     */
    private void goHomeActivity() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                AppApplication.status_height = DeviceUtil.getStatusBarHeight(SplashActivity.this);
                userManager.setUserAgree(true);

                openActivity(MainActivity.class);
                finish();
                // 设置Activity的切换效果
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }, 1000);
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
    protected void permissionsResultCallback(boolean result) {
        if (result) {
            // 已授权
            checkPermissionPass();
        } else {
            finish();
        }
    }

    static class MyHandler extends Handler {

        WeakReference<SplashActivity> mActivity;

        MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case 6969:
                        theActivity.goHomeActivity();
                        break;
                    case AppConfig.DIALOG_CLICK_NO:
                    case AppConfig.DIALOG_CLICK_OK:
                        theActivity.finish();
                        break;
                }
            }
        }
    }
}

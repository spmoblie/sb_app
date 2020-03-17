package com.songbao.sampo_c.activity.login;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.songbao.sampo_c.AppApplication;
import com.songbao.sampo_c.AppConfig;
import com.songbao.sampo_c.R;
import com.songbao.sampo_c.activity.BaseActivity;
import com.songbao.sampo_c.entity.BaseEntity;
import com.songbao.sampo_c.entity.QQEntity;
import com.songbao.sampo_c.entity.QQUserInfoEntity;
import com.songbao.sampo_c.entity.UserInfoEntity;
import com.songbao.sampo_c.entity.WXUserInfoEntity;
import com.songbao.sampo_c.utils.CommonTools;
import com.songbao.sampo_c.utils.ExceptionUtil;
import com.songbao.sampo_c.utils.JsonLogin;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.StringUtil;
import com.songbao.sampo_c.utils.URLSpanUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements OnClickListener {

    String TAG = LoginActivity.class.getSimpleName();

    public static final String LOGIN_TYPE_WX = "1";
    public static final String LOGIN_TYPE_QQ = "2";
    public static final String LOGIN_TYPE_WB = "3";

    @BindView(R.id.login_iv_close)
    ImageView login_iv_close;

    @BindView(R.id.login_btn_login)
    Button login_btn_login;

    @BindView(R.id.login_tv_register)
    TextView login_tv_register;

    @BindView(R.id.login_tv_wx)
    TextView login_tv_wx;

    @BindView(R.id.login_tv_qq)
    TextView login_tv_qq;

    @BindView(R.id.login_tv_wb)
    TextView login_tv_wb;

    @BindView(R.id.login_tv_agreement)
    TextView tv_agreement;

    private MyBroadcastReceiver myReceiver;
    private UserInfoEntity fbOauthEn;
    private boolean isStop = false;
    private String loginType, postUid;
    // WX
    private IWXAPI mIWXApi;
    private static final String WX_APP_ID = AppConfig.WX_APP_ID;
    private String access_token, openid, unionid, refresh_token;
    // QQ
    private Tencent mTencent;
    private boolean isQQLogin = true;
    private static final String QQ_APP_ID = AppConfig.QQ_APP_ID;
    private static final String QQ_SCOPE = AppConfig.QQ_SCOPE;
    // WB
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // WX
        mIWXApi = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
        mIWXApi.registerApp(AppConfig.WX_APP_ID);
        // QQ
        mTencent = Tencent.createInstance(QQ_APP_ID, mContext);
        // WB
        WbSdk.install(this, new AuthInfo(this,
                AppConfig.WB_APP_ID, AppConfig.WB_REDIRECT_URL, AppConfig.WB_SCOPE));
        mSsoHandler = new SsoHandler(this);

        // 注册广播
        myReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConfig.RA_PAGE_LOGIN);
        registerReceiver(myReceiver, filter);

        initView();
    }

    private void initView() {
        setHeadVisibility(View.GONE);
        login_btn_login.setOnClickListener(this);
        login_iv_close.setOnClickListener(this);
        login_tv_register.setOnClickListener(this);
        login_tv_wx.setOnClickListener(this);
        login_tv_qq.setOnClickListener(this);
        login_tv_wb.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //API 24
            setTextUrl();
        }
    }

    @SuppressLint("NewApi")
    private void setTextUrl() {
        String s1 = "<font  color='#222222'>登录即同意</font>";
        s1 += "<a href = 'http://www.baidu.com'>《松小堡用户协议》</a>";
        //<font>：设置颜色和字体。
        //<big>：设置字体大号
        //<small>：设置字体小号
        //<i><b>：斜体粗体
        //<a>：连接网址
        //<img>：图片
        //其中的flags表示：
        //FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
        //FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
        CharSequence charSequence = Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT);
        tv_agreement.setText(charSequence);
        tv_agreement.setLinkTextColor(getResources().getColor(R.color.tv_color_status));
        //为TextView添加链接
        tv_agreement.setMovementMethod(LinkMovementMethod.getInstance());
        stripUnderlines(tv_agreement);
    }

    private void stripUnderlines(TextView textView) {
        if(null != textView && textView.getText() instanceof Spannable){
            Spannable s = (Spannable)textView.getText();
            URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
            for (URLSpan span: spans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);
                span = new URLSpanUtil(span.getURL());
                s.setSpan(span, start, end, 0);
            }
            textView.setText(s);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_iv_close:
                finish();
                break;
            case R.id.login_btn_login:
                openActivity(LoginPhoneActivity.class);
                break;
            case R.id.login_tv_register:
                openActivity(RegisterActivity.class);
                break;
            case R.id.login_tv_wx:
                loginWX();
                break;
            case R.id.login_tv_qq:
                //loginQQ();
                CommonTools.showToast(getString(R.string.toast_no_open));
                break;
            case R.id.login_tv_wb:
                //loginWB();
                CommonTools.showToast(getString(R.string.toast_no_open));
                break;
        }
    }

    /**
     * 微信登录
     */
    private void loginWX() {
        if (!mIWXApi.isWXAppInstalled()) { //检测是否安装微信客户端
            CommonTools.showToast(mContext.getString(R.string.share_msg_no_wx));
            return;
        }
        startAnimation();
        // 用户授权获取access_token
        AppApplication.isWXShare = false;
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "songbao";
        mIWXApi.sendReq(req);
        mIWXApi.getWXAppSupportAPI();
    }

    /**
     * 提交微信授权登录请求
     */
    public void postWXLoginRequest() {
        loginType = LOGIN_TYPE_WX;
        access_token = userManager.getWXAccessToken();
        openid = userManager.getWXOpenId();
        unionid = userManager.getWXUnionId();
        refresh_token = userManager.getWXRefreshToken();
        postUid = unionid;
        requestThirdPartiesLogin();
    }

    /**
     * 获取微信用户信息
     */
    private void getWXUserInfo() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("openid", openid);
        loadSVData("https://api.weixin.qq.com/", "sns/userinfo", map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_AUTH_WX_USER);
    }

    /**
     * QQ登录
     */
    private void loginQQ() {
        if (mTencent != null) {
            isQQLogin = true;
            mTencent.login(LoginActivity.this, QQ_SCOPE, qqLoginListener);
        } else {
            showLoginError();
        }
    }

    /**
     * 提交QQ授权登录请求
     */
    private void postQQLoginRequest() {
        if (mTencent != null) {
            loginType = LOGIN_TYPE_QQ;
            postUid = mTencent.getOpenId();
            requestThirdPartiesLogin();
        } else {
            showLoginError();
        }
    }

    /**
     * 获取QQ用户信息
     */
    private void getQQUserInfo() {
        if (mTencent != null) {
            isQQLogin = false;
            UserInfo info = new UserInfo(LoginActivity.this, mTencent.getQQToken());
            info.getUserInfo(qqLoginListener);
        } else {
            showLoginError();
        }
    }

    /**
     * QQ登录、快速支付登录、应用分享、应用邀请等回调接口
     */
    IUiListener qqLoginListener = new IUiListener() {

        @Override
        public void onCancel() {
            showLoginCancel();
        }

        @Override
        public void onComplete(Object jsonObject) {
            try {
                if (isQQLogin) {
                    QQEntity qqEn = JsonLogin.getQQLoginResult(jsonObject);
                    if (mTencent != null && qqEn != null && qqEn.getErrcode() == 0) { //用户授权成功
                        mTencent.setOpenId(qqEn.getOpenid());
                        mTencent.setAccessToken(qqEn.getAccess_token(), qqEn.getExpires_in());
                        postQQLoginRequest();
                    } else {
                        showLoginError();
                    }
                } else {
                    QQUserInfoEntity userInfo = JsonLogin.getQQUserInfo(jsonObject);
                    if (userInfo != null && userInfo.getErrcode() == 0) { //获取用户信息成功
                        UserInfoEntity oauthEn = new UserInfoEntity();
						oauthEn.setUserArea(LOGIN_TYPE_QQ);
                        oauthEn.setUserId(mTencent.getOpenId());
                        oauthEn.setUserNick(userInfo.getNickname());
                        oauthEn.setUserHead(userInfo.getAvatar());
                        oauthEn.setGenderStr(userInfo.getGender());
                        // 注册QQ用户信息
                        startRegisterOauthActivity(oauthEn);
                    } else {
                        showLoginError();
                    }
                }
            } catch (Exception e) {
                ExceptionUtil.handle(e);
                showLoginError();
            }
        }

        @Override
        public void onError(UiError jsonObject) {
            showLoginError();
        }

    };

    /**
     * 微博登录
     */
    private void loginWB() {
        mSsoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(final Oauth2AccessToken token) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAccessToken = token;
                        if (mAccessToken.isSessionValid()) {
                            // 保存 Token 到 SharedPreferences
                            AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                            postWBLoginRequest();
                        }
                    }
                });
            }

            @Override
            public void cancel() {

            }

            @Override
            public void onFailure(WbConnectErrorMessage errorMessage) {

            }
        });
    }

    /**
     * 提交微博授权登录请求
     */
    private void postWBLoginRequest() {
        if (mAccessToken != null) {
            loginType = LOGIN_TYPE_WB;
            postUid = mAccessToken.getUid();
            requestThirdPartiesLogin();
        } else {
            showLoginError();
        }
    }

    /**
     * 获取微博用户信息
     */
    private void getWBUserInfo() {
        if (mAccessToken != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("access_token", mAccessToken.getToken());
            map.put("uid", mAccessToken.getUid());
            loadSVData("https://api.weibo.com/2/", "users/show.json", map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_AUTH_WB_USER);
        } else {
            showLoginError();
        }
    }

    /**
     * 用户取消授权登录
     */
    private void showLoginCancel() {
        stopAnimation();
        CommonTools.showToast(getString(R.string.login_oauth_cancel));
    }

    /**
     * 提示功能无法使用
     */
    private void showObjectNull() {
        stopAnimation();
        CommonTools.showToast(getString(R.string.toast_object_null));
    }

    /**
     * 提示授权登录失败
     */
    private void showLoginError() {
        stopAnimation();
        CommonTools.showToast(getString(R.string.login_error_oauth));
    }

    /**
     * 发起第三方授权登录请求
     */
    private void requestThirdPartiesLogin() {
        if (StringUtil.isNull(loginType) || StringUtil.isNull(postUid)) {
            showLoginError();
            return;
        }
        startAnimation();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("type", loginType);
                map.put("otherId", postUid);
                loadSVData(AppConfig.URL_AUTH_OAUTH, map, HttpRequests.HTTP_POST, AppConfig.REQUEST_SV_AUTH_OAUTH);
			}
		}, AppConfig.LOADING_TIME);
    }

    /**
     * 跳转到绑定账号页面
     */
    private void startRegisterOauthActivity(UserInfoEntity oauthEn) {
        if (isStop) return;
		Intent intent = new Intent(mContext, RegisterOauthActivity.class);
		intent.putExtra("oauthEn", oauthEn);
		startActivity(intent);
		stopAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QQ
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
        }
        // WB
        // SSO 授权回调 (重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults)
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnListenerLeft() {
        super.OnListenerLeft();
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
        isStop = true;

        // 注销广播
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void callbackData(JSONObject jsonObject, int dataType) {
        UserInfoEntity oauthEn;
        try {
            switch (dataType) {
                case AppConfig.REQUEST_SV_AUTH_WX_USER:
                    WXUserInfoEntity wxUserInfo = JsonLogin.getWXUserInfo(jsonObject);
                    oauthEn = new UserInfoEntity();
                    oauthEn.setUserArea(LOGIN_TYPE_WX);
                    oauthEn.setUserId(wxUserInfo.getUnionid());
                    oauthEn.setUserNick(wxUserInfo.getNickname());
                    oauthEn.setUserHead(wxUserInfo.getAvatar());
                    oauthEn.setGenderStr(wxUserInfo.getGender());
                    // 注册微信用户信息
                    startRegisterOauthActivity(oauthEn);
                    break;
                case AppConfig.REQUEST_SV_AUTH_WB_USER:
                    oauthEn = JsonLogin.getWBUserInfo(jsonObject);
                    if (oauthEn != null) {
                        oauthEn.setUserArea(LOGIN_TYPE_WB);
                        // 注册微博用户信息
                        startRegisterOauthActivity(oauthEn);
                    } else {
                        showLoginError();
                    }
                    break;
                case AppConfig.REQUEST_SV_AUTH_OAUTH:
                    BaseEntity baseEn = JsonLogin.getLoginData(jsonObject);
                    if (baseEn.getErrNo() == AppConfig.ERROR_CODE_SUCCESS) { //校验通过
                        userManager.saveUserLoginSuccess((UserInfoEntity) baseEn.getData());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                closeLoginActivity();
                            }
                        }, 500);
                    } else if (baseEn.getErrNo() == 99990) { //校验不通过
                        switch (loginType) {
                            case LOGIN_TYPE_WX:
                                getWXUserInfo();
                                break;
                            case LOGIN_TYPE_QQ:
                                getQQUserInfo();
                                break;
                            case LOGIN_TYPE_WB:
                                getWBUserInfo();
                                break;
                        }
                    } else {
                        handleErrorCode(baseEn);
                    }
                    break;
            }
        } catch (Exception e) {
            showLoginError();
            ExceptionUtil.handle(e);
        }
    }


    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            postWXLoginRequest();
        }
    }

}

package com.songbao.sxb.activity.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.entity.QQEntity;
import com.songbao.sxb.entity.QQUserInfoEntity;
import com.songbao.sxb.entity.UserInfoEntity;
import com.songbao.sxb.entity.WXUserInfoEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.JsonLogin;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements OnClickListener {

    String TAG = LoginActivity.class.getSimpleName();

    public static final String LOGIN_TYPE_WX = "wechat_app";
    public static final String LOGIN_TYPE_QQ = "qq";
    public static final String LOGIN_TYPE_WB = "weibo";

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
    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;
//    private UsersAPI mUsersAPI;
    private static final String WB_APP_ID = AppConfig.WB_APP_ID;
    private static final String WB_REDIRECT_URL = AppConfig.WB_REDIRECT_URL;
    private static final String WB_SCOPE = AppConfig.WB_SCOPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // WX
        mIWXApi = WXAPIFactory.createWXAPI(mContext, AppConfig.WX_APP_ID);
        mIWXApi.registerApp(AppConfig.WX_APP_ID);
        // QQ
        mTencent = Tencent.createInstance(QQ_APP_ID, mContext);

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
                loginQQ();
                break;
            case R.id.login_tv_wb:
                loginWB();
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
        new HttpWXUserTask().execute("https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid);
    }

    /**
     * 异步获取微信用户信息
     */
    class HttpWXUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
//				return sc.getServerJSONString(params[0]);
                return null;
            } catch (Exception e) {
                ExceptionUtil.handle(e);
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WXUserInfoEntity userInfo = JsonLogin.getWexiUserInfo(result);
                if (userInfo != null) {
                    UserInfoEntity oauthEn = new UserInfoEntity();
//					oauthEn.setUserRankName(LOGIN_TYPE_WX);
                    oauthEn.setUserId(userInfo.getUnionid());
                    oauthEn.setUserNick(userInfo.getNickname());
                    oauthEn.setUserIntro(userInfo.getGender());
                    oauthEn.setUserHead(userInfo.getAvatar());
                    // 注册微信用户信息
                    startRegisterOauthActivity(oauthEn);
                } else {
                    showLoginError();
                }
            } catch (JSONException e) {
                ExceptionUtil.handle(e);
                showLoginError();
            }
        }
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
//						oauthEn.setUserRankName(LOGIN_TYPE_QQ);
                        oauthEn.setUserId(mTencent.getOpenId());
                        oauthEn.setUserNick(userInfo.getNickname());
                        oauthEn.setUserIntro(userInfo.getGender());
                        oauthEn.setUserHead(userInfo.getAvatar());
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
//		mAccessToken = AccessTokenKeeper.readAccessToken(this);
//		if (mAccessToken != null  && mAccessToken.isSessionValid()) { //检测登录有效性
//			String date = TimeUtil.dateToStrLong(new java.util.Date(mAccessToken.getExpiresTime()));  
//            LogUtil.i(TAG, "weibo access_token 仍在有效期内,无需再次登录！有效期：" + date);
//            postWBLoginRequest();
//		}else {
//        mAuthInfo = new AuthInfo(mContext, WB_APP_ID, WB_REDIRECT_URL, WB_SCOPE);
//        mSsoHandler = new SsoHandler(this, mAuthInfo);
//        // SSO 授权, 仅客户端
//        mSsoHandler.authorize(new WeiboAuthListener() {
//            @Override
//            public void onComplete(Bundle values) {
//                mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//                if (mAccessToken != null && mAccessToken.isSessionValid()) {
//                    AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
//                    postWBLoginRequest();
//                } else {
//                    showLoginError();
//                }
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onWeiboException(WeiboException e) {
//                ExceptionUtil.handle(e);
//                showLoginError();
//            }
//        });
//		}
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
//        if (mAccessToken != null) {
//            mUsersAPI = new UsersAPI(getApplicationContext(), WB_APP_ID, mAccessToken);
//            mUsersAPI.show(Long.parseLong(mAccessToken.getUid()), wbListener);
//        } else {
//            showLoginError();
//        }
    }

    /**
     * 微博异步请求回调接口
     */
    private RequestListener wbListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
//            if (!TextUtils.isEmpty(response)) {
//                User user = User.parse(response);
//                if (user != null) {
//                    Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
//                    UserInfoEntity oauthEn = new UserInfoEntity();
////                    oauthEn.setUserRankName(LOGIN_TYPE_WB);
//                    oauthEn.setUserId(token.getUid());
//                    oauthEn.setUserNick(user.name);
//                    oauthEn.setUserIntro(user.gender);
//                    oauthEn.setUserHead(user.profile_image_url);
//                    // 注册微博用户信息
//                    startRegisterOauthActivity(oauthEn);
//                } else {
//                    showLoginError();
//                }
//            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ExceptionUtil.handle(e);
            showLoginError();
        }
    };


    /**
     * 用户取消授权登录
     */
    private void showLoginCancel() {
        stopAnimation();
        CommonTools.showToast(getString(R.string.login_oauth_cancel));
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
				//request(AppConfig.REQUEST_SV_THIRD_PARTIES_LOGIN);
			}
		}, AppConfig.LOADING_TIME);
    }

    /**
     * 跳转到绑定账号页面
     */
    private void startRegisterOauthActivity(UserInfoEntity oauthEn) {
        if (isStop) return;
//		Intent intent = new Intent(mContext, RegisterOauthActivity.class);
//		intent.putExtra("oauthEn", oauthEn);
//		startActivity(intent);
//		stopAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QQ
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
        }
        // WB
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

        super.onDestroy();
    }

}

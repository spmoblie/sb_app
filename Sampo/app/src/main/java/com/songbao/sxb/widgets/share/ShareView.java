package com.songbao.sxb.widgets.share;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.entity.ShareEntity;
import com.songbao.sxb.utils.BitmapUtil;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.LogUtil;
import com.songbao.sxb.utils.StringUtil;
import com.songbao.sxb.utils.UserManager;
import com.songbao.sxb.widgets.share.weixi.WXShareUtil;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


public class ShareView implements WbShareCallback, IWXAPIEventHandler {

	private static final String TAG = "ShareView";
	private static final int animationDuration = 500;
	private static int screenHeight = AppApplication.screen_height;

	Context mContext;
	Activity mActivity;
	ShareEntity mShareEn;
	Animation animShow, animDsms;
	ObjectAnimator moverShow, moverDsms;
	ShareVewButtonListener listener;
	int animHeight;

	View rootView;
	View viewDim;
	View ll_mainLayout;
	View tv_Share_QQ;
	View tv_Share_QQ_Space;
	View tv_Share_WX;
	View tv_Share_Friends;
	View tv_Share_WB;
	View tv_Share_Email;
	View tv_Share_Message;
	View tv_Share_Copy;
	
	// QQ
	private Tencent mTencent;
	private static final String QQ_APP_ID = AppConfig.QQ_APP_ID;
	// WX
	private IWXAPI mIWXApi;
	private static final String WX_APP_ID = AppConfig.WX_APP_ID;
	// WB
	private WbShareHandler shareHandler;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
	private Oauth2AccessToken mAccessToken;

	public ShareView(Context context, Activity activity, View rootView, ShareVewButtonListener listener){
		this.mContext = context;
		this.mActivity = activity;
		this.rootView = rootView;
		this.listener = listener;
		this.animHeight = screenHeight * 13/32;

		// QQ
		if (mTencent == null) {
            mTencent = Tencent.createInstance(QQ_APP_ID, context);
        }
		// WX
		mIWXApi = WXAPIFactory.createWXAPI(activity, WX_APP_ID, false);
		mIWXApi.registerApp(WX_APP_ID);
		// WB
		WbSdk.install(mContext, new AuthInfo(mContext,
				AppConfig.WB_APP_ID, AppConfig.WB_REDIRECT_URL, AppConfig.WB_SCOPE));
		shareHandler = new WbShareHandler(mActivity);
		shareHandler.registerApp();
		shareHandler.setProgressColor(0xff33b5e5);

		initView();
		createAnimation();
	}

	private void createAnimation(){
		animShow = AnimationUtils.loadAnimation(mContext, R.anim.anim_popup_show);
		animDsms = AnimationUtils.loadAnimation(mContext, R.anim.anim_popup_dsms);

		moverShow = ObjectAnimator.ofFloat(ll_mainLayout, "translationY", animHeight, 0f);
		moverShow.setDuration(animationDuration);
		moverDsms = ObjectAnimator.ofFloat(ll_mainLayout, "translationY", 0f, animHeight);
		moverDsms.setDuration(animationDuration);
	}
	
	public void setRootView(View rootView){
		this.rootView = rootView;
		initView();
	}
	
	public void setShareEntity(ShareEntity entity){
		this.mShareEn = entity;
		if (mShareEn != null) {
			// 添加Uid
			String newUrl = mShareEn.getUrl() + "&u=" + StringUtil.getInteger(UserManager.getInstance().getUserId());
			mShareEn.setUrl(newUrl);
			/*if (UserManager.getInstance().isTalent()) { //达人
				// 生成二维码
				Bitmap dropBm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_img_qr_code);
				String qrPath = BitmapUtil.createPath("qr_code.png", false).getAbsolutePath();
				QRCodeUtil.createQRImage(dropBm, newUrl, 400, 400, null, qrPath);
				// 拼接长图
				ArrayList<String> imagePaths = new ArrayList<String>();
				imagePaths.add(mShareEn.getImagePath());
				imagePaths.add(qrPath);
				String longImgPath = BitmapUtil.addLongBitmap(imagePaths);
				if (!StringUtil.isNull(longImgPath)) {
					mShareEn.setImagePath(longImgPath);
				}
			}*/
		}
	}

	public ShareEntity getShareEntity(){
		return mShareEn;
	}
	
	public void setListener(ShareVewButtonListener listener){
		this.listener = listener;
	}

	public void showShareLayer(boolean show){
		if(checkViewIsOk()){
			if(show){
				viewDim.startAnimation(animShow);
				moverShow.start();
				viewDim.setVisibility(View.VISIBLE);
				ll_mainLayout.setVisibility(View.VISIBLE);
			}else{
				viewDim.startAnimation(animDsms);
				moverDsms.start();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						viewDim.setVisibility(View.GONE);
						ll_mainLayout.setVisibility(View.GONE);
					}
				}, animationDuration);
			}
		}
	}
	
	public boolean isShowing(){
		return viewDim.getVisibility() == View.VISIBLE;
	}
	
	private boolean checkViewIsOk(){
		if(rootView == null){
			LogUtil.i(TAG, "Error! rootView is null");
			return false;
		}
		if(ll_mainLayout == null){
			LogUtil.i(TAG, "Error! ll_mainLayout is null");
			return false;
		}
		if(tv_Share_QQ == null){
			LogUtil.i(TAG, "Error! tv_Share_QQ is null");
			return false;
		}
		if(tv_Share_QQ_Space == null){
			LogUtil.i(TAG, "Error! tv_Share_QQ_Space is null");
			return false;
		}
		if(tv_Share_WX == null){
			LogUtil.i(TAG, "Error! tv_Share_WX is null");
			return false;
		}
		if(tv_Share_Friends == null){
			LogUtil.i(TAG, "Error! tv_Share_Friends is null");
			return false;
		}
		if(tv_Share_WB == null){
			LogUtil.i(TAG, "Error! tv_Share_WB is null");
			return false;
		}
		if(tv_Share_Email == null){
			LogUtil.i(TAG, "Error! tv_Share_Email is null");
			return false;
		}
		if(tv_Share_Message == null){
			LogUtil.i(TAG, "Error! tv_Share_Message is null");
			return false;
		}
		if(tv_Share_Copy == null){
			LogUtil.i(TAG, "Error! tv_Share_Copy is null");
			return false;
		}
		return true;
	}
	
	private void initView(){
		if (rootView == null) return;
		viewDim = rootView.findViewById(R.id.share_view_dismiss);
		viewDim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showShareLayer(false);
				if(listener != null){
					listener.onClick_Dismiss();
				}
			}
		});
		ll_mainLayout = rootView.findViewById(R.id.share_view_ll_show_main);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.height = animHeight;
		lp.gravity = Gravity.BOTTOM;
		ll_mainLayout.setLayoutParams(lp);
		ll_mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
		tv_Share_QQ = rootView.findViewById(R.id.share_view_tv_share_qq);
		tv_Share_QQ.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_QQ();
				}else {
					//qqShare();
				}
			}
		});
		tv_Share_QQ_Space = rootView.findViewById(R.id.share_view_tv_share_qq_space);
		tv_Share_QQ_Space.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_QQ_Space();
				}else {
					qqSpaceShare();
				}
			}
		});
		tv_Share_WX = rootView.findViewById(R.id.share_view_tv_share_wx);
		tv_Share_WX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_WX();
				}else {
					//wxShare(false);
				}
			}
		});
		tv_Share_Friends = rootView.findViewById(R.id.share_view_tv_share_friends);
		tv_Share_Friends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Friends();
				}else {
					//wxShare(true);
				}
			}
		});
		tv_Share_WB = rootView.findViewById(R.id.share_view_tv_share_wb);
		tv_Share_WB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_WB();
				}else {
					wbShare1();
				}
			}
		});
		tv_Share_Email = rootView.findViewById(R.id.share_view_tv_share_email);
		tv_Share_Email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Email();
				}else {
					emailShare();
				}
			}
		});
		tv_Share_Message = rootView.findViewById(R.id.share_view_tv_share_message);
		tv_Share_Message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(listener != null){
					listener.onClick_Share_Message();
				}else {
					messageShare();
				}
			}
		});
		tv_Share_Copy = rootView.findViewById(R.id.share_view_tv_share_copy);
		tv_Share_Copy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onClick_Share_Copy();
				}else {
					urlCopy();
				}
			}
		});
	}

	/**
	 * QQ分享
	 */
	private void qqShare() {
		if (mShareEn != null) {
			//showShareLayer(false);
			Bundle params = new Bundle();
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareEn.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mShareEn.getText());
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareEn.getUrl());
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mShareEn.getImageUrl());
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getResources().getString(R.string.app_name));
			mTencent.shareToQQ(mActivity, params, qqShareListener);
		}else {
			showEntityError();
		}
	}
	
	/**
     * QQ登录、快速支付登录、应用分享、应用邀请等回调接口
     */
	IUiListener qqShareListener = new IUiListener() {
		
		@Override
		public void onComplete(Object response) {
			showShareSuccess();
		}

		@Override
		public void onError(UiError e) {
			showShareError();
		}
		
		@Override
		public void onCancel() {
			showShareCancel();
	    }
		
	};

	/**
	 * QQ空间分享
	 */
	private void qqSpaceShare() {
		if (mShareEn != null) {
			//showShareLayer(false);
		}else {
			showEntityError();
		}
	}

	/**
	 * 微信好友、朋友圈分享
	 */
	private void wxShare(boolean isFriends){
		if (!mIWXApi.isWXAppInstalled()) { //检测是否安装微信客户端
			CommonTools.showToast(mContext.getString(R.string.share_msg_no_wx));
			return;
		}
		if (mShareEn != null) {
			//showShareLayer(false);
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = mShareEn.getUrl();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = mShareEn.getTitle();
			msg.description = mShareEn.getText();
			Bitmap bitmap = mShareEn.getShareBm();
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
			}
			if (bitmap != null) {
				bitmap = BitmapUtil.getBitmap(bitmap, 80, 80);
			}
			msg.thumbData = WXShareUtil.bmpToByteArray(bitmap, false);
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			if(isFriends){
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
			}else{
				req.scene = SendMessageToWX.Req.WXSceneSession;
			}
			mIWXApi.sendReq(req);
			AppApplication.isWXShare = true; //标记为微信分享
		}else {
			showEntityError();
		}
	}
    
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 微信的请求回调
	 */
	@Override
	public void onReq(BaseReq arg0) {

	}

	/**
	 * 微信的响应结果
	 */
	@Override
	public void onResp(BaseResp arg0) {

	}

	/**
	 * 微博分享-授权
	 */
	private void wbShare1(){
		//showShareLayer(false);
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		if (mAccessToken != null && mAccessToken.isSessionValid()) {
			wbShare2();
		} else {
			mSsoHandler = new SsoHandler(mActivity);
			// SSO 授权, 仅客户端
			mSsoHandler.authorize(new WbAuthListener() {
				@Override
				public void onSuccess(Oauth2AccessToken token) {
					mAccessToken = token;
					if (mAccessToken != null && mAccessToken.isSessionValid()) {
						// 保存 Token 到 SharedPreferences
						AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
						wbShare2();
					} else {
						// 以下几种情况，您会收到 Code：
						// 1. 当您未在平台上注册的应用程序的包名与签名时；
						// 2. 当您注册的应用程序包名与签名不正确时；
						// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
						showAuthFail();
					}
				}

				@Override
				public void cancel() {
					showShareCancel();
				}

				@Override
				public void onFailure(WbConnectErrorMessage errorMessage) {
					showAuthFail();
				}
			});
		}
	}

	/**
	 * 微博分享-发送
	 */
	private void wbShare2(){
		if (mShareEn != null) {
			// 封装微博的分享消息
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			TextObject textObject = new TextObject();
			textObject.text = "【" + mShareEn.getTitle() + "】 " + mShareEn.getUrl();
			weiboMessage.textObject = textObject;
			ImageObject imageObject = new ImageObject();
			Bitmap bitmap = mShareEn.getShareBm();
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
			}
			imageObject.setImageObject(bitmap);
			weiboMessage.imageObject = imageObject;
			// 发送请求消息到微博，唤起微博分享界面
			shareHandler.shareMessage(weiboMessage, false);
		}else {
			showEntityError();
		}
	}

	/**
	 * 微博分享回调-成功
	 */
	@Override
	public void onWbShareSuccess() {
		showShareSuccess();
	}

	/**
	 * 微博分享回调-失败
	 */
	@Override
	public void onWbShareFail() {
		showShareError();
	}

	/**
	 * 微博分享回调-取消
	 */
	@Override
	public void onWbShareCancel() {
		showShareCancel();
	}

	/**
	 * 邮件分享
	 */
	private void emailShare() {
		if (mShareEn != null) {
			//showShareLayer(false);
		}else {
			showEntityError();
		}
	}

	/**
	 * 短信分享
	 */
	private void messageShare() {
		if (mShareEn != null) {
			//showShareLayer(false);
		}else {
			showEntityError();
		}
	}

	/**
	 * 复制链接
	 */
	@SuppressWarnings("deprecation")
	private void urlCopy() {
		if (mShareEn != null) {
			ClipboardManager clip = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(mShareEn.getUrl()); // Copy link
			CommonTools.showToast(mContext.getString(R.string.share_msg_copy_link_ok), Toast.LENGTH_SHORT);
		}else {
			showEntityError();
		}
	}
	
	/**
	 * 分享成功提示
	 */
	private void showShareSuccess() {
		CommonTools.showToast(mContext.getString(R.string.share_msg_success), Toast.LENGTH_SHORT);
		shareFeedback();
	}

	/**
	 * 分享出错提示
	 */
	private void showShareError() {
		CommonTools.showToast(mContext.getString(R.string.share_msg_error), Toast.LENGTH_SHORT);
	}

	/**
	 * 用户取消了分享操作
	 */
	private void showShareCancel() {
		CommonTools.showToast(mContext.getString(R.string.share_msg_cancel), Toast.LENGTH_SHORT);
	}
	
	/**
	 * 分享参数出错提示
	 */
	private void showEntityError() {
		CommonTools.showToast(mContext.getString(R.string.share_msg_entity_error), Toast.LENGTH_SHORT);
	}

	/**
	 * 提示授权失败
	 */
	private void showAuthFail() {
		CommonTools.showToast(mContext.getString(R.string.share_msg_error_license), Toast.LENGTH_SHORT);
	}

	private void shareFeedback() {
		new Share_Feedback_Task().execute("");
	}

	/**
	 * 分享结果反馈
	 */
	class Share_Feedback_Task extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
//				return ServiceContext.getServiceContext().getServerJSONString(params[0]);
				return "";
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}
	
	/**
	 * 在Activity中的onActivityResult调用此方法
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// QQ
		if (requestCode == Constants.REQUEST_QQ_SHARE) {
			Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
		}
		// WB
		if (shareHandler != null) {
			shareHandler.doResultIntent(data,this);
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	
	/**
	 * 在Activity中的onNewIntent调用此方法
	 */
	public void onNewIntent(Intent intent){
		// WX
		if(mIWXApi != null){
			mIWXApi.handleIntent(intent, this);
		}
	}

	public interface ShareVewButtonListener{
		void onClick_Dismiss();
		void onClick_Share_QQ();
		void onClick_Share_QQ_Space();
		void onClick_Share_WX();
		void onClick_Share_Friends();
		void onClick_Share_WB();
		void onClick_Share_Email();
		void onClick_Share_Message();
		void onClick_Share_Copy();
	}

}

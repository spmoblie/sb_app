package com.songbao.sxb.wxapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.AppConfig;
import com.songbao.sxb.R;
import com.songbao.sxb.activity.BaseActivity;
import com.songbao.sxb.entity.WXEntity;
import com.songbao.sxb.utils.CommonTools;
import com.songbao.sxb.utils.ExceptionUtil;
import com.songbao.sxb.utils.NetworkUtil;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;


public class WXEntryActivity extends BaseActivity {

	private static final String APP_ID = AppConfig.WX_APP_ID;
	private static final String SECRET = AppConfig.WX_APP_SECRET;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wx_loading);

		setHeadVisibility(View.GONE);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	/**
	 * 微信SDK回调函数
	 */
	private void handleIntent(Intent intent) {
		stopAnimation();
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK)
		{
			if (AppApplication.isWXShare) {
				//request(AppConfig.REQUEST_SV_GET_WX_SHARE_CODE); //微信分享反馈
				showWechatResult(getString(R.string.share_msg_success));
			}else {
				if (NetworkUtil.isNetworkAvailable()) {
					new HttpAccess_token_Task().execute("https://api.weixin.qq.com/sns/oauth2/access_token?"
							+ "appid=" + APP_ID + "&secret=" + SECRET + "&code=" + resp.code + "&grant_type=authorization_code");
				} else {
					showWechatResult(getString(R.string.network_fault));
				}
			}
		}
		else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL)
		{
			if (AppApplication.isWXShare) {
				showWechatResult(getString(R.string.share_msg_cancel));
			}else {
				finish();
			}
		}
		else 
		{
			if (AppApplication.isWXShare) {
				showWechatResult(getString(R.string.share_msg_error));
			}else {
				showWechatResult(getString(R.string.share_msg_error_license));
			}
		}
	}
	
	/**
	 * 显示微信回调结果
	 */
	private void showWechatResult(String showStr) {
		CommonTools.showToast(showStr);
		finish();
	}

	/**
	 * 异步获取Access_token
	 */
	class HttpAccess_token_Task extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
//				return sc.getServerJSONString(params[0]);
				return "";
			} catch (Exception e) {
				ExceptionUtil.handle(e);
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			WXEntity wxEn = null;
			if (result != null) {
				/*try {
					wxEn = LoginJsonParser.getWexiAccessToken(result);
				} catch (JSONException e) {
					ExceptionUtil.handle(e);
				}
				if (wxEn != null) {
					UserManager.getInstance().saveWechatUserInfo(wxEn);
					if (LoginActivity.instance != null) {
						LoginActivity.instance.postWechatLoginRequest();
					}else {
						showWechatResult(getString(R.string.share_msg_error_license));
					}
					finish();
				} else {
					showWechatResult(getString(R.string.share_msg_error_license));
				}*/
			}else {
				showWechatResult(getString(R.string.share_msg_error_license));
			}
		}
	}

}

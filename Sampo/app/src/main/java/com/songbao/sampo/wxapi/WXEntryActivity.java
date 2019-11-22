package com.songbao.sampo.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.R;
import com.songbao.sampo.activity.BaseActivity;
import com.songbao.sampo.entity.WXEntity;
import com.songbao.sampo.utils.CommonTools;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.JsonLogin;
import com.songbao.sampo.utils.NetworkUtil;
import com.songbao.sampo.utils.UserManager;
import com.songbao.sampo.utils.retrofit.HttpRequests;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONObject;

import java.util.HashMap;


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
					getWXAccessToken(resp.code);
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

	private void getWXAccessToken(String code) {
		HashMap<String, String> map = new HashMap<>();
		map.put("appid", APP_ID);
		map.put("secret", SECRET);
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		loadSVData("https://api.weixin.qq.com/", "sns/oauth2/access_token", map, HttpRequests.HTTP_GET, AppConfig.REQUEST_SV_AUTH_WX_TOKEN);
	}

	@Override
	protected void callbackData(JSONObject jsonObject, int dataType) {
		try {
			switch (dataType) {
				case AppConfig.REQUEST_SV_AUTH_WX_TOKEN:
					WXEntity wxEn = JsonLogin.getWXAccessToken(jsonObject);
					if (wxEn != null) {
						UserManager.getInstance().saveWechatUserInfo(wxEn);
						//发送广播
						Intent intent = new Intent();
						intent.setAction(AppConfig.RA_PAGE_LOGIN);
						sendBroadcast(intent);
						finish();
					} else {
						showWechatResult(getString(R.string.share_msg_error_license));
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
		showWechatResult(getString(R.string.share_msg_error_license));
	}
}

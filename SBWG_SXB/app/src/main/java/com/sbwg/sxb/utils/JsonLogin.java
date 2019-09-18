package com.sbwg.sxb.utils;

import com.sbwg.sxb.entity.AuthResult;
import com.sbwg.sxb.entity.BaseEntity;
import com.sbwg.sxb.entity.QQEntity;
import com.sbwg.sxb.entity.QQUserInfoEntity;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.entity.WXEntity;
import com.sbwg.sxb.entity.WXUserInfoEntity;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonLogin {

	/**
	 * 解析通用数据
	 */
	private static BaseEntity getCommonKeyValue(JSONObject jsonObj) throws JSONException {
		BaseEntity baseEn = new BaseEntity();
		if (jsonObj.has("errno")) {
			baseEn.setErrno(jsonObj.getInt("errno"));
		}
		if (jsonObj.has("errmsg")) {
			baseEn.setErrmsg(jsonObj.getString("errmsg"));
		}
		return baseEn;
	}

	/**
	 * 解析返回的状态码
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	public static BaseEntity getBaseErrorData(JSONObject jsonObject) throws JSONException {
		return getCommonKeyValue(jsonObject);
	}

	/**
	 * 解析登录返回数据
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	public static BaseEntity getLoginData(JSONObject jsonObject) throws JSONException {
		BaseEntity mainEn = getCommonKeyValue(jsonObject);

		UserInfoEntity userInfo = new UserInfoEntity();
		if (StringUtil.notNull(jsonObject, "data")) {
			JSONObject jsonData = jsonObject.getJSONObject("data");
			if (StringUtil.notNull(jsonData, "userInfo")) {
				JSONObject data = jsonData.getJSONObject("userInfo");
				userInfo.setUserId(data.getString("songbaoId"));
				userInfo.setUserNick(data.getString("nickName"));
				userInfo.setUserHead(data.getString("avatarUrl"));
			}
			if (StringUtil.notNull(jsonData, "token")) {
				userInfo.setAppToken(jsonData.getString("token"));
			}
		}
		mainEn.setData(userInfo);
		return mainEn;
	}

	/**
	 * 获取微信AccessToken
	 */
	public static WXEntity getWexiAccessToken(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(jsonObject.getString("access_token"), 
				jsonObject.getString("expires_in"), jsonObject.getString("refresh_token"), 
				jsonObject.getString("openid"), jsonObject.getString("scope"), jsonObject.getString("unionid"));
	}
	
	/**
	 * 校验微信AccessToken
	 */
	public static WXEntity authWexiAccessToken(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(Integer.parseInt(jsonObject.getString("errcode")), jsonObject.getString("errmsg"));
	}

	/**
	 * 微信刷新AccessToken结果
	 */
	public static WXEntity getWexiAccessAuth(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(jsonObject.getString("access_token"), 
				jsonObject.getString("expires_in"), jsonObject.getString("refresh_token"),
				jsonObject.getString("openid"), jsonObject.getString("scope"));
	}

	/**
	 * 获取微信用户信息
	 */
	public static WXUserInfoEntity getWexiUserInfo(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXUserInfoEntity(
				jsonObject.getString("openid"), jsonObject.getString("nickname"), 
				jsonObject.getString("sex"), jsonObject.getString("province"), 
				jsonObject.getString("city"), jsonObject.getString("country"),
				jsonObject.getString("headimgurl"), jsonObject.getString("privilege"),
				jsonObject.getString("unionid"), jsonObject.getString("language"));
	}

	/**
	 * 微信校验AccessToken有效性
	 */
	public static WXEntity getWexiAccessTokenAuto(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new WXEntity(Integer.parseInt(jsonObject.getString("errcode")), jsonObject.getString("errmsg"));
	}

	/**
	 * 获取QQ登录结果
	 */
	public static QQEntity getQQLoginResult(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		QQEntity qqEn;
		int ret = jsonObj.getInt("ret");
		String msg = jsonObj.getString("msg");
		if (ret == 0) {
			qqEn = new QQEntity(ret, msg, jsonObj.getString("access_token"), jsonObj.getString("expires_in"), 
					jsonObj.getString("openid"), jsonObj.getString("pay_token"), 
					jsonObj.getString("pf"), jsonObj.getString("pfkey"));
		}else {
			qqEn = new QQEntity(ret, msg);
		}
		return qqEn;
	}

	/**
	 * 获取QQ用户资料
	 */
	public static QQUserInfoEntity getQQUserInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		QQUserInfoEntity userInfo;
		int ret = jsonObj.getInt("ret");
		String msg = jsonObj.getString("msg");
		if (ret == 0) {
			userInfo = new QQUserInfoEntity(ret, msg, jsonObj.getString("nickname"), 
					jsonObj.getString("gender"), jsonObj.getString("figureurl_qq_2"));
		}else {
			userInfo = new QQUserInfoEntity(ret, msg);
		}
		return userInfo;
	}

	/**
	 * 获取支付宝授权信息
	 */
	public static AuthResult getAlipayAuthInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		AuthResult authResult = null;
		return authResult;
	}

	/**
	 * 获取支付宝用户信息
	 */
	public static UserInfoEntity getAlipayUserInfo(Object jsonObject) throws JSONException {
		JSONObject jsonObj = (JSONObject) jsonObject;
		UserInfoEntity userInfo = null;
		return userInfo;
	}

}

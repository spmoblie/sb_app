package com.songbao.sampo_b.utils;

import com.songbao.sampo_b.entity.BaseEntity;
import com.songbao.sampo_b.entity.UserInfoEntity;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonLogin {

	/**
	 * 解析通用数据
	 */
	private static <T extends BaseEntity> BaseEntity<T> getCommonKeyValue(JSONObject jsonObj) throws JSONException {
		BaseEntity<T> baseEn = new BaseEntity<>();
		if (jsonObj.has("errno")) {
			baseEn.setErrNo(jsonObj.getInt("errno"));
		}
		if (jsonObj.has("errmsg")) {
			baseEn.setErrMsg(jsonObj.getString("errmsg"));
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
	public static BaseEntity<UserInfoEntity> getLoginData(JSONObject jsonObject) throws JSONException {
		BaseEntity<UserInfoEntity> mainEn = getCommonKeyValue(jsonObject);

		UserInfoEntity userInfo = new UserInfoEntity();
		if (StringUtil.notNull(jsonObject, "data")) {
			JSONObject jsonData = jsonObject.getJSONObject("data");
			if (StringUtil.notNull(jsonData, "userInfo")) {
				JSONObject data = jsonData.getJSONObject("userInfo");
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

}

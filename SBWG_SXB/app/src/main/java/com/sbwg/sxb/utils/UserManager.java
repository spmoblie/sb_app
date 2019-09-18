package com.sbwg.sxb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sbwg.sxb.AppApplication;
import com.sbwg.sxb.AppConfig;
import com.sbwg.sxb.entity.UserInfoEntity;
import com.sbwg.sxb.entity.WXEntity;
import com.sbwg.sxb.utils.retrofit.HttpRequests;
import com.sbwg.sxb.widgets.share.weibo.AccessTokenKeeper;


public class UserManager {

	private static UserManager instance;
	private SharedPreferences sp;
	private Editor editor;

	private String mUserId = null;
	private String shareId = null;
	private String loginAccount = null;
	private String mUserName = null;
	private String mUserNick = null;
	private String mUserHead = null;
	private String mUserIntro = null;
	private String mUserBirthday = null;
	private String mUserArea = null;
	private String mUserEmail = null;
	private String mUserPhone = null;
	private String mUserMoney = null;

	private String xAppToken = null;

	private String wxAccessToken = null;
	private String wxOpenId = null;
	private String wxUnionId = null;
	private String wxRefreshToken = null;

	public static UserManager getInstance(){
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new UserManager();
		}
	}

	private UserManager(){
		sp = AppApplication.getSharedPreferences();
		editor = sp.edit();
		editor.apply();
	}

	public String getUserId(){
		if(StringUtil.isNull(mUserId)){
			mUserId = sp.getString(AppConfig.KEY_USER_ID, "");
		}
		LogUtil.i("isLogin", "getUserId = " + mUserId);
		return mUserId;
	}

	private void saveUserId(String userId){
		editor.putString(AppConfig.KEY_USER_ID, userId).apply();
		mUserId = userId;
		LogUtil.i("isLogin", "saveUserId = " + userId);
	}

	public String getShareId(){
		if(StringUtil.isNull(shareId)){
			shareId = sp.getString(AppConfig.KEY_SHARE_ID, "");
		}
		LogUtil.i("isLogin", "getShareId = " + shareId);
		return shareId;
	}

	private void saveShareId(String id){
		editor.putString(AppConfig.KEY_SHARE_ID, id).apply();
		shareId = id;
		LogUtil.i("isLogin", "saveShareId = " + shareId);
	}

	public String getLoginAccount(){
		if(StringUtil.isNull(loginAccount)){
			loginAccount = sp.getString(AppConfig.KEY_LOGIN_ACCOUNT, "");
		}
		return loginAccount;
	}

	public void saveLoginAccount(String account){
		editor.putString(AppConfig.KEY_LOGIN_ACCOUNT, account).apply();
		loginAccount = account;
	}

	public String getUserName(){
		if(StringUtil.isNull(mUserName)){
			mUserName = sp.getString(AppConfig.KEY_USER_NAME, "");
		}
		return mUserName;
	}

	public void saveUserName(String userName){
		editor.putString(AppConfig.KEY_USER_NAME, userName).apply();
		mUserName = userName;
	}

	public String getUserPhone(){
		if(StringUtil.isNull(mUserPhone)){
			mUserPhone = sp.getString(AppConfig.KEY_USER_PHONE, "");
		}
		return mUserPhone;
	}

	public void saveUserPhone(String userPhone){
		editor.putString(AppConfig.KEY_USER_PHONE, userPhone).apply();
		mUserPhone = userPhone;
	}

	public String getUserEmail(){
		if(StringUtil.isNull(mUserEmail)){
			mUserEmail = sp.getString(AppConfig.KEY_USER_EMAIL, "");
		}
		return mUserEmail;
	}

	public void saveUserEmail(String userEmail){
		editor.putString(AppConfig.KEY_USER_EMAIL, userEmail).apply();
		mUserEmail = userEmail;
	}

	public String getUserNick(){
		if(StringUtil.isNull(mUserNick)){
			mUserNick = sp.getString(AppConfig.KEY_USER_NICK, "");
		}
		return mUserNick;
	}

	public void saveUserNick(String userNick){
		editor.putString(AppConfig.KEY_USER_NICK, userNick).apply();
		mUserNick = userNick;
	}

	public String getUserHead(){
		if(StringUtil.isNull(mUserHead)){
			mUserHead = sp.getString(AppConfig.KEY_USER_HEAD_URL, "");
		}
		return mUserHead;
	}

	public void saveUserHead(String userHead){
		editor.putString(AppConfig.KEY_USER_HEAD_URL, userHead).apply();
		mUserHead = userHead;
	}

	public String getUserIntro(){
		if(StringUtil.isNull(mUserIntro)){
			mUserIntro = sp.getString(AppConfig.KEY_USER_INTRO, "");
		}
		return mUserIntro;
	}

	public void saveUserIntro(String userIntro){
		editor.putString(AppConfig.KEY_USER_INTRO, userIntro).apply();
		mUserIntro = userIntro;
	}

	public int getUserGender(){
		return sp.getInt(AppConfig.KEY_USER_GENDER, 0);
	}

	public void saveUserGender(int genderCode){
		editor.putInt(AppConfig.KEY_USER_GENDER, genderCode).apply();
	}

	public String getUserBirthday(){
		if(StringUtil.isNull(mUserBirthday)){
			mUserBirthday = sp.getString(AppConfig.KEY_USER_BIRTHDAY, "");
		}
		return mUserBirthday;
	}

	public void saveUserBirthday(String userBirthday){
		editor.putString(AppConfig.KEY_USER_BIRTHDAY, userBirthday).apply();
		mUserBirthday = userBirthday;
	}

	public String getUserArea() {
		if(StringUtil.isNull(mUserArea)){
			mUserArea = sp.getString(AppConfig.KEY_USER_AREA, "");
		}
		return mUserArea;
	}

	public void saveUserArea(String userArea) {
		editor.putString(AppConfig.KEY_USER_AREA, userArea).apply();
		mUserArea = userArea;
	}

	public String getUserMoney(){
		if(StringUtil.isNull(mUserMoney)){
			mUserMoney = sp.getString(AppConfig.KEY_USER_MONEY, "0.00");
		}
		return mUserMoney;
	}

	public void saveUserMoney(String userMoney){
		editor.putString(AppConfig.KEY_USER_MONEY, userMoney).commit();
		mUserMoney = userMoney;
	}

	public String getXAppToken(){
		if(StringUtil.isNull(xAppToken)){
			xAppToken = sp.getString(AppConfig.KEY_X_APP_TOKEN, "");
		}
		return xAppToken;
	}

	public void saveXAppToken(String app_token){
		editor.putString(AppConfig.KEY_X_APP_TOKEN, app_token).apply();
		xAppToken = app_token;
	}

	public String getWXAccessToken(){
		if(StringUtil.isNull(wxAccessToken)){
			wxAccessToken = sp.getString(AppConfig.KEY_WX_ACCESS_TOKEN, "");
		}
		return wxAccessToken;
	}

	public void saveWXAccessToken(String access_token){
		editor.putString(AppConfig.KEY_WX_ACCESS_TOKEN, access_token).apply();
		wxAccessToken = access_token;
	}

	public String getWXOpenId(){
		if(StringUtil.isNull(wxOpenId)){
			wxOpenId = sp.getString(AppConfig.KEY_WX_OPEN_ID, "");
		}
		return wxOpenId;
	}

	public void saveWXOpenId(String openId){
		editor.putString(AppConfig.KEY_WX_OPEN_ID, openId).apply();
		wxOpenId = openId;
	}

	public String getWXUnionId(){
		if(StringUtil.isNull(wxUnionId)){
			wxUnionId = sp.getString(AppConfig.KEY_WX_UNION_ID, "");
		}
		return wxUnionId;
	}

	public void saveWXUnionId(String unionId){
		editor.putString(AppConfig.KEY_WX_UNION_ID, unionId).apply();
		wxUnionId = unionId;
	}

	public String getWXRefreshToken(){
		if(StringUtil.isNull(wxRefreshToken)){
			wxRefreshToken = sp.getString(AppConfig.KEY_WX_REFRESH_TOKEN, "");
		}
		return wxRefreshToken;
	}

	public void saveWXRefreshToken(String refreshToken){
		editor.putString(AppConfig.KEY_WX_REFRESH_TOKEN, refreshToken).apply();
		wxRefreshToken = refreshToken;
	}

	/**
	 * 清除课程Id
	 */
	public void clearCourseId() {
		editor.putString(AppConfig.KEY_SIGN_UP_ID, "").apply();
	}

	/**
	 * 标记课程Id
	 * @param courseId 课程Id
	 */
	public void saveCourseId(int courseId) {
		String keyStr = "_" + courseId + "_";
		String idStr = sp.getString(AppConfig.KEY_SIGN_UP_ID, "");
		editor.putString(AppConfig.KEY_SIGN_UP_ID, idStr + keyStr).apply();
	}

	/**
	 * 判断课程是否已报名
	 * @param courseId 课程Id
	 */
	public boolean isCourseSignUp(int courseId) {
		String keyStr = "_" + courseId + "_";
		String idStr = sp.getString(AppConfig.KEY_SIGN_UP_ID, "");
		if (idStr.contains(keyStr)) {
			return true;
		}
		return false;
	}

	/**
	 * 判定是否登录
	 */
	public boolean checkIsLogin(){
		return !StringUtil.isNull(getXAppToken())
			&& !StringUtil.isNull(getUserId())
			&& !getUserId().equals("0");
	}

	/**
	 * 登入成功保存状态
	 */
	public void saveUserLoginSuccess(UserInfoEntity infoEn){
		if (infoEn != null) {
			saveUserId(infoEn.getUserId());
			saveUserNick(infoEn.getUserNick());
			saveUserHead(infoEn.getUserHead());
			saveXAppToken(infoEn.getAppToken());
			changeAllDataStatus();
		}
	}

	/**
	 * 用户登出清除状态
	 */
	public void clearUserLoginInfo(Context ctx){
		// 解绑推送服务的用户信息
		AppApplication.onPushRegister(false);
		// 清空微信授权信息
		clearWechatUserInfo();
		// 清空微博授权信息
		AccessTokenKeeper.clear(ctx);
		// 清空缓存的用户信息
		clearUserLoginInfo();
		// 刷新所有状态数据
		changeAllDataStatus();
	}

	/**
	 * 刷新所有状态数据
	 */
	private void changeAllDataStatus() {
		HttpRequests.clearInstance(); //刷新Token
	}

	/**
	 * 清空用户信息
	 */
	private void clearUserLoginInfo(){
		saveUserId("");
		saveShareId("");
		saveUserName("");
		saveUserPhone("");
		saveUserEmail("");
		saveUserNick("");
		saveUserHead("");
		saveUserIntro("");
		saveUserGender(0);
		saveUserBirthday("");
		saveUserArea("");
		saveUserMoney("0.00");
		saveXAppToken("");
	}

	/**
	 * 保存用户信息
	 */
	public void saveUserInfo(UserInfoEntity infoEn) {
		if (infoEn != null) {
			saveShareId(infoEn.getShareId());
			saveUserName(infoEn.getUserName());
			saveUserPhone(infoEn.getUserPhone());
			saveUserEmail(infoEn.getUserEmail());
			saveUserNick(infoEn.getUserNick());
			saveUserHead(infoEn.getUserHead());
			saveUserIntro(infoEn.getUserIntro());
			saveUserGender(infoEn.getGenderCode());
			saveUserBirthday(infoEn.getBirthday());
			saveUserArea(infoEn.getUserArea());
			if (StringUtil.isNull(infoEn.getMoney())) {
				saveUserMoney("0.00");
			} else {
				saveUserMoney(infoEn.getMoney());
			}
			// 绑定用户信息至推送服务
			AppApplication.onPushRegister(true);
		}
	}

	/**
	 * 保存微信授权信息
	 */
	public void saveWechatUserInfo(WXEntity wxEn) {
		if (wxEn != null) {
			saveWXAccessToken(wxEn.getAccess_token());
			saveWXOpenId(wxEn.getOpenid());
			saveWXUnionId(wxEn.getUnionid());
			saveWXRefreshToken(wxEn.getRefresh_token());
		}
	}

	/**
	 * 清除微信授权信息
	 */
	private void clearWechatUserInfo() {
		saveWXAccessToken("");
		saveWXOpenId("");
		saveWXUnionId("");
		saveWXRefreshToken("");
	}

}
